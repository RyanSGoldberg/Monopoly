public interface GameDisplay {
    int prompt(String question, int[] buttonsToDisplay);

    void message(String message, Player player);

    void updatePlayerPane(Player p);

    void showProperty(Property p, Player player);

    void movePlayer(Player p);

    void showChance(String title, String message, Player player);

    void updateGameBoard();
}