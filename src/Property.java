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

    public Property(int location, String name, int group, int[] rent, boolean canBuild, int[] costs) {
        type = Utilities.Type.PROPERTY;
        this.location = location;
        this.name = name;
        this.groupName = group;
        this.rent = rent;
        this.canBuild = canBuild;
        this.costs = costs;
        owner = null;
    }

    @Override
    public String toString() { // returns information about property

        if(numberHouses < 5 && owner != null) {
            return name + "'s ent costs: " + rent[numberHouses] + " with " + numberHouses + " houses";
        }
        else if(numberHouses == 5 && owner != null){
            return name + "'s ent costs: " + rent[numberHouses] + " with 1 hotel";
        }
        else{

            return name+" is an available property, it costs "+this.getCost()+" to buy and rent costs "+rent[0];

        }
    }


    public void landedOn(Player p){ // charges money for somebody that lands on a proeprty they do not own
        if(owner != null){//If there is an owner pay rent
            if(playerHasMonopoly()){
                p.removeMoney(rent[numberHouses]*2);
            }else {
                p.removeMoney(rent[numberHouses]);
            }
        }
    }

    public boolean canBuild() {
        return canBuild;
    }

    public void buildHouse(){
        //TODO
        if(owner.getBalance() >= getCost() && numberHouses < 5 && canBuild){

            owner.removeMoney(getCost());

            numberHouses++;

        }
        else{

            if(owner.getBalance() < getCost()){

                System.out.println("You do not have enough money to buy this property");

            } if(canBuild == false){

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
    }

    public Player getOwner(){
        return owner;
    }

    public int getCost(){ // when number of houses == 0 it returns the cost to buy the property otherwise it returns the cost to buy a house
        return costs[numberHouses];
    }

    public int getSalePrice(){
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
        return 1;
        //TODO
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

       /* if(owner.getInventory().contains()){

        }*/
    }



}
