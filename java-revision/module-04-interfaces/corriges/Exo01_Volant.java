import java.util.List;

/**
 * Exercice 1 : première interface (corrigé).
 * Lancer : java Exo01_Volant.java
 */
public class Exo01_Volant {

    static void faireUnTour(Volant v) {
        v.decoller();
        System.out.println(v.nom() + " vole à " + v.altitude() + " m");
        v.atterrir();
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

        Volant v = new Oiseau();          // type déclaré : l'interface
        Check.vrai(v instanceof Oiseau, "Type réel : Oiseau");
        Check.fin();
    }
}

interface Volant {
    int ALTITUDE_MAX = 12_000;

    void decoller();
    void atterrir();
    int altitude();
    String nom();
}

class Oiseau implements Volant {
    private int altitude = 0;

    @Override public void decoller() { altitude = 50; }
    @Override public void atterrir() { altitude = 0; }
    @Override public int altitude() { return altitude; }
    @Override public String nom() { return "Oiseau"; }
}

class Avion implements Volant {
    private int altitude = 0;

    @Override public void decoller() { altitude = 10_000; }
    @Override public void atterrir() { altitude = 0; }
    @Override public int altitude() { return altitude; }
    @Override public String nom() { return "Avion"; }
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
