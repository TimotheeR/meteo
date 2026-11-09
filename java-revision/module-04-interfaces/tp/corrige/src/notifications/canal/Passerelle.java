package notifications.canal;

/**
 * Ce qui transmet réellement (serveur SMTP, API SMS, Firebase Messaging…).
 * Interface fonctionnelle : en démo, une lambda qui affiche ; en test, une lambda qui enregistre.
 */
@FunctionalInterface
public interface Passerelle {
    void transmettre(String destination, String contenu);
}
