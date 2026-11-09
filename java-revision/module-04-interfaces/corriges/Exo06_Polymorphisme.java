import java.util.List;

/**
 * Exercice 6 : remplacer des if/switch sur un "type" par une interface (corrigé).
 * Lancer : java Exo06_Polymorphisme.java
 *
 * Avant (code de départ de l'énoncé) :
 *   class Apprenant { String type; ... }
 *   double tarif(Apprenant a) { if (a.type.equals("ENTREPRISE")) ... else if ... }
 *   String libelle(Apprenant a) { switch (a.type) { ... } }
 *   => chaque nouveau type oblige à modifier TOUTES les méthodes qui testent le type.
 *
 * Après : chaque catégorie porte son propre comportement.
 * Ajouter une catégorie = ajouter une classe. Facturation ne change jamais (principe ouvert/fermé).
 */
public class Exo06_Polymorphisme {
    public static void main(String[] args) {
        Facturation f = new Facturation(40.0);   // tarif horaire de base

        Check.egal(40.0 * 10, f.montant(new Apprenant("Société X", new Entreprise()), 10), "Entreprise : plein tarif");
        Check.egal(30.0 * 10, f.montant(new Apprenant("Léa", new Particulier()), 10), "Particulier : -25 %");
        Check.egal(20.0 * 10, f.montant(new Apprenant("Tom", new Etudiant()), 10), "Étudiant : -50 %");
        Check.egal(0.0, f.montant(new Apprenant("Sam", new DemandeurEmploi()), 10), "Demandeur d'emploi : gratuit (ajouté sans toucher Facturation)");

        List<Apprenant> groupe = List.of(
                new Apprenant("Léa", new Particulier()),
                new Apprenant("Tom", new Etudiant()));
        Check.egal(500.0, f.total(groupe, 10), "Total du groupe : 300 + 200");
        Check.egal("Étudiant", new Etudiant().libelle(), "Libellé de la catégorie");
        Check.fin();
    }
}

interface CategorieApprenant {
    String libelle();

    /** Coefficient appliqué au tarif de base (1.0 = plein tarif). */
    double coefficient();
}

class Entreprise implements CategorieApprenant {
    public String libelle() { return "Entreprise"; }
    public double coefficient() { return 1.0; }
}

class Particulier implements CategorieApprenant {
    public String libelle() { return "Particulier"; }
    public double coefficient() { return 0.75; }
}

class Etudiant implements CategorieApprenant {
    public String libelle() { return "Étudiant"; }
    public double coefficient() { return 0.5; }
}

// Le nouveau cas demandé : une classe de plus, rien d'autre à modifier
class DemandeurEmploi implements CategorieApprenant {
    public String libelle() { return "Demandeur d'emploi"; }
    public double coefficient() { return 0.0; }
}

record Apprenant(String nom, CategorieApprenant categorie) { }

class Facturation {
    private final double tarifHoraireBase;

    Facturation(double tarifHoraireBase) { this.tarifHoraireBase = tarifHoraireBase; }

    double montant(Apprenant a, int heures) {
        return tarifHoraireBase * a.categorie().coefficient() * heures;
    }

    double total(List<Apprenant> groupe, int heures) {
        double total = 0;
        for (Apprenant a : groupe) total += montant(a, heures);
        return total;
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
