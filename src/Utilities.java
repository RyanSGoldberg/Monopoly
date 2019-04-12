/**
 * PURPOSE OF CLASS
 * @author Goldberg
 */

public class Utilities {
    public static int roll(){
        return (int)(Math.random()*6+1);
    }

    public static int generateNumber(int start, int end){//Inclusive-Exclusive
        if(end < start){
            System.err.println("Invalid Input: Utilities.java 'generateNumber'");
            System.exit(1);
        }

        return start+(int)(Math.random()*(end-start));
    }

    enum Type{PROPERTY, SPECIAL}

    //have to write board.ToString and in that method, put in all the info on each of the players
    //from there, I need to write the board.ToString file to a file
}
