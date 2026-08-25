package com.pizzeria.socle;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PizzeriaApplication {

    public static void main(String[] args) {
        Meta.amorcer();                                  // le registre des tenants et des droits
        ConfigurableApplicationContext ctx = SpringApplication.run(PizzeriaApplication.class, args);

        String mode = ctx.getEnvironment().getProperty("pizzeria.garde", "structurel");
        boolean naif = "naif".equals(mode);

        List<ModuleDescriptor> fiches = List.copyOf(ctx.getBeansOfType(ModuleDescriptor.class).values());
        List<Module> modules = List.copyOf(ctx.getBeansOfType(Module.class).values());
        List<JobModule> jobs = List.copyOf(ctx.getBeansOfType(JobModule.class).values());

        System.out.println();
        System.out.println("  BASES  : " + Meta.RACINE);
        System.out.println("  GARDE  : " + mode);
        if (!naif) CouvertureEntitlement.verifier(ctx);
        MigrateurTenant.migrerTout(fiches);
        System.out.println();

        // Sans tenant dans le contexte, toute connexion est refusee — fail-closed.
        try { modules.get(0).rapport(); System.out.println("  ⚠ servi SANS tenant"); }
        catch (RuntimeException e) { System.out.println("  HORS TENANT : refuse (" + court(e) + ")"); }

        for (String tenant : TenantDirectory.actifs()) {
            TenantContext.executerAvec(tenant, () -> {
                System.out.println("  ── " + tenant.toUpperCase()
                        + " ── a achete : " + TenantDirectory.droits(tenant));
                for (Module m : modules) {                                    // 1. SERVICE
                    try {
                        String nom = m.nom();
                        if (naif && !TenantDirectory.aDroit(tenant, nom)) {
                            dire("service ", nom, "REFUSE (le socle a verifie lui-meme)");
                        } else {
                            dire("service ", nom, m.rapport());
                        }
                    } catch (ModuleNonAchete e) { refuse("service ", e); }
                }
                try {                                                         // 2. LISTENER
                    ctx.publishEvent(new CommandePassee("CMD-" + tenant));
                    dire("listener", "livraison", "execute");
                } catch (ModuleNonAchete e) { refuse("listener", e); }

                for (JobModule j : jobs) {                                    // 3. JOB PLANIFIE
                    try { j.executer(); dire("job     ", j.code(), "execute"); }
                    catch (ModuleNonAchete e) { refuse("job     ", e); }
                }
            });
        }
        System.out.println();
        ctx.close();
    }

    private static String court(Throwable e) {
        String m = e.getMessage();
        return m == null ? e.getClass().getSimpleName() : m.split("—")[0].trim();
    }
    private static void dire(String famille, String module, String texte) {
        System.out.println("     " + famille + " " + pad(module) + texte);
    }
    private static void refuse(String famille, ModuleNonAchete e) {
        System.out.println("     " + famille + " " + pad(e.module()) + "REFUSE par le garde structurel");
    }
    private static String pad(String s) { return (s + "            ").substring(0, 12); }
}
