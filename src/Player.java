import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

/**
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

    public Player(String name,String token, int tokenSize) {
        this.name = name;
        this.wallet = 1500;
        this.position = 0;
        this.numberOfJailCards = 0;
        this.inJail = false;
        this.turnsLeftInJail = 0;
        this.inventory = new ArrayList<>();

        this.type = Type.PC;

        this.tokenName = token;
        initializeSprite(token,tokenSize);
    }

    public Player(String name, int wallet, int position, int numberOfJailCards, boolean inJail, int turnsLeftInJail, boolean inDebt, int debt, String tokenName, int tokenSize){
        this.name = name;
        this.wallet = wallet;
        this.position = position;
        this.numberOfJailCards = numberOfJailCards;
        this.inJail = inJail;
        this.turnsLeftInJail = turnsLeftInJail;
        this.inDebt = inDebt;
        this.debt = debt;

        this.inventory = new ArrayList<>();

        this.type = Type.PC;

        this.tokenName = tokenName;
        initializeSprite(tokenName,tokenSize);
    }

    public void removeMoney(int amount){
        if(wallet  >= amount){
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
        return  name+","+wallet+","+position+","+numberOfJailCards+","+inJail+","+turnsLeftInJail+","+inDebt+","+debt+","+tokenName+","+"PC";
    }

    enum Type {PC,NPC}
}
