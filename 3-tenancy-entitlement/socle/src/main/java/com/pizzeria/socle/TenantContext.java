package com.pizzeria.socle;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Le tenant courant. Aucune entite ne porte de colonne tenant : l'isolation est physique.
 *
 * <p>Un {@code ThreadLocal} ne traverse AUCUNE frontiere de thread. Un job planifie, un appel
 * asynchrone ou un pool d'executeurs perd le tenant en silence — et ce qui suit se fait alors
 * sur la mauvaise base. D'ou {@link #executerAvec} (portee bornee) et {@link #propager}
 * (transmission explicite) : les deux seules facons sures de franchir un thread.
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static void set(String tenant) { CURRENT.set(tenant); }
    public static String get()            { return CURRENT.get(); }
    public static boolean isSet()         { return CURRENT.get() != null; }
    public static void clear()            { CURRENT.remove(); }

    /** Porte bornee : le tenant precedent est restaure, meme en cas d'exception. */
    public static <T> T executerAvec(String tenant, Supplier<T> travail) {
        String precedent = CURRENT.get();
        CURRENT.set(tenant);
        try { return travail.get(); }
        finally { if (precedent == null) CURRENT.remove(); else CURRENT.set(precedent); }
    }

    public static void executerAvec(String tenant, Runnable travail) {
        executerAvec(tenant, () -> { travail.run(); return null; });
    }

    /** Capture le tenant ICI pour le rejouer LA-BAS — a utiliser avant tout passage de thread. */
    public static Runnable propager(Runnable travail) {
        String capture = CURRENT.get();
        return () -> executerAvec(capture, travail);
    }

    public static <T> Callable<T> propager(Callable<T> travail) {
        String capture = CURRENT.get();
        return () -> executerAvec(capture, () -> {
            try { return travail.call(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}
