package com.pizzeria.livraison;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/** Prefixe les beans SCANNES du module. Ne touche PAS aux methodes @Bean — c'est tout le sujet. */
public class LivraisonBeanNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    public String generateBeanName(BeanDefinition d, BeanDefinitionRegistry r) {
        return "livraison." + super.generateBeanName(d, r);
    }
}
