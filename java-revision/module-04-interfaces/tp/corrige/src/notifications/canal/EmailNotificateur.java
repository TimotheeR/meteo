package notifications.canal;

import notifications.Message;
import notifications.ResultatEnvoi;
import notifications.Utilisateur;

public class EmailNotificateur implements Notificateur {
    private final Passerelle passerelle;

    public EmailNotificateur(Passerelle passerelle) { this.passerelle = passerelle; }

    @Override public String canal() { return "EMAIL"; }

    @Override
    public boolean peutJoindre(Utilisateur u) {
        return u.email() != null && u.email().contains("@");
    }

    @Override
    public ResultatEnvoi envoyer(Utilisateur u, Message m) {
        passerelle.transmettre(u.email(), "Objet : " + m.titre() + "\n\nBonjour " + u.nom() + ",\n" + m.corps());
        return new ResultatEnvoi.Envoye(canal(), u.email());
    }
}
