/**
 * The parent of Property and Special. It is a general tile type, who's methods are overwritten in its children
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

    /**
     * The function which controls what happens when a tile is landed on
     * @param p The player who landed on this Tile
     * @param rollSum The sum of the die roll, which resulted on this tile being landed on
     * @param show A boolean stating whether a popup should be displayed
     */
    public void landedOn(Player p, int rollSum, boolean show){
    }

    /**
     * A ToString() method for tile
     * @return A string displaying all needed information about the tile
     */
    public String toString() {
        return super.toString();
    }

    /**
     * @return The Tile's name
     */
    public String getName() {
        return name;
    }

    /**
     *Possible Tile types
     */
    enum Type{PROPERTY, SPECIAL}
}
