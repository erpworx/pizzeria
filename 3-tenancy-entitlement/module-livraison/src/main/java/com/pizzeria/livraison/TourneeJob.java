package com.pizzeria.livraison;

import com.pizzeria.socle.JobModule;

/** TROISIEME famille : le job planifie. */
public class TourneeJob implements JobModule {
    public String code() { return "livraison"; }
    public void executer() {
        System.out.println("        [livraison] tournee du soir preparee");
    }
}
