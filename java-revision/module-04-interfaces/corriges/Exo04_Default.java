/**
 * Exercice 4 : faire évoluer une interface avec des méthodes default et static (corrigé).
 * Lancer : java Exo04_Default.java
 */
public class Exo04_Default {
    public static void main(String[] args) {
        Forme c = new Cercle(1);
        Forme r = new Rectangle(2, 3);
        Forme carre = new Carre(2);

        Check.egal("Rectangle d'aire 6,00", r.description(), "description() par défaut");
        Check.egal("Cercle de rayon 1,0", c.description(), "description() redéfinie par Cercle");
        Check.vrai(r.estPlusGrandeQue(carre), "Rectangle (6) plus grand que Carré (4)");
        Check.vrai(!carre.estPlusGrandeQue(r), "Carré (4) pas plus grand que Rectangle (6)");
        Check.egal(r, Forme.laPlusGrande(c, r, carre), "Forme.laPlusGrande renvoie le rectangle");
        Check.fin();
    }
}

interface Forme {
    double aire();

    String nom();

    // Ajouts "après coup" : aucune classe existante n'a eu besoin d'être modifiée
    default String description() {
        return nom() + " d'aire " + String.format(java.util.Locale.FRANCE, "%.2f", aire());
    }

    default boolean estPlusGrandeQue(Forme autre) {
        return this.aire() > autre.aire();
    }

    static Forme laPlusGrande(Forme... formes) {
        Forme max = formes[0];
        for (Forme f : formes) {
            if (f.estPlusGrandeQue(max)) max = f;
        }
        return max;
    }
}

record Rectangle(double largeur, double hauteur) implements Forme {
    public double aire() { return largeur * hauteur; }
    public String nom() { return "Rectangle"; }
}

record Carre(double cote) implements Forme {
    public double aire() { return cote * cote; }
    public String nom() { return "Carré"; }
}

record Cercle(double rayon) implements Forme {
    public double aire() { return Math.PI * rayon * rayon; }
    public String nom() { return "Cercle"; }

    @Override
    public String description() {
        return "Cercle de rayon " + String.format(java.util.Locale.FRANCE, "%.1f", rayon);
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
