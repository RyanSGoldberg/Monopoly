public class NPC extends Player{
    public NPC(String name, Board myBoard, String token, int tokenSize) {
        super(name, myBoard, token, tokenSize);
        this.type = Type.NPC;
    }

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

    public boolean makeDecisionBuyProperty(int[] options){
        if(options[1] == 5){
            return true;
        }
        else{
            return false;
        }

    }

    public boolean makeDecisionBuildHouse(int[] options){
        if(options[2] == 6){
            return true;
        } else{
            return false;
        }

    }

    public boolean makeDecisionSellProperty(int[] options){
        if(this.getBalance() <= 169 && options[3] == 7){
            return true;
        } else{
            return false;
        }
    }

    public boolean makeDecisionSellHouse(int[] options){
        if(this.getBalance() <= 169 && options[4] == 8){
            return true;
        } else{
            return false;
        }
    }

    public int makeDecisionTaxes(int[] options){
        //If %10 of their wallet is less than $150
        if(options[0] == 9 && this.getBalance() < 1500){
            return 9;
        }

        return 10;
    }
}
