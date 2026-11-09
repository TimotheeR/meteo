package notifications.canal;

import notifications.Message;
import notifications.ResultatEnvoi;
import notifications.Utilisateur;

public class SmsNotificateur implements Notificateur {
    static final int LONGUEUR_MAX = 160;
    private final Passerelle passerelle;

    public SmsNotificateur(Passerelle passerelle) { this.passerelle = passerelle; }

    @Override public String canal() { return "SMS"; }

    @Override
    public boolean peutJoindre(Utilisateur u) {
        return u.telephone() != null && !u.telephone().isBlank();
    }

    @Override
    public ResultatEnvoi envoyer(Utilisateur u, Message m) {
        String texte = m.texte();
        if (texte.length() > LONGUEUR_MAX) texte = texte.substring(0, LONGUEUR_MAX - 3) + "...";
        passerelle.transmettre(u.telephone(), texte);
        return new ResultatEnvoi.Envoye(canal(), u.telephone());
    }
}
