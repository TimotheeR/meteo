package notifications;

/** Partie 5 : une interface scellée, comme une sealed class Dart. */
public sealed interface ResultatEnvoi permits ResultatEnvoi.Envoye, ResultatEnvoi.Echec {

    record Envoye(String canal, String destination) implements ResultatEnvoi { }

    record Echec(String raison) implements ResultatEnvoi { }

    default boolean estEnvoye() { return this instanceof Envoye; }

    default String resume() {
        return switch (this) {
            case Envoye e -> "Envoyé par " + e.canal() + " à " + e.destination();
            case Echec e  -> "Échec : " + e.raison();
        };
    }
}
