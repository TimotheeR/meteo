import java.util.List;
import java.util.function.Predicate;

/**
 * Exercice 7 : écrire sa propre interface fonctionnelle (corrigé).
 * Thème : la validation de formulaire (🔁 formz dans HeadsUp).
 * Lancer : java Exo07_Fonctionnelle.java
 */
public class Exo07_Fonctionnelle {

    // 3. Validateurs écrits avec des lambdas
    static final Validateur<String> NON_VIDE = s -> s != null && !s.isBlank();
    static final Validateur<String> EMAIL    = s -> s.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$");

    // 4. Validateur paramétré : une méthode qui fabrique une lambda
    static Validateur<String> longueurMin(int min) {
        return s -> s.length() >= min;
    }

    public static void main(String[] args) {
        Validateur<String> champEmail = NON_VIDE.et(EMAIL);
        Validateur<String> motDePasse = NON_VIDE.et(longueurMin(8));

        Check.vrai(champEmail.estValide("lea@exemple.fr"), "Email valide");
        Check.vrai(!champEmail.estValide("lea@"), "Email invalide");
        Check.vrai(!champEmail.estValide(""), "Email vide refusé");
        Check.vrai(motDePasse.estValide("12345678"), "Mot de passe de 8 caractères accepté");
        Check.vrai(!motDePasse.estValide("1234"), "Mot de passe trop court refusé");
        Check.vrai(NON_VIDE.non().estValide("  "), "non() inverse le résultat");
        Check.vrai(EMAIL.ou(s -> s.equals("admin")).estValide("admin"), "ou() accepte l'un ou l'autre");

        // 5. Une référence de méthode est aussi une instance d'interface fonctionnelle
        Validateur<String> estMajuscule = Exo07_Fonctionnelle::toutEnMajuscules;
        Check.vrai(estMajuscule.estValide("HEADSUP"), "Référence de méthode");

        // 6. Passer d'une interface fonctionnelle du JDK à la nôtre
        Predicate<String> predicat = champEmail::estValide;
        Check.egal(1L, List.of("a@b.fr", "pas un email").stream().filter(predicat).count(),
                   "Validateur utilisé comme Predicate dans un stream");
        Check.fin();
    }

    static boolean toutEnMajuscules(String s) {
        return s.equals(s.toUpperCase());
    }
}

@FunctionalInterface
interface Validateur<T> {
    boolean estValide(T valeur);   // LA méthode abstraite

    default Validateur<T> et(Validateur<T> autre) {
        return v -> this.estValide(v) && autre.estValide(v);
    }

    default Validateur<T> ou(Validateur<T> autre) {
        return v -> this.estValide(v) || autre.estValide(v);
    }

    default Validateur<T> non() {
        return v -> !this.estValide(v);
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
