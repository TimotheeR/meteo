import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Exercice 3 : rendre une classe utilisable dans un for-each (corrigé).
 * Lancer : java Exo03_Iterable.java
 */
public class Exo03_Iterable {
    public static void main(String[] args) {
        int somme = 0;
        for (int i : new Plage(1, 5)) {      // possible car Plage implements Iterable<Integer>
            somme += i;
        }
        Check.egal(15, somme, "Somme de 1 à 5 = 15");

        int pairs = 0;
        for (int i : new Plage(0, 10, 2)) {
            pairs++;
        }
        Check.egal(6, pairs, "0, 2, 4, 6, 8, 10 : 6 valeurs");

        int vide = 0;
        for (int i : new Plage(5, 1)) vide++;
        Check.egal(0, vide, "Plage(5, 1) est vide");

        Iterator<Integer> it = new Plage(1, 1).iterator();
        it.next();
        try {
            it.next();
            Check.vrai(false, "next() au-delà de la fin lève NoSuchElementException");
        } catch (NoSuchElementException e) {
            Check.vrai(true, "next() au-delà de la fin lève NoSuchElementException");
        }

        // Deux parcours indépendants : chaque appel à iterator() repart du début
        Plage p = new Plage(1, 3);
        int n = 0;
        for (int a : p) for (int b : p) n++;
        Check.egal(9, n, "Deux boucles imbriquées sur la même plage : 9 tours");
        Check.fin();
    }
}

class Plage implements Iterable<Integer> {
    private final int debut, fin, pas;

    Plage(int debut, int fin) { this(debut, fin, 1); }

    Plage(int debut, int fin, int pas) {
        if (pas <= 0) throw new IllegalArgumentException("pas > 0 requis");
        this.debut = debut;
        this.fin = fin;
        this.pas = pas;
    }

    @Override
    public Iterator<Integer> iterator() {
        // Classe anonyme : un nouvel itérateur, avec son propre état, à chaque appel
        return new Iterator<>() {
            private int courant = debut;

            @Override
            public boolean hasNext() { return courant <= fin; }

            @Override
            public Integer next() {
                if (!hasNext()) throw new NoSuchElementException();
                int valeur = courant;
                courant += pas;
                return valeur;
            }
        };
    }
}

/** Mini outil de vérification (on passera à JUnit au Module 12). */
class Check {
    private static int ok = 0, ko = 0;

    static void vrai(boolean condition, String message) {
        if (condition) { ok++; System.out.println("  ✅ " + message); }
        else           { ko++; System.out.println("  ❌ " + message); }
    }

    static void egal(Object attendu, Object obtenu, String message) {
        vrai(java.util.Objects.equals(attendu, obtenu),
             message + (java.util.Objects.equals(attendu, obtenu) ? "" : " (attendu " + attendu + ", obtenu " + obtenu + ")"));
    }

    static void fin() {
        System.out.println(ko == 0 ? "\n🎉 " + ok + "/" + ok + " vérifications OK"
                                   : "\n" + ok + "/" + (ok + ko) + " vérifications OK");
    }
}
