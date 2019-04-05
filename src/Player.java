import java.util.ArrayList;

/**
 * @author Goldberg
 */

public class Player {
    private String name;
    private int wallet;

    private int position;

    private int numberOfJailCards;
    private boolean inJail;
    private int turnsLeftInJail;

    private ArrayList<Property> inventory;

    public Player(String name) {
        this.name = name;
        this.wallet = 1500;
        this.position = 0;
        this.numberOfJailCards = 0;
        this.inJail = false;
        this.turnsLeftInJail = 0;
        this.inventory = new ArrayList<>();
    }

    public void move(int numberOfSpaces){
        position += numberOfSpaces;
        if(position > 39){
            position = position%40;
            passGo();
        }
    }

    public void moveTo(int newPos){
        if(newPos < position){
            move((39-position)+newPos);
            passGo();
        }else {
            move(newPos-position);
        }
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

    public void passGo(){
        wallet+=200;
    }

    public void sendToJail(){
        turnsLeftInJail = 3;
        inJail = true;
        moveTo(10);
    }

    public void drawCard(){
        int random = Utilities.generateNumber(1,101);

        if(random < 20){//Get money
            random = Utilities.generateNumber(1,10);
            addMoney(20*random);
        }else if(random < 50){//Give money
            random = Utilities.generateNumber(1,10);
            removeMoney(15*random);
        }else if(random < 99){//Move spaces
            while (random != 0){
                random = Utilities.generateNumber(-3,3);
            }
            move(random);
        }else {//Get out of jail card
            numberOfJailCards++;
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
}
