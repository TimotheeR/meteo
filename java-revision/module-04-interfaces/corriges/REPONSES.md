# Réponses aux questions des exercices

## Exercice 2 : pourquoi l'ordre par note n'est-il pas dans `compareTo` ?

`compareTo` définit **l'ordre naturel**, et il n'y en a qu'un par classe. Il doit être évident et
stable. Pour un élève, l'ordre alphabétique l'est ; « par note » n'est qu'un tri parmi d'autres
(croissant, décroissant, par moyenne…). Les ordres « à la demande » vont dans des `Comparator`.

Bonne pratique en plus : `compareTo` doit être cohérent avec `equals`. Ici, deux élèves de même nom
mais de notes différentes donnent `compareTo == 0` alors que `equals` est faux. Dans un `TreeSet`,
l'un des deux disparaîtrait. Pour un vrai projet : `Comparator.comparing(Eleve::nom).thenComparingDouble(Eleve::note)`.

## Exercice 3 : et si `Plage` était son propre `Iterator` ?

Il n'y aurait qu'**un seul compteur** partagé. La boucle intérieure le pousserait jusqu'à la fin dès
le premier tour de la boucle extérieure, qui s'arrêterait aussitôt : 2 tours au lieu de 9, et une
seconde boucle `for` sur la même plage ne ferait plus rien. D'où la règle : `iterator()` renvoie
**un nouvel objet** à chaque appel.

## Exercice 5 : prédictions

- `Ordinateur` → `"filaire"`. Règle 1 : une méthode héritée d'une **classe** (`Machine`) passe avant
  toute méthode `default`, même en cas de conflit entre `Wifi` et `Bluetooth`.
- `Modem` → `"5G"`. Règle 2 : `CinqG` étend `Wifi`, donc sa version est **plus spécifique**.
  Pas de conflit, même si `Modem` cite aussi `Wifi`.

## Exercice 6 : quel principe SOLID ?

Le **O** : *Open/Closed* (ouvert à l'extension, fermé à la modification). On ajoute
`DemandeurEmploi` sans toucher `Facturation`. Le **S** aussi en partie : chaque catégorie porte
sa propre règle.

Remarque : ici, un `enum CategorieApprenant` avec deux champs (libellé, coefficient) suffirait aussi.
L'interface devient indispensable quand les catégories ont des **comportements** différents
(par exemple un calcul de remise complexe), ou quand on veut pouvoir en ajouter **hors** du fichier.

## Exercice 7 : pourquoi pas `var v = s -> ...;` ?

Une lambda **n'a pas de type par elle-même** : c'est la cible (la variable, le paramètre)
qui dit à quelle interface fonctionnelle elle correspond. `s -> !s.isBlank()` pourrait être un
`Predicate<String>`, un `Validateur<String>`, un `Function<String, Boolean>`… Avec `var`,
il n'y a pas de cible : erreur « cannot infer type for local variable ». 🔁 En Dart, une fonction
a son propre type (`bool Function(String)`), d'où la surprise.

## Exercice 8 bonus : `pickFor` n'est appelée nulle part dans HeadsUp

C'est du **code mort**, et dupliqué. Deux options : le supprimer (YAGNI, *You Aren't Gonna Need It*),
ou, s'il doit servir, le factoriser comme dans le corrigé (un seul contrat générique). La leçon pour
les interfaces : en créer une quand il y a un **besoin réel** (plusieurs implémentations, tests,
frontière technique), pas par anticipation.

## Exercice 9 : ce qui empêche de tester `ProfilService` (version statique)

1. Les méthodes sont `static` : on ne peut pas passer une autre implémentation au service.
2. Elles vont chercher elles-mêmes un singleton global (`RemoteConfig.INSTANCE`, en vrai
   `FirebaseRemoteConfig.instance`) : un test aurait besoin d'un vrai Remote Config initialisé.
3. Une seule classe mélange deux responsabilités : trouver les clés **et** chiffrer.
4. Les dépendances sont **cachées** : rien dans la signature de `ProfilService` ne dit qu'il dépend
   de Firebase.

Remède : deux interfaces, et des dépendances reçues par le constructeur. Ça prépare directement
les Modules 9 (Singleton, injection) et 12 (tests).

> ⚠️ Remarque sur le vrai code de HeadsUp : `lastSettings()` affiche avec `debugPrint` la valeur
> complète de chaque réglage Remote Config, donc **la clé et l'IV de chiffrement**, dans les logs.
> À retirer.
