package com.pizzeria.livraison;

import com.pizzeria.pizza.api.Catalogue;
import com.pizzeria.socle.Module;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LivraisonAutoConfiguration {

    /**
     * Le module pizza est OPTIONNEL. Present, on livre des pizzas nommees ; absent, on livre
     * ce que le client a commande ailleurs. Le module ne tombe pas, il se degrade.
     */
    @Bean
    public Module livraisonModule(ObjectProvider<Catalogue> catalogue) {
        Catalogue c = catalogue.getIfAvailable();
        return new Module() {
            public String nom() { return "livraison"; }
            public String carte() {
                return c == null
                        ? "scooter dispo — aucun catalogue, livraison par reference de commande"
                        : "scooter dispo — livre : " + String.join(", ", c.pizzas());
            }
        };
    }
}
