# TP 3 : Système de notifications

> Durée : 2 à 3 h. Notions : interfaces, `default`, interfaces fonctionnelles, `sealed`,
> injection par constructeur, principe ouvert/fermé.
> 🔁 Thème proche de HeadsUp (rappels de cours par e-mail, SMS, push).

## Point de départ

Le dossier [`depart/`](depart/) contient une application qui fonctionne :

```bash
cd depart
javac -d out $(find src -name '*.java') && java -cp out notifications.Main
```

Ses défauts :

- `NotificationService` contient un `switch` sur le nom du canal : chaque nouveau canal oblige à le modifier ;
- le formatage et l'envoi sont mélangés ;
- tout part dans `System.out` : impossible de **vérifier** dans un test ce qui a été envoyé ;
- un seul canal par utilisateur, sans solution de repli.

Tu vas refactoriser le code **par étapes**. Après chaque partie, l'application doit compiler et tourner.

---

## Partie 1 : le contrat (45 min)

1. Crée le package `notifications.canal` et une interface `Notificateur` :
   - `String canal()` : identifiant (`"EMAIL"`, `"SMS"`, `"PUSH"`) ;
   - `boolean peutJoindre(Utilisateur u)` : méthode **default** qui renvoie `true` ;
   - `void envoyer(Utilisateur u, Message m)` (le type de retour changera en partie 5).
2. Crée un `record Message(String titre, String corps)`.
3. Crée une interface fonctionnelle `Passerelle` avec une seule méthode
   `void transmettre(String destination, String contenu)` : c'est elle qui « envoie vraiment ».
4. Écris `EmailNotificateur`, `SmsNotificateur` et `PushNotificateur`. Chacun **reçoit une `Passerelle`
   dans son constructeur** et redéfinit `peutJoindre` quand c'est utile
   (pas de téléphone, pas de jeton push…). Le SMS est tronqué à 160 caractères.
5. Dans `Main`, la passerelle est une lambda qui affiche dans la console.

✔️ Critère : plus aucun `System.out` dans les notificateurs.

## Partie 2 : ouvert à l'extension, fermé à la modification (20 min)

1. Réécris `NotificationService` pour qu'il reçoive une `List<Notificateur>` dans son constructeur
   et ne contienne **aucun** `switch` ni `if` sur le nom d'un canal.
2. Ajoute un `SlackNotificateur` (il poste dans un salon, par exemple `"#formateurs"`).

✔️ Critère : ajouter Slack ne modifie **aucune ligne** de `NotificationService`. Vérifie-le avec `git diff`.

## Partie 3 : préférences et repli (30 min)

1. Retire `canalPrefere` de `Utilisateur`.
2. Crée le package `notifications.preferences` avec une interface `PreferencesRepository`
   (`List<String> canaux(String userId)`, par ordre de préférence) et une implémentation
   `PreferencesEnMemoire` basée sur une `Map`.
3. `NotificationService` reçoit aussi un `PreferencesRepository` et un canal de secours.
   Il essaie les canaux dans l'ordre et utilise le premier qui `peutJoindre` l'utilisateur.
   `notifier` renvoie `true` si un canal a été utilisé, `false` sinon.

🔁 Dans HeadsUp, ces préférences viendraient de Firestore : on écrirait `PreferencesFirestore implements PreferencesRepository`,
et le service n'en saurait rien.

## Partie 4 : tests (30 min)

Écris une classe `TestsNotifications` avec un `main`, sur le modèle de l'outil `Check` des exercices
(JUnit arrive au Module 12). Crée un `FauxNotificateur` qui **enregistre** les envois dans une liste, puis teste :

1. le canal préféré est utilisé ;
2. repli sur le canal de secours quand le préféré ne peut pas joindre ;
3. échec propre quand aucun canal ne peut joindre ;
4. un canal inconnu dans les préférences est ignoré ;
5. un vrai `SmsNotificateur`, avec une **lambda** comme passerelle, tronque bien à 160 caractères.

✔️ Critère : les tests n'affichent rien d'autre que leurs résultats (aucun « envoi » réel).

## Partie 5 : bonus (30 min)

1. Remplace le type de retour de `envoyer` et `notifier` par une interface **scellée** `ResultatEnvoi`
   avec deux records : `Envoye(canal, destination)` et `Echec(raison)`.
   Ajoute une méthode default `resume()` qui utilise un `switch` exhaustif.
2. Ajoute `notifierTous(List<Utilisateur>, Message, Predicate<Utilisateur> filtre)`.

---

## Grille d'auto-évaluation (/20)

| Critère | Points |
|---|---|
| `Notificateur` bien conçu (méthodes, `default` pertinent) | 3 |
| Passerelle injectée, aucun `System.out` dans les canaux | 3 |
| `NotificationService` sans `switch` ni `if` sur le canal | 4 |
| Slack ajouté sans modifier le service | 2 |
| Préférences via une interface, repli correct | 3 |
| Tests avec de faux notificateurs (5 cas) | 3 |
| Bonus `sealed` + `Predicate` | 2 |

Corrigé : [`corrige/`](corrige/) (`java -cp out notifications.TestsNotifications` pour lancer les tests).
