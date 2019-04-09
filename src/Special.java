/**
 * PURPOSE OF CLASS
 * @author Shafran
 */
public class Special extends Tile{
    public Special() {
        type = Utilities.Type.SPECIAL;
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
                //TODO
                break;
            case "tax":
                //TODO
                break;
        }
    }
}