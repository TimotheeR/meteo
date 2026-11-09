/**
 * Exercice 5 : résoudre un conflit de méthodes default.
 * Lancer : java Exo05_Diamant.java
 *
 * TODO
 *  1. Telephone ne compile pas : corrige-le pour qu'il renvoie "wifi+bluetooth"
 *     en réutilisant les deux méthodes default (syntaxe Interface.super.methode()).
 *  2. Tablette : renvoie la version Wifi.
 *  3. Avant de lancer, PRÉDIS ce que renvoient Ordinateur et Modem, et quelle règle s'applique.
 *     Écris ta prédiction ici :
 *       Ordinateur : ...  (règle : ...)
 *       Modem      : ...  (règle : ...)
 */
public class Exo05_Diamant {
    public static void main(String[] args) {
        Check.egal("wifi+bluetooth", new Telephone().connecter(), "Telephone combine les deux");
        Check.egal("wifi", new Tablette().connecter(), "Tablette choisit le wifi");
        Check.egal("filaire", new Ordinateur().connecter(), "Règle 1 : la classe mère gagne");
        Check.egal("5G", new Modem().connecter(), "Règle 2 : l'interface la plus spécifique gagne");
        Check.fin();
    }
}

interface Wifi      { default String connecter() { return "wifi"; } }
interface Bluetooth { default String connecter() { return "bluetooth"; } }

class Telephone implements Wifi, Bluetooth {
    // TODO
}

class Tablette implements Wifi, Bluetooth {
    // TODO
}

class Machine { public String connecter() { return "filaire"; } }
class Ordinateur extends Machine implements Wifi, Bluetooth { }

interface CinqG extends Wifi { @Override default String connecter() { return "5G"; } }
class Modem implements Wifi, CinqG { }

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
