# Module 4 : Les interfaces 🎯

Durée prévue : **3 semaines** (environ 15 h), temps triplé car c'est un point faible déclaré.

## Parcours

| Étape | Contenu | Durée |
|---|---|---|
| 1 | 📘 [Leçon](lecon.md), puis l'auto-évaluation de la fin | 3 h |
| 2 | ✏️ [Exercices 1 à 5](exercices/) : les bases (contrat, `Comparable`, `Iterable`, `default`, diamant) | 1 h 30 |
| 3 | ✏️ [Exercices 6 et 7](exercices/) : polymorphisme, interfaces fonctionnelles | 1 h |
| 4 | ✏️ [Exercices 8 et 9 🪞](exercices/) : ton code HeadsUp (`HasFeature`, `EncryptionRepository`) | 1 h 15 |
| 5 | 🛠️ [TP 3 : notifications](tp/enonce.md) | 2 h 30 |
| 6 | 🧵 [Fil rouge : `WeatherProvider`](fil-rouge/) | 45 min |
| 7 | 📝 [Contrôle ciblé](controle/sujet.md) (1 h 30), puis [corrigé](controle/corrige.md) | 2 h |
| 8 | 🔁 Bonus : une interface de repository dans HeadsUp (voir ci-dessous) | 1 à 2 h |

Corrigés des exercices : [`corriges/`](corriges/) et [`corriges/REPONSES.md`](corriges/REPONSES.md).
Ne les ouvre qu'après avoir obtenu 🎉, ou après 20 minutes bloqué sur un exercice.

## Lancer le code

Java 21 (`java -version`). Aucun outil de build n'est nécessaire pour ce module :

```bash
# Exercices, corrigés, fil rouge : un fichier = un programme
java exercices/Exo01_Volant.java

# TP (plusieurs fichiers, packages)
cd tp/corrige
javac -d out $(find src -name '*.java')
java -cp out notifications.Main
java -cp out notifications.TestsNotifications
```

Si les accents ou les émojis s'affichent mal : ajoute `-Dstdout.encoding=UTF-8` après `java`.

## Bonus HeadsUp

Applique ce que tu as appris directement dans HeadsUp, sur une branche à part :

1. Choisis un repository simple (par exemple celui des adresses ou des téléphones).
2. Extrais une `abstract interface class` Dart avec ses méthodes publiques, et renomme l'actuelle
   en `Firestore…Repository implements …`.
3. Fais en sorte que le cubit qui l'utilise le **reçoive** dans son constructeur, au lieu d'appeler
   `FirebaseFirestore.instance` lui-même.
4. Écris un test de ce cubit avec un faux repository (le 5e fichier de test du projet !).

## Objectifs : à la fin du module, je sais…

- [ ] expliquer la différence entre Dart et Java sur les interfaces
- [ ] choisir entre interface et classe abstraite, et justifier
- [ ] utiliser `default`, `static` et `private` dans une interface, et résoudre un diamant
- [ ] écrire une interface fonctionnelle et l'utiliser avec des lambdas et des références de méthodes
- [ ] implémenter `Comparable`, `Comparator` et `Iterable`
- [ ] remplacer un `switch` sur un type par du polymorphisme
- [ ] extraire une interface d'une classe concrète pour la rendre testable
