# Module 4 : Exercices

Chaque exercice est un fichier Java autonome, lancé directement (Java 11+, ici Java 21) :

```bash
java Exo01_Volant.java
```

- Les consignes sont **en tête de chaque fichier** (`TODO`).
- Le `main` contient les vérifications : ✅ / ❌, puis un total. Le but est d'arriver à 🎉.
- La plupart des fichiers **ne compilent pas au départ** : c'est voulu, les erreurs du compilateur
  indiquent ce qui manque. Les exercices 6 et 9 tournent dès le départ sur le code « avant »
  qu'il faut refactoriser.
- La petite classe `Check` en bas de chaque fichier est un outil de vérification provisoire
  (JUnit arrive au Module 12).
- Si les accents ou les émojis s'affichent mal : `java -Dstdout.encoding=UTF-8 Exo01_Volant.java`.

| # | Fichier | Notion | Durée |
|---|---|---|---|
| 1 | `Exo01_Volant.java` | Déclarer et implémenter une interface, programmer vers l'interface | 15 min |
| 2 | `Exo02_Comparer.java` | `Comparable`, `Comparator`, `reversed`, `thenComparing` | 20 min |
| 3 | `Exo03_Iterable.java` | `Iterable` / `Iterator`, classe anonyme | 25 min |
| 4 | `Exo04_Default.java` | Faire évoluer un contrat : `default` et `static` | 20 min |
| 5 | `Exo05_Diamant.java` | Conflit de `default`, `X.super.m()`, les 3 règles | 15 min |
| 6 | `Exo06_Polymorphisme.java` | Remplacer des `if`/`switch` sur un type par une interface | 30 min |
| 7 | `Exo07_Fonctionnelle.java` | Interface fonctionnelle, lambdas, références de méthodes | 30 min |
| 8 | `Exo08_HasFeature.java` 🪞 | Interface générique : `HasFeature` de HeadsUp | 30 min |
| 9 | `Exo09_Chiffrement.java` 🪞 | Extraire des interfaces d'`EncryptionRepository` de HeadsUp, injection | 45 min |

Ordre conseillé : 1 → 9. Les corrigés sont dans [`../corriges/`](../corriges/), et les réponses
aux questions posées dans les fichiers sont dans [`../corriges/REPONSES.md`](../corriges/REPONSES.md).
