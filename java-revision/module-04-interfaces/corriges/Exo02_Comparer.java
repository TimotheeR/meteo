import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Exercice 2 : Comparable et Comparator (corrigé).
 * Lancer : java Exo02_Comparer.java
 */
public class Exo02_Comparer {

    // 2. Ordre par note décroissante, puis par nom en cas d'égalité
    static final Comparator<Eleve> PAR_NOTE_DESC_PUIS_NOM =
            Comparator.comparingDouble(Eleve::note).reversed()
                      .thenComparing(Eleve::nom);

    // 3. Ordre par longueur du nom (sans lambda : une classe qui implémente Comparator)
    static class ParLongueurDeNom implements Comparator<Eleve> {
        @Override
        public int compare(Eleve a, Eleve b) {
            return Integer.compare(a.nom().length(), b.nom().length());
        }
    }

    public static void main(String[] args) {
        List<Eleve> eleves = new ArrayList<>(List.of(
                new Eleve("Zoé", 14), new Eleve("Adam", 17),
                new Eleve("Inès", 14), new Eleve("Bob", 9)));

        // 1. Ordre naturel (compareTo) : par nom
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
        parLongueur.sort(new ParLongueurDeNom());
        Check.egal(3, parLongueur.get(0).nom().length(), "Par longueur : un nom de 3 lettres en premier");

        // 4. Bonus : la même chose avec une lambda
        parLongueur.sort((a, b) -> b.nom().length() - a.nom().length());
        Check.egal(4, parLongueur.get(0).nom().length(), "Lambda : un nom de 4 lettres en premier");

        Check.vrai(new Eleve("Adam", 1).compareTo(new Eleve("Bob", 20)) < 0, "compareTo : Adam < Bob");
        Check.fin();
    }
}

record Eleve(String nom, double note) implements Comparable<Eleve> {
    @Override
    public int compareTo(Eleve autre) {
        return this.nom.compareTo(autre.nom);
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
