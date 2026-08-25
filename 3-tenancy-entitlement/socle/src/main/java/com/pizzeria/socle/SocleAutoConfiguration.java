package com.pizzeria.socle;

import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * AVANT l'auto-configuration de Spring Boot : sinon Boot fabrique sa propre DataSource H2 en
 * memoire et les modules interrogent une base vide, SANS QUE RIEN NE LE SIGNALE.
 */
@AutoConfiguration(before = DataSourceAutoConfiguration.class)
public class SocleAutoConfiguration {

    @Bean
    public DataSource dataSource() { return new RoutageParTenant(); }

    /**
     * Le garde structurel. La liste des modules vendables est figee au demarrage a partir des
     * fiches presentes dans l'edition — un module absent n'a rien a garder.
     */
    @Bean
    @ConditionalOnProperty(name = "pizzeria.garde", havingValue = "structurel", matchIfMissing = true)
    public static GardeEntitlement gardeEntitlement(
            @Value("${pizzeria.modules-vendables:pizza,livraison}") String vendables) {
        return new GardeEntitlement(
                Set.of(vendables.split(",")).stream().map(String::trim).collect(Collectors.toSet()));
    }
}
