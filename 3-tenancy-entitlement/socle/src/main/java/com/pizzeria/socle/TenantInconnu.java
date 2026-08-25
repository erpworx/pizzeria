package com.pizzeria.socle;

public class TenantInconnu extends RuntimeException {
    public TenantInconnu(String tenant) { super("tenant inconnu : " + tenant); }
}
