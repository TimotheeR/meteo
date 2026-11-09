# Contrôle ciblé : les interfaces

> Durée : 1 h 30. Sans documentation ni IDE pour les parties A à C (papier ou éditeur de texte simple).
> IDE autorisé pour la partie D. Total : 20 points.
> À faire après les exercices et le TP 3. Corrigé et barème : [`corrige.md`](corrige.md).

---

## Partie A : QCM (5 points, 0,5 par question)

Une seule bonne réponse par question.

**A1.** Dans une interface, `int LIMITE = 5;` est implicitement :
- a) `private int`
- b) `public static final int`
- c) `protected static int`
- d) un champ d'instance

**A2.** Combien de classes une classe Java peut-elle étendre, et combien d'interfaces peut-elle implémenter ?
- a) 1 et 1
- b) plusieurs et 1
- c) 1 et plusieurs
- d) plusieurs et plusieurs

**A3.** Laquelle de ces déclarations **compile** ?
- a) `class A implements ArrayList<String> { }`
- b) `interface I extends Runnable, Comparable<I> { }`
- c) `interface I implements Runnable { }`
- d) `interface I { I() { } }`

**A4.** Une interface fonctionnelle est une interface qui :
- a) ne contient que des méthodes `default`
- b) a exactement une méthode abstraite
- c) est annotée `@FunctionalInterface`, sinon les lambdas sont refusées
- d) n'a aucune méthode

**A5.** Comment appelle-t-on une méthode `static` déclarée dans l'interface `Outils` et implémentée par `MaClasse` ?
- a) `MaClasse.methode()`
- b) `new MaClasse().methode()`
- c) `Outils.methode()`
- d) les trois

**A6.** Une classe hérite de deux méthodes `default` de même signature venant de deux interfaces sans lien :
- a) la première interface citée dans `implements` gagne
- b) la dernière gagne
- c) la classe doit redéfinir la méthode, sinon erreur de compilation
- d) une exception est levée à l'exécution

**A7.** Quelle interface faut-il implémenter pour qu'un objet soit utilisable dans `for (T x : objet)` ?
- a) `Iterator<T>`
- b) `Iterable<T>`
- c) `Collection<T>`
- d) `Enumerable<T>`

**A8.** Pour trier une liste de produits **à la demande** par prix puis par nom, on utilise plutôt :
- a) `Comparable`, en modifiant `compareTo` à chaque fois
- b) un `Comparator` construit avec `comparing(...).thenComparing(...)`
- c) `Iterable`
- d) une méthode `default` dans `List`

**A9.** Lequel de ces éléments une interface **ne peut pas** contenir ?
- a) une méthode `private`
- b) une méthode `static`
- c) un champ d'instance modifiable
- d) une interface imbriquée

**A10.** `List<String> noms = new ArrayList<>();`. Le **type déclaré** de `noms` est :
- a) `ArrayList<String>`
- b) `List<String>`
- c) `Object`
- d) déterminé à l'exécution

---

## Partie B : questions de cours (5 points)

Réponds en quelques phrases, avec tes mots.

**B1.** (1 pt) Un développeur Dart te demande pourquoi, en Java, il ne peut pas écrire
`class FauxRepo implements UserRepository` alors que `UserRepository` est une classe. Explique-lui,
et dis-lui comment faire.

**B2.** (1,5 pt) Donne trois différences entre une interface et une classe abstraite. Puis donne
un exemple où tu choisirais la classe abstraite, en justifiant.

**B3.** (1 pt) Pour quel problème les méthodes `default` ont-elles été introduites en Java 8 ?
Quelle est leur principale limite par rapport à une classe abstraite ?

**B4.** (1,5 pt) Que veut dire « programmer vers une interface » ? En t'appuyant sur un exemple
proche de HeadsUp (un service qui dépend de Firestore), explique ce que ça change pour les tests.

---

## Partie C : compile ou pas ? Qu'affiche-t-il ? (4 points, 0,5 par question)

Pour chaque extrait : s'il ne compile pas, explique pourquoi en une ligne. S'il compile, donne
l'affichage exact.

**C1.**
```java
interface A { default String m() { return "A"; } }
interface B extends A { default String m() { return "B"; } }
class C implements A, B { }
// System.out.println(new C().m());
```

**C2.**
```java
interface X { void f(); }
class Y implements X { void f() { System.out.println("f"); } }
```

**C3.**
```java
interface Compteur {
    int valeur();
    static Compteur zero() { return () -> 0; }
}
class Impl implements Compteur { public int valeur() { return 1; } }
// System.out.println(Impl.zero().valeur());
```

**C4.**
```java
interface Forme {
    double aire();
    default String info() { return "aire=" + aire(); }
}
class Carre implements Forme {
    public double aire() { return 4; }
    public String info() { return "carré"; }
}
// Forme f = new Carre();
// Forme g = () -> 2.0;
// System.out.println(f.info() + " / " + g.info());
```

**C5.**
```java
@FunctionalInterface
interface Op { int appliquer(int a, int b); int neutre(); }
// Op o = (x, y) -> x + y;
```

**C6.**
```java
interface Volant { void voler(); }
class Oiseau implements Volant {
    public void voler() { }
    public void chanter() { System.out.println("cui"); }
}
// Volant v = new Oiseau();
// v.chanter();
```

**C7.**
```java
List<String> l = new ArrayList<>(List.of("kiwi", "Ananas", "fraise", "pomme"));
l.sort(Comparator.comparing(String::length).thenComparing(Comparator.reverseOrder()));
System.out.println(l);
```

**C8.**
```java
interface Config { int MAX = 10; default int max() { return MAX * 2; } }
class Impl implements Config { }
// System.out.println(Config.MAX + " " + new Impl().max());
```

---

## Partie D : code (6 points, IDE autorisé)

### D1. Taux de présence (3 points) 🔁 HeadsUp

On veut calculer des statistiques de présence à une session de cours **sans dépendre de Firestore**.

1. Écris `record Presence(String apprenantId, boolean present)`.
2. Écris une interface `SourceDePresences` avec `List<Presence> presences(String sessionId)`.
3. Écris `StatistiquesPresence`, qui **reçoit** une `SourceDePresences` dans son constructeur et propose :
   - `double taux(String sessionId)` : pourcentage de présents (0.0 si aucune présence) ;
   - `List<String> absents(String sessionId)` : les identifiants des absents.
4. Dans un `main`, vérifie ces deux méthodes avec une fausse source écrite **sous forme de lambda**.
   Avec lea (présente), tom (absent), sam (présente) et noe (présent), on attend `75.0` et `[tom]`.

### D2. Chaîne de transformations (3 points)

1. Écris une interface fonctionnelle `Transformation` avec `String appliquer(String s)`.
2. Ajoute une méthode `default Transformation puis(Transformation suivante)` qui applique `this`, puis `suivante`.
3. Ajoute deux méthodes `static` : `identite()` et `enchainer(Transformation... etapes)`.
4. Montre que `nettoyer.puis(crier).puis(exclamer).appliquer("  bonjour  ")` donne `"BONJOUR !"`,
   avec `nettoyer` et `crier` écrites en **références de méthodes** et `exclamer` en lambda.
