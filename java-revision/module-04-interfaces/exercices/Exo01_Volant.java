import java.util.List;

/**
 * Exercice 1 : première interface.
 * Lancer : java Exo01_Volant.java
 *
 * ⚠️ Ce fichier ne compile pas tant que tu n'as pas écrit le code demandé :
 * c'est voulu, les erreurs du compilateur te disent ce qui manque.
 *
 * TODO
 *  1. Écris l'interface Volant : decoller(), atterrir(), altitude() (int), nom() (String),
 *     et une constante ALTITUDE_MAX = 12 000.
 *  2. Écris Oiseau (vole à 50 m) et Avion (vole à 10 000 m), qui implémentent Volant.
 *  3. Complète faireUnTour : elle ne doit dépendre QUE de l'interface.
 */
public class Exo01_Volant {

    static void faireUnTour(Volant v) {
        // TODO : décoller, afficher "<nom> vole à <altitude> m", atterrir
    }

    public static void main(String[] args) {
        List<Volant> flotte = List.of(new Oiseau(), new Avion());
        for (Volant v : flotte) {
            faireUnTour(v);
        }

        Oiseau o = new Oiseau();
        o.decoller();
        Check.egal(50, o.altitude(), "Oiseau en vol : 50 m");
        o.atterrir();
        Check.egal(0, o.altitude(), "Oiseau au sol : 0 m");

        Avion a = new Avion();
        a.decoller();
        Check.egal(10_000, a.altitude(), "Avion en vol : 10 000 m");
        Check.vrai(a.altitude() <= Volant.ALTITUDE_MAX, "Avion sous l'altitude max");

        Volant v = new Oiseau();
        Check.vrai(v instanceof Oiseau, "Type réel : Oiseau");
        Check.fin();
    }
}

// TODO : interface Volant

// TODO : class Oiseau

// TODO : class Avion

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
