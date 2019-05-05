import javafx.scene.layout.StackPane;

public class PlayerCreatorPane extends StackPane {
    public Player.Type type;
    public String name = "";
    public String token = "";

    @Override
    public String toString() {
        return "PlayerCreatorPane{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
