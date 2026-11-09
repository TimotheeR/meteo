package notifications.preferences;

import java.util.List;
import java.util.Map;

public class PreferencesEnMemoire implements PreferencesRepository {
    private final Map<String, List<String>> preferences;

    public PreferencesEnMemoire(Map<String, List<String>> preferences) {
        this.preferences = Map.copyOf(preferences);
    }

    @Override
    public List<String> canaux(String userId) {
        return preferences.getOrDefault(userId, List.of());
    }
}
