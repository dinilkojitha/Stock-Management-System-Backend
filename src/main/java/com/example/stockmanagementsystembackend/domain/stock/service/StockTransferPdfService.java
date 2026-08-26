package com.example.stockmanagementsystembackend.domain.stock.service;

import com.example.stockmanagementsystembackend.domain.stock.dto.response.StockTransferResponse;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class StockTransferPdfService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm z", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());

    private final TemplateEngine templateEngine;
    private final StockTransferService transferService;

    public StockTransferPdfService(TemplateEngine templateEngine, StockTransferService transferService) {
        this.templateEngine = templateEngine;
        this.transferService = transferService;
    }

    public byte[] createTransferPdf(Integer transferId) {
        StockTransferResponse transfer = transferService.getTransferById(transferId);
        Instant requestInstant = Instant.parse(transfer.getRequestTime());
        Context context = new Context();
        context.setVariable("transfer", transfer);
        context.setVariable("requestDate", DATE_FORMAT.format(requestInstant));
        context.setVariable("requestTime", TIME_FORMAT.format(requestInstant));

        String html = templateEngine.process("stock-transfer-receipt", context);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            new PdfRendererBuilder().withHtmlContent(html, new ClassPathResource("templates/").getURL().toExternalForm())
                    .toStream(output).run();
            return output.toByteArray();
        } catch (IOException | RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate transfer PDF", exception);
        }
    }
}
