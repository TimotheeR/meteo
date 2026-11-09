# 🧵 Fil rouge : étape du Module 4

**Objectif :** isoler la source des données météo derrière une interface.

🔁 Dans l'app Flutter `meteo`, `fetchDataWeather()` est une fonction globale qui appelle
`http.get` avec l'URL **et la clé API en dur**. Le code qui affiche les prévisions est donc lié
au réseau et à OpenWeatherMap.

## À faire (45 min)

1. Un `record Prevision(LocalDate jour, double tempMin, double tempMax, String description)`.
2. Une interface `WeatherProvider` avec `List<Prevision> previsions(String ville)`.
3. `FakeWeatherProvider` : renvoie une liste fixée dans son constructeur.
4. `OpenWeatherMapProvider` : reçoit la clé API **dans son constructeur** et sait construire l'URL
   (ville encodée avec `URLEncoder`). L'appel HTTP lui-même viendra au Module 10 :
   pour l'instant, `previsions` lève `UnsupportedOperationException`.
5. `MeteoService`, qui reçoit un `WeatherProvider` et propose
   `Optional<LocalDate> jourLePlusChaud(String ville)` et `boolean vaPleuvoir(String ville)`.
6. Vérifie `MeteoService` avec le faux provider uniquement.

Corrigé : [`FilRouge_WeatherProvider.java`](FilRouge_WeatherProvider.java).
Quand le projet `meteo-java` (Module 0) existera, ces classes iront dans ses packages.
