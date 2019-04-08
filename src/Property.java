/**
 * PURPOSE OF CLASS
 * @author Zaionz
 */
public class Property extends Tile {
    private Player owner;

    private int cost;
    private int[] rent;
    private int numberHouses;

    private int[] houseCosts;
    private boolean canBuild;

    public Property() {
        type = Utilities.Type.PROPERTY;

    }

    @Override
    public String toString() {
        //Information about property
        //TODO
        return "";
    }

    public void landedOn(Player p){
        if(owner != null){//If there is an owner pay rent
            //TODO
        }
    }

    public boolean hasOwner(){
        //TODO (Note doesnt always return false, just so i could run without errors msgs)
        return false;
    }

    public Player getOwner(){
        return owner;
    }

}
