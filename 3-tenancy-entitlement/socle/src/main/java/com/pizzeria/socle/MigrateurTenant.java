package com.pizzeria.socle;

import java.util.Comparator;
import java.util.List;
import org.flywaydb.core.Flyway;

/**
 * Applique les migrations de CHAQUE module a CHAQUE tenant, par rang croissant.
 * Le socle ne connait aucun module : il lit les fiches trouvees dans le contexte.
 */
public class MigrateurTenant {

    public static void migrerTout(List<ModuleDescriptor> modules) {
        List<ModuleDescriptor> ordonnes = modules.stream()
                .sorted(Comparator.comparingInt(ModuleDescriptor::rang)).toList();

        for (String tenant : Tenants.TOUS) {
            for (ModuleDescriptor m : ordonnes) {
                Flyway.configure()
                      .dataSource(Tenants.url(tenant), "sa", "")
                      .schemas(m.schema())
                      .defaultSchema(m.schema())
                      .table(m.code() + "_flyway_history")   // une histoire par module
                      .locations(m.emplacementMigrations())
                      .load()
                      .migrate();
                System.out.println("    " + tenant + " ← rang " + m.rang()
                        + " · schema '" + m.schema() + "' migre");
            }
        }
    }
}
