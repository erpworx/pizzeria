package com.pizzeria.socle;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * La base de CONTROLE — l'equivalent d'{@code erp_meta}. Elle porte le registre des tenants et
 * leurs droits d'usage. Elle n'est PAS une base de tenant : aucune donnee metier n'y entre.
 */
public final class Meta {

    /** Chemin ABSOLU : un chemin relatif ecrirait la ou la commande a ete lancee. */
    public static final Path RACINE = Path.of(System.getProperty("pizzeria.donnees",
            System.getProperty("java.io.tmpdir") + "/pizzeria-donnees")).toAbsolutePath();

    public static String url(String base) {
        return "jdbc:h2:" + RACINE.resolve(base) + ";DB_CLOSE_DELAY=-1";
    }

    public static Connection ouvrir(String base) throws SQLException {
        return DriverManager.getConnection(url(base), "sa", "");
    }

    /** Cree le registre s'il n'existe pas et l'amorce. En production ce serait une migration meta. */
    public static void amorcer() {
        try (Connection c = ouvrir("meta"); Statement s = c.createStatement()) {
            s.execute("""
                create table if not exists tenants (
                  code varchar(40) primary key, base varchar(60) not null,
                  statut varchar(20) not null)""");
            s.execute("""
                create table if not exists module_entitlements (
                  tenant varchar(40) not null, module varchar(40) not null,
                  primary key (tenant, module))""");
            s.execute("""
                create table if not exists tenant_schema_versions (
                  tenant varchar(40) not null, module varchar(40) not null,
                  etat varchar(20) not null, primary key (tenant, module))""");
            s.execute("merge into tenants key(code) values "
                    + "('lyon','pizzeria_lyon','ACTIF'), ('marseille','pizzeria_marseille','ACTIF')");
            s.execute("merge into module_entitlements key(tenant,module) values "
                    + "('lyon','pizza'), ('lyon','livraison'), ('marseille','pizza')");
        } catch (SQLException e) {
            throw new IllegalStateException("base meta indisponible", e);
        }
    }
}
