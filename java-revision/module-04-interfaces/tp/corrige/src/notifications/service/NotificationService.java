package notifications.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import notifications.Message;
import notifications.ResultatEnvoi;
import notifications.Utilisateur;
import notifications.canal.Notificateur;
import notifications.preferences.PreferencesRepository;

/**
 * Ne connaît AUCUN canal concret : uniquement les interfaces Notificateur et PreferencesRepository.
 * Aucun switch, aucun if sur le nom d'un canal.
 */
public class NotificationService {
    private final Map<String, Notificateur> notificateurs = new LinkedHashMap<>();
    private final PreferencesRepository preferences;
    private final String canalDeSecours;

    public NotificationService(List<Notificateur> notificateurs,
                               PreferencesRepository preferences,
                               String canalDeSecours) {
        for (Notificateur n : notificateurs) this.notificateurs.put(n.canal(), n);
        this.preferences = preferences;
        this.canalDeSecours = canalDeSecours;
    }

    /**
     * Essaie les canaux préférés dans l'ordre, puis le canal de secours.
     * Le premier canal qui peut joindre l'utilisateur est utilisé.
     */
    public ResultatEnvoi notifier(Utilisateur u, Message m) {
        List<String> essais = new ArrayList<>(preferences.canaux(u.id()));
        if (!essais.contains(canalDeSecours)) essais.add(canalDeSecours);

        for (String nomCanal : essais) {
            Notificateur n = notificateurs.get(nomCanal);
            if (n != null && n.peutJoindre(u)) {
                return n.envoyer(u, m);
            }
        }
        return new ResultatEnvoi.Echec("Aucun canal disponible pour " + u.nom());
    }

    /** Partie 5 : envoi groupé avec un filtre (interface fonctionnelle du JDK). */
    public List<ResultatEnvoi> notifierTous(List<Utilisateur> utilisateurs, Message m, Predicate<Utilisateur> filtre) {
        return utilisateurs.stream().filter(filtre).map(u -> notifier(u, m)).toList();
    }
}
