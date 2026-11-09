package notifications.canal;

import notifications.Message;
import notifications.ResultatEnvoi;
import notifications.Utilisateur;

/** Partie 2 : nouveau canal ajouté SANS modifier NotificationService. */
public class SlackNotificateur implements Notificateur {
    private final Passerelle passerelle;
    private final String salon;

    public SlackNotificateur(Passerelle passerelle, String salon) {
        this.passerelle = passerelle;
        this.salon = salon;
    }

    @Override public String canal() { return "SLACK"; }

    // peutJoindre : la version default (toujours vrai) convient

    @Override
    public ResultatEnvoi envoyer(Utilisateur u, Message m) {
        passerelle.transmettre(salon, "@" + u.nom() + " " + m.texte());
        return new ResultatEnvoi.Envoye(canal(), salon);
    }
}
