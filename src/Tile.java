/**
 * PURPOSE OF CLASS
 * @author Zaionz
 */
public class Tile{
    protected Board myBoard;
    protected Utilities.Type type;

    protected String name;
    protected byte location;
    protected byte groupName;

    public Tile() {
        //TODO
    }

    public void landedOn(Player p){
    }

    public Utilities.Type getType() {
        return type;
    }

    public void setType(Utilities.Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getLocation() {
        return location;
    }

    public void setLocation(byte location) {
        this.location = location;
    }

    public byte getGroupName() {
        return groupName;
    }

    public void setGroupName(byte groupName) {
        this.groupName = groupName;
    }
}
