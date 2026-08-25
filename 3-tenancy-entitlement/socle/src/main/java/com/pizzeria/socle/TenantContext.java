package com.pizzeria.socle;

/** Le tenant courant. Aucune entite ne porte de colonne tenant : l'isolation est physique. */
public final class TenantContext {
    private static final ThreadLocal<String> COURANT = new ThreadLocal<>();
    public static void poser(String t) { COURANT.set(t); }
    public static String actuel()      { return COURANT.get(); }
    public static void vider()         { COURANT.remove(); }
}
