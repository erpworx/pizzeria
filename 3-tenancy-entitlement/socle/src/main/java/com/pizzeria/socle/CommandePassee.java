package com.pizzeria.socle;

/** Un evenement du socle. Les modules s'y abonnent — c'est la famille qu'on oublie. */
public record CommandePassee(String reference) {}
