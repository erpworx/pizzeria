package com.pizzeria.socle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Le module peut etre PRESENT dans l'edition sans etre ACHETE. Deuxieme optionalite (v4.2 §6). */
public final class Entitlements {
    private static final Map<String, Set<String>> ACHATS = Map.of(
            "lyon",      Set.of("pizza", "livraison"),
            "marseille", Set.of("pizza"));            // Marseille n'a pas pris la livraison

    public static boolean aDroit(String tenant, String moduleCode) {
        return ACHATS.getOrDefault(tenant, Set.of()).contains(moduleCode);
    }
    public static String achats(String tenant) {
        return String.join(", ", List.copyOf(ACHATS.getOrDefault(tenant, Set.of())));
    }
}
