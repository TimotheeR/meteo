import java.util.List;
import java.util.function.Predicate;

/**
 * Exercice 7 : écrire sa propre interface fonctionnelle.
 * Thème : la validation de formulaire (🔁 formz dans HeadsUp).
 * Lancer : java Exo07_Fonctionnelle.java
 *
 * TODO
 *  1. Complète l'interface fonctionnelle Validateur<T> : une méthode abstraite estValide(T),
 *     et trois méthodes default et(...), ou(...), non().
 *  2. Écris NON_VIDE et EMAIL avec des lambdas.
 *  3. Écris longueurMin(int) : une méthode qui RENVOIE une lambda.
 *  4. Remplace le null de estMajuscule par une référence de méthode vers toutMajuscules.
 *  5. Remplace le null de predicat par une référence de méthode (champEmail::...).
 *
 * Question : pourquoi peut-on écrire "Validateur<String> v = s -> ...;" mais pas
 * "var v = s -> ...;" ?
 */
public class Exo07_Fonctionnelle {

    static final Validateur<String> NON_VIDE = null;   // TODO
    static final Validateur<String> EMAIL    = null;   // TODO (indice : String.matches)

    static Validateur<String> longueurMin(int min) {
        return null;   // TODO
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

        Validateur<String> estMajuscule = null;   // TODO : référence de méthode
        Check.vrai(estMajuscule.estValide("HEADSUP"), "Référence de méthode");

        Predicate<String> predicat = null;        // TODO : champEmail::...
        Check.egal(1L, List.of("a@b.fr", "pas un email").stream().filter(predicat).count(),
                   "Validateur utilisé comme Predicate dans un stream");
        Check.fin();
    }

    static boolean toutEnMajuscules(String s) {
        return s.equals(s.toUpperCase());
    }
}

// TODO : @FunctionalInterface
interface Validateur<T> {
    // TODO
    default Validateur<T> et(Validateur<T> autre) { return null; }   // TODO
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
