/**
 * PURPOSE OF CLASS
 * @author Zaionz
 */
public class Tile{
    protected Board myBoard;
    protected Utilities.Type type;

    protected String name;
    protected int location;
    protected int groupName;

    public Tile() {
    }

    public void landedOn(Player p){
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public int getGroupName() {
        return groupName;
    }
}
