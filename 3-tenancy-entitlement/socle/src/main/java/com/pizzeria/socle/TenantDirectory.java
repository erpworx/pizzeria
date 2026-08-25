package com.pizzeria.socle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ou vivent les tenants. Lu dans la base meta, mis en cache — l'equivalent du resolveur reel
 * (cache court, repli sur la base de controle). Le cache ne porte QUE le routage : jamais un secret.
 */
public final class TenantDirectory {

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    public static List<String> actifs() {
        List<String> codes = new ArrayList<>();
        try (Connection c = Meta.ouvrir("meta"); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("select code, base from tenants where statut = 'ACTIF' order by code")) {
            while (r.next()) { codes.add(r.getString(1)); CACHE.put(r.getString(1), r.getString(2)); }
        } catch (SQLException e) { throw new IllegalStateException("registre des tenants illisible", e); }
        return codes;
    }

    public static String base(String tenant) {
        String base = CACHE.get(tenant);
        if (base == null) { actifs(); base = CACHE.get(tenant); }
        if (base == null) throw new TenantInconnu(tenant);
        return base;
    }

    public static boolean aDroit(String tenant, String module) {
        try (Connection c = Meta.ouvrir("meta"); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("select 1 from module_entitlements where tenant = '"
                     + tenant + "' and module = '" + module + "'")) {
            return r.next();
        } catch (SQLException e) { throw new IllegalStateException("droits illisibles", e); }
    }

    public static String droits(String tenant) {
        List<String> m = new ArrayList<>();
        try (Connection c = Meta.ouvrir("meta"); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("select module from module_entitlements where tenant = '"
                     + tenant + "' order by module")) {
            while (r.next()) m.add(r.getString(1));
        } catch (SQLException e) { throw new IllegalStateException("droits illisibles", e); }
        return String.join(", ", m);
    }
}
