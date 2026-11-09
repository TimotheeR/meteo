package notifications;

import java.util.List;

/**
 * TP 3 : code de départ.
 * Compiler et lancer depuis le dossier "depart" :
 *   javac -d out $(find src -name '*.java') && java -cp out notifications.Main
 */
public class Main {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();

        List<Utilisateur> utilisateurs = List.of(
                new Utilisateur("u1", "Léa", "lea@exemple.fr", "+33600000001", "EMAIL"),
                new Utilisateur("u2", "Tom", "tom@exemple.fr", "+33600000002", "SMS"),
                new Utilisateur("u3", "Sam", "sam@exemple.fr", null, "PUSH"));

        for (Utilisateur u : utilisateurs) {
            service.notifier(u, "Votre cours d'anglais commence dans 1 h");
        }
    }
}
