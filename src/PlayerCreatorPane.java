import javafx.scene.layout.StackPane;

/**
 * A child of a StackPane which has additional information stored within it
 */

public class PlayerCreatorPane extends StackPane {
    public Player.Type type;
    public String name = "";
    public String token = "";

    /**
     * @return A string with the additional information stored within the PlayerCreatorPane
     */
    @Override
    public String toString() {
        return "PlayerCreatorPane{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
