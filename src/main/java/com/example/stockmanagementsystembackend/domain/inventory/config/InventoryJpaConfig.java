package com.example.stockmanagementsystembackend.domain.inventory.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class InventoryJpaConfig {
    @Bean
    public Clock stockBatchClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public HibernatePropertiesCustomizer inventoryTableNames() {
        // Preserve the supplied inventory schema names. All other identifiers use
        // the existing Spring Boot snake-case convention; no DDL is enabled.
        return properties -> properties.put("hibernate.physical_naming_strategy", new InventoryTableNames());
    }

    public static class InventoryTableNames extends PhysicalNamingStrategySnakeCaseImpl {
        @Override
        public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment environment) {
            if (name != null && switch (name.getText()) {
                case "InventoryItem", "Category", "UnitType", "Stock", "Branch" -> true;
                default -> false;
            }) {
                return Identifier.toIdentifier(name.getText(), true);
            }
            return super.toPhysicalTableName(name, environment);
        }
    }
}
