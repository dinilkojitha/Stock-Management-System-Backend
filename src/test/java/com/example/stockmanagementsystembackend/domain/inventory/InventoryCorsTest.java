package com.example.stockmanagementsystembackend.domain.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class InventoryCorsTest {
    @Autowired private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void vitePreflightPassesThroughSecurityForInventoryAndExistingBranchLookup() throws Exception {
        for (String origin : new String[]{"http://localhost:5173", "http://127.0.0.1:5173"}) {
            for (String path : new String[]{"categories", "unit-types", "inventory-items",
                    "inventory-items/low-stock", "stocks", "stocks/expiring", "stocks/expired",
                    "inventory/dashboard", "branches"}) {
                for (String method : new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"}) {
                    mvc.perform(options("/api/" + path).header("Origin", origin)
                                    .header("Access-Control-Request-Method", method)
                                    .header("Access-Control-Request-Headers", "content-type"))
                            .andExpect(status().isOk())
                            .andExpect(header().string("Access-Control-Allow-Origin", origin));
                }
            }
        }
    }

    @Test
    void normalBadRequestRemainsReadableByViteInsteadOfLookingLikeNetworkFailure() throws Exception {
        mvc.perform(get("/api/categories/not-an-integer").header("Origin", "http://localhost:5173"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void unknownOriginIsStillRejected() throws Exception {
        mvc.perform(options("/api/categories").header("Origin", "http://untrusted.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
