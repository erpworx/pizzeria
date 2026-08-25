package com.pizzeria.pizza;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/** Prefixe les beans SCANNES du module. Ne touche PAS aux methodes @Bean — c'est tout le sujet. */
public class PizzaBeanNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    public String generateBeanName(BeanDefinition d, BeanDefinitionRegistry r) {
        return "pizza." + super.generateBeanName(d, r);
    }
}
