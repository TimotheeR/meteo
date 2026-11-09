package notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import notifications.canal.Notificateur;
import notifications.canal.SmsNotificateur;
import notifications.preferences.PreferencesEnMemoire;
import notifications.service.NotificationService;

/** Partie 4 : tester le service sans rien envoyer, grâce à de faux notificateurs. */
public class TestsNotifications {

    /** Faux canal qui enregistre ce qu'on lui demande d'envoyer. */
    static class FauxNotificateur implements Notificateur {
        final String canal;
        final boolean joignable;
        final List<String> envois = new ArrayList<>();

        FauxNotificateur(String canal, boolean joignable) {
            this.canal = canal;
            this.joignable = joignable;
        }

        @Override public String canal() { return canal; }
        @Override public boolean peutJoindre(Utilisateur u) { return joignable; }

        @Override
        public ResultatEnvoi envoyer(Utilisateur u, Message m) {
            envois.add(u.id() + ":" + m.titre());
            return new ResultatEnvoi.Envoye(canal, u.id());
        }
    }

    static int ok = 0, ko = 0;

    static void verifier(boolean condition, String message) {
        if (condition) { ok++; System.out.println("  ✅ " + message); }
        else           { ko++; System.out.println("  ❌ " + message); }
    }

    public static void main(String[] args) {
        Utilisateur lea = new Utilisateur("u1", "Léa", "lea@exemple.fr", null);
        Message m = new Message("Rappel", "Cours à 14 h");

        // 1. Le canal préféré est utilisé
        var email = new FauxNotificateur("EMAIL", true);
        var sms = new FauxNotificateur("SMS", true);
        var service = new NotificationService(List.of(email, sms),
                new PreferencesEnMemoire(Map.of("u1", List.of("SMS"))), "EMAIL");
        service.notifier(lea, m);
        verifier(sms.envois.equals(List.of("u1:Rappel")) && email.envois.isEmpty(), "Canal préféré utilisé");

        // 2. Repli sur le canal de secours si le préféré ne peut pas joindre
        var smsInjoignable = new FauxNotificateur("SMS", false);
        var email2 = new FauxNotificateur("EMAIL", true);
        var service2 = new NotificationService(List.of(email2, smsInjoignable),
                new PreferencesEnMemoire(Map.of("u1", List.of("SMS"))), "EMAIL");
        ResultatEnvoi r2 = service2.notifier(lea, m);
        verifier(r2.estEnvoye() && email2.envois.size() == 1, "Repli sur le canal de secours");

        // 3. Échec propre si personne ne peut joindre
        var service3 = new NotificationService(List.of(new FauxNotificateur("EMAIL", false)),
                new PreferencesEnMemoire(Map.of()), "EMAIL");
        verifier(service3.notifier(lea, m) instanceof ResultatEnvoi.Echec, "Échec si aucun canal");

        // 4. Canal inconnu dans les préférences : ignoré
        var email4 = new FauxNotificateur("EMAIL", true);
        var service4 = new NotificationService(List.of(email4),
                new PreferencesEnMemoire(Map.of("u1", List.of("PIGEON"))), "EMAIL");
        verifier(service4.notifier(lea, m).estEnvoye(), "Canal inconnu ignoré");

        // 5. Un vrai SmsNotificateur avec une fausse passerelle (lambda) : troncature à 160
        List<String> transmis = new ArrayList<>();
        var vraiSms = new SmsNotificateur((dest, contenu) -> transmis.add(contenu));
        vraiSms.envoyer(new Utilisateur("u2", "Tom", null, "+336"), new Message("Long", "x".repeat(300)));
        verifier(transmis.get(0).length() == 160 && transmis.get(0).endsWith("..."), "SMS tronqué à 160 caractères");

        // 6. Envoi groupé filtré
        var email6 = new FauxNotificateur("EMAIL", true);
        var service6 = new NotificationService(List.of(email6), new PreferencesEnMemoire(Map.of()), "EMAIL");
        var tom = new Utilisateur("u2", "Tom", "tom@exemple.fr", null);
        service6.notifierTous(List.of(lea, tom), m, u -> u.nom().startsWith("T"));
        verifier(email6.envois.equals(List.of("u2:Rappel")), "notifierTous respecte le filtre");

        System.out.println(ko == 0 ? "\n🎉 " + ok + "/" + ok + " tests OK" : "\n" + ok + "/" + (ok + ko) + " tests OK");
        if (ko > 0) System.exit(1);
    }
}
