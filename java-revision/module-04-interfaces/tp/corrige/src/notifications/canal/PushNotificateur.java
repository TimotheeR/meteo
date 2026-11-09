package notifications.canal;

import java.util.Map;
import notifications.Message;
import notifications.ResultatEnvoi;
import notifications.Utilisateur;

public class PushNotificateur implements Notificateur {
    private final Passerelle passerelle;
    private final Map<String, String> jetonsParUtilisateur;   // userId -> jeton FCM

    public PushNotificateur(Passerelle passerelle, Map<String, String> jetonsParUtilisateur) {
        this.passerelle = passerelle;
        this.jetonsParUtilisateur = jetonsParUtilisateur;
    }

    @Override public String canal() { return "PUSH"; }

    @Override
    public boolean peutJoindre(Utilisateur u) {
        return jetonsParUtilisateur.containsKey(u.id());
    }

    @Override
    public ResultatEnvoi envoyer(Utilisateur u, Message m) {
        String jeton = jetonsParUtilisateur.get(u.id());
        passerelle.transmettre(jeton, m.texte());
        return new ResultatEnvoi.Envoye(canal(), jeton);
    }
}
