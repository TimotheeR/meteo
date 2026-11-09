import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Exercice 3 : rendre une classe utilisable dans un for-each.
 * Lancer : java Exo03_Iterable.java
 *
 * TODO
 *  1. Plage doit implémenter Iterable<Integer>.
 *  2. iterator() renvoie un NOUVEL Iterator à chaque appel (classe anonyme ou classe interne).
 *  3. next() lève NoSuchElementException quand il n'y a plus d'élément.
 *  4. Le constructeur refuse un pas <= 0 (IllegalArgumentException).
 *
 * Question : que se passerait-il pour les boucles imbriquées si Plage était elle-même
 * son propre Iterator (un seul compteur partagé) ?
 */
public class Exo03_Iterable {
    public static void main(String[] args) {
        int somme = 0;
        for (int i : new Plage(1, 5)) {
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

        Plage p = new Plage(1, 3);
        int n = 0;
        for (int a : p) for (int b : p) n++;
        Check.egal(9, n, "Deux boucles imbriquées sur la même plage : 9 tours");
        Check.fin();
    }
}

class Plage /* TODO implements ... */ {
    private final int debut, fin, pas;

    Plage(int debut, int fin) { this(debut, fin, 1); }

    Plage(int debut, int fin, int pas) {
        // TODO : vérifier le pas
        this.debut = debut;
        this.fin = fin;
        this.pas = pas;
    }

    // TODO : iterator()
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
