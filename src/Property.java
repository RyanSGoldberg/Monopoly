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
        type = Utilities.Type.PROPERTY;
        this.location = location;
        this.name = name;
        this.groupName = group;
        this.rent = rent;
        this.canBuild = canBuild;
        this.costs = costs;
        owner = null;
        this.myBoard = myBoard;
    }

    public String toString(int rollSum) { // returns information about property

        if(owner != null){ // if the property is owned

            if(this.groupName == 005){// if it is a utility

                if(this.numberOfAGroupOwned(005)==2) {

                    return this.name + " is owned by " + this.owner.getName() + " rent costs $" + this.getRent(rollSum) + " which is 10 times what you rolled";

                }else {

                    return this.name + " is owned by " + this.owner.getName() + " rent costs $" + this.getRent(rollSum) + " which is 4 times what you rolled";

                }

            } else if(this.groupName == 002){// if it is a railroad

                return this.name+" is owned by "+this.owner.getName()+" rent costs $"+this.getRent(rollSum)+" "+this.owner+" owns "+numberOfAGroupOwned(002)+" railraods";

            } else{// if it is a regular property

                if(this.numberHouses == 5){// if player has hotels

                    return this.name+" is owned by "+this.owner.getName()+" rent costs $"+this.getRent(rollSum)+" with a hotel";

                }else{// if player does not have hotels

                    return this.name+" is owned by "+this.owner.getName()+" rent costs $"+this.getRent(rollSum)+" with "+numberHouses+" houses";

                }

            }

        } else{ // if the property is available

            if(this.groupName != 005){// if it is not a utility

                return this.name + " is an available property, it costs $" + this.getCost() + " to buy, rent costs $"+this.rent[0];

            } else{// if it is a utility

                if(playerHasMonopoly(this.groupName)){// if player has both utilities

                    return this.name + " is an available property, it costs $" + this.getCost() + " to buy, rent costs 10 time whatever you roll";

                } else{// if player has one utility

                    return this.name + " is an available property, it costs $" + this.getCost() + " to buy, rent costs 4 time whatever you roll";

                }

            }
        }
    }

    public void landedOn(Player p, int rollSum){ // charges money for somebody that lands on a proeprty they do not own

        if(owner != null && !owner.equals(p)){
            p.removeMoney(getRent(rollSum));
            owner.addMoney(getRent(rollSum));

            System.out.println(p.getName() + " just paid $" + getRent(rollSum) + " to " + owner.getName());
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
        //TODO
        if(/*owner.getBalance() >= this.getCost() && */numberHouses < 5 && canBuild){

            owner.removeMoney(this.getCost());

            numberHouses++;

        }
        else{

            if(canBuild == false){

                System.out.println("Sorry you cannot build here");

            } if(numberHouses == 5){

                System.out.println("You already have a hotel (5 houses) here and cannot build more");

            }

        }
    }

    public boolean hasOwner(){
        if(owner != null){
            return true;
        }
        else{
            return false;
        }
    }

    public void buy (Player p){
        p.addProperty(this);
        owner = p;
        p.removeMoney(costs[0]);
        System.out.println("you have just bought "+this.name+" your current balance is $"+p.getBalance());
    }

    public Player getOwner(){
        return owner;
    }

    public int getCost(){ // when number of houses == 0 it returns the cost to buy the property otherwise it returns the cost to buy a house
        return costs[numberHouses];
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
        return (costs[numberHouses]/2);
    }

    public void sellHouse(){
        if(numberHouses != 0){
            owner.addMoney(costs[numberHouses]/2);
            numberHouses--;
        }
    }

    private boolean playerHasMonopoly(int group){
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

    public int[] getRents() {
        return rent;
    }

    public boolean isBuildable() {
        return canBuild;
    }
}
