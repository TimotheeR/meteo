import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 🧵 Fil rouge, étape Module 4 : WeatherProvider (corrigé).
 * Lancer : java FilRouge_WeatherProvider.java
 *
 * 🔁 Dans l'app Flutter meteo, fetchDataWeather() est une fonction globale qui appelle
 * directement http.get avec l'URL et la clé en dur. Ici, la source des données devient
 * une interface : le service métier ne sait pas d'où viennent les prévisions.
 */
public class FilRouge_WeatherProvider {
    public static void main(String[] args) {
        WeatherProvider faux = new FakeWeatherProvider(List.of(
                new Prevision(LocalDate.of(2026, 11, 9), 8.5, 14.0, "nuageux"),
                new Prevision(LocalDate.of(2026, 11, 10), 6.0, 17.5, "soleil"),
                new Prevision(LocalDate.of(2026, 11, 11), 4.0, 9.0, "pluie")));

        MeteoService service = new MeteoService(faux);

        Check.egal(LocalDate.of(2026, 11, 10), service.jourLePlusChaud("Paris").orElseThrow(),
                   "Jour le plus chaud : le 10");
        Check.vrai(service.vaPleuvoir("Paris"), "Il va pleuvoir");
        Check.egal(Optional.empty(), new MeteoService(new FakeWeatherProvider(List.of())).jourLePlusChaud("Paris"),
                   "Aucune prévision : Optional vide");

        var owm = new OpenWeatherMapProvider("CLE_BIDON");
        Check.egal("https://api.openweathermap.org/data/2.5/forecast?q=Saint-%C3%89tienne&appid=CLE_BIDON&lang=fr&units=metric",
                   owm.url("Saint-Étienne").toString(), "URL correctement construite et encodée");
        Check.fin();
    }
}

record Prevision(LocalDate jour, double tempMin, double tempMax, String description) { }

interface WeatherProvider {
    /** Prévisions sur 5 jours pour une ville. */
    List<Prevision> previsions(String ville);
}

/** Pour les tests et le développement hors ligne. */
class FakeWeatherProvider implements WeatherProvider {
    private final List<Prevision> donnees;

    FakeWeatherProvider(List<Prevision> donnees) { this.donnees = List.copyOf(donnees); }

    @Override
    public List<Prevision> previsions(String ville) { return donnees; }
}

/**
 * La vraie source. La clé est INJECTÉE (variable d'environnement en prod), jamais écrite en dur.
 * L'appel HTTP et la lecture du JSON arrivent au Module 10.
 */
class OpenWeatherMapProvider implements WeatherProvider {
    private static final String BASE = "https://api.openweathermap.org/data/2.5/forecast";
    private final String cleApi;

    OpenWeatherMapProvider(String cleApi) { this.cleApi = cleApi; }

    URI url(String ville) {
        return URI.create(BASE + "?q=" + URLEncoder.encode(ville, StandardCharsets.UTF_8)
                + "&appid=" + cleApi + "&lang=fr&units=metric");
    }

    @Override
    public List<Prevision> previsions(String ville) {
        throw new UnsupportedOperationException("À écrire au Module 10 (HttpClient + JSON)");
    }
}

class MeteoService {
    private final WeatherProvider provider;

    MeteoService(WeatherProvider provider) { this.provider = provider; }

    Optional<LocalDate> jourLePlusChaud(String ville) {
        return provider.previsions(ville).stream()
                .max(Comparator.comparingDouble(Prevision::tempMax))
                .map(Prevision::jour);
    }

    boolean vaPleuvoir(String ville) {
        return provider.previsions(ville).stream().anyMatch(p -> p.description().contains("pluie"));
    }
}

/** Mini outil de vérification (on passera à JUnit au Module 12). */
class Check {
    private static int ok = 0, ko = 0;

    static void vrai(boolean condition, String message) {
        if (condition) { ok++; System.out.println("  ✅ " + message); }
        else           { ko++; System.out.println("  ❌ " + message); }
    }

    static void egal(Object attendu, Object obtenu, String message) {
        vrai(java.util.Objects.equals(attendu, obtenu),
             message + (java.util.Objects.equals(attendu, obtenu) ? "" : " (attendu " + attendu + ", obtenu " + obtenu + ")"));
    }

    static void fin() {
        System.out.println(ko == 0 ? "\n🎉 " + ok + "/" + ok + " vérifications OK"
                                   : "\n" + ok + "/" + (ok + ko) + " vérifications OK");
    }
}
