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
        String userChoice;
        switch (name){
            case "go":
                myBoard.passGo(p);
                break;
            case "goToJail":
                myBoard.sendToJail(p);
                break;
            case "getCard":
                myBoard.drawCard(p);
                break;
            case "freeParking":
                p.addMoney(myBoard.emptyCashPot());
                break;
            case "tax":
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