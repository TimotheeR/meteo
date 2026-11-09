# Module 4 : Les interfaces 🎯

> Durée prévue : 3 semaines (environ 15 h).
> Prérequis : Modules 1 à 3 (classes, héritage, classes abstraites).
> Objectif : savoir **quand** et **comment** écrire une interface en Java, et pouvoir l'expliquer
> à l'oral comme en entretien.

---

## 0. Pourquoi c'est flou quand on vient de Dart

En Dart, **toute classe est une interface implicite**. Tu peux écrire `class A implements B`
avec n'importe quelle classe `B`, sans jamais déclarer d'interface. Dans HeadsUp, tes repositories
sont des classes concrètes et `HasFeature` est une `abstract class` utilisée comme contrat :
tu as toujours eu des interfaces, sans avoir eu besoin de les nommer.

En Java, c'est l'inverse :

- une **classe** ne peut pas servir d'interface : `implements` n'accepte qu'une `interface` ;
- une classe hérite d'**une seule** classe (`extends`), mais peut implémenter **plusieurs** interfaces ;
- la séparation « contrat » / « implémentation » est donc **explicite et obligatoire**.

Retiens cette idée pour tout le module :

> **Une interface dit CE QU'un objet sait faire. Une classe dit COMMENT il le fait.**

---

## 1. Déclarer une interface

```java
public interface Volant {
    int ALTITUDE_MAX = 12_000;   // constante : implicitement public static final

    void decoller();             // méthode abstraite : implicitement public abstract
    void atterrir();
    int altitude();
}
```

Ce que le compilateur ajoute tout seul :

| Tu écris | Le compilateur comprend |
|---|---|
| `void decoller();` | `public abstract void decoller();` |
| `int ALTITUDE_MAX = 12_000;` | `public static final int ALTITUDE_MAX = 12_000;` |

Une interface **n'a pas** :
- de constructeur ;
- de champ d'instance (pas d'état) ;
- de méthode `protected`.

On ne peut donc pas l'instancier : `new Volant()` ne compile pas.

---

## 2. Implémenter une interface

```java
public class Oiseau implements Volant {
    private int altitude = 0;

    @Override
    public void decoller() { altitude = 50; }

    @Override
    public void atterrir() { altitude = 0; }

    @Override
    public int altitude() { return altitude; }
}
```

Règles :

1. La classe doit implémenter **toutes** les méthodes abstraites, sinon elle doit être déclarée `abstract`.
2. Les méthodes implémentées doivent être `public` : oublier `public` est une erreur classique
   (« attempting to assign weaker access privileges »).
3. `@Override` est facultatif mais **toujours recommandé** : le compilateur vérifie que tu
   redéfinis bien une méthode existante (🔁 comme `@override` en Dart).

### Plusieurs interfaces, une seule classe mère

```java
public class Canard extends Animal implements Volant, Nageur, Comparable<Canard> {
    // ...
}
```

L'ordre est imposé : d'abord `extends`, ensuite `implements`.

### Une interface peut en étendre d'autres

```java
public interface Amphibie extends Volant, Nageur { }
```

Entre interfaces on utilise `extends`, et l'héritage multiple est permis.

---

## 3. L'interface comme type : programmer vers une interface

C'est ici que les interfaces deviennent utiles. Une variable, un paramètre ou un retour peut avoir
**le type d'une interface** :

```java
List<String> noms = new ArrayList<>();   // et non ArrayList<String> noms
Volant v = new Oiseau();
```

```java
static void faireUnTour(Volant v) {
    v.decoller();
    System.out.println("Altitude : " + v.altitude());
    v.atterrir();
}

faireUnTour(new Oiseau());
faireUnTour(new Avion());
```

`faireUnTour` ignore la classe réelle de l'objet. Elle ne dépend que du contrat. Résultat :

- on peut ajouter `Drone implements Volant` **sans modifier** `faireUnTour` ;
- on peut lui passer un **faux** `Volant` dans un test.

Le type déclaré (`Volant`) détermine les méthodes que tu as le droit d'**appeler**.
Le type réel (`Oiseau`) détermine le code **exécuté** (liaison dynamique, voir Module 3).

> 🔁 Dans HeadsUp, `App(AuthenticationRepository ...)` reçoit sa dépendance par le constructeur :
> c'est la bonne moitié du travail. L'autre moitié, c'est que `AuthenticationRepository` soit une
> interface, pour pouvoir donner autre chose qu'un vrai Firebase. Le Module 9 y revient.

---

## 4. Interface ou classe abstraite ?

| | Interface | Classe abstraite |
|---|---|---|
| Héritage | Plusieurs par classe | Une seule par classe |
| État (champs d'instance) | ❌ | ✅ |
| Constructeur | ❌ | ✅ |
| Méthodes avec du code | ✅ (`default`, `static`, `private`) | ✅ |
| Visibilité des méthodes | `public` (ou `private` pour l'aide interne) | toutes |
| Relation exprimée | « **sait faire** » (*can-do*) | « **est un** » (*is-a*), avec du code commun |
| Exemples JDK | `Comparable`, `Runnable`, `List` | `AbstractList`, `InputStream` |

**Règle de décision :**

1. Par défaut, commence par une **interface**.
2. Passe à une **classe abstraite** seulement si plusieurs implémentations doivent partager
   un **état** (des champs) ou une logique de construction.
3. Les deux se combinent souvent : `interface List` + `abstract class AbstractList implements List`
   + `class ArrayList extends AbstractList`. L'interface est le contrat public, la classe abstraite
   un kit de démarrage.

---

## 5. Méthodes `default`, `static` et `private` (Java 8 et 9)

### `default` : faire évoluer un contrat sans tout casser

Problème historique : en 2014, Java 8 veut ajouter `stream()` à `Collection`. Si c'était une méthode
abstraite, **toutes** les classes qui implémentent `Collection` dans le monde cesseraient de compiler.
D'où les méthodes `default`, qui ont un corps :

```java
public interface Volant {
    void decoller();
    void atterrir();
    int altitude();

    default boolean estEnVol() {
        return altitude() > 0;   // s'appuie sur les méthodes abstraites
    }
}
```

- Les classes existantes en héritent automatiquement.
- Elles peuvent la redéfinir avec `@Override`.
- Une méthode `default` **n'a pas accès à un état** : elle ne peut appeler que d'autres méthodes.

> 🔁 C'est ce qui se rapproche le plus d'un `mixin` Dart, mais **sans champs**.

### `static` : utilitaires liés au contrat

```java
public interface Volant {
    static boolean memeAltitude(Volant a, Volant b) {
        return a.altitude() == b.altitude();
    }
}

Volant.memeAltitude(oiseau, avion);   // appel via le nom de l'interface
```

Exemples JDK : `List.of(...)`, `Comparator.comparing(...)`, `Map.entry(...)`.

Une méthode `static` d'interface **ne s'hérite pas** : `Oiseau.memeAltitude(...)` ne compile pas.

> 🔁 Java n'a pas d'`extension`. Quand tu possèdes l'interface, une méthode `default` ou `static`
> joue souvent le rôle que tes ~180 extensions jouent dans HeadsUp.

### `private` : factoriser le code des méthodes `default`

```java
public interface Journal {
    default void info(String m)   { ecrire("INFO", m); }
    default void erreur(String m) { ecrire("ERREUR", m); }

    private void ecrire(String niveau, String m) {
        System.out.println("[" + niveau + "] " + m);
    }
}
```

---

## 6. Le problème du diamant

Deux interfaces fournissent la même méthode `default` :

```java
interface Wifi      { default String connecter() { return "wifi"; } }
interface Bluetooth { default String connecter() { return "bluetooth"; } }

class Telephone implements Wifi, Bluetooth { }   // ❌ ne compile pas
```

Java refuse de choisir. Tu dois redéfinir la méthode, et tu peux appeler une version précise
avec `NomInterface.super.methode()` :

```java
class Telephone implements Wifi, Bluetooth {
    @Override
    public String connecter() {
        return Wifi.super.connecter() + "+" + Bluetooth.super.connecter();
    }
}
```

**Les trois règles de résolution** (à savoir par cœur) :

1. **La classe gagne** : une méthode définie dans la classe ou une classe mère passe avant toute `default`.
2. **L'interface la plus spécifique gagne** : si `B extends A` et que les deux définissent `m()`,
   c'est celle de `B`.
3. Sinon, **conflit** : la classe doit redéfinir la méthode.

---

## 7. Interfaces fonctionnelles et lambdas

Une **interface fonctionnelle** a **exactement une** méthode abstraite (les `default` et `static`
ne comptent pas). On parle aussi de SAM (*Single Abstract Method*).

```java
@FunctionalInterface
public interface Validateur<T> {
    boolean estValide(T valeur);

    default Validateur<T> et(Validateur<T> autre) {
        return v -> this.estValide(v) && autre.estValide(v);
    }
}
```

Une **lambda est une instance d'interface fonctionnelle**. C'est le point clé :

```java
Validateur<String> nonVide = s -> !s.isBlank();
Validateur<String> email   = s -> s.contains("@");
Validateur<String> valide  = nonVide.et(email);

valide.estValide("a@b.fr");   // true
```

> 🔁 En Dart, une fonction a un type de fonction (`bool Function(String)`).
> Java n'en a pas : une lambda doit toujours « atterrir » dans une interface fonctionnelle.
> Le type de `s -> !s.isBlank()` dépend de la variable qui le reçoit.

`@FunctionalInterface` est facultatif, mais le compilateur vérifie alors qu'il n'y a bien qu'une
seule méthode abstraite.

Les interfaces fonctionnelles du JDK (`java.util.function`), à connaître :

| Interface | Méthode | Équivalent Dart |
|---|---|---|
| `Function<T, R>` | `R apply(T t)` | `R Function(T)` |
| `Predicate<T>` | `boolean test(T t)` | `bool Function(T)` |
| `Consumer<T>` | `void accept(T t)` | `void Function(T)` |
| `Supplier<T>` | `T get()` | `T Function()` |
| `BiFunction<T, U, R>` | `R apply(T t, U u)` | `R Function(T, U)` |
| `UnaryOperator<T>` | `T apply(T t)` | `T Function(T)` |
| `Runnable` | `void run()` | `void Function()` |

Le Module 8 approfondit les lambdas et les streams.

---

## 8. Les interfaces incontournables du JDK

### `Comparable<T>` : l'ordre naturel

```java
public record Eleve(String nom, double note) implements Comparable<Eleve> {
    @Override
    public int compareTo(Eleve autre) {
        return this.nom.compareTo(autre.nom);   // négatif, 0 ou positif
    }
}

Collections.sort(eleves);   // utilise compareTo
```

> 🔁 Identique à `Comparable<T>` en Dart.

### `Comparator<T>` : d'autres ordres, à la demande

```java
Comparator<Eleve> parNoteDecroissante =
        Comparator.comparingDouble(Eleve::note).reversed();

Comparator<Eleve> parNoteDecroissanteEtNom =
        parNoteDecroissante.thenComparing(Eleve::nom);

eleves.sort(parNoteDecroissanteEtNom);
```

`Comparator` est une interface fonctionnelle remplie de méthodes `default`
(`reversed`, `thenComparing`) et `static` (`comparing`) : un bon modèle de conception.

### `Iterable<T>` et `Iterator<T>` : le `for-each`

N'importe quelle classe qui implémente `Iterable<T>` peut être parcourue avec `for (T x : objet)`.

```java
public interface Iterable<T> { Iterator<T> iterator(); }
public interface Iterator<T> { boolean hasNext(); T next(); }
```

### `AutoCloseable` : `try-with-resources`

```java
try (var connexion = new Connexion()) {   // Connexion implements AutoCloseable
    connexion.envoyer("...");
}                                          // close() est appelé automatiquement
```

Le Module 5 y revient.

### `Runnable` et `Callable<V>` : du travail à exécuter

On les retrouvera au Module 11 (threads).

---

## 9. Implémenter sans créer de fichier : classe anonyme ou lambda

```java
// Classe anonyme : possible pour toute interface
Volant planeur = new Volant() {
    private int alt = 0;
    @Override public void decoller() { alt = 300; }
    @Override public void atterrir() { alt = 0; }
    @Override public int altitude()  { return alt; }
};

// Lambda : seulement si l'interface est fonctionnelle
Runnable tache = () -> System.out.println("Go");
```

Utile pour les tests, les callbacks et les petits cas particuliers.

---

## 10. Interfaces génériques (aperçu du Module 6)

Une interface peut avoir des paramètres de type, ce qui permet d'écrire **un seul** contrat
pour plusieurs entités :

```java
public interface Repository<T, ID> {
    Optional<T> trouver(ID id);
    List<T> tous();
    void enregistrer(T entite);
    void supprimer(ID id);
}

public class UtilisateurRepositoryFirestore implements Repository<Utilisateur, String> { ... }
public class UtilisateurRepositoryMemoire   implements Repository<Utilisateur, String> { ... }
```

> 🪞 Dans HeadsUp, `HasFeature` et `HasDataShareFeature` sont deux contrats identiques à un enum près,
> avec deux extensions `pickFor` copiées-collées. Une interface générique `HasFeature<F>` et une seule
> méthode suffiraient (exercice 8).

---

## 11. Interfaces scellées (Java 17)

```java
public sealed interface Resultat<T> permits Succes, Echec { }
public record Succes<T>(T valeur) implements Resultat<T> { }
public record Echec<T>(String message) implements Resultat<T> { }

String afficher(Resultat<String> r) {
    return switch (r) {                        // exhaustif : pas besoin de default
        case Succes<String> s -> "OK : " + s.valeur();
        case Echec<String> e  -> "Erreur : " + e.message();
    };
}
```

> 🔁 C'est exactement ta `sealed class AppEvent` de HeadsUp, avec le `switch` exhaustif de Dart 3.

Les implémentations autorisées doivent être `final`, `sealed` ou `non-sealed`.
Les `record` sont implicitement `final`.

---

## 12. Interfaces marqueurs

Une interface **vide**, qui sert d'étiquette : `Serializable`, `Cloneable`, `RandomAccess`.
C'est historique. Aujourd'hui on utilise plutôt une **annotation** (Module 13). Il faut savoir
les reconnaître, pas en écrire.

---

## 13. Pourquoi tout ça compte : interfaces et testabilité

Version couplée, sur le modèle de HeadsUp :

```java
public class ProfilService {
    public String sauvegarder(String telephone) {
        String chiffre = EncryptionRepository.encryptData(telephone, "cle_v2"); // static + RemoteConfig
        FirestoreDb.INSTANCE.ecrire("profils", chiffre);                        // singleton global
        return chiffre;
    }
}
```

Pour tester `ProfilService`, il faut un vrai Remote Config et un vrai Firestore. C'est infaisable
dans un test unitaire, ce qui explique en partie qu'il y ait si peu de tests dans HeadsUp.

Version découplée :

```java
public interface Chiffreur { String chiffrer(String clair); }
public interface Base      { void ecrire(String collection, String donnee); }

public class ProfilService {
    private final Chiffreur chiffreur;
    private final Base base;

    public ProfilService(Chiffreur chiffreur, Base base) {   // injection par constructeur
        this.chiffreur = chiffreur;
        this.base = base;
    }

    public String sauvegarder(String telephone) {
        String chiffre = chiffreur.chiffrer(telephone);
        base.ecrire("profils", chiffre);
        return chiffre;
    }
}

// Dans un test :
var service = new ProfilService(clair -> "X" + clair, (c, d) -> {});
```

C'est le **D** de SOLID (*Dependency Inversion*) : dépendre d'abstractions, pas d'implémentations.
L'exercice 9 le fait sur le vrai code de HeadsUp, et les Modules 9 et 12 vont plus loin.

---

## 14. Pièges fréquents

| Piège | Symptôme |
|---|---|
| Oublier `public` sur la méthode implémentée | Erreur « weaker access privileges » |
| Écrire `class A implements UneClasse` | Erreur « interface expected here » |
| Mettre un champ modifiable dans une interface | Impossible : c'est une constante `static final` |
| Appeler une méthode `static` d'interface sur la classe | Ne compile pas : `Interface.methode()` uniquement |
| Deux `default` identiques sans redéfinir | Erreur « inherits unrelated defaults » |
| Une lambda vers une interface à 2 méthodes abstraites | Erreur « not a functional interface » |
| Mettre de la logique métier lourde dans les `default` | Compile, mais transforme le contrat en classe déguisée |
| Créer une interface pour **chaque** classe « au cas où » | Sur-ingénierie : en créer une quand il y a ≥ 2 implémentations (tests compris) ou une frontière (réseau, base, disque) |

---

## 15. Correspondances Dart → Java

| Dart | Java |
|---|---|
| `abstract class X { void m(); }` + `implements X` | `interface X { void m(); }` |
| `abstract interface class X` (Dart 3) | `interface X` |
| `abstract class` avec du code + `extends` | `abstract class` |
| `class A implements B` (B classe concrète) | ❌ impossible : extraire une interface |
| `mixin M on A` | `default` méthodes (sans état) |
| `sealed class` + sous-classes | `sealed interface` + `record` |
| `bool Function(String)` | `Predicate<String>` (ou ton interface fonctionnelle) |
| `extension on X` | méthode `static` utilitaire, ou `default` si tu possèdes `X` |
| `Comparable<T>` | `Comparable<T>` (identique) |
| `Iterable<T>` | `Iterable<T>` + `Iterator<T>` |

---

## 16. Auto-évaluation

Réponds sans regarder la leçon. Si tu bloques sur plus de deux questions, relis la section indiquée.

1. Quelle différence fondamentale y a-t-il entre Dart et Java sur les interfaces ? (§0)
2. Quels modificateurs le compilateur ajoute-t-il à une méthode et à un champ d'interface ? (§1)
3. Pourquoi écrire `List<String> l = new ArrayList<>()` plutôt que `ArrayList<String> l = ...` ? (§3)
4. Donne deux raisons de choisir une classe abstraite plutôt qu'une interface. (§4)
5. Pour quel problème les méthodes `default` ont-elles été créées ? (§5)
6. Énonce les trois règles de résolution du diamant. (§6)
7. Qu'est-ce qu'une interface fonctionnelle ? Quel est le lien avec les lambdas ? (§7)
8. `Comparable` ou `Comparator` : lequel pour trier des élèves par note, puis par nom ? (§8)
9. Que faut-il pour qu'une classe soit utilisable dans un `for-each` ? (§8)
10. En quoi une interface rend-elle `ProfilService` testable ? (§13)

---

➡️ Suite : les [exercices](exercices/), puis le [TP 3](tp/), puis le [contrôle](controle/).
