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

}
