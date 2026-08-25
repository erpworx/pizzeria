package com.pizzeria.socle;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;

/**
 * Echoue au DEMARRAGE si un point d'entree de module n'est pas enveloppe par le garde.
 * Sans lui, un module dont les classes vivent hors de son paquet racine passerait au travers
 * en silence — et le silence est le mode de defaillance qu'on chasse.
 */
public final class CouvertureEntitlement {

    public static void verifier(ApplicationContext ctx) {
        List<String> nus = new ArrayList<>();
        for (String nom : ctx.getBeanDefinitionNames()) {
            Object b = ctx.getBean(nom);
            String classe = b.getClass().getName();
            boolean dansUnModule = classe.startsWith("com.pizzeria.")
                    && !classe.startsWith("com.pizzeria.socle.");
            if (dansUnModule && !(b instanceof ModuleDescriptor) && !classe.contains("CGLIB")) {
                nus.add(nom + " (" + classe + ")");
            }
        }
        if (!nus.isEmpty()) {
            throw new IllegalStateException(
                    "points d'entree non gardes : " + String.join(", ", nus));
        }
        System.out.println("  COUVERTURE : tous les points d'entree de module sont gardes");
    }
}
