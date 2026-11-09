import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Exercice 9 🪞 : extraire des interfaces d'EncryptionRepository.
 * Lancer : java Exo09_Chiffrement.java
 *
 * Dans HeadsUp, lib/packages/encryption/repository/encryption_repository.dart ressemble à :
 *
 *   class EncryptionRepository {
 *     static String encryptData(String? data, String settingName) {
 *       final remoteConfig = FirebaseRemoteConfig.instance;   // singleton global
 *       ... lit la clé et l'IV, chiffre en AES-CBC, renvoie du Base64
 *     }
 *     static String? decryptData(String? data, String settingName) { ... }
 *   }
 *
 * La classe EncryptionRepositoryStatique ci-dessous en est la traduction Java,
 * avec une fausse "RemoteConfig.INSTANCE".
 *
 * TODO
 *  1. Liste (en commentaire) ce qui empêche de tester un service qui appelle EncryptionRepositoryStatique.
 *  2. Crée deux interfaces :
 *       FournisseurDeCle { ReglageChiffrement reglage(String nom); }
 *       Chiffreur { String chiffrer(String clair, String nomReglage);
 *                   String dechiffrer(String chiffre, String nomReglage); }
 *  3. Crée FournisseurEnMemoire (une Map) et ChiffreurAes, qui RECOIT un FournisseurDeCle
 *     dans son constructeur (déplace-y le code AES de la classe statique).
 *  4. Crée ChiffreurInverse (faux chiffreur de test : inverse la chaîne).
 *  5. Fais en sorte que ProfilService reçoive un Chiffreur au lieu d'appeler la classe statique.
 *  6. Remplace le main par les vérifications du bas du fichier (décommente-les).
 */
public class Exo09_Chiffrement {
    public static void main(String[] args) {
        var base = new HashMap<String, String>();
        var service = new ProfilService(base);
        service.enregistrerTelephone("u1", "0612");
        Check.egal("0612", service.lireTelephone("u1"), "Aller-retour avec la version statique");
        Check.fin();

        /* Vérifications à utiliser après la refactorisation :
        String cle = Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        String iv  = Base64.getEncoder().encodeToString("abcdef9876543210".getBytes(StandardCharsets.UTF_8));
        FournisseurDeCle cles = new FournisseurEnMemoire(Map.of("cle_v2", new ReglageChiffrement(cle, iv)));
        Chiffreur aes = new ChiffreurAes(cles);

        String chiffre = aes.chiffrer("06 12 34 56 78", "cle_v2");
        Check.vrai(!chiffre.equals("06 12 34 56 78"), "Le texte chiffré diffère du texte clair");
        Check.egal("06 12 34 56 78", aes.dechiffrer(chiffre, "cle_v2"), "Aller-retour AES");
        Check.egal("", aes.chiffrer(null, "cle_v2"), "null -> chaîne vide (comme dans HeadsUp)");
        Check.egal(null, aes.dechiffrer("", "cle_v2"), "Déchiffrer une chaîne vide -> null");
        Check.egal(null, aes.dechiffrer("pas du base64 valide !", "cle_v2"), "Donnée corrompue -> null");

        var base = new HashMap<String, String>();
        var service = new ProfilService(new ChiffreurInverse(), base);
        service.enregistrerTelephone("u1", "0612");
        Check.egal("2160", base.get("u1"), "ProfilService testé sans AES ni Remote Config");
        Check.egal("0612", service.lireTelephone("u1"), "Lecture via le même faux chiffreur");

        var serviceProd = new ProfilService(aes, new HashMap<>());
        serviceProd.enregistrerTelephone("u1", "0612");
        Check.egal("0612", serviceProd.lireTelephone("u1"), "Même service, vrai chiffreur");
        Check.fin();
        */
    }
}

record ReglageChiffrement(String cleBase64, String ivBase64) { }

// ----- Code de départ (traduction de HeadsUp) -----

/** Fausse Remote Config : un singleton global, comme FirebaseRemoteConfig.instance. */
class RemoteConfig {
    static final RemoteConfig INSTANCE = new RemoteConfig();
    private RemoteConfig() { }

    ReglageChiffrement get(String nom) {
        return new ReglageChiffrement(
                Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8)),
                Base64.getEncoder().encodeToString("abcdef9876543210".getBytes(StandardCharsets.UTF_8)));
    }
}

class EncryptionRepositoryStatique {
    static String encryptData(String data, String settingName) {
        if (data == null || data.isEmpty()) return "";
        try {
            ReglageChiffrement r = RemoteConfig.INSTANCE.get(settingName);
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(javax.crypto.Cipher.ENCRYPT_MODE,
                   new javax.crypto.spec.SecretKeySpec(Base64.getDecoder().decode(r.cleBase64()), "AES"),
                   new javax.crypto.spec.IvParameterSpec(Base64.getDecoder().decode(r.ivBase64())));
            return Base64.getEncoder().encodeToString(c.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    static String decryptData(String data, String settingName) {
        if (data == null || data.isEmpty()) return null;
        try {
            ReglageChiffrement r = RemoteConfig.INSTANCE.get(settingName);
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(javax.crypto.Cipher.DECRYPT_MODE,
                   new javax.crypto.spec.SecretKeySpec(Base64.getDecoder().decode(r.cleBase64()), "AES"),
                   new javax.crypto.spec.IvParameterSpec(Base64.getDecoder().decode(r.ivBase64())));
            return new String(c.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
        } catch (java.security.GeneralSecurityException | IllegalArgumentException e) {
            return null;
        }
    }
}

class ProfilService {
    private final Map<String, String> base;

    ProfilService(Map<String, String> base) { this.base = base; }

    void enregistrerTelephone(String userId, String telephone) {
        base.put(userId, EncryptionRepositoryStatique.encryptData(telephone, "cle_v2"));
    }

    String lireTelephone(String userId) {
        return EncryptionRepositoryStatique.decryptData(base.get(userId), "cle_v2");
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
