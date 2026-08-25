package com.pizzeria.socle;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * AVANT l'auto-configuration de Spring Boot : sinon Boot fabrique sa propre DataSource H2 en
 * memoire et le module interroge une base vide, SANS QUE RIEN NE LE SIGNALE.
 */
@AutoConfiguration(before = DataSourceAutoConfiguration.class)
public class SocleAutoConfiguration {

    @Bean
    public DataSource dataSource() { return new RoutageParTenant(); }

    /** Le garde structurel. Absent quand pizzeria.garde=naif — pour montrer ce qui passe alors. */
    @Bean
    @ConditionalOnProperty(name = "pizzeria.garde", havingValue = "structurel", matchIfMissing = true)
    public static GardeEntitlement gardeEntitlement() {
        return new GardeEntitlement();
    }
}
