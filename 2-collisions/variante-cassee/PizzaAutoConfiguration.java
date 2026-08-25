package com.pizzeria.pizza;

import com.pizzeria.socle.Four;
import com.pizzeria.socle.Module;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "com.pizzeria.pizza")
public class PizzaAutoConfiguration {

    /** Le four a pizza. Nom du bean = nom de la methode = "four". */
    @Bean
    public Four four() {
        return new Four("pizza", 450);
    }

    @Bean
    public Module pizzaModule(Four four, Caisse caisse) {
        return new Module() {
            public String nom()   { return "pizza"; }
            public String carte() { return "Margherita cuite a " + four.temperature() + "°C"
                                          + " — encaissee par " + caisse.enseigne(); }
        };
    }
}
