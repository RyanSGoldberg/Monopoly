import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 * A player object, which stored all of the user's in game information
 * @author Goldberg
 */

public class Player {
    protected String name;
    protected int wallet;

    public int position;

    protected boolean inJail;
    protected int turnsLeftInJail;

    protected ArrayList<Property> inventory;
    protected int numberOfJailCards;

    protected Type type;

    public ImageView sprite;
    public String tokenName;

    protected boolean inDebt;
    protected int debt;

    /**
     * A constructor for a Player
     * @param name The Player's name
     * @param token The token chosen by the user for the Player
     * @param tokenSize The Display generated tokenSize
     */
    public Player(String name,String token, int tokenSize) {
        this.name = name;
        this.wallet = 1500;
        this.position = 0;
        this.numberOfJailCards = 0;
        this.inJail = false;
        this.turnsLeftInJail = 0;
        this.inventory = new ArrayList<>();

        //A Playable Character
        this.type = Type.PC;

        this.tokenName = token;
        initializeSprite(token,tokenSize);
    }

    /**
     * @param name The Player's name
     * @param wallet The Player's balance
     * @param position The Player's position on the board
     * @param numberOfJailCards The Player's number of get out of jail free cards
     * @param inJail If the player is in jail
     * @param turnsLeftInJail The number of turns the player has left in jail (Can be 0)
     * @param inDebt If the player is in debt
     * @param debt The Player's debt (Can be 0)
     * @param token The token chosen by the user for the Player
     * @param tokenSize The Display generated tokenSize
     */
    public Player(String name, int wallet, int position, int numberOfJailCards, boolean inJail, int turnsLeftInJail, boolean inDebt, int debt, String token, int tokenSize){
        this.name = name;
        this.wallet = wallet;
        this.position = position;
        this.numberOfJailCards = numberOfJailCards;
        this.inJail = inJail;
        this.turnsLeftInJail = turnsLeftInJail;
        this.inDebt = inDebt;
        this.debt = debt;

        this.inventory = new ArrayList<>();

        //A Playable Character
        this.type = Type.PC;

        this.tokenName = token;
        initializeSprite(token,tokenSize);
    }

    /**
     * Handles the removal of money from the player's account
     * @param amount The amount of money to be removed
     */
    public void removeMoney(int amount){
        //If the player does not have enough funds they are in debt

        if(wallet  >= amount){
            wallet -= amount;
        }else {
            debt = amount - wallet;
            wallet = 0;
            inDebt = true;
        }
    }

    /**
     * Adds money to the user's account
     * @param amount The amount of money to be added
     */
    public void addMoney(int amount){
        if(inDebt && amount < debt){
            debt = debt-amount;
        }else if(inDebt && amount > debt){
            wallet+=amount-debt;
            inDebt = false;
            debt = 0;
        }else {
            wallet+=amount;
        }
    }

    /**
     * @return The player's current balance
     */
    public int getBalance() {
        return wallet;
    }

    /**
     * Adds a property to the player's inventory
     * @param prop The Property to be added
     */
    public void addProperty(Property prop){
        inventory.add(prop);
    }

    /**
     * Removes a property from the player's inventory
     * @param prop The Property to be removed
     */
    public void removeProperty(Property prop){inventory.remove(prop);}

    /**
     * Sets the player's jail stats
     */
    public void setJail(){
        turnsLeftInJail = 3;
        inJail = true;
    }

    /**
     * @return The player's current inventory of owned Properties
     */
    public ArrayList<Property> getInventory() {
        return inventory;
    }

    /**
     * @return If the player is currently in jail
     */
    public boolean isInJail() {
        return inJail;
    }

    /**
     * Handles the jail sentence, decreasing it until the sentence is done. OR releasing the player if there is a TRUE release boolean
     * @param release If the user should be freed immediately
     */
    public void decreaseJail(boolean release){
        if(release){
            turnsLeftInJail = 0;
        }else {
            turnsLeftInJail--;
        }

        if(turnsLeftInJail == 0){
            inJail = false;
        }


    }

    /**
     * @return If the player has a get out of jail free card
     */
    public boolean hasJailCard(){
        if(numberOfJailCards > 0){
            return true;
        }
        return false;
    }

    /**
     * Uses on of the get out jail free cards
     */
    public void useJailCard(){
        numberOfJailCards--;
    }

    /**
     * The player gains a get out of jail free card
     */
    public void getJailCard(){
        numberOfJailCards++;
    }

    /**
     * @return The number of get out jail free cards the players has
     */
    public int getNumberOfJailCards(){
        return numberOfJailCards;
    }

    /**
     * @return The player's position on the board
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The player's Net Worth
     * The Sum of their balance, and the values of all the Properties / Houses they own
     */
    public int netWorth(){
        int net = wallet;
        for (Property p:inventory) {
            net+= (p.getNumberHouses()*p.houseSalePrice())+p.propertySalePrice();
        }
        return net;
    }

    /**
     * @return The player's type. It will always be a PC (Playable Character)
     */
    public Type getType() {
        return type;
    }

    /**
     * @return If the player is in debt
     */
    public boolean isInDebt() {
        return inDebt;
    }

    /**
     * @return The player's debt
     */
    public int getDebt() {
        return debt;
    }

    /**
     * @return If the player is completely broke
     */
    public boolean isBroke(){
        if(debt > netWorth()){
            return true;
        }
        return false;
    }

    /**
     * Initializes the Player's token
     * @param token The token name
     * @param size The token's size
     */
    private void initializeSprite(String token, int size){
        try{
            sprite = new ImageView(new Image(Display.class.getResourceAsStream("Images/" +token+".png")));
            sprite.setPreserveRatio(true);
            sprite.setFitHeight(size);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @return The ImageView of the player's token
     */
    public ImageView getSprite() {
        return sprite;
    }

    /**
     * @return A string with all useful player information
     */
    @Override
    public String toString() {
        return  name+","+wallet+","+position+","+numberOfJailCards+","+inJail+","+turnsLeftInJail+","+inDebt+","+debt+","+tokenName+","+"PC";
    }

    /**
     * The possible types of players
     */
    enum Type {PC,NPC}
}
