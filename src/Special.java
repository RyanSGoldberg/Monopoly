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
                //TODO
                break;
            case "jail":
                //TODO
                break;
            case "goToJail":
                //TODO
                break;
            case "getCard":
                //TODO
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