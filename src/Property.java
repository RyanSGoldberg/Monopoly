/**
 * PURPOSE OF CLASS
 * @author Zaionz
 */
public class Property extends Tile {
    private Player owner;

    private int[] rent;
    private int numberHouses;

    private int[] costs;
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
            if(playerHasMonopoly()){
                owner.removeMoney(rent[numberHouses]*2);
            }else {
                owner.removeMoney(rent[numberHouses]);
            }
        }
    }

    public boolean canBuild() {
        return canBuild;
    }

    public void buildHouse(){
        //TODO
    }

    public boolean hasOwner(){
        //TODO (Note doesnt always return false, just so i could run without errors msgs)
        return false;
    }

    public void buy (Player p){
        p.addProperty(this);
        owner = p;
        p.removeMoney(costs[0]);
    }

    public Player getOwner(){
        return owner;
    }

    public int getCost(){
        return costs[numberHouses];
    }

    public void sellProperty(){
        owner.addMoney(costs[0]/2);
        owner.removeProperty(this);
        owner = null;
    }

    public void sellHouse(){
        if(numberHouses != 0){
            owner.addMoney(costs[numberHouses]/2);
            numberHouses--;
        }
    }

    private boolean playerHasMonopoly(){
        //TODO if the player owns the whole group
        return false;
    }

}
