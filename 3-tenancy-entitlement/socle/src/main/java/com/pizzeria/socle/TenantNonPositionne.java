package com.pizzeria.socle;

/**
 * Levee quand une connexion est demandee sans tenant. Le socle ECHOUE plutot que de servir une
 * base par defaut : un repli silencieux sur la dimension qui porte l'isolation est un fail-open.
 */
public class TenantNonPositionne extends IllegalStateException {
    public TenantNonPositionne() {
        super("aucun tenant dans le contexte — refus de servir une connexion (fail-closed)");
    }
}
