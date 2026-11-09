import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Exercice 2 : Comparable et Comparator.
 * Lancer : java Exo02_Comparer.java
 *
 * TODO
 *  1. Fais en sorte que Eleve implémente Comparable<Eleve> : ordre naturel = par nom.
 *  2. Définis PAR_NOTE_DESC_PUIS_NOM avec Comparator.comparingDouble, reversed, thenComparing.
 *  3. Écris ParLongueurDeNom comme une vraie classe qui implémente Comparator<Eleve>.
 *  4. Remplace le null de la dernière ligne de tri par une lambda : nom le plus long en premier.
 *
 * Question : pourquoi l'ordre "par note" n'a-t-il pas sa place dans compareTo ?
 */
public class Exo02_Comparer {

    static final Comparator<Eleve> PAR_NOTE_DESC_PUIS_NOM = null;   // TODO

    static class ParLongueurDeNom /* TODO implements ... */ {
        // TODO
    }

    public static void main(String[] args) {
        List<Eleve> eleves = new ArrayList<>(List.of(
                new Eleve("Zoé", 14), new Eleve("Adam", 17),
                new Eleve("Inès", 14), new Eleve("Bob", 9)));

        List<Eleve> parNom = new ArrayList<>(eleves);
        parNom.sort(null);                 // null => ordre naturel
        Check.egal("Adam", parNom.get(0).nom(), "Ordre naturel : Adam en premier");
        Check.egal("Zoé", parNom.get(3).nom(), "Ordre naturel : Zoé en dernier");

        List<Eleve> parNote = new ArrayList<>(eleves);
        parNote.sort(PAR_NOTE_DESC_PUIS_NOM);
        Check.egal(List.of("Adam", "Inès", "Zoé", "Bob"),
                   parNote.stream().map(Eleve::nom).toList(),
                   "Par note décroissante puis nom");

        List<Eleve> parLongueur = new ArrayList<>(eleves);
        parLongueur.sort(null /* TODO : new ParLongueurDeNom() */);
        Check.egal(3, parLongueur.get(0).nom().length(), "Par longueur : un nom de 3 lettres en premier");

        parLongueur.sort(null /* TODO : une lambda (a, b) -> ... */);
        Check.egal(4, parLongueur.get(0).nom().length(), "Lambda : un nom de 4 lettres en premier");

        Check.vrai(new Eleve("Adam", 1).compareTo(new Eleve("Bob", 20)) < 0, "compareTo : Adam < Bob");
        Check.fin();
    }
}

record Eleve(String nom, double note) /* TODO implements ... */ {
    // TODO compareTo
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
