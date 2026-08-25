package com.pizzeria.socle;

import java.util.Set;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * « Tout point d'entree d'un module verifie l'entitlement du tenant. Il y a TROIS familles : les
 * services applicatifs, les listeners d'evenements, les jobs planifies. »
 *
 * <p>Le controle est STRUCTUREL : tout bean vivant sous {@code com.pizzeria.<code>} est enveloppe,
 * et le module cible est le SEGMENT DE PAQUET. Jamais une annotation a maintenir a la main — donc
 * un nouveau point d'entree est couvert sans que personne y pense. L'oubli classique est le
 * listener : le service est protege par l'API, l'ecran par la navigation, mais l'evenement
 * « arrive par derriere ».
 */
public class GardeEntitlement implements BeanPostProcessor {

    private static final String RACINE = "com.pizzeria.";

    /** Les modules non vendables sont toujours actifs — renseigne au demarrage par le socle. */
    private final Set<String> vendables;

    public GardeEntitlement(Set<String> vendables) { this.vendables = vendables; }

    @Override
    public Object postProcessAfterInitialization(Object bean, String nom) {
        String classe = bean.getClass().getName();
        if (!classe.startsWith(RACINE) || classe.startsWith(RACINE + "socle.")) return bean;
        if (bean instanceof ModuleDescriptor) return bean;   // la fiche n'est pas un point d'entree

        String reste = classe.substring(RACINE.length());
        int point = reste.indexOf('.');
        if (point < 0) return bean;
        String code = reste.substring(0, point);
        if (!vendables.contains(code)) return bean;          // module non vendable : toujours actif

        ProxyFactory usine = new ProxyFactory(bean);
        usine.setProxyTargetClass(true);
        usine.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation -> {
            if (TenantContext.isSet() && !TenantDirectory.aDroit(TenantContext.get(), code)) {
                throw new ModuleNonAchete(TenantContext.get(), code);
            }
            return invocation.proceed();
        });
        return usine.getProxy(bean.getClass().getClassLoader());
    }
}
