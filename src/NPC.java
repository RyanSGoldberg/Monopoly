/**
 * A child of Player, which has internal logic
 * This allows it to be played without active user control
 * @author Zaionz
 */

public class NPC extends Player{
    /**
     * A constructor for a NPC (Non Playable Character)
     * @param name The NPC's name
     * @param token The token chosen by the user for the NPC
     * @param tokenSize The Display generated tokenSize
     */
    public NPC(String name, String token, int tokenSize) {
        super(name, token, tokenSize);
        this.type = Type.NPC;
    }

    /**
     * @param name The NPC's name
     * @param wallet The NPC's balance
     * @param position The NPC's position on the board
     * @param numberOfJailCards The NPC's number of get out of jail free cards
     * @param inJail If the NPC is in jail
     * @param turnsLeftInJail The number of turns the NPC has left in jail (Can be 0)
     * @param inDebt If the NPC is in debt
     * @param debt The NPC's debt (Can be 0)
     * @param token The token chosen by the user for the NPC
     * @param tokenSize The Display generated tokenSize
     */
    public NPC(String name, int wallet, int position, int numberOfJailCards, boolean inJail, int turnsLeftInJail, boolean inDebt, int debt, String token, int tokenSize){
        super(name,wallet,position,numberOfJailCards,inJail,turnsLeftInJail,inDebt,debt,token,tokenSize);
        this.type = Type.NPC;
    }

    /**
     * Handle's jail related decisions
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public int makeDecisionJail(int[] options){
        //If the NPC has a get out of jail card, use it
        if(options[2] == 3){
            return 3;
        }

        //If the NPC has $1000+ buy his way out of jail
        if(options[1] == 2 && this.getBalance() > 1000){
            return 2;
        }

        //Do jail time for another turn
        return 1;
    }

    /**
     * Handle's property buying and house building choices
     * @param boughtProperty If the NPC bought a property that turn
     * @param boughtHouse If the NPC bought a house that turn
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public int makeDecisionLandedOn(int[] options,boolean boughtProperty, boolean boughtHouse){
        if(makeDecisionBuyProperty(options)) {
            return 5;
        } else if(makeDecisionBuildHouse(options)){
            return 6;
        } else if(makeDecisionSellProperty(options) && !boughtProperty){
            return 7;
        } else if(makeDecisionSellHouse(options) && !boughtHouse){
            return 8;
        } else {
            return 4;
        }
    }

    /**
     * Handles choice to buy properties
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public boolean makeDecisionBuyProperty(int[] options){
        if(options[1] == 5){
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * Handles choice to build house
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public boolean makeDecisionBuildHouse(int[] options){
        if(options[2] == 6){
            return true;
        } else{
            return false;
        }

    }

    /**
     * Handles choice to sell property
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public boolean makeDecisionSellProperty(int[] options){
        if(this.getBalance() <= 169 && options[3] == 7){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Handles decision to sell house
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public boolean makeDecisionSellHouse(int[] options){
        if(this.getBalance() <= 169 && options[4] == 8){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Handles decision of how to pay taxes
     * @param options The possible options the NPC can chose from
     * @return The chosen option
     */
    public int makeDecisionTaxes(int[] options){
        //If %10 of their wallet is less than $150
        if(options[0] == 9 && this.getBalance() < 1500){
            return 9;
        }

        return 10;
    }

    /**
     @return A string with all useful NPC information
     */
    @Override
    public String toString() {
        return  name+","+wallet+","+position+","+numberOfJailCards+","+inJail+","+turnsLeftInJail+","+inDebt+","+debt+","+tokenName+","+"NPC";
    }
}
