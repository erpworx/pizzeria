package com.pizzeria.pizza;

import com.pizzeria.socle.Module;
import com.pizzeria.socle.ModuleDescriptor;
import com.pizzeria.socle.SimpleModuleDescriptor;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@AutoConfiguration
public class PizzaAutoConfiguration {

    @Bean
    public ModuleDescriptor fichePizza() {
        return new SimpleModuleDescriptor(
                "pizza", "pizza", 10, "classpath:db/pizza",
                "pizza_flyway_history", "0", true, "com.pizzeria.pizza", true);
    }

    /** Aucune colonne tenant dans la requete. Le routage s'en charge. */
    @Bean
    public Module modulePizza(DataSource dataSource) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        return new Module() {
            public String nom() { return "pizza"; }
            public String rapport() {
                return String.join(", ", jdbc.queryForList(
                        "select nom from \"pizza\".carte order by nom", String.class));
            }
        };
    }
}
