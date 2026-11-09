package notifications;

/** canalPrefere : "EMAIL", "SMS" ou "PUSH". */
public record Utilisateur(String id, String nom, String email, String telephone, String canalPrefere) { }
