package com.pizzeria.socle;

import java.util.Collection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PizzeriaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(PizzeriaApplication.class, args);

        Collection<Module> modules = ctx.getBeansOfType(Module.class).values();
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  PIZZERIA — " + modules.size() + " module(s) dans cette edition      ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        modules.forEach(m -> System.out.println("  ▸ " + m.nom() + " : " + m.carte()));
        System.out.println();
        ctx.close();
    }
}
