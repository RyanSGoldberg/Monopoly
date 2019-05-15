public interface GameDisplay {
    int prompt(String question, int[] buttonsToDisplay);

    String propertyManagerPrompt(String[] buttonsToDisplay);

    void message(String message, boolean show);

    void updatePlayerPane(Player p);

    void showProperty(Property p, boolean show);

    void showChance(String title, String message,boolean show);

    void updateGameBoard();

    void diceRoll(int die1, int die2, boolean show);

    void winScreen(Player p);

    int spriteSize(int numSprites);
}