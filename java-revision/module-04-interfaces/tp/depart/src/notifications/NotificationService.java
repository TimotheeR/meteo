package notifications;

/**
 * Le service "fait tout" : il choisit le canal, formate et envoie.
 * Chaque nouveau canal oblige à modifier cette classe.
 * Impossible de vérifier dans un test ce qui a été envoyé (tout part dans System.out).
 */
public class NotificationService {

    public void notifier(Utilisateur u, String message) {
        switch (u.canalPrefere()) {
            case "EMAIL" -> {
                System.out.println("[EMAIL] à " + u.email() + " : " + message);
            }
            case "SMS" -> {
                if (u.telephone() == null) {
                    System.out.println("Pas de téléphone pour " + u.nom());
                    return;
                }
                String court = message.length() > 160 ? message.substring(0, 157) + "..." : message;
                System.out.println("[SMS] à " + u.telephone() + " : " + court);
            }
            case "PUSH" -> {
                System.out.println("[PUSH] à l'appareil de " + u.id() + " : " + message);
            }
            default -> throw new IllegalArgumentException("Canal inconnu : " + u.canalPrefere());
        }
    }
}
