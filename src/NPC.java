import javafx.scene.paint.Color;

public class NPC extends Player{

    public NPC(String name, Board myBoard, Color token) {
        super(name, myBoard, token);
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

    public int makeDecisionLandedOn(int[] options, int numActions){
        //Always buy
        if(options[1] == 5){
            return 5;
        }

        //Buy a house if they can, and they haven't done more than 2 things
        if(options[2] == 6 && numActions < 2){
            return 6;
        }

        //Never sell house

        //Never sell property

        //Never Trade

        //Ends the turn
        return 4;
    }

    public int makeDecisionTaxes(int[] options){
        //If %10 of their wallet is less than $150
        if(options[0] == 9 && this.getBalance() < 1500){
            return 9;
        }

        return 10;
    }
}
