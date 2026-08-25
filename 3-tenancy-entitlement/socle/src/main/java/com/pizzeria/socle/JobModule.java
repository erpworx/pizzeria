package com.pizzeria.socle;

/** Un job planifie publie par un module. Le socle le declenche, une fois par tenant. */
public interface JobModule { String code(); void executer(); }
