package com.pizzeria.socle;

/** La fiche d'identite d'un module. Sans elle, son schema n'est ni cree ni migre. */
public interface ModuleDescriptor {
    String code();
    String schema();
    int rang();
    String emplacementMigrations();
    String paquetRacine();          // sert au garde d'entitlement (v4.2 §6.2)
}
