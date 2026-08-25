package com.pizzeria.socle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Route vers la base du tenant courant. Le module ecrit du SQL sans jamais savoir ou il va.
 *
 * <p><b>Fail-closed, deliberement.</b> Pas de {@code defaultTargetDataSource} : sans tenant dans
 * le contexte, la demande ECHOUE. Servir une base par defaut — celle d'un tenant, ou pire celle de
 * controle — transforme un oubli de contexte en fuite entre clients, et le rend invisible.
 */
public class RoutageParTenant extends AbstractRoutingDataSource {

    private final Map<String, DataSource> sources = new ConcurrentHashMap<>();

    public RoutageParTenant() {
        setTargetDataSources(Map.of());          // aucune cible d'avance : les tenants sont decouverts
        setLenientFallback(false);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        if (!TenantContext.isSet()) throw new TenantNonPositionne();
        return TenantContext.get();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenant = (String) determineCurrentLookupKey();
        return sources.computeIfAbsent(tenant,
                t -> new DriverManagerDataSource(Meta.url(TenantDirectory.base(t)), "sa", ""));
    }
}
