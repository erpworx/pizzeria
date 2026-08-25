package com.pizzeria.socle;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/** Route vers la base du tenant courant. Le module ecrit du SQL sans jamais savoir ou il va. */
public class RoutageParTenant extends AbstractRoutingDataSource {

    public RoutageParTenant() {
        Map<Object, Object> cibles = new HashMap<>();
        Tenants.TOUS.forEach(t -> cibles.put(t, new DriverManagerDataSource(Tenants.url(t), "sa", "")));
        setTargetDataSources(cibles);
        setDefaultTargetDataSource(cibles.get(Tenants.TOUS.get(0)));
    }

    @Override
    protected Object determineCurrentLookupKey() { return TenantContext.actuel(); }
}
