import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.concurrent.Semaphore;

public class Display extends Application implements GameDisplay{
    Stage window;
    Scene scene;
    BorderPane screen;
    BorderPane gameBoard;

    private Board game;
    private int boardSize;

    private Color tileColors[] = new Color[]{null,Color.SADDLEBROWN,null,Color.CORNFLOWERBLUE,Color.MAGENTA,null,Color.ORANGE,Color.RED,Color.YELLOW,Color.GREEN,Color.DODGERBLUE};
    private Color tileBaseColor = Color.PALEGREEN;

    private Semaphore semaphore = new Semaphore(0);
    private int outValue;

    @Override
    public void start(Stage primaryStage){
        //The main window
        window = primaryStage;
        window.setResizable(false);

        //Gets the current screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //TODO Scaling stuff

        //Calculates the board size based on the screen size
        if(screenSize.width < screenSize.height){
            boardSize = screenSize.width-55;
        }else {
            boardSize = screenSize.height-55;
        }

        //Makes a new instance of board, which is the game logic
        game = new Board(true,this, 2);

        //TODO GET PLAYERS via UI
        game.players.add(new Player("Ryan",game,Color.DODGERBLUE));
        game.players.add(new Player("Hannah",game,Color.RED));

        //The border pane covering the entire 'screen'
        screen = new BorderPane();

        //Updates the physical board
        updateGameBoardFX();

        //Shows player stats / inventory
        updatePlayerPaneFX(game.players.get(game.currentPlayer));

        //The main scene of the game
        scene = new Scene(screen,window.getWidth(),window.getHeight());

        //The Task and Thread that the game logic is run om
        Task task = new Task() {
            @Override
            protected Object call(){
                game.play();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    System.exit(0);
                    break;
            }
        });

        window.setScene(scene);
        window.show();
    }

    private void generateGameBoard(){
        int tileWidth  = (int)(3.0*boardSize/37.0);
        /*
        let board width be n
        let tile width be w

        9w + 2(5/3)w = n
        n = 37w/3
        w = 3n/37
         */
        int tileLength = (int) ((double)tileWidth*(5.0/3.0));

        gameBoard = new BorderPane();
        gameBoard.setMaxSize(boardSize,boardSize);

        //Bottom Row
        HBox bottom = new HBox();
        //Left Column
        VBox left = new VBox();
        left.setPadding(new Insets(0,-tileLength,0,0));
        //Right Column
        VBox right = new VBox();
        right.setPadding(new Insets(0,0,0,-tileLength));
        //Top Row
        HBox top = new HBox();

        for (int i = 0; i < 40; i++) {
            if(i == 0){
                int x = 10;
                bottom.getChildren().add(drawCorner(tileLength,x));
            }else if(i < 10){
                int x = 10-i; //Flips the bottom row, so it is oriented correctly
                bottom.getChildren().add(drawMid(tileWidth,tileLength,tileColors[game.tiles[x].groupName],x,Orientation.UP));
            }else if(i == 10){
                int x = 0;
                bottom.getChildren().add(drawCorner(tileLength,x));
            }else if(i < 20){
                int x = 30-i;//Flips the left column, so it is oriented correctly
                left.getChildren().add(drawMid(tileLength,tileWidth,tileColors[game.tiles[x].groupName],x,Orientation.LEFT));
            }else if(i == 20){
                top.getChildren().add(drawCorner(tileLength,i));
            }else if(i < 30){
                top.getChildren().add(drawMid(tileWidth,tileLength,tileColors[game.tiles[i].groupName],i,Orientation.DOWN));
            }else if(i == 30){
                top.getChildren().add(drawCorner(tileLength,i));
            }else {
                right.getChildren().add(drawMid(tileLength,tileWidth,tileColors[game.tiles[i].groupName],i,Orientation.RIGHT));
            }
        }

        gameBoard.setBottom(bottom);
        gameBoard.setLeft(left);
        gameBoard.setRight(right);
        gameBoard.setTop(top);
    }

    private StackPane drawCorner(int len,int i){
        StackPane base = new StackPane();

        Rectangle baseRec = new Rectangle(len,len,tileBaseColor);
        baseRec.setStroke(Color.BLACK);

        ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/"+game.tiles[i].name+".png")));
        image.setPreserveRatio(true);
        image.setFitHeight(len);

        //The pane, players are stored on
        VBox players = new VBox(5);
        players.setAlignment(Pos.CENTER);
        for (Player p:game.players) {
            if(p.getPosition() == i){
                players.getChildren().add(p.sprite);
            }
        }

        base.getChildren().addAll(baseRec,image,players);
        return base;
    }

    private StackPane drawMid(int wid, int height, Color c, int i, Orientation orientation){
        StackPane tempTile = new StackPane();

        //The base of the tile
        StackPane base = new StackPane();
        Rectangle baseRec = new Rectangle(wid,height,tileBaseColor);
        baseRec.setStroke(Color.BLACK);

        //Generates the tile based on orientation and type
        if((game.tiles[i].type == Tile.Type.SPECIAL) || (game.tiles[i].groupName == 002) || (game.tiles[i].groupName == 005)){
            String imageName;
            if(game.tiles[i].groupName == 002){//Railroads
                imageName = "Railroad";
            }else {
                imageName = game.tiles[i].name;
            }

            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/"+imageName+".png")));
            image.setPreserveRatio(true);
            image.setFitHeight(50);//TODO Make Dynamic

            switch (orientation){
                case UP:
                    image.setRotate(0);
                    break;
                case LEFT:
                    image.setRotate(90);
                    break;
                case DOWN:
                    image.setRotate(180);
                    break;
                case RIGHT:
                    image.setRotate(270);
                    break;
            }
            base.getChildren().addAll(baseRec,image);

        }else {
            //The colored rectangle
            Rectangle coloredRec = null;
            switch (orientation){
                case UP:
                    coloredRec = new Rectangle(wid-1,height/3,c);
                    base.setAlignment(Pos.TOP_CENTER);
                    break;
                case LEFT:
                    coloredRec = new Rectangle(wid/3,height-1,c);
                    base.setAlignment(Pos.CENTER_RIGHT);
                    break;
                case DOWN:
                    coloredRec = new Rectangle(wid-1,height/3,c);
                    base.setAlignment(Pos.BOTTOM_CENTER);
                    break;
                case RIGHT:
                    coloredRec = new Rectangle(wid/3,height-1,c);
                    base.setAlignment(Pos.CENTER_LEFT);
                    break;
            }
            base.getChildren().addAll(baseRec,coloredRec);
        }

        //TODO
        Text text = new Text(Integer.toString(game.tiles[i].location));
        text.setRotate(0);

        //The pane, players are stored on
        VBox players = new VBox(5);
        players.setAlignment(Pos.CENTER);
        for (Player p:game.players) {
            if(p.getPosition() == i){
                players.getChildren().add(p.sprite);
            }
        }

        tempTile.getChildren().addAll(base,text,players);
        tempTile.setOnMouseClicked(event -> {
            if(game.tiles[i].type == Tile.Type.PROPERTY){
                showPropertyFX((Property)game.tiles[i]);
            }
        });
        return tempTile;
    }

    public void updateGameBoard(){
        Platform.runLater(() -> {
            updateGameBoardFX();
        });
    }

    public void updateGameBoardFX(){
        generateGameBoard();
        screen.setCenter(gameBoard);
    }

    private VBox generatePlayerPane(Player p){
        VBox temp = new VBox(10);
        temp.setMinWidth(400);
        temp.setPadding(new Insets(30,0,0,10));
        temp.setMouseTransparent(false);

        //Name
        Text name = new Text(p.getName());

        //Balance
        Text balance = new Text("Balance: $"+p.getBalance());

        Text inventory = new Text("Inventory: ");

        temp.getChildren().addAll(name,balance,inventory);

        for (Property prop:p.getInventory()) {
            Text t = new Text("\t"+prop.getName());

            //FIXME
            //When the property is clicked, display its card
            t.setOnMouseClicked(event -> {
                showPropertyFX(prop);
            });

            temp.getChildren().add(t);
        }

        if(p.hasJailCard()){
            temp.getChildren().add(new Text("\t Get Out Of Jail Free Card x"+ p.getNumberOfJailCards()));
        }
        return temp;
    }

    public void updatePlayerPane(Player p){
        Platform.runLater(() ->{
            updatePlayerPaneFX(p);
        });
    }

    public void updatePlayerPaneFX(Player p){
        screen.setRight(generatePlayerPane(p));
    }

    public int prompt(String question, int[] buttonsToDisplay){
        if(semaphore.availablePermits() != 0){
            try{
                throw new Exception ("Invalid Permit Count");
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        Platform.runLater(() ->{
            promptFX(question,buttonsToDisplay);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return outValue;
    }

    public void promptFX(String question, int[] buttonsToDisplay){
        Text text = new Text(question);

        VBox pane = new VBox();
        pane.getChildren().add(text);
        pane.setAlignment(Pos.CENTER);

        Property prop = null;
        //To use if button text requires property values
        if(game.tiles[game.players.get(game.currentPlayer).position].type == Tile.Type.PROPERTY){
            prop = (Property) game.tiles[game.players.get(game.currentPlayer).position];
        }

        //Adds the corresponding buttons to those given in options[]
        for (int i = 0; i < buttonsToDisplay.length; i++) {
            switch (buttonsToDisplay[i]){
                case 0:
                    pane.getChildren().add(buttonBuilder("ROLL",0,pane));
                    break;
                case 1:
                    pane.getChildren().add(buttonBuilder("Do your time, and wait another turn",1,pane));
                    break;
                case 2:
                    pane.getChildren().add(buttonBuilder("Bribe a guard, and free yourself for $150",2,pane));
                    break;
                case 3:
                    pane.getChildren().add(buttonBuilder("Use your get out of jail free card",3,pane));
                    break;
                case 4:
                    pane.getChildren().add(buttonBuilder("Just chill here",4,pane));
                    break;
                case 5:
                    pane.getChildren().add(buttonBuilder("You can buy this property for " + prop.getCost(),5,pane));
                    break;
                case 6:
                    pane.getChildren().add(buttonBuilder("You can build a house here for " + prop.getCost(),6,pane));
                    break;
                case 7:
                    pane.getChildren().add(buttonBuilder("You can sell this property for " + prop.propertySalePrice(),7,pane));
                    break;
                case 8:
                    pane.getChildren().add(buttonBuilder("You can sell a house for " + prop.houseSalePrice(),8,pane));
                    break;
                case 9:
                    pane.getChildren().add(buttonBuilder("You can pay $150",9,pane));
                    break;
                case 10:
                    pane.getChildren().add(buttonBuilder("You can pay 10% of your wallet",10,pane));
                    break;
                case 11:
                    pane.getChildren().add(buttonBuilder("Trade Mode",11,pane));
                    break;
            }
        }
        gameBoard.setCenter(pane);
    }

    private Button buttonBuilder(String text, int returnValue, Pane parent){
        Button b = new Button(text);
        b.setDefaultButton(true);
        b.setOnAction(event ->{
            setOutValue(returnValue);
            parent.getChildren().clear();
            semaphore.release(1);
        });
        return b;
    }

    /**
    The value to be returned to prompt
     */
    private void setOutValue(int outValue) {
        this.outValue = outValue;
    }

    public void movePlayer(Player p){}//TODO Player token animation

    public void showProperty(Property p){
        if(semaphore.availablePermits() != 0) {
            try {
                throw new Exception("Invalid Permit Count");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        Platform.runLater(() -> {
            showPropertyFX(p);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showPropertyFX(Property p){
        int wid = 350;
        int height = 500;

        //The window
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);//No window border
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);//Can't access below windows

        //The background of the card
        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        //A button which closes the card, and returns to the game
        Button close = new Button("Return to game");
        close.setOnAction(event -> {
            popup.close();
            semaphore.release();
        });

        //A vbox containing the column of nodes
        VBox vBox = new VBox(15);
        vBox.setMaxSize(wid,height);
        vBox.setMinSize(wid,height);


        if(p.groupName == 002){//Railroad
            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/Railroad.png")));
            image.setPreserveRatio(true);
            image.setFitHeight(wid/2);//TODO Make Dynamic

            Text name = new Text(p.name);

            Text text = new Text("Rent $20"+
                    "\nIf 2 Railroads Are Owned: $50"+
                    "\nIf 3 Railroads Are Owned: $100"+
                    "\nIf 4 Railroads Are Owned: $200"+
                    "\nMortgage Value $"+p.propertySalePrice());

            vBox.getChildren().addAll(image,name,text,close);

        }else if(p.groupName == 005){//Utilities
            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/"+p.name+".png")));
            image.setPreserveRatio(true);
            image.setFitHeight(wid/2);//TODO Make Dynamic

            Text name = new Text(p.name);

            Text text = new Text("If ONE Utility is owned," +
                    "\nRent is 4x number shown on the dice" +
                    "\nIf BOTH Utilities are owned," +
                    "\nRent is 10x the amount shown on the dice."+
                    "\nMortgage Value $"+p.propertySalePrice());

            vBox.getChildren().addAll(image,name,text,close);
        }else{
            StackPane coloredStack = new StackPane();
            Rectangle coloredRec = new Rectangle(wid,height/4,tileColors[p.groupName]);

            VBox textColumn = new VBox(10);
            Text header = new Text("TITLE DEED");
            Text name = new Text(p.name);
            textColumn.getChildren().addAll(header,name);
            textColumn.setAlignment(Pos.CENTER);

            coloredStack.getChildren().addAll(coloredRec,textColumn);

            Text values = new Text(
                    "Rent $"+p.getRents()[0]+
                            "\nWith 1 house :$"+p.getRents()[1]+
                            "\nWith 2 houses :$"+p.getRents()[2]+
                            "\nWith 3 houses :$"+p.getRents()[3]+
                            "\nWith 4 houses :$"+p.getRents()[4]+
                            "\nWith 1 hotel :$"+p.getRents()[5]+
                            "\nMortgage Value $"+p.propertySalePrice()
            );
            values.setTextAlignment(TextAlignment.CENTER);

            vBox.getChildren().addAll(coloredStack,values,close);


        }

        vBox.setAlignment(Pos.TOP_CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(card,vBox);

        Scene scene = new Scene(stackPane,wid,height);

        popup.setScene(scene);
        popup.showAndWait();
    }

    public void message(String message){
        if(semaphore.availablePermits() != 0){
            try{
                throw new Exception ("Invalid Permit Count");
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        Platform.runLater(() ->{
            messageFX(message);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void messageFX(String message){
        int wid = 400;
        int height = 150;

        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);

        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        Text text = new Text(message);
        text.setTextAlignment(TextAlignment.CENTER);

        Button close = new Button("Return to game");
        close.setOnAction(event -> {
            popup.close();
            semaphore.release(1);
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(text,close);
        vBox.setAlignment(Pos.TOP_CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(card,vBox);

        Scene scene = new Scene(stackPane,wid,height);

        scene.setOnKeyPressed(event -> {
            popup.close();
            semaphore.release(1);
        });

        popup.setScene(scene);
        popup.showAndWait();
    }

    public void showChance(String title, String message){
        if(semaphore.availablePermits() != 0){
            try{
                throw new Exception ("Invalid Permit Count");
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        Platform.runLater(() ->{
            showChanceFX(title,message);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showChanceFX(String title, String message){
        int wid = 400;
        int height = 150;

        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);

        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        Text ti = new Text(title);
        ti.setTextAlignment(TextAlignment.CENTER);

        Text mes = new Text(message);
        mes.setTextAlignment(TextAlignment.CENTER);

        Button close = new Button("Return to game");
        close.setOnAction(event -> {
            popup.close();
            semaphore.release(1);
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(ti,mes,close);
        vBox.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(card,vBox);

        Scene scene = new Scene(stackPane,wid,height);

        scene.setOnKeyPressed(event -> {
            popup.close();
            semaphore.release(1);
        });

        popup.setScene(scene);
        popup.showAndWait();
    }

    private enum Orientation {UP,DOWN,LEFT,RIGHT}
}