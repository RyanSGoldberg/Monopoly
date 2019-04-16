/**
 * PURPOSE OF CLASS
 * @author Shafran
 */
import java.util.*;
public class Special extends Tile{
    public Special(int location, String name, Board myBoard) {
        type = Utilities.Type.SPECIAL;
        this.name = name;
        this.location = location;
        this.myBoard = myBoard;
    }

    public void landedOn(Player p){
        switch (name){
            //Has the player pass the Go tile
            case "Go":
                myBoard.passGo(p);
                break;
            //Sends the player to jail due to landing on the Go To Jail tile
            case "Go To Jail":
                System.out.println("Sorry Pal, off to the clink with you");
                myBoard.sendToJail(p);
                break;
            //Has the player draw a card due to landing on the Chance tile
            case "Chance":
                System.out.println("You just got a chance card?!?!");
                myBoard.drawCard(p);
                break;
            //Has the player take all the money from the money pot due to landing on the Free Parking tile
            case "Free Parking":
                int received = myBoard.emptyCashPot();
                System.out.println("You lucky duck. You just got $"+received);
                p.addMoney(received);
                break;
            //Has the player pay either $150 or 10% of his/her wallet due to landing on the Income Tax tile
            case "Income Tax":
                System.out.println("You have to pay your taxes pal. Here are your options ...");
                if(p.getBalance()>=150){
                    System.out.println("1) You can pay $150");
                }

                System.out.println("2) You can pay 10% of your wallet");

                Scanner sc=new Scanner(System.in);
                int userChoice=sc.nextInt();
                switch (userChoice){
                    case 1:
                        p.removeMoney(150);
                        myBoard.addToCashPot(150);
                        break;
                    case 2:
                        p.removeMoney((int)(p.getBalance()*0.1));
                        myBoard.addToCashPot((int)(p.getBalance()*0.1));
                        break;
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "You are on "+this.name+" at "+this.location;
    }
}