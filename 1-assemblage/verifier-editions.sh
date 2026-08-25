#!/bin/sh
# Une edition est une liste de JARs. Un depot d'edition ne porte AUCUN code.
echec=0
for e in edition-*; do
  code=$(find "$e" -name '*.java' -not -path '*/target/*')
  if [ -n "$code" ]; then
    echo "  ✗ $e — du code source dans une edition :"; echo "$code" | sed 's/^/      /'; echec=1
  else
    echo "  ✓ $e — aucun .java ($(ls "$e" | tr '\n' ' ')seulement)"
  fi
done
exit $echec
