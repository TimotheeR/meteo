package notifications.preferences;

import java.util.List;

/** Partie 3 : d'où viennent les préférences (Firestore en vrai, mémoire en test). */
public interface PreferencesRepository {
    /** Canaux par ordre de préférence. Liste vide si aucune préférence. */
    List<String> canaux(String userId);
}
