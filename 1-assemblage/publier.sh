#!/bin/sh
# Publie les modules du chapitre dans le depot de paquets local (depot/).
# Apres ca, les editions les resolvent par COORDONNEE, hors de tout reacteur.
set -e
cd "$(dirname "$0")"
DEPOT="$(pwd)/depot"
rm -rf ~/.m2/repository/com/pizzeria   # une version republiee ne serait pas rechargee
(cd bom && mvn -q -B -ntp clean deploy -DaltDeploymentRepository="pizzeria::file://$DEPOT") && echo "  publie : bom"
(cd pizza-api && mvn -q -B -ntp clean deploy -DaltDeploymentRepository="pizzeria::file://$DEPOT") && echo "  publie : pizza-api"
(cd socle && mvn -q -B -ntp clean deploy -DaltDeploymentRepository="pizzeria::file://$DEPOT") && echo "  publie : socle"
(cd module-pizza && mvn -q -B -ntp clean deploy -DaltDeploymentRepository="pizzeria::file://$DEPOT") && echo "  publie : module-pizza"
(cd module-livraison && mvn -q -B -ntp clean deploy -DaltDeploymentRepository="pizzeria::file://$DEPOT") && echo "  publie : module-livraison"
echo "→ depot : $DEPOT"
