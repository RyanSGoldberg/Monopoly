/**
 * Assorted utilities needed in multiple different classes
 * @author Goldberg
 */

public class Utilities {
    /**
     * @return An integer from 1-6
     */
    public static int roll(){
        return (int)(Math.random()*6+1);
    }

    /**
     * A random number generator within [start, end)
     * @param start The inclusive beginning of the number range
     * @param end The exclusive end of the number range
     * @return An int within the range
     */
    public static int generateNumber(int start, int end){//Inclusive-Exclusive
        if(end < start){
            System.err.println("Invalid Input: Utilities.java 'generateNumber'");
            System.exit(1);
        }

        return start+(int)(Math.random()*(end-start));
    }

}
