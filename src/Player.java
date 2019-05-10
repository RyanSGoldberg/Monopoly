import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
 * @author Goldberg
 */

public class Player {
    private String name;
    private int wallet;

    public int position;

    private boolean inJail;
    private int turnsLeftInJail;

    private ArrayList<Property> inventory;
    private int numberOfJailCards;

    private Board myBoard;

    protected Type type;

    public ImageView sprite;
    public String tokenName;

    private boolean inDebt;
    private int debt;

    public Player(String name, Board myBoard,String token, int tokenSize) {
        this.name = name;
        this.wallet = 1500;
        this.position = 0;
        this.numberOfJailCards = 0;
        this.inJail = false;
        this.turnsLeftInJail = 0;
        this.inventory = new ArrayList<>();
        this.myBoard = myBoard;

        this.type = Type.PC;

        this.tokenName = token;
        initializeSprite(token,tokenSize);
    }

    public void removeMoney(int amount){
        if(wallet - amount > 0){
            wallet -= amount;
        }else {
            debt = amount - wallet;
            wallet = 0;
            inDebt = true;
        }
    }

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

    public int getBalance() {
        return wallet;
    }

    public void addProperty(Property prop){
        inventory.add(prop);
    }

    public void removeProperty(Property prop){inventory.remove(prop);}

    public void setJail(){
        turnsLeftInJail = 3;
        inJail = true;
    }

    public ArrayList<Property> getInventory() {
        return inventory;
    }

    public boolean isInJail() {
        return inJail;
    }

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

    public boolean hasJailCard(){
        if(numberOfJailCards > 0){
            return true;
        }
        return false;
    }

    public void useJailCard(){
        numberOfJailCards--;
    }

    public void getJailCard(){
        numberOfJailCards++;
    }

    public int getNumberOfJailCards(){
        return numberOfJailCards;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public int netWorth(){
        int net = wallet;
        for (Property p:inventory) {
            net+= (p.getNumberHouses()*p.houseSalePrice())+p.propertySalePrice();
        }
        return net;
    }

    public Type getType() {
        return type;
    }

    private void initializeSprite(String token, int size){
        sprite = new ImageView(new Image(Display.class.getResourceAsStream("Images/" +token+".png")));
        sprite.setPreserveRatio(true);
        sprite.setFitHeight(size);
    }

    public boolean isInDebt() {
        return inDebt;
    }

    public int getDebt() {
        return debt;
    }

    public boolean isBroke(){
        if(debt > netWorth()){
            return true;
        }
        return false;
    }

    public ImageView getSprite() {
        return sprite;
    }

    @Override
    public String toString() {
        return  name+","+wallet+","+position+","+numberOfJailCards+","+inJail+","+turnsLeftInJail+","+inDebt+","+debt+","+tokenName;
    }

    enum Type {PC,NPC}
}
