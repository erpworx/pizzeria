#!/bin/sh
# Un assemblage sain ne remplace aucune definition de bean. Des tests verts ne le prouvent pas :
# seul le demarrage du contexte complet le montre. (Lecon du 24/08 + retrospective Packwerk.)
jar=$1
sortie=$(java -jar "$jar" 2>&1)

if echo "$sortie" | grep -q "allow-bean-definition-overriding.*true"; then
  echo "✗ l'ecrasement de beans est AUTORISE — le garde-fou de Spring est desactive"; exit 1
fi

n=$(echo "$sortie" | grep -c "Overriding bean definition")
if [ "$n" -gt 0 ]; then
  echo "✗ $n definition(s) de bean ecrasee(s) — l'assemblage n'est pas sain :"
  echo "$sortie" | grep "Overriding bean definition" \
    | sed -E "s/.*for bean '([^']+)'.*/     bean ecrase : \1/"
  exit 1
fi

if echo "$sortie" | grep -q "APPLICATION FAILED TO START"; then
  echo "✗ l'edition ne demarre pas"; exit 1
fi
echo "✓ assemblage sain — 0 ecrasement, l'edition demarre"
