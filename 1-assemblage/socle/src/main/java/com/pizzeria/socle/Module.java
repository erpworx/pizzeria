package com.pizzeria.socle;

/** Le contrat. Tout module de la pizzeria publie un bean de ce type. */
public interface Module {
    String nom();
    String carte();
}
