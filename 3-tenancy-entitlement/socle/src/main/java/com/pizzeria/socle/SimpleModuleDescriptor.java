package com.pizzeria.socle;

/** La mise en oeuvre triviale du contrat, comme dans le socle reel. */
public record SimpleModuleDescriptor(
        String code, String schema, int rank, String migrationLocation,
        String historyTable, String baselineVersion, boolean onSearchPath,
        String basePackage, boolean sellable) implements ModuleDescriptor {
}
