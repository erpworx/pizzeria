package com.pizzeria.socle;

public class ModuleNonAchete extends RuntimeException {
    private final String module;
    public ModuleNonAchete(String tenant, String module) {
        super("le tenant '" + tenant + "' n'a pas achete le module '" + module + "'");
        this.module = module;
    }
    public String module() { return module; }
}
