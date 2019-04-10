/**
 * PURPOSE OF CLASS
 * @author Shafran
 */
public class Special extends Tile{
    public Special(int location, String name) {
        type = Utilities.Type.SPECIAL;
        this.name = name;
        this.location = location;
    }

    public void landedOn(Player p){
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
                //TODO return money pot
                break;
            case "tax":
                //TODO take money from player (either 150 or 10% of their wallet) --> Prompt the user asking which they want
                break;
        }
    }
}