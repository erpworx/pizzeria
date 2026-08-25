package com.pizzeria.pizza;

import org.springframework.stereotype.Component;

/** Classe SCANNEE. Nom simple "Caisse" — la livraison en a une aussi. */
@Component
public class Caisse {
    public String enseigne() { return "caisse comptoir"; }
}
