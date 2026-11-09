# Contrôle ciblé « interfaces » : corrigé et barème

Seuils indicatifs : **≥ 16** module acquis ; **12 à 15** refaire les exercices ratés ;
**< 12** relire la leçon et refaire les exercices 4 à 9 avant de passer au Module 5.

---

## Partie A : QCM (5 points)

| Question | Réponse | Explication |
|---|---|---|
| A1 | **b** | Les champs d'interface sont toujours des constantes `public static final`. |
| A2 | **c** | Une seule classe mère, autant d'interfaces que voulu. |
| A3 | **b** | Une interface peut étendre plusieurs interfaces. a) : `ArrayList` est une classe ; c) : une interface n'« implémente » pas ; d) : pas de constructeur. |
| A4 | **b** | L'annotation est facultative : elle demande seulement au compilateur de vérifier. |
| A5 | **c** | Les méthodes `static` d'interface ne s'héritent pas. |
| A6 | **c** | Erreur « inherits unrelated defaults » : redéfinir, éventuellement avec `X.super.m()`. |
| A7 | **b** | `Iterable<T>` fournit `iterator()`, utilisé par le `for-each`. |
| A8 | **b** | `Comparable` définit l'ordre naturel (un seul) ; les ordres à la demande vont dans des `Comparator`. |
| A9 | **c** | Pas d'état dans une interface. |
| A10 | **b** | Le type déclaré est celui de la variable, `List<String>` ; le type réel est `ArrayList`. |

---

## Partie B : questions de cours (5 points)

**B1. (1 pt)** En Dart, toute classe définit une interface implicite, donc `implements UneClasse` fonctionne.
En Java, `implements` n'accepte **que des interfaces** déclarées avec le mot-clé `interface` *(0,5)*.
Solution : extraire une `interface UserRepository` (les méthodes publiques), renommer la classe actuelle
en `FirestoreUserRepository implements UserRepository`, puis écrire `FauxRepo implements UserRepository` *(0,5)*.
Accepté aussi : `extends UserRepository` en redéfinissant tout, à condition de signaler que c'est fragile
(constructeur parent, code exécuté, classe pas forcément ouverte à l'héritage).

**B2. (1,5 pt)** Trois différences parmi *(0,25 chacune)* :
- héritage multiple d'interfaces, mais une seule classe mère ;
- pas d'état (champs d'instance) dans une interface ;
- pas de constructeur dans une interface ;
- méthodes d'interface `public` (ou `private`), alors qu'une classe abstraite permet `protected` et package-private ;
- relation « sait faire » contre « est un, avec du code commun ».

Exemple *(0,75)* : on choisit une classe abstraite quand les sous-classes partagent un **état** ou
une logique de construction. Par exemple `abstract class Notificateur` avec un champ `journal`, un compteur
d'envois et une méthode *template* `envoyer()` qui journalise puis appelle `abstract transmettre()`.
Une réponse qui combine interface et classe abstraite (comme `List` / `AbstractList`) vaut le maximum.

**B3. (1 pt)** Pouvoir **ajouter des méthodes à une interface existante sans casser** toutes les classes
qui l'implémentent (cas de `Collection.stream()` en Java 8) *(0,5)*. Limite : une méthode `default`
n'a **pas accès à un état**, elle ne peut s'appuyer que sur les autres méthodes de l'interface *(0,5)*.

**B4. (1,5 pt)** Déclarer les variables, paramètres et dépendances avec le **type de l'interface**,
pas de la classe concrète. Le code ne dépend alors que du contrat *(0,5)*.
Exemple : `PresenceService` reçoit un `PresenceRepository` (interface) dans son constructeur, au lieu
d'appeler `FirebaseFirestore.instance` *(0,5)*. En test, on lui passe un `FakePresenceRepository`
(ou une lambda) qui renvoie des données fixes : le test est rapide, ne demande ni réseau ni Firebase,
et vérifie seulement la logique du service *(0,5)*.

---

## Partie C : compile ou pas ? (4 points)

Toutes les réponses ont été vérifiées avec `javac` 21.

| | Réponse |
|---|---|
| C1 | Compile, affiche **`B`**. Règle 2 : `B` étend `A`, sa version est plus spécifique, donc pas de conflit. |
| C2 | **Ne compile pas** : `f()` doit être `public` (on ne peut pas réduire la visibilité d'une méthode d'interface). |
| C3 | **Ne compile pas** : une méthode `static` d'interface ne s'hérite pas, il faut écrire `Compteur.zero()`. (`Compteur.zero().valeur()` afficherait `0`.) |
| C4 | Compile, affiche **`carré / aire=2.0`**. `Carre` redéfinit `info()` ; la lambda utilise la version `default`. |
| C5 | **Ne compile pas** : deux méthodes abstraites, donc `Op` n'est pas une interface fonctionnelle (l'annotation provoque l'erreur, et la lambda aussi). |
| C6 | **Ne compile pas** : le type déclaré est `Volant`, qui n'a pas `chanter()`. Il faudrait un cast, ou mieux `if (v instanceof Oiseau o) o.chanter();`. |
| C7 | Compile, affiche **`[kiwi, pomme, fraise, Ananas]`**. Tri par longueur (4, 5, 6, 6), puis ordre alphabétique **inverse** entre `fraise` et `Ananas`. Piège : les majuscules viennent avant les minuscules (`'A'` < `'f'`), donc `fraise` > `Ananas` et `fraise` passe en premier. |
| C8 | Compile, affiche **`10 20`**. |

---

## Partie D : code (6 points)

Code vérifié avec Java 21.

### D1 (3 points)

```java
record Presence(String apprenantId, boolean present) { }

interface SourceDePresences {
    List<Presence> presences(String sessionId);
}

class StatistiquesPresence {
    private final SourceDePresences source;

    StatistiquesPresence(SourceDePresences source) { this.source = source; }

    double taux(String sessionId) {
        List<Presence> liste = source.presences(sessionId);
        if (liste.isEmpty()) return 0.0;
        long presents = liste.stream().filter(Presence::present).count();
        return 100.0 * presents / liste.size();
    }

    List<String> absents(String sessionId) {
        return source.presences(sessionId).stream()
                .filter(p -> !p.present())
                .map(Presence::apprenantId)
                .toList();
    }
}

// main
SourceDePresences fausse = id -> List.of(
        new Presence("lea", true), new Presence("tom", false),
        new Presence("sam", true), new Presence("noe", true));
var stats = new StatistiquesPresence(fausse);
System.out.println(stats.taux("s1"));      // 75.0
System.out.println(stats.absents("s1"));   // [tom]
System.out.println(new StatistiquesPresence(id -> List.of()).taux("s2"));   // 0.0
```

Barème :

| Critère | Points |
|---|---|
| Interface correcte (une méthode, donc utilisable en lambda) | 0,5 |
| Dépendance reçue par le constructeur, champ `private final` | 0,75 |
| `taux` correct, cas de la liste vide compris (pas de division par zéro) | 0,75 |
| `absents` correct | 0,5 |
| Fausse source en lambda dans le `main` | 0,5 |

Les boucles classiques sont acceptées à la place des streams. Piège : `100 * presents / size`
en entiers donne un résultat faux (division entière), d'où `100.0`.

### D2 (3 points)

```java
@FunctionalInterface
interface Transformation {
    String appliquer(String s);

    default Transformation puis(Transformation suivante) {
        return s -> suivante.appliquer(this.appliquer(s));
    }

    static Transformation identite() {
        return s -> s;
    }

    static Transformation enchainer(Transformation... etapes) {
        Transformation resultat = identite();
        for (Transformation t : etapes) resultat = resultat.puis(t);
        return resultat;
    }
}

// main
Transformation nettoyer = String::trim;
Transformation crier    = String::toUpperCase;
Transformation exclamer = s -> s + " !";
System.out.println(nettoyer.puis(crier).puis(exclamer).appliquer("  bonjour  "));   // BONJOUR !
```

Barème :

| Critère | Points |
|---|---|
| Interface fonctionnelle correcte (annotation bienvenue) | 0,5 |
| `puis` : renvoie une **nouvelle** lambda, avec le bon ordre (`this` d'abord) | 1 |
| `identite` et `enchainer` | 0,75 |
| Références de méthodes `String::trim` et `String::toUpperCase` | 0,5 |
| Résultat `"BONJOUR !"` | 0,25 |

Erreur fréquente : écrire `this.appliquer(suivante.appliquer(s))`, ce qui inverse l'ordre.
