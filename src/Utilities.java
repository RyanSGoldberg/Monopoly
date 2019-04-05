/**
 * PURPOSE OF CLASS
 * @author Ryan
 */

public class Utilities {
    public static int roll(){
        return (int)(Math.random()*6+1);
    }

    public static int generateNumber(int end){
        return (int)(Math.random()*end+1);
    }
}
