#!/bin/sh
# Remet les deux modules dans l'etat d'AVANT les correctifs : meme nom de methode @Bean ("four")
# et aucun BeanNameGenerator. Rejoue les actes 1 a 3 du chapitre.
set -e; cd "$(dirname "$0")"
cp variante-cassee/PizzaAutoConfiguration.java     module-pizza/src/main/java/com/pizzeria/pizza/
cp variante-cassee/LivraisonAutoConfiguration.java module-livraison/src/main/java/com/pizzeria/livraison/
echo "modules casses. ./publier.sh puis (cd edition-collision && mvn -q package && java -jar target/*.jar)"
