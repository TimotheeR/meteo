import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Exercice 9 🪞 : extraire des interfaces d'EncryptionRepository (corrigé).
 * Lancer : java Exo09_Chiffrement.java
 *
 * Dans HeadsUp (lib/packages/encryption/repository/encryption_repository.dart),
 * EncryptionRepository n'a que des méthodes static qui vont chercher elles-mêmes
 * FirebaseRemoteConfig.instance. Conséquences :
 *   - impossible de remplacer la source des clés (Remote Config) dans un test ;
 *   - impossible de remplacer le chiffrement pour tester le code qui l'appelle ;
 *   - tout appelant est lié à Firebase.
 *
 * Découpage proposé : deux responsabilités, deux interfaces.
 *   FournisseurDeCle : "où sont les clés ?"   (Remote Config, mémoire, fichier…)
 *   Chiffreur        : "comment chiffrer ?"    (AES, ou un faux pour les tests)
 * Chaque dépendance arrive par le constructeur.
 */
public class Exo09_Chiffrement {
    public static void main(String[] args) {
        // Clé AES-256 (32 octets) et IV (16 octets), encodés en Base64 comme dans Remote Config
        String cle = Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        String iv  = Base64.getEncoder().encodeToString("abcdef9876543210".getBytes(StandardCharsets.UTF_8));

        FournisseurDeCle cles = new FournisseurEnMemoire(Map.of("cle_v2", new ReglageChiffrement(cle, iv)));
        Chiffreur aes = new ChiffreurAes(cles);

        String chiffre = aes.chiffrer("06 12 34 56 78", "cle_v2");
        Check.vrai(!chiffre.equals("06 12 34 56 78"), "Le texte chiffré diffère du texte clair");
        Check.egal("06 12 34 56 78", aes.dechiffrer(chiffre, "cle_v2"), "Aller-retour AES");
        Check.egal("", aes.chiffrer(null, "cle_v2"), "null -> chaîne vide (comme dans HeadsUp)");
        Check.egal(null, aes.dechiffrer("", "cle_v2"), "Déchiffrer une chaîne vide -> null");
        Check.egal(null, aes.dechiffrer("pas du base64 valide !", "cle_v2"), "Donnée corrompue -> null (pas d'exception)");

        // Le service métier ne connaît que l'interface : on lui donne un faux chiffreur
        var base = new HashMap<String, String>();
        var service = new ProfilService(new ChiffreurInverse(), base);
        service.enregistrerTelephone("u1", "0612");
        Check.egal("2160", base.get("u1"), "ProfilService testé sans AES ni Remote Config");
        Check.egal("0612", service.lireTelephone("u1"), "Lecture via le même faux chiffreur");

        // ... et en production, le vrai
        var serviceProd = new ProfilService(aes, new HashMap<>());
        serviceProd.enregistrerTelephone("u1", "0612");
        Check.egal("0612", serviceProd.lireTelephone("u1"), "Même service, vrai chiffreur");
        Check.fin();
    }
}

record ReglageChiffrement(String cleBase64, String ivBase64) { }

interface FournisseurDeCle {
    ReglageChiffrement reglage(String nom);
}

interface Chiffreur {
    /** Renvoie "" si la donnée est nulle ou vide. */
    String chiffrer(String clair, String nomReglage);

    /** Renvoie null si la donnée est nulle, vide ou illisible. */
    String dechiffrer(String chiffre, String nomReglage);
}

/** Remplace Remote Config en test. En prod : FournisseurRemoteConfig implements FournisseurDeCle. */
class FournisseurEnMemoire implements FournisseurDeCle {
    private final Map<String, ReglageChiffrement> reglages;

    FournisseurEnMemoire(Map<String, ReglageChiffrement> reglages) { this.reglages = reglages; }

    @Override
    public ReglageChiffrement reglage(String nom) {
        ReglageChiffrement r = reglages.get(nom);
        if (r == null) throw new IllegalArgumentException("Réglage inconnu : " + nom);
        return r;
    }
}

/** AES/CBC avec padding PKCS#5 (équivalent du PKCS#7 du package Dart encrypt pour AES). */
class ChiffreurAes implements Chiffreur {
    private final FournisseurDeCle cles;

    ChiffreurAes(FournisseurDeCle cles) { this.cles = cles; }

    @Override
    public String chiffrer(String clair, String nomReglage) {
        if (clair == null || clair.isEmpty()) return "";
        try {
            byte[] octets = cipher(Cipher.ENCRYPT_MODE, nomReglage).doFinal(clair.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(octets);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Échec du chiffrement", e);
        }
    }

    @Override
    public String dechiffrer(String chiffre, String nomReglage) {
        if (chiffre == null || chiffre.isEmpty()) return null;
        try {
            byte[] octets = cipher(Cipher.DECRYPT_MODE, nomReglage).doFinal(Base64.getDecoder().decode(chiffre));
            return new String(octets, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return null;
        }
    }

    private Cipher cipher(int mode, String nomReglage) throws GeneralSecurityException {
        ReglageChiffrement r = cles.reglage(nomReglage);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(mode,
               new SecretKeySpec(Base64.getDecoder().decode(r.cleBase64()), "AES"),
               new IvParameterSpec(Base64.getDecoder().decode(r.ivBase64())));
        return c;
    }
}

/** Faux chiffreur pour les tests : inverse la chaîne. */
class ChiffreurInverse implements Chiffreur {
    public String chiffrer(String clair, String nomReglage) {
        return clair == null ? "" : new StringBuilder(clair).reverse().toString();
    }
    public String dechiffrer(String chiffre, String nomReglage) {
        return chiffre == null || chiffre.isEmpty() ? null : new StringBuilder(chiffre).reverse().toString();
    }
}

class ProfilService {
    private static final String REGLAGE = "cle_v2";
    private final Chiffreur chiffreur;
    private final Map<String, String> base;   // simplification de Firestore

    ProfilService(Chiffreur chiffreur, Map<String, String> base) {
        this.chiffreur = chiffreur;
        this.base = base;
    }

    void enregistrerTelephone(String userId, String telephone) {
        base.put(userId, chiffreur.chiffrer(telephone, REGLAGE));
    }

    String lireTelephone(String userId) {
        return chiffreur.dechiffrer(base.get(userId), REGLAGE);
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
