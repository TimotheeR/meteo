package notifications;

public record Message(String titre, String corps) {
    public String texte() { return titre + " : " + corps; }
}
