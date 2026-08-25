package com.pizzeria.socle;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PizzeriaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(PizzeriaApplication.class, args);

        Map<String, Four> fours = ctx.getBeansOfType(Four.class);
        System.out.println("  FOURS PRESENTS : " + fours.size());
        fours.forEach((nomDuBean, f) ->
                System.out.println("    bean '" + nomDuBean + "' → four de " + f.proprietaire()
                        + " a " + f.temperature() + "°C"));
        ctx.getBeansOfType(Module.class).values()
           .forEach(m -> System.out.println("  ▸ " + m.nom() + " : " + m.carte()));
        ctx.close();
    }
}
