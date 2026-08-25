package com.pizzeria.pizza.api;

import java.util.List;

/** Ce que le module pizza offre aux autres. Un module ne depend JAMAIS que de ceci. */
public interface Catalogue {
    List<String> pizzas();
}
