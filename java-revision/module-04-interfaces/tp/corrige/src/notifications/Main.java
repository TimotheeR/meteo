package notifications;

import java.util.List;
import java.util.Map;
import notifications.canal.EmailNotificateur;
import notifications.canal.Passerelle;
import notifications.canal.PushNotificateur;
import notifications.canal.SlackNotificateur;
import notifications.canal.SmsNotificateur;
import notifications.preferences.PreferencesEnMemoire;
import notifications.service.NotificationService;

/**
 * Point d'assemblage : le SEUL endroit qui connaît les classes concrètes.
 * (🔁 c'est le rôle de App / MultiBlocProvider dans HeadsUp.)
 *
 * Depuis le dossier "corrige" :
 *   javac -d out $(find src -name '*.java') && java -cp out notifications.Main
 *   java -cp out notifications.TestsNotifications
 */
public class Main {
    public static void main(String[] args) {
        Passerelle console = (destination, contenu) ->
                System.out.println("  -> " + destination + " | " + contenu.replace("\n", " "));

        var service = new NotificationService(
                List.of(new EmailNotificateur(console),
                        new SmsNotificateur(console),
                        new PushNotificateur(console, Map.of("u3", "fcm-jeton-u3")),
                        new SlackNotificateur(console, "#formateurs")),
                new PreferencesEnMemoire(Map.of(
                        "u1", List.of("EMAIL"),
                        "u2", List.of("SMS", "EMAIL"),
                        "u3", List.of("PUSH"),
                        "u4", List.of("SLACK"))),
                "EMAIL");

        List<Utilisateur> utilisateurs = List.of(
                new Utilisateur("u1", "Léa", "lea@exemple.fr", "+33600000001"),
                new Utilisateur("u2", "Tom", "tom@exemple.fr", null),          // pas de téléphone : repli e-mail
                new Utilisateur("u3", "Sam", "sam@exemple.fr", null),
                new Utilisateur("u4", "Noé", null, null));

        Message rappel = new Message("Rappel", "Votre cours d'anglais commence dans 1 h");
        for (Utilisateur u : utilisateurs) {
            System.out.println(u.nom() + " : " + service.notifier(u, rappel).resume());
        }
    }
}
