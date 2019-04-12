/**
 * PURPOSE OF CLASS
 * @author Shafran
 */
import java.util.*;
public class Special extends Tile{
    public Special(int location, String name) {
        type = Utilities.Type.SPECIAL;
        this.name = name;
        this.location = location;
    }

    public void landedOn(Player p){
        //TODO In each option with a TODO let the user know what is happening
        String userChoice;
        switch (name){
            case "Go":
                myBoard.passGo(p);
                break;
            case "Go To Jail":
                myBoard.sendToJail(p);//TODO
                break;
            case "Chance":
                myBoard.drawCard(p);
                break;
            case "Free Parking":
                p.addMoney(myBoard.emptyCashPot()); //TODO (tell them how much they are getting)
                break;
            case "Income Tax":
                System.out.println("Would you like to: a) pay $150 or b) 10% of your wallet?");
                Scanner sc=new Scanner(System.in);
                userChoice=sc.nextLine();
                if(userChoice.equalsIgnoreCase("a")||userChoice.equalsIgnoreCase("a)")){
                    p.removeMoney(150);
                    myBoard.addToCashPot(150);
                }
                else if(userChoice.equalsIgnoreCase("b")||userChoice.equalsIgnoreCase("b)")){
                    p.removeMoney((int)(p.getBalance()*0.1));
                    myBoard.addToCashPot((int)(p.getBalance()*0.1));
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "";
        //TODO
    }
}