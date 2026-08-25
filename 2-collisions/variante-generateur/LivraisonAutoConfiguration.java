package com.pizzeria.livraison;

import com.pizzeria.socle.Four;
import com.pizzeria.socle.Module;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "com.pizzeria.livraison", nameGenerator = LivraisonBeanNameGenerator.class)
public class LivraisonAutoConfiguration {

    /** L'etuve du scooter. Legitime chez elle. Meme nom de bean : "four". */
    @Bean
    public Four four() {
        return new Four("livraison", 60);
    }

    @Bean
    public Module livraisonModule(Four four, Caisse caisse) {
        return new Module() {
            public String nom()   { return "livraison"; }
            public String carte() { return "maintien a " + four.temperature() + "°C"
                                          + " — encaissee par " + caisse.enseigne(); }
        };
    }
}
