package com.pizzeria.socle;

import java.nio.file.Path;
import java.util.List;

/** Le registre des pizzerias. Une pizzeria = une base de donnees a elle. */
public final class Tenants {
    public static final List<String> TOUS = List.of("lyon", "marseille");

    /** Chemin ABSOLU : un chemin relatif ecrirait la ou la commande a ete lancee. */
    private static final Path RACINE = Path.of(
            System.getProperty("pizzeria.donnees",
                    System.getProperty("java.io.tmpdir") + "/pizzeria-donnees")).toAbsolutePath();

    public static String url(String tenant) {
        return "jdbc:h2:" + RACINE.resolve("pizzeria_" + tenant) + ";DB_CLOSE_DELAY=-1";
    }
    public static Path racine() { return RACINE; }
}
