package com.pizzeria.socle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exigence 3 du contrat de module : deux modules ne peuvent partager ni leur code, ni leur schema.
 * Le controle a lieu AU DEMARRAGE — une collision decouverte en production serait deux modules
 * ecrivant dans les memes tables.
 */
public final class ModuleRegistry {

    public static List<ModuleDescriptor> ordonner(List<ModuleDescriptor> modules) {
        Map<String, String> parCode = new HashMap<>();
        Map<String, String> parSchema = new HashMap<>();
        for (ModuleDescriptor m : modules) {
            String duplique = parCode.put(m.code(), m.basePackage());
            if (duplique != null) {
                throw new IllegalStateException("deux modules declarent le code '" + m.code()
                        + "' : " + duplique + " et " + m.basePackage());
            }
            duplique = parSchema.put(m.schema(), m.basePackage());
            if (duplique != null) {
                throw new IllegalStateException("deux modules declarent le schema '" + m.schema()
                        + "' : " + duplique + " et " + m.basePackage());
            }
        }
        return modules.stream().sorted(Comparator.comparingInt(ModuleDescriptor::rank)).toList();
    }
}
