package com.example.stockmanagementsystembackend.Core;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an order confirmation email to the vendor.
     *
     * @param recipientEmail Target email address
     * @param recipientName  Name of the recipient or business contact
     * @param loginUrl       URL where the vendor logs in to confirm the order
     */
    public void sendOrderConfirmationEmail(String recipientEmail, String recipientName, String loginUrl) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject("New Order Received - Dan Sapadha Pvt Ltd");

        String htmlContent = buildOrderEmailTemplate(recipientName, loginUrl);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    private String buildOrderEmailTemplate(String recipientName, String loginUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; padding: 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { text-align: center; border-bottom: 2px solid #3b82f6; padding-bottom: 15px; margin-bottom: 20px; }
                    .header h2 { color: #1e293b; margin: 0; }
                    .content { color: #334155; line-height: 1.6; font-size: 15px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #2563eb; color: #ffffff !important; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; }
                    .footer { text-align: center; font-size: 12px; color: #94a3b8; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <h2>Dan Sapadha Pvt Ltd</h2>
                    </div>
                    <div class="content">
                        <p>Have a nice day, %s!</p>
                        <p><strong>Dan Sapadha Pvt Ltd</strong> has placed a new order for items from you.</p>
                        <p>Please log in to your account using the button below to review and confirm the order details.</p>
                        <div class="btn-container">
                            <a href="%s" class="btn">Log In & Confirm Order</a>
                        </div>
                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p><a href="%s" style="color: #2563eb;">%s</a></p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message from Stock Management System. Please do not reply directly.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(recipientName, loginUrl, loginUrl, loginUrl);
    }
}