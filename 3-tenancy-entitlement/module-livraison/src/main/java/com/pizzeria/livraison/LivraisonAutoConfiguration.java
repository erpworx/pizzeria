package com.pizzeria.livraison;

import com.pizzeria.socle.Module;
import com.pizzeria.socle.ModuleDescriptor;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@AutoConfiguration
public class LivraisonAutoConfiguration {

    @Bean
    public ModuleDescriptor ficheLivraison() {
        return new ModuleDescriptor() {
            public String code()   { return "livraison"; }
            public String schema() { return "livraison"; }
            public int    rang()   { return 20; }
            public String emplacementMigrations() { return "classpath:db/livraison"; }
            public String paquetRacine() { return "com.pizzeria.livraison"; }
        };
    }

    @Bean
    public Module moduleLivraison(DataSource dataSource) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        return new Module() {
            public String nom() { return "livraison"; }
            public String rapport() {
                return jdbc.queryForObject(
                           "select count(*) from \"livraison\".tournee", Integer.class)
                       + " tournee(s) · scooters : "
                       + String.join(", ", jdbc.queryForList(
                           "select distinct scooter from \"livraison\".tournee order by scooter",
                           String.class));
            }
        };
    }

    @Bean
    public EcouteurCommande ecouteurCommande() { return new EcouteurCommande(); }

    @Bean
    public TourneeJob tourneeJob() { return new TourneeJob(); }
}
