package com.pizzeria.socle;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * v4.2 §6.2 : « tout point d'entree d'un module verifie l'entitlement du tenant. Il y a TROIS
 * familles : les services applicatifs, les listeners d'evenements, les jobs planifies. »
 *
 * <p>Le controle est STRUCTUREL : tout bean vivant sous {@code com.pizzeria.<code>} est enveloppe,
 * et le module cible est le SEGMENT DE PAQUET. Jamais une annotation a maintenir a la main — donc
 * un nouveau point d'entree est couvert sans que personne y pense.
 *
 * <p>Aucune dependance vers le contexte pendant l'enveloppement : la resoudre ici creerait un
 * cycle avec les beans qu'on enveloppe. Le droit est verifie A L'APPEL, quand le tenant est connu.
 */
public class GardeEntitlement implements BeanPostProcessor {

    private static final String RACINE = "com.pizzeria.";

    @Override
    public Object postProcessAfterInitialization(Object bean, String nom) {
        String classe = bean.getClass().getName();
        if (!classe.startsWith(RACINE) || classe.startsWith(RACINE + "socle.")) return bean;
        if (bean instanceof ModuleDescriptor) return bean;      // la fiche n'est pas un point d'entree

        String reste = classe.substring(RACINE.length());
        int point = reste.indexOf('.');
        if (point < 0) return bean;
        String code = reste.substring(0, point);                // "livraison", "pizza"

        ProxyFactory usine = new ProxyFactory(bean);
        usine.setProxyTargetClass(true);
        usine.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation -> {
            String tenant = TenantContext.actuel();
            if (tenant != null && !Entitlements.aDroit(tenant, code)) {
                throw new ModuleNonAchete(tenant, code);
            }
            return invocation.proceed();
        });
        return usine.getProxy(bean.getClass().getClassLoader());
    }
}
