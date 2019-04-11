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

    public Player(String name) {
        this.name = name;
        this.wallet = 1500;
        this.position = 0;
        this.numberOfJailCards = 0;
        this.inJail = false;
        this.turnsLeftInJail = 0;
        this.inventory = new ArrayList<>();
    }

    public void removeMoney(int amount){
        if(wallet - amount > 0){
            wallet -= amount;
        }else {
            int extra = amount - wallet;
            wallet = 0;
            mortgageMode(extra);
        }
    }

    public void addMoney(int amount){
        wallet+=amount;
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

    public void mortgageMode(int debt){
        //TODO
        //Player cannot leave this mode until debt is paid, if they have no more properties then game is over for player
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", wallet=" + wallet +
                ", position=" + position +
                ", numberOfJailCards=" + numberOfJailCards +
                ", inJail=" + inJail +
                ", turnsLeftInJail=" + turnsLeftInJail +
                ", inventory=" + inventory +
                '}';
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

    public String getName() {
        return name;
    }
}
