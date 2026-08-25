# 🍕 La pizzeria — assembler des modules sans copier leur code

Un exemple **volontairement bidon**, sans aucun rapport avec un ERP, qui démontre une seule chose :

> **Un produit peut être composé de modules consommés comme des artefacts versionnés, plutôt que
> comme du code recopié dans le dépôt qui les assemble.**

621 lignes de Java, quatre chapitres, trois pièges de production reproduits en miniature.

---

## Pourquoi cet exemple existe

Quand plusieurs équipes écrivent des modules dans leurs propres dépôts, il n'y a que deux façons de
les réunir dans un produit :

| | Copier le code | Consommer un artefact |
|---|---|---|
| Monter de version | fusionner à la main, coût croissant | **changer un numéro** |
| Revenir en arrière | impossible en pratique | **le même numéro, dans l'autre sens** |
| Qui fait foi | plus personne ne sait | **la coordonnée** |
| Auteur du module | doit déménager son code | **reste chez lui** |

La copie n'est pas un raccourci de méthode : c'est souvent le **seul chemin que l'outillage rend
possible**. Cet exemple montre l'autre chemin, en marche.

---

## Prérequis

Java 21+ et Maven 3.9+. Rien d'autre — pas de base de données à installer, pas de Docker, pas de
compte sur un dépôt de paquets. Le « dépôt de paquets » est un simple répertoire `depot/`.

Chaque chapitre est autonome : `publier.sh` construit les modules et les dépose dans `depot/`, puis
les éditions les résolvent **par coordonnée**, hors de tout réacteur Maven — exactement comme le
ferait un dépôt tiers.

---

## Chapitre 1 — l'assemblage

```sh
cd 1-assemblage
./publier.sh
for e in edition-sur-place edition-revendeur edition-complete; do
  (cd $e && mvn -q package && java -jar target/$e-1.0.0.jar)
done
```

Trois produits différents sortent des **mêmes JARs** :

```
edition-sur-place   ▸ pizza : Margherita, Calzone, Quatre fromages
edition-revendeur   ▸ livraison : scooter dispo — aucun catalogue, livraison par reference
edition-complete    ▸ livraison : scooter dispo — livre : Margherita, Calzone, Quatre fromages
                    ▸ pizza : Margherita, Calzone, Quatre fromages
```

Ce que ça démontre :

- **Une édition est une liste de JARs.** Le pom de l'édition ne nomme aucun paquet Java. Le module
  se câble seul, par un fichier d'une ligne dans son JAR :
  `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
  Retirer le JAR retire le module — pas de profil, pas de drapeau, pas de `if`.
- **La dépendance entre modules est optionnelle et se dégrade.** `module-livraison` dépend de
  `pizza-api` (le contrat, Java pur), **jamais** de `module-pizza` (l'implémentation). Sans le module
  pizza, la livraison tourne quand même : le revendeur est un produit vendable.
- **Le BOM tranche les versions.** Les éditions déclarent leurs dépendances **sans version**.
  Monter tout l'ensemble, ou revenir en arrière, c'est une ligne.

### Le verrou

```sh
./verifier-editions.sh
  ✓ edition-complete  — aucun .java
  ✓ edition-revendeur — aucun .java
  ✓ edition-sur-place — aucun .java
```

Un dépôt d'édition **ne contient aucun fichier `.java`**. Ce n'est pas une règle à respecter, c'est
une propriété du dépôt : il n'y a pas de répertoire `src/`, donc nulle part où coller un module.
Essayez d'en poser un — le script sort en 1 et nomme le fichier.

---

## Chapitre 2 — les collisions de beans

Deux modules qui veulent chacun **leur four**. Chacun a raison chez lui.

```java
// module-pizza                          // module-livraison
@Bean Four four()                        @Bean Four four()
  → new Four("pizza", 450)                 → new Four("livraison", 60)   // étuve du scooter
```

```sh
cd 2-collisions
./casser.sh && ./publier.sh
cd edition-collision && mvn -q package && java -jar target/edition-collision-1.0.0.jar
```

**Acte 1** — deux classes scannées nommées `Caisse` :
`ConflictingBeanDefinitionException`. Échec **bruyant** : c'est le bon cas.

**Acte 2** — un `BeanNameGenerator` préfixe les beans scannés. `Caisse` est réglé, et aussitôt :

```
BeanDefinitionOverrideException: bean 'four' ... overriding is disabled
```

> 🔑 **Un `BeanNameGenerator` renomme les composants *scannés*. Il ne renomme pas les méthodes
> `@Bean`.** C'est la découverte qui coûte une nuit quand on l'apprend sur un vrai module.

**Acte 3** — quelqu'un « répare » avec un drapeau :

```sh
java -jar target/edition-collision-1.0.0.jar --spring.main.allow-bean-definition-overriding=true
```

```
INFO  Overriding bean definition for bean 'four' ...
INFO  Started PizzeriaApplication in 0.497 seconds

  FOURS PRESENTS : 1
  ▸ livraison : maintien a 450°C   ← 🔥 les pizzas cuisent dans le scooter
```

Un four sur deux a disparu. **Une ligne INFO, l'application démarre, tout est vert.**

**Actes 4 et 5** — le vrai correctif, `./reparer.sh && ./publier.sh` : renommer les méthodes
`@Bean`. L'écrasement disparaît, mais l'injection **par type** devient ambiguë — et c'est
`-parameters` au compilateur qui la désambiguïse par le nom du paramètre.

### Le garde-fou

```sh
./verifier-assemblage.sh edition-collision/target/edition-collision-1.0.0.jar
```

Le dépôt d'édition n'a pas de sources, donc son contrôle ne peut pas être statique : **il démarre
l'édition et compte les écrasements**. Zéro toléré. Il refuse aussi le drapeau
`allow-bean-definition-overriding=true` — désactiver le garde-fou devient lui-même une faute.

---

## Chapitre 3 — la tenancy, puis l'entitlement

```sh
cd 3-tenancy-entitlement
./publier.sh
(cd edition-complete && mvn -q package && java -jar target/edition-complete-1.0.0.jar)
```

### Le registre : une base de contrôle, comme `erp_meta`

Les tenants ne sont pas une liste en dur. Ils vivent dans une **base de contrôle** — `meta` — qui
porte trois tables et **aucune donnée métier** :

| Table | Rôle |
|---|---|
| `tenants` | `code · base · statut` — seuls les `ACTIF` sont migrés et servis |
| `module_entitlements` | qui a acheté quel module |
| `tenant_schema_versions` | l'inventaire : où en est chaque couple (tenant, module) |

`TenantDirectory` la lit et met le **routage** en cache — jamais un secret.

### La fiche de module

Les neuf méthodes du contrat réel. Un auteur de module recopie cette forme telle quelle :

```java
public interface ModuleDescriptor {
    String code();              // "pizza" — unique dans l'assemblage
    String schema();            // le schéma SQL qui lui appartient
    int rank();                 // ordre d'application des migrations
    String migrationLocation(); // "classpath:db/pizza"
    String historyTable();      // sa propre table d'historique Flyway
    String baselineVersion();   // "0" sur un schéma neuf, "1" sur un schéma repris
    boolean onSearchPath();
    String basePackage();       // sert au garde d'entitlement — jamais une annotation
    default boolean sellable() { return true; }
}
```

`ModuleRegistry` **refuse au démarrage** deux modules qui déclarent le même `code` ou le même
`schema` — une collision découverte en production, ce serait deux modules écrivant dans les mêmes
tables.

### Les migrations, par tenant × module, par rang

```
  lyon      ← rang 10 · schema 'pizza' migre
  lyon      ← rang 20 · schema 'livraison' migre
  marseille ← rang 10 · schema 'pizza' migre
  marseille ← rang 20 · schema 'livraison' migre
```

Chaque module a **sa propre table d'historique Flyway**. Un échec sur un couple est **isolé** :
inscrit `MIGRATION_REQUISE` dans l'inventaire, la passe continue, le démarrage n'est pas empêché —
sinon un seul tenant en défaut bloquerait tous les autres.

**Aucune colonne `tenant_id`. Aucun `where tenant = ?`.** Le module écrit du SQL sans savoir où il
va ; une `DataSource` de routage choisit la base d'après le contexte.

### Fail-closed — le point non négociable

```
HORS TENANT : refuse (Failed to obtain JDBC Connection)
```

Le routage n'a **pas** de `defaultTargetDataSource`. Sans tenant dans le contexte, la demande
**échoue**. Servir une base par défaut — celle d'un tenant, ou pire celle de contrôle — transforme
un oubli de contexte en fuite entre clients, et la rend invisible.

Pour la même raison, `TenantContext` expose `executerAvec()` (portée bornée, restauration garantie)
et `propager()` (transmission explicite) : un `ThreadLocal` ne traverse **aucune** frontière de
thread, et un job planifié qui le perd travaille sur la mauvaise base sans rien dire.

### L'édition détermine le schéma de la base

```sh
(cd edition-pizza-seule && mvn -q package \
  && java -Dpizzeria.donnees=/tmp/pz-demo -Dpizzeria.modules-vendables=pizza \
     -jar target/edition-pizza-seule-1.0.0.jar)
```

| | schémas dans la base `lyon` |
|---|---|
| édition complète | `pizza`, `livraison` |
| édition pizza seule | `pizza` |

Le schéma `livraison` **n'a jamais existé**. Pas créé puis vidé — jamais créé.

### L'entitlement — « présent, mais pas acheté »

Le JAR est dans l'édition, mais le tenant n'a pas le droit d'usage. Trois familles de points
d'entrée : les **services**, les **listeners d'événements**, les **jobs planifiés**.

Le garde naïf — le socle vérifie avant d'appeler le service (`-Dpizzeria.garde=naif`) :

```
── MARSEILLE ── a achete : pizza
   service  livraison   REFUSE (le socle a verifie lui-meme)   ✅
      [livraison] scooter affecte a CMD-marseille              ← 🔴
   listener livraison   execute
      [livraison] tournee du soir preparee                     ← 🔴
   job      livraison   execute
```

**Le listener « arrive par derrière ».** Marseille n'a pas acheté la livraison, et pourtant un
scooter lui est affecté et sa tournée est préparée. Rien n'échoue, rien n'est journalisé.

Le garde structurel enveloppe **tout bean vivant sous `com.pizzeria.<code>`** ; le module cible est
le **segment de paquet**, et le droit est lu dans la base de contrôle. Aucune annotation à
maintenir, donc rien à oublier sur un nouveau point d'entrée. Les modules **non vendables** en sont
exemptés — c'est `sellable()` :

```
── MARSEILLE ── a achete : pizza
   service  livraison   REFUSE par le garde structurel     ✅
   listener livraison   REFUSE par le garde structurel     ✅
   job      livraison   REFUSE par le garde structurel     ✅
```

Et un contrôle de couverture **échoue au démarrage** si un bean de module échappe au garde :

```
COUVERTURE : tous les points d'entree de module sont gardes
```

---

## Représentativité — ce que cet exemple reproduit, et ce qu'il ignore

L'exemple vise la **forme** des mécanismes, pas leur surface. Ce qui est reproduit fidèlement :

| Mécanisme | Forme reproduite |
|---|---|
| Fiche de module | les 9 méthodes du contrat, plus une mise en œuvre `record` |
| Registre des modules | refus des collisions de `code` et de `schema` au démarrage |
| Base de contrôle | registre des tenants + droits + inventaire des versions, sans donnée métier |
| Résolution de tenant | lecture de la base de contrôle, cache du routage seul |
| Routage | **fail-closed** — pas de base par défaut |
| Contexte de tenant | portée bornée et propagation explicite entre threads |
| Migrations | par tenant × module, par rang, une histoire Flyway par module, échec isolé |
| Auto-câblage | `AutoConfiguration.imports`, à l'identique |
| Entitlement | trois familles de points d'entrée, garde par paquet, contrôle de couverture |

Ce qui est **volontairement absent** : authentification, SSO, clés d'API, référentiel tiers, RGPD
et effacement, journal d'événements et relais sortant, webhooks, notifications, courrier, coffre à
secrets, audit, observabilité, licences, pooling de connexions, détection de dérive de schéma,
provisionnement et cycle de vie des tenants. Ce sont des sous-systèmes entiers ; leur absence ne
change rien à ce que l'exemple démontre, mais **elle fait paraître le socle plus petit qu'il ne
l'est**.

---

## Le fil rouge

**Le silence est le mode de défaillance, pas l'erreur.** Les trois pièges reproduits ici démarrent,
passent, et sont faux :

| Piège | Symptôme |
|---|---|
| `AutoConfiguration.imports` oublié | Spring Boot substitue **sa propre base en mémoire** ; les modules lisent une base vide |
| Méthodes `@Bean` en collision | une ligne INFO, un bean sur deux disparaît, l'application démarre |
| Listener non gardé | un module non acheté travaille pour le client |

Aucun test unitaire n'attrape ces trois-là. **Ce qui les attrape, c'est le démarrage réel de
l'édition assemblée** — ce que fait `verifier-assemblage.sh`.

---

## Pièges rencontrés en construisant cet exemple

Ils valent d'être connus, ils coûtent tous une heure au minimum :

| Piège | Remède |
|---|---|
| **Republier la même version** ne recharge rien : `~/.m2` garde l'ancien JAR | ne jamais réutiliser un numéro de version (`publier.sh` purge par précaution) |
| **Chemin de données relatif** → les bases sont créées là où la commande a été lancée | chemin absolu, ou propriété explicite (`-Dpizzeria.donnees=…`) |
| `target/` **périmé** : Maven ne recompile pas si seul le `pom.xml` a changé | `mvn clean` |
| **Spring Boot 4** : `spring-boot-starter-aop` n'existe plus | `spring-boot-starter-aspectj` |
| **Instrumenter plutôt que deviner** : imprimer l'URL réelle de la connexion a résolu en un essai ce que six hypothèses n'avaient pas résolu | lire la trace avant de formuler une théorie |

---

## Ce qui n'y est pas

Pas de sécurité, pas d'API HTTP, pas de tests automatisés, pas de CI, pas de PostgreSQL — H2 en
fichier suffit à démontrer l'architecture. Ce n'est pas un modèle de code de production : c'est une
démonstration de **composition**.
