#!/bin/sh
# Applique les correctifs : methodes @Bean renommees (fourAPizza / etuveScooter) et
# BeanNameGenerator par module. Avec -parameters au compilateur, l'assemblage devient sain.
set -e; cd "$(dirname "$0")"
cp variante-corrigee/PizzaAutoConfiguration.java     module-pizza/src/main/java/com/pizzeria/pizza/
cp variante-corrigee/LivraisonAutoConfiguration.java module-livraison/src/main/java/com/pizzeria/livraison/
echo "modules corriges. ./publier.sh puis (cd edition-collision && mvn -q clean package && java -jar target/*.jar)"
