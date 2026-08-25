package com.pizzeria.socle;

/**
 * La fiche d'identite d'un module — publiee par le module, decouverte par le socle.
 * Les noms sont ceux du contrat reel : un auteur de module recopie cette forme telle quelle.
 */
public interface ModuleDescriptor {
    String code();              // "pizza" — unique dans l'assemblage
    String schema();            // le schema SQL qui lui appartient
    int rank();                 // ordre d'application des migrations
    String migrationLocation(); // "classpath:db/pizza"
    String historyTable();      // sa propre table d'historique Flyway
    String baselineVersion();   // "0" sur un schema neuf, "1" sur un schema repris
    boolean onSearchPath();     // le schema est-il pose sur le chemin de recherche
    String basePackage();       // sert au garde d'entitlement — jamais une annotation

    /** Un module non vendable est toujours actif : le socle ne le soumet pas a l'entitlement. */
    default boolean sellable() { return true; }
}
