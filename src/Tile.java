/**
 * PURPOSE OF CLASS
 * @author Zaionz
 */
public class Tile{
    protected Board myBoard;
    protected Type type;

    protected String name;
    protected int location;
    protected int groupName;

    public Tile() {
    }

    public void landedOn(Player p, int rollSum){
    }

    public String toString(int rollSum) {
        return super.toString();
    }

    public String getName() {
        return name;
    }

    enum Type{PROPERTY, SPECIAL}
}
