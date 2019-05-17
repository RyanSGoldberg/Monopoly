/**
 * A child of Tile who can be purchased and has a rent should others land on it
 * @author Zaionz
 */
public class Property extends Tile {
    private Player owner;

    private int[] rent;
    private int numberHouses;
    private int[] costs;
    private boolean canBuild;

    /**
     * A constructor for a property
     * @param location The properties location on the board
     * @param name The properties name
     * @param group The properties Monopoly Group (Ex, Brown, Orange, Railroads)
     * @param rent The properties rents(Depending on houses owned)
     * @param canBuild If houses can be built on the property
     * @param costs The costs for buying the property/building houses
     * @param myBoard The Board which this Property is in
     */
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

    /**
     * Handles players who landed on properties
     * @param p The player who landed on this Tile
     * @param rollSum The sum of the die roll, which resulted on this tile being landed on
     * @param show A boolean stating whether a popup should be displayed
     */
    public void landedOn(Player p, int rollSum, boolean show){ // charges money for somebody that lands on a property they do not own
        //Handles rent
        if(owner != null && !owner.equals(p)){
            p.removeMoney(getRent(rollSum));
            owner.addMoney(getRent(rollSum));

            myBoard.gameDisplay.message(p.getName() + " just paid $" + getRent(rollSum) + " to " + owner.getName(),show);
        }
    }

    /**
     * Returns the rent needed to be paid
     * @param rollSum The sum of the dice roll which resulted in the player landing on this Property
     * @return The amount needed to be paid
     */
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

                if (playerHasMonopoly() && this.numberHouses == 0) {// if they have a monopoly but no houses

                    return this.rent[numberHouses] * 2;


                }else if(playerHasMonopoly() && this.numberHouses != 0) {// if they have a monopoly and any amount of houses

                    return this.rent[numberHouses];

                } else{// if they don't have a monopoly

                    return this.rent[numberHouses];

                }

            }
        } else{

            return 0;

        }

    }

    /**
     * @return If a property can have houses built on it
     */
    public boolean canBuild() {
        return canBuild;
    }

    /**
     * Builds a house
     */
    public void buildHouse(){
        owner.removeMoney(costs[1]);
        numberHouses++;
    }

    /**
     * @return If the property has an owner
     */
    public boolean hasOwner(){
        if(owner != null){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Buys Property
     * @param p Player who buys the property
     */
    public void buy(Player p){
        p.addProperty(this);
        owner = p;
        p.removeMoney(costs[0]);
    }

    /**
     * @return The Property's Owner
     */
    public Player getOwner(){
        return owner;
    }

    /**
     * @return The cost of the next purchase on this property
     */
    public int getCost(){ // when number of houses == 0 it returns the cost to buy the property otherwise it returns the cost to buy a house
        if(numberHouses != 0){
            return costs[1];
        }else {
            return costs[0];
        }
    }

    /**
     * @return The sale price for this Property
     */
    public int propertySalePrice(){
        return costs[0]/2;
    }

    /**
     * Sells the property for half of its sales price
     */
    public void sellProperty(){
        owner.addMoney(costs[0]/2);
        owner.removeProperty(this);
        owner = null;
    }

    /**
     * @return The number of build houses
     */
    public int getNumberHouses() {
        return numberHouses;
    }

    /**
     * @return The house sale price
     */
    public int houseSalePrice(){
        return (costs[1]/2);
    }

    /**
     * Sells a house for half the sales price
     */
    public void sellHouse(){
        if(numberHouses != 0){
            owner.addMoney(costs[1]/2);
            numberHouses--;
        }
    }

    /**
     * @return If this property is in a Monopoly
     */
    public boolean playerHasMonopoly(){
        if(numberOfAGroupOwned(groupName) == myBoard.monopolies[groupName-1]){
            return true;
        }
        return false;
    }

    /**
     * @param group The Monopoly in question
     * @return The Number of that Monopoly owned
     */
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

    /**
     * @return The cost to build a house
     */
    public int getHouseCost(){
        return costs[1];
    }

    /**
     * @return The array of rents
     */
    public int[] getRents() {
        return rent;
    }

    /**
     * Sets the Property as having an owner, and some houses built on it
     * @param owner The new owner
     * @param numberHouses The new number of houses
     */
    public void setStatus(Player owner, int numberHouses){
        this.owner = owner;
        this.numberHouses = numberHouses;
    }

    /**
     * A ToString() method for Property
     * @return A string displaying all needed information about the Property
     */
    public String toString() {
        // returns information about property
        if (owner != null) {
            return owner.getName()+","+numberHouses;
        }else{
            return "NULL";
        }
    }
}
