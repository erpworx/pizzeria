#!/bin/sh
# Remet les deux modules dans un etat d'AVANT correctif.
#
#   ./casser.sh              actes 1-2 : deux classes Caisse homonymes, aucun BeanNameGenerator,
#                            et deux methodes @Bean "four". L'edition refuse de demarrer.
#   ./casser.sh --generateur acte 3 : le BeanNameGenerator est en place (Caisse est regle) mais les
#                            methodes @Bean restent homonymes — c'est la que le drapeau
#                            --spring.main.allow-bean-definition-overriding=true fait demarrer une
#                            edition ou un four sur deux a disparu, en silence.
set -e; cd "$(dirname "$0")"
source=variante-cassee
[ "$1" = "--generateur" ] && source=variante-generateur
cp "$source/PizzaAutoConfiguration.java"     module-pizza/src/main/java/com/pizzeria/pizza/
cp "$source/LivraisonAutoConfiguration.java" module-livraison/src/main/java/com/pizzeria/livraison/
echo "modules en etat '$source'. ./publier.sh puis (cd edition-collision && mvn -q clean package && java -jar target/*.jar)"
