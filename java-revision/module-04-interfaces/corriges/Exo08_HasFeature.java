import java.util.List;

/**
 * Exercice 8 🪞 : HasFeature de HeadsUp, en Java (corrigé).
 * Lancer : java Exo08_HasFeature.java
 *
 * Dans HeadsUp (lib/packages/_enums/feature.dart et lib/src/data_share/_enum/feature.dart) :
 *   - deux abstract class quasi identiques, HasFeature et HasDataShareFeature,
 *     qui ne diffèrent que par le type de l'enum ;
 *   - deux extensions pickFor copiées-collées.
 *
 * Réponse à la question 1 (interface ou classe abstraite ?) :
 *   une INTERFACE. Ces classes n'ont ni état, ni constructeur, ni code commun :
 *   ce sont de purs contrats "sait fournir une feature". En Java, une classe abstraite
 *   empêcherait en plus les implémentations d'hériter d'autre chose (et un record ne peut
 *   pas étendre de classe du tout).
 *
 * Réponse à la question 2 : un paramètre de type F remplace les deux versions.
 * Réponse à la question 3 : l'extension devient une méthode static de l'interface.
 */
public class Exo08_HasFeature {
    public static void main(String[] args) {
        var defaut = new Adresse(Feature.UNKNOWN, "", false, "—");
        List<Adresse> adresses = List.of(
                new Adresse(Feature.USERS, "u1", false, "Lyon"),
                new Adresse(Feature.USERS, "u1", true, "Paris"),
                new Adresse(Feature.USERS, "u2", false, "Lille"),
                new Adresse(Feature.SETTINGS, "u1", true, "Nantes"));

        Check.egal("Paris", HasFeature.pickFor(adresses, Feature.USERS, "u1", defaut).ville(),
                   "Plusieurs candidats : la principale (isPrimary) est choisie");
        Check.egal("Lille", HasFeature.pickFor(adresses, Feature.USERS, "u2", defaut).ville(),
                   "Un seul candidat : il est renvoyé");
        Check.egal("—", HasFeature.pickFor(adresses, Feature.USERS, "u9", defaut).ville(),
                   "Aucun candidat : la valeur par défaut");

        var defautPartage = new Partage(DataShareFeature.UNKNOWN, "", false, "");
        List<Partage> partages = List.of(
                new Partage(DataShareFeature.VOCABULARY, "l1", false, "Lien A"),
                new Partage(DataShareFeature.VOCABULARY, "l1", false, "Lien B"));
        Check.egal("Lien A", HasFeature.pickFor(partages, DataShareFeature.VOCABULARY, "l1", defautPartage).lien(),
                   "Même méthode pour l'autre enum ; sans principale, le premier");

        // Le compilateur refuse de mélanger les enums :
        // HasFeature.pickFor(partages, Feature.USERS, "l1", defautPartage);  // ❌ ne compile pas
        Check.fin();
    }
}

interface HasFeature<F extends Enum<F>> {
    F feature();
    String featureId();
    boolean isPrimary();

    static <F extends Enum<F>, T extends HasFeature<F>> T pickFor(
            List<T> elements, F feature, String featureId, T defaut) {
        List<T> filtres = elements.stream()
                .filter(e -> e.feature() == feature && e.featureId().equals(featureId))
                .toList();

        if (filtres.isEmpty()) return defaut;
        if (filtres.size() == 1) return filtres.get(0);

        return filtres.stream()
                .filter(HasFeature::isPrimary)
                .findFirst()
                .orElse(filtres.get(0));
    }
}

enum Feature { USERS, SETTINGS, LESSON_SESSIONS, UNKNOWN }
enum DataShareFeature { LISTENING, VOCABULARY, ASSESSMENT, UNKNOWN }

// Un record implémente l'interface : ses accesseurs feature(), featureId(), isPrimary()
// correspondent exactement aux méthodes du contrat.
record Adresse(Feature feature, String featureId, boolean isPrimary, String ville)
        implements HasFeature<Feature> { }

record Partage(DataShareFeature feature, String featureId, boolean isPrimary, String lien)
        implements HasFeature<DataShareFeature> { }

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
