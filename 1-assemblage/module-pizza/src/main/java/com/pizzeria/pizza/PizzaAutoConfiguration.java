package com.pizzeria.pizza;

import com.pizzeria.pizza.api.Catalogue;
import com.pizzeria.socle.Module;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class PizzaAutoConfiguration {

    @Bean
    public Catalogue catalogue() {
        return () -> List.of("Margherita", "Calzone", "Quatre fromages", "Truffe (nouveaute)");
    }

    @Bean
    public Module pizzaModule(Catalogue catalogue) {
        return new Module() {
            public String nom()   { return "pizza"; }
            public String carte() { return String.join(", ", catalogue.pizzas()); }
        };
    }
}
