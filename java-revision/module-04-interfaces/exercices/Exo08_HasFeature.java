import java.util.List;

/**
 * Exercice 8 🪞 : HasFeature de HeadsUp, en Java.
 * Lancer : java Exo08_HasFeature.java
 *
 * Dans HeadsUp :
 *   lib/packages/_enums/feature.dart          -> abstract class HasFeature + extension pickFor
 *   lib/src/data_share/_enum/feature.dart     -> abstract class HasDataShareFeature + extension pickFor
 * Les deux sont identiques à l'enum près (Feature / DataShareFeature).
 *
 * Rappel du comportement de pickFor(feature, featureId, defaultValue) :
 *   - garder les éléments dont feature ET featureId correspondent ;
 *   - aucun : defaultValue ; un seul : celui-là ;
 *   - plusieurs : le premier isPrimary, sinon le premier tout court.
 *
 * TODO
 *  1. Interface ou classe abstraite en Java ? Réponds en commentaire, avec tes arguments.
 *  2. Écris UNE interface générique HasFeature<F extends Enum<F>> qui remplace les deux.
 *  3. Écris pickFor comme méthode static de l'interface (Java n'a pas d'extensions).
 *  4. Fais implémenter l'interface aux records Adresse et Partage.
 *  5. Vérifie que le compilateur refuse de chercher une Feature dans une liste de Partage.
 *
 * Question bonus : dans HeadsUp, pickFor n'est appelée nulle part. Qu'en conclus-tu ?
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
        Check.fin();
    }
}

// TODO : interface HasFeature<F extends Enum<F>> { ... static ... pickFor(...) }

enum Feature { USERS, SETTINGS, LESSON_SESSIONS, UNKNOWN }
enum DataShareFeature { LISTENING, VOCABULARY, ASSESSMENT, UNKNOWN }

record Adresse(Feature feature, String featureId, boolean isPrimary, String ville) /* TODO */ { }

record Partage(DataShareFeature feature, String featureId, boolean isPrimary, String lien) /* TODO */ { }

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
