package com.pizzeria.livraison;

import com.pizzeria.socle.CommandePassee;
import org.springframework.context.event.EventListener;

/** DEUXIEME famille : le listener. Celui qu'on oublie — il « arrive par derriere ». */
public class EcouteurCommande {
    @EventListener
    public void surCommande(CommandePassee e) {
        System.out.println("        [livraison] scooter affecte a " + e.reference());
    }
}
