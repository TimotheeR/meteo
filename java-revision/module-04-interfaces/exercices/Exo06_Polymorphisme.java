/**
 * Exercice 6 : remplacer des if/switch sur un "type" par une interface.
 * Lancer : java Exo06_Polymorphisme.java
 *
 * Le code ci-dessous fonctionne, mais :
 *   - chaque méthode teste le type avec des if / switch ;
 *   - ajouter une catégorie oblige à modifier toutes ces méthodes (et à n'en oublier aucune).
 *
 * TODO
 *  1. Crée une interface CategorieApprenant (libelle(), coefficient()).
 *  2. Crée une classe par catégorie : Entreprise (1.0), Particulier (0.75), Etudiant (0.5).
 *  3. Apprenant devient record Apprenant(String nom, CategorieApprenant categorie).
 *  4. Facturation ne doit plus contenir AUCUN if ni switch sur la catégorie.
 *  5. Ajoute DemandeurEmploi (gratuit) sans modifier Facturation.
 *  6. Remplace le main par les vérifications du bas du fichier (décommente-les).
 *
 * Question : quel principe SOLID cette refactorisation applique-t-elle ?
 */
public class Exo06_Polymorphisme {
    public static void main(String[] args) {
        Facturation f = new Facturation(40.0);
        Check.egal(400.0, f.montant(new Apprenant("Société X", "ENTREPRISE"), 10), "Entreprise : plein tarif");
        Check.egal(300.0, f.montant(new Apprenant("Léa", "PARTICULIER"), 10), "Particulier : -25 %");
        Check.egal(200.0, f.montant(new Apprenant("Tom", "ETUDIANT"), 10), "Étudiant : -50 %");
        Check.egal("Étudiant", f.libelle(new Apprenant("Tom", "ETUDIANT")), "Libellé");
        Check.fin();

        /* Vérifications à utiliser après la refactorisation :
        Facturation f = new Facturation(40.0);
        Check.egal(400.0, f.montant(new Apprenant("Société X", new Entreprise()), 10), "Entreprise : plein tarif");
        Check.egal(300.0, f.montant(new Apprenant("Léa", new Particulier()), 10), "Particulier : -25 %");
        Check.egal(200.0, f.montant(new Apprenant("Tom", new Etudiant()), 10), "Étudiant : -50 %");
        Check.egal(0.0, f.montant(new Apprenant("Sam", new DemandeurEmploi()), 10), "Demandeur d'emploi : gratuit");
        java.util.List<Apprenant> groupe = java.util.List.of(
                new Apprenant("Léa", new Particulier()), new Apprenant("Tom", new Etudiant()));
        Check.egal(500.0, f.total(groupe, 10), "Total du groupe : 300 + 200");
        Check.egal("Étudiant", new Etudiant().libelle(), "Libellé de la catégorie");
        Check.fin();
        */
    }
}

// ----- Code de départ à refactoriser -----
class Apprenant {
    final String nom;
    final String type;   // "ENTREPRISE", "PARTICULIER" ou "ETUDIANT"

    Apprenant(String nom, String type) { this.nom = nom; this.type = type; }
}

class Facturation {
    private final double tarifHoraireBase;

    Facturation(double tarifHoraireBase) { this.tarifHoraireBase = tarifHoraireBase; }

    double montant(Apprenant a, int heures) {
        double coefficient;
        if (a.type.equals("ENTREPRISE")) coefficient = 1.0;
        else if (a.type.equals("PARTICULIER")) coefficient = 0.75;
        else if (a.type.equals("ETUDIANT")) coefficient = 0.5;
        else throw new IllegalArgumentException("Type inconnu : " + a.type);
        return tarifHoraireBase * coefficient * heures;
    }

    String libelle(Apprenant a) {
        switch (a.type) {
            case "ENTREPRISE":  return "Entreprise";
            case "PARTICULIER": return "Particulier";
            case "ETUDIANT":    return "Étudiant";
            default: throw new IllegalArgumentException("Type inconnu : " + a.type);
        }
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
