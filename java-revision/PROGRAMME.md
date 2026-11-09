# Programme de révision / mise à niveau Java

> **Profil** : Java vu en M1/M2, Flutter/Dart en pro depuis 2019 (projets : `meteo`, `headsup`).
> **Points faibles déclarés** : singletons, interfaces.
> **Objectif** : remettre Java à niveau (Java 21 LTS) avec un passage par Dart quand ça aide.
>
> Ce document est le **plan** : notions, exercices, TP et examens, sans leur contenu.
> Les leçons, corrigés et sujets seront écrits ensuite, module par module.

---

## Fil rouge : « Météo en Java »

Un même projet tout au long du programme : réécrire en Java la logique de l'app
`test_sncf_connect_tech` (connexion à partir de `users.json`, prévisions sur 5 jours
venant d'OpenWeatherMap). Il commence en ligne de commande et devient à la fin une API REST.
Chaque module y ajoute une brique (marqué 🧵).

## Ce que montre HeadsUp (et ce que ça change au programme)

Constats tirés du code de `headsup` (Flutter, Dart 3.8, Bloc/Cubit, Firebase) :

| Dans HeadsUp | Conséquence pour le programme Java |
|---|---|
| ~270 classes `extends Equatable` | Tu manies déjà l'égalité par valeur : Module 2 (`equals`/`hashCode`, `record`) ira vite |
| `sealed class AppEvent`, Bloc/Cubit | Module 3 (`sealed`, `switch` exhaustif) : terrain connu |
| Exceptions maison (`AddFailure`, `FetchFailure`… `implements Exception`) | Module 5 centré sur ce qui est nouveau : *checked* et *unchecked* |
| ~180 fichiers avec des `extension` | Java n'a pas d'extensions : méthodes utilitaires `static`, composition |
| Repositories **concrets**, aucune interface abstraite | 🎯 Interfaces : tu n'as jamais eu besoin de les écrire, d'où le flou |
| ~55 appels directs à `FirebaseFirestore.instance`, cubits créés sans dépendances (`ItemsCubit()`) | 🎯 Singleton : tu en **utilises** partout sans le voir, et ils couplent le code à Firebase |
| 4 fichiers de test seulement | Module 12 : on verra que ces deux points sont justement ce qui rend le code difficile à tester |

Conclusion : les Modules 4, 9 et 12 forment un seul bloc
(« interface + injection = code testable »), avec des **exercices miroirs HeadsUp** (marqués 🪞) :
on reproduit en Java un morceau de ton architecture, puis on le refactorise.

## Légende

- 📘 Leçon : la notion
- ✏️ Exo : petit exercice ciblé (5 à 20 min)
- 🛠️ TP : travail pratique guidé (1 à 3 h)
- 🧵 Fil rouge : étape du projet Météo
- 📝 Examen : évaluation (QCM, questions de code, sujet pratique)
- 🎯 Point faible à travailler en priorité
- 🔁 Équivalent Dart / Flutter
- 🪞 Exercice miroir HeadsUp

---

## Module 0 : Mise en route et outils

📘 Notions
- JDK, JRE, JVM : compilation en bytecode, `javac` / `java`
- Versions LTS (8, 11, 17, 21, 25) et ce qui a changé depuis le M2
- IDE : IntelliJ IDEA (proche d'Android Studio 🔁)
- Build : Maven puis Gradle (🔁 `pubspec.yaml`, `flutter pub`)
- Structure d'un projet : `src/main/java`, `src/test/java`, packages
- `jshell` pour tester sans projet

✏️ Exos
- Hello World à la main avec `javac`, puis avec Maven
- Créer un projet Gradle multi-packages
- Explorer `jshell`

🧵 Initialiser le dépôt `meteo-java` (Gradle, Java 21)

---

## Module 1 : Bases du langage (passage rapide)

📘 Notions
- Types primitifs et types wrappers (`int` / `Integer`), autoboxing
- 🔁 Différences avec Dart : tout n'est pas objet, pas de `null` sur les primitifs
- `var`, `final`, constantes `static final` (🔁 `final` / `const`)
- Opérateurs, conversions, division entière
- Conditions, boucles, `switch` classique et expressions `switch` (Java 14+)
- `String`, immuabilité, `StringBuilder`, text blocks `"""`
- Tableaux
- Méthodes, surcharge, passage par valeur (les références sont copiées)
- `main`, arguments en ligne de commande

✏️ Exos
- FizzBuzz avec un `switch` expression
- Inverser une chaîne, palindrome, compter les voyelles
- Manipuler des tableaux : min, max, moyenne, tri à la main
- Quiz « Qu'affiche ce code ? » (pièges `==` / `equals`, cache des `Integer`)
- Remplacer les `extension` Dart par leur équivalent Java (méthodes `static` utilitaires)

📝 Mini-quiz 1 (QCM, 15 questions)

---

## Module 2 : Programmation orientée objet, les fondamentaux

📘 Notions
- Classe, objet, constructeurs, `this`, chaînage de constructeurs
- Encapsulation : `private`, `protected`, package-private, `public`
  (🔁 en Dart il n'y a que le `_` privé à la bibliothèque)
- Getters / setters, et pourquoi Java n'a pas de propriétés
- Membres `static` : à quoi ils servent, quand les éviter
- `equals`, `hashCode`, `toString` : le contrat
- Immuabilité : classes `final`, champs `final`, copies défensives
- `record` (Java 16+) (🔁 classes de données / `freezed`)
- `enum` avec champs et méthodes (🔁 enums améliorés de Dart 2.17)

✏️ Exos
- Classe `Point` avec `equals` / `hashCode` corrects, puis en `record`
- `enum Planete` avec masse, rayon et calcul de gravité
- Trouver les bugs d'encapsulation dans un code fourni
- 🪞 Traduire 3 modèles `Equatable` de HeadsUp (ex. adresse, téléphone, tarif) : à la main, puis en `record`

🛠️ TP 1 : Modéliser un compte bancaire (dépôt, retrait, historique, invariants)

🧵 Modèles `User`, `Weather`, `Forecast` en `record`

---

## Module 3 : Héritage et polymorphisme

📘 Notions
- `extends`, `super`, redéfinition (`@Override`)
- Polymorphisme : liaison dynamique, upcast et downcast
- `instanceof` et pattern matching (`if (o instanceof Chat c)`)
- Classes et méthodes `abstract`
- `final` sur une classe ou une méthode
- Classe `Object` et ses méthodes
- Composition ou héritage
- Classes `sealed` et `permits` (Java 17) (🔁 `sealed class` de Dart 3)
- Pattern matching dans `switch` (Java 21)

✏️ Exos
- Hiérarchie `Forme` → `Cercle`, `Rectangle`, `Triangle` (aire, périmètre)
- Réécrire cette hiérarchie en `sealed` + `switch` exhaustif
- Prédire la méthode appelée (surcharge vs redéfinition)
- 🪞 Porter `sealed class AppEvent` et son traitement en `switch` Java 21

🛠️ TP 2 : Gestion d'un parc de véhicules (abstraction, polymorphisme)

📝 Examen partiel 1 (Modules 1 à 3) : QCM + 2 exercices de code

---

## Module 4 : Interfaces 🎯

> ✅ Contenu rédigé : [module-04-interfaces/](module-04-interfaces/README.md)

📘 Notions
- Ce qu'est une interface : un contrat, pas une implémentation
- 🔁 Dart : toute classe est une interface implicite (`implements`), Java demande
  une déclaration explicite. C'est souvent cette différence qui rend la notion confuse
- Classe abstraite ou interface : comment choisir (tableau comparatif)
- Implémenter plusieurs interfaces
- Méthodes `default` et `static` dans les interfaces (Java 8+)
- Méthodes `private` dans les interfaces (Java 9+)
- Conflits entre méthodes `default` (problème du diamant)
- Interfaces fonctionnelles (`@FunctionalInterface`), lien avec les lambdas
- Interfaces du JDK à connaître : `Comparable`, `Comparator`, `Iterable`,
  `AutoCloseable`, `Runnable`
- Programmer vers une interface (`List<String> l = new ArrayList<>()`)
- Interfaces `sealed`
- Interfaces marqueurs (`Serializable`) et pourquoi on les évite aujourd'hui

✏️ Exos (progressifs)
- Écrire une interface `Volant` et l'implémenter dans `Oiseau` et `Avion`
- Rendre une classe `Comparable`, puis trier avec plusieurs `Comparator`
- Rendre une classe `Iterable` pour l'utiliser dans un `for-each`
- Ajouter une méthode `default` sans casser les classes existantes
- Résoudre un conflit de méthodes `default`
- Refactoriser un code plein de `if (type == ...)` en interface + polymorphisme
- 🪞 Traduire en Java `HasFeature` / `HasDataShareFeature` (classes abstraites de HeadsUp
  utilisées comme contrats) et décider : interface ou classe abstraite ?
- 🪞 Extraire une interface `EncryptionRepository` de la classe concrète, et écrire
  une seconde implémentation en mémoire

🛠️ TP 3 : Système de notifications (`Notifier` → Email, SMS, Push), puis
ajout d'un canal sans toucher au code existant (principe ouvert/fermé)

🧵 Interface `WeatherProvider` avec deux implémentations :
`OpenWeatherMapProvider` et `FakeWeatherProvider` (pour les tests)

📝 Contrôle ciblé « Interfaces » (questions de cours + 3 exercices)

---

## Module 5 : Exceptions

📘 Notions
- Hiérarchie : `Throwable`, `Error`, `Exception`, `RuntimeException`
- Exceptions *checked* et *unchecked* (🔁 Dart n'a que des unchecked)
- `try` / `catch` / `finally`, multi-catch
- `try-with-resources` et `AutoCloseable`
- Créer ses propres exceptions
- Bonnes pratiques : ne pas avaler une exception, garder la cause, choisir le bon niveau

✏️ Exos
- Lire un fichier avec `try-with-resources`
- Écrire `SoldeInsuffisantException` pour le TP 1
- 🪞 Porter `AddFailure`, `FetchFailure`, `UpdateFailure`… : lesquelles doivent être *checked* ?

🧵 Gestion des erreurs réseau et JSON (🔁 le `throw Exception('Failed to load datas')`)

---

## Module 6 : Génériques

📘 Notions
- Classes et méthodes génériques
- Bornes : `<T extends Comparable<T>>`
- Wildcards : `?`, `? extends`, `? super`, règle PECS
- Effacement de type (type erasure) et ses conséquences
  (🔁 en Dart les génériques sont réifiés)
- Inférence, opérateur diamant `<>`

✏️ Exos
- `Paire<A, B>`, `Boite<T>`
- Méthode générique `max(List<T>)`
- Choisir `extends` ou `super` dans des signatures données

🛠️ TP 4 : Implémenter un cache générique `Cache<K, V>` avec expiration

---

## Module 7 : Collections

📘 Notions
- Interfaces `Collection`, `List`, `Set`, `Queue`, `Deque`, `Map`
- Implémentations : `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`, `LinkedHashMap`,
  `HashMap`, `TreeMap`, `ArrayDeque`, `PriorityQueue`
- Complexités et choix de la bonne structure
- Collections immuables : `List.of`, `Map.of`, `Collections.unmodifiable...`
- Itérateurs, `ConcurrentModificationException`
- `Optional` (🔁 types nullables `?`, `??`)

✏️ Exos
- Compter la fréquence des mots d'un texte (`Map`)
- Supprimer les doublons en gardant l'ordre
- File d'attente prioritaire de tâches

🧵 Regrouper les prévisions par jour (`Map<LocalDate, List<Weather>>`)

📝 Examen partiel 2 (Modules 4 à 7)

---

## Module 8 : Programmation fonctionnelle

📘 Notions
- Lambdas et références de méthodes (`String::length`)
- Interfaces fonctionnelles du JDK : `Function`, `Predicate`, `Consumer`, `Supplier`,
  `BiFunction`, `UnaryOperator`
- API Stream : `filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`
- Opérations terminales : `collect`, `reduce`, `count`, `anyMatch`
- `Collectors` : `groupingBy`, `partitioningBy`, `joining`, `toMap`
- Streams parallèles : quand les utiliser, quand les éviter
- 🔁 Correspondances avec `Iterable` de Dart (`where`, `map`, `fold`, `expand`)

✏️ Exos
- 10 transformations de listes à écrire en streams
- Réécrire des boucles impératives en streams, puis l'inverse
- Statistiques sur des prévisions (min, max, moyenne par jour)

🛠️ TP 5 : Analyse d'un fichier CSV de ventes (100 % streams)

🧵 Calculer les températures min / max par jour

---

## Module 9 : Design patterns 🎯

📘 Notions
- **Singleton** 🎯
  - Le problème qu'il résout
  - Version naïve et pourquoi elle pose problème en multithread
  - Initialisation *eager*, *lazy*, `synchronized`, double-checked locking + `volatile`
  - Idiome du *holder* (Bill Pugh)
  - Singleton par `enum` (la version recommandée)
  - Comment on le casse : réflexion, sérialisation, clonage
  - Pourquoi on le considère souvent comme un anti-pattern
    (état global, tests difficiles)
  - Ce qu'on fait à la place : injection de dépendances
  - 🔁 Dart : `factory` constructor + instance `static`, `GetIt`, `Provider` / `Riverpod`
- Factory Method et Abstract Factory
- Builder (🔁 paramètres nommés de Dart, que Java n'a pas)
- Strategy (avec interfaces, puis avec lambdas)
- Observer (🔁 `ChangeNotifier`, `Stream`)
- Decorator, Adapter
- Repository (🔁 couche data d'une app Flutter)
- Principes SOLID, avec un exemple pour chacun

✏️ Exos
- Écrire 5 versions du Singleton et tester chacune avec plusieurs threads
- Casser un Singleton naïf par réflexion
- Remplacer un Singleton par de l'injection et voir ce que ça change pour les tests
- Builder pour une requête HTTP
- Strategy : calcul de frais de livraison
- 🪞 Repérer les singletons cachés de HeadsUp (`FirebaseFirestore.instance`,
  `FirebaseAuth.instance`, `WidgetsBinding.instance`…) : lesquels sont légitimes, lesquels gênent ?
- 🪞 Écrire en Java un `ItemsService` qui appelle directement un `Database.INSTANCE`,
  essayer de le tester, puis le refactoriser avec injection par constructeur
  (🔁 ce que fait déjà `App(AuthenticationRepository ...)` avec `RepositoryProvider`)

🛠️ TP 6 : Refactoring d'un code « spaghetti » en appliquant 3 patterns et SOLID
(🪞 point de départ : une version Java simplifiée d'un module HeadsUp, cubit + repository + Firestore)

🧵 `ConfigManager` en Singleton (`enum`), `WeatherService` construit par injection

📝 Contrôle ciblé « Singleton & patterns »

---

## Module 10 : Entrées/sorties, fichiers et JSON

📘 Notions
- `java.nio.file` : `Path`, `Files` (lecture, écriture, parcours)
- Encodages, `BufferedReader`
- Sérialisation JSON avec Jackson (🔁 `fromJson` / `toJson`, `json_serializable`)
- Dates : `java.time` (`LocalDate`, `Instant`, `ZonedDateTime`, `DateTimeFormatter`)
  (🔁 `DateTime`, `intl`)

✏️ Exos
- Lire `users.json` et le convertir en `List<User>`
- Convertir des timestamps Unix en dates locales

🧵 Écran de connexion en console : vérification email / mot de passe à partir de `users.json`

---

## Module 11 : Programmation concurrente et asynchrone

📘 Notions
- 🔁 Le grand écart : Dart a une seule boucle d'événements (`Future`, `async/await`, isolates),
  Java utilise de vrais threads qui partagent la mémoire
- `Thread`, `Runnable`, cycle de vie
- Conditions de course, `synchronized`, `volatile`, verrous
- `ExecutorService`, pools de threads
- `Future` puis `CompletableFuture` (🔁 `Future.then`, `Future.wait`)
- Collections concurrentes, `Atomic*`
- Threads virtuels (Java 21)
- Structured concurrency (aperçu)

✏️ Exos
- Mettre en évidence une race condition, puis la corriger
- Lancer 10 requêtes en parallèle avec `CompletableFuture.allOf`
- Comparer un pool classique et des threads virtuels
- 🪞 Reproduire un flux type `snapshots()` de Firestore : `Flow.Publisher` / Observer
  (🔁 `Stream`, `rxdart`)

🛠️ TP 7 : Téléchargeur de fichiers en parallèle avec suivi de progression

🧵 Récupérer en parallèle la météo de plusieurs villes (`HttpClient` asynchrone)

📝 Examen partiel 3 (Modules 8 à 11)

---

## Module 12 : Tests et qualité

📘 Notions
- JUnit 5 : `@Test`, assertions, `@BeforeEach`, tests paramétrés
- AssertJ pour des assertions lisibles
- Mockito : mocks, stubs, `verify` (🔁 `mockito` / `mocktail` en Dart)
- TDD : écrire le test d'abord
- Couverture de code (JaCoCo)
- Outils de qualité : Checkstyle, SpotBugs, SonarLint

✏️ Exos
- Tester les classes du TP 1
- Kata TDD : calculatrice de chaînes (*String Calculator*)
- Mocker `WeatherProvider`
- 🪞 Tester le `ItemsService` refactorisé du Module 9 avec un faux repository :
  comparer avec la version Singleton (impossible à tester proprement)

🧵 Tests unitaires du service météo grâce à `FakeWeatherProvider`
(on récupère ici le travail fait sur les interfaces au Module 4)

Bonus 🔁 : appliquer la même démarche dans HeadsUp (une interface de repository + un test de cubit
avec `bloc_test`), pour transposer ce qu'on a appris en Java

---

## Module 13 : Fonctionnement de la JVM

📘 Notions
- Mémoire : pile (stack) et tas (heap)
- Garbage collector : principe et principaux collecteurs (G1, ZGC)
- Chargement des classes, *classpath*, modules (JPMS, aperçu)
- `String pool`, références faibles
- Réflexion et annotations (comment marchent Spring et JUnit)
- Écrire une annotation personnalisée

✏️ Exos
- Provoquer une `StackOverflowError` et une `OutOfMemoryError`, puis lire les traces
- Lire les annotations d'une classe par réflexion

---

## Module 14 : Écosystème pro (ouverture)

📘 Notions
- Spring Boot : injection de dépendances, contrôleurs REST, configuration
- Persistance : JDBC, JPA / Hibernate (aperçu)
- Logs : SLF4J / Logback
- Docker pour une app Java (aperçu)
- 🔁 Kotlin, comparé à Java et à Dart (utile côté Android / plugins Flutter)

🛠️ TP 8 : Petite API REST Spring Boot (CRUD)

🧵 **Final** : exposer le service météo en API REST
(`GET /forecast?city=Paris`) que l'app Flutter pourra appeler

---

## Examen final

📝 Trois parties
1. **QCM** (40 questions) sur tout le programme
2. **Questions de cours** : expliquer des notions avec ses mots
   (ex. « Interface ou classe abstraite ? », « Écris un Singleton thread-safe et justifie ton choix »)
3. **Épreuve pratique** (3 à 4 h) : petit projet à rendre, avec tests,
   corrigé selon une grille (fonctionnel, conception, tests, lisibilité)

Bonus : une série de questions façon **entretien technique Java**.

---

## Calendrier indicatif (≈ 5 h / semaine)

| Semaines | Modules |
|---|---|
| 1 | 0, 1 |
| 2 | 2, 3 (rapide : notions proches de ce que tu fais avec Equatable / sealed) + Partiel 1 |
| 3 à 5 | 4 🎯 (temps triplé) |
| 6 à 7 | 5, 6, 7 + Partiel 2 |
| 8 | 8 |
| 9 à 10 | 9 🎯 (temps doublé) |
| 11 | 10 |
| 12 à 13 | 11 + Partiel 3 |
| 14 | 12 |
| 15 | 13 |
| 16 à 17 | 14 + Fil rouge final |
| 18 | Examen final |

---

## Organisation prévue des fichiers (à venir)

```
java-revision/
├── PROGRAMME.md              ← ce fichier
├── module-XX-<nom>/
│   ├── lecon.md
│   ├── exercices/            ← énoncés + squelettes de code
│   ├── corriges/
│   └── tp/
├── examens/
│   ├── partiel-1/ … partiel-3/
│   └── final/
└── meteo-java/               ← projet fil rouge (Gradle)
```
