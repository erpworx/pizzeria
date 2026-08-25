package com.pizzeria.socle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.flywaydb.core.Flyway;

/**
 * Applique les migrations de CHAQUE module a CHAQUE tenant actif, par rang croissant.
 *
 * <p>Le socle ne connait aucun module : il lit les fiches trouvees dans le contexte. Un echec sur
 * un couple (tenant, module) est ISOLE — il est inscrit dans l'inventaire, la passe continue, et
 * le demarrage n'est pas empeche. Sinon un seul tenant en defaut bloquerait tous les autres.
 */
public final class MigrateurTenant {

    public static void migrerTout(List<ModuleDescriptor> modules) {
        List<ModuleDescriptor> ordonnes = ModuleRegistry.ordonner(modules);

        for (String tenant : TenantDirectory.actifs()) {
            for (ModuleDescriptor m : ordonnes) {
                try {
                    Flyway.configure()
                          .dataSource(Meta.url(TenantDirectory.base(tenant)), "sa", "")
                          .schemas(m.schema()).defaultSchema(m.schema())
                          .table(m.historyTable())
                          .baselineVersion(m.baselineVersion()).baselineOnMigrate(true)
                          .locations(m.migrationLocation())
                          .load().migrate();
                    inventorier(tenant, m.code(), "OK");
                    System.out.println("    " + tenant + " ← rang " + m.rank()
                            + " · schema '" + m.schema() + "' migre");
                } catch (RuntimeException e) {
                    inventorier(tenant, m.code(), "MIGRATION_REQUISE");
                    System.out.println("    " + tenant + " ← rang " + m.rank()
                            + " · schema '" + m.schema() + "' EN DEFAUT : " + e.getMessage());
                }
            }
        }
    }

    private static void inventorier(String tenant, String module, String etat) {
        try (Connection c = Meta.ouvrir("meta"); Statement s = c.createStatement()) {
            s.execute("merge into tenant_schema_versions key(tenant, module) values ('"
                    + tenant + "','" + module + "','" + etat + "')");
        } catch (SQLException e) {
            throw new IllegalStateException("inventaire des versions impossible", e);
        }
    }
}
