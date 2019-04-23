public interface GameDisplay {
    int prompt(String question, int[] buttonsToDisplay);

    void message(String message);

    void updatePlayerPane(Player p);

    void showProperty(Property p);

    void movePlayer(Player p);
}