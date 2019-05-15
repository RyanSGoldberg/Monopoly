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

    public Property(int location, String name, int group, int[] rent, boolean canBuild, int[] costs, Board myBoard) {
        type = Tile.Type.PROPERTY;
        this.location = location;
        this.name = name;
        this.groupName = group;
        this.rent = rent;
        this.canBuild = canBuild;
        this.costs = costs;
        owner = null;
        this.myBoard = myBoard;
    }

    public String toString() {
        // returns information about property
        if (owner != null) {
            return owner.getName()+","+numberHouses;
        }else{
            return "NULL";
        }
            }

    public void landedOn(Player p, int rollSum, boolean show){ // charges money for somebody that lands on a property they do not own
        if(owner != null && !owner.equals(p)){
            p.removeMoney(getRent(rollSum));
            owner.addMoney(getRent(rollSum));

            myBoard.gameDisplay.message(p.getName() + " just paid $" + getRent(rollSum) + " to " + owner.getName(),show);
        }


    }

    public int getRent(int rollSum){
        if(owner != null) {
            if (groupName == 002) {// railroad

                return (int) (Math.pow(2, (numberOfAGroupOwned(002) - 1))) * 25;

            } else if (groupName == 005) {// utilities

                if (numberOfAGroupOwned(005)==2) {// if they own both utilities

                    return rollSum * 10;

                } else {// if they own 1 utility

                    return rollSum * 4;

                }

            } else {//normal properties

                if (playerHasMonopoly(this.groupName) && this.numberHouses == 0) {// if they have a monopoly but no houses

                    return this.rent[numberHouses] * 2;


                }else if(playerHasMonopoly(this.groupName) && this.numberHouses != 0) {// if they have a monopoly and any amount of houses

                    return this.rent[numberHouses];

                } else{// if they don't have a monopoly

                    return this.rent[numberHouses];

                }

            }
        } else{

            return 0;

        }

    }

    public boolean canBuild() {
        return canBuild;
    }

    public void buildHouse(){
        owner.removeMoney(costs[1]);
        numberHouses++;
    }

    public boolean hasOwner(){
        if(owner != null){
            return true;
        }
        else{
            return false;
        }
    }

    public void buy(Player p){
        p.addProperty(this);
        owner = p;
        p.removeMoney(costs[0]);
    }

    public Player getOwner(){
        return owner;
    }

    public int getCost(){ // when number of houses == 0 it returns the cost to buy the property otherwise it returns the cost to buy a house
        if(numberHouses != 0){
            return costs[1];
        }else {
            return costs[0];
        }
    }

    public int propertySalePrice(){
        return costs[0]/2;
    }

    public void sellProperty(){
        owner.addMoney(costs[0]/2);
        owner.removeProperty(this);
        owner = null;
    }

    public int getNumberHouses() {
        return numberHouses;
    }

    public int houseSalePrice(){
        return (costs[1]/2);
    }

    public void sellHouse(){
        if(numberHouses != 0){
            owner.addMoney(costs[1]/2);
            numberHouses--;
        }
    }

    public boolean playerHasMonopoly(int group){
        if(numberOfAGroupOwned(group) == myBoard.monopolies[group-1]){
            return true;
        }
        return false;
    }

    private int numberOfAGroupOwned(int group){
        int num = 0;

        if(owner == null){
            return 0;
        }

        for (Property p:owner.getInventory()) {
            if(p.groupName == group){
                num++;
            }
        }
        return num;
    }

    public int getHouseCost(){
        return costs[1];
    }

    public int[] getRents() {
        return rent;
    }

    public boolean isBuildable() {
        return canBuild;
    }

    public void setStatus(Player owner, int numberHouses){
        this.owner = owner;
        this.numberHouses = numberHouses;
    }
}
