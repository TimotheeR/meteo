package notifications.canal;

import notifications.Message;
import notifications.ResultatEnvoi;
import notifications.Utilisateur;

/** LE contrat du TP : un canal capable d'envoyer un message à un utilisateur. */
public interface Notificateur {

    /** Identifiant du canal : "EMAIL", "SMS", "PUSH", "SLACK"… */
    String canal();

    /** Peut-on joindre cet utilisateur par ce canal ? Par défaut : oui. */
    default boolean peutJoindre(Utilisateur u) {
        return true;
    }

    /** Appelé uniquement si peutJoindre(u) est vrai. */
    ResultatEnvoi envoyer(Utilisateur u, Message m);
}
