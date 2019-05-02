import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.concurrent.Semaphore;

public class Display extends Application implements GameDisplay{
    private Stage window;
    private BorderPane screen;
    private BorderPane gameBoard;

    private Board game;

    private int BOARD_SIZE;
    private int TILE_LENGTH;
    private int TILE_WIDTH;

    private Color tileColors[] = new Color[]{null,Color.SADDLEBROWN,null,Color.CORNFLOWERBLUE,Color.MAGENTA,null,Color.ORANGE,Color.RED,Color.YELLOW,Color.GREEN,Color.DODGERBLUE};
    private Color tileBaseColor = Color.PALEGREEN;

    private Semaphore semaphore = new Semaphore(0);
    private int outValue;
    private String outString;

    @Override
    public void start(Stage primaryStage){
        //The main window
        window = primaryStage;
        window.setTitle("Monopoly");
        window.setResizable(false);
        window.toFront();

        //Initializes the scale of the window/components
        initializeSizes();

        //Start the main menu
        startMainMenu();

        window.show();
    }

    private void initializeSizes(){
        //Gets the current screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //TODO Scaling stuff

        //Calculates the board size based on the screen size
        if(screenSize.width < screenSize.height){
            BOARD_SIZE = screenSize.width-155;
        }else {
            BOARD_SIZE = screenSize.height-155;
        }

        /*
        let board width be n
        let tile width be w

        9w + 2(5/3)w = n
        n = 37w/3
        w = 3n/37
         */
        TILE_WIDTH  = (int)(3.0* BOARD_SIZE /37.0);

        //The L is 5/3 of the W
        TILE_LENGTH = (int) ((double)TILE_WIDTH*(5.0/3.0));


    }

    private void startMainMenu(){
        //The border pane covering the entire 'screen'
        BorderPane mainMenu = new BorderPane();
        mainMenu.setMaxSize(BOARD_SIZE, BOARD_SIZE);
        mainMenu.setMinSize(BOARD_SIZE, BOARD_SIZE);

        StackPane stack = new StackPane();

        ImageView background = new ImageView(new Image("Images/Background.jpg"));
        background.setPreserveRatio(true);
        background.setFitHeight(BOARD_SIZE);

        stack.getChildren().addAll(background, mainMenu);

        VBox centre = new VBox(10);

        Button newGame = new Button("New Game");
        newGame.setOnAction(event -> {
            //Makes a new instance of board, which is the game logic
            game = new Board(true,this);
            startPlayerCreator();
            startGame();

        });

        Button loadGame = new Button("Load Game");
        loadGame.setOnAction(event -> {
            System.out.println("Load Game");
            //game = new Board(false,this);
            //startGame();
            System.out.println("TODO");
        });

        centre.getChildren().addAll(newGame,loadGame);
        centre.setAlignment(Pos.CENTER_LEFT);

        mainMenu.setCenter(centre);

        Scene scene = new Scene(stack);
        window.setScene(scene);
    }

    private void initializeToolbar(){
        HBox hBox = new HBox(10);

        Button save_game = new Button("Save Game");
        save_game.setOnAction(event -> {
            System.out.println("Load Clicked");
            game.saveBoard();
        });

        hBox.getChildren().addAll(save_game);
        hBox.setAlignment(Pos.CENTER);

        screen.setTop(hBox);
    }

    private void startPlayerCreator(){
        //TODO GET PLAYERS via UI

        int spriteSize = 40;

        //game.players.add(new NPC("AI",game,"car",spriteSize));
        game.players.add(new Player("Ryan",game,"dog",spriteSize));
        game.players.add(new Player("Zack",game,"hat",spriteSize));
        //game.players.add(new NPC("Computer",game,"hat",spriteSize));

        game.setNumPlayers();
    }//TODO

    private void startGame(){
        //The border pane covering the entire 'screen'
        screen = new BorderPane();

        //Updates the physical board
        updateGameBoardFX();

        //Shows player stats / inventory
        updatePlayerPaneFX(game.getCurrentPlayer());

        //The toolbar
        initializeToolbar();

        //The main scene of the game
        Scene scene = new Scene(screen);

        //The Task and Thread that the game logic is run on
        Task task = new Task() {
            @Override
            protected Object call(){
                game.play();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        window.setScene(scene);
    }

    private void generateGameBoard(){
        gameBoard = new BorderPane();
        gameBoard.setMaxSize(BOARD_SIZE, BOARD_SIZE);

        //Bottom Row
        HBox bottom = new HBox();
        //Left Column
        VBox left = new VBox();
        //Right Column
        VBox right = new VBox();
        //Top Row
        HBox top = new HBox();

        for (int i = 0; i < 40; i++) {
            if(i == 0){
                int x = 10;
                bottom.getChildren().add(drawCorner(TILE_LENGTH,x));
            }else if(i < 10){
                int x = 10-i; //Flips the bottom row, so it is oriented correctly
                bottom.getChildren().add(drawMid(TILE_WIDTH,TILE_LENGTH,tileColors[game.tiles[x].groupName],x,Orientation.UP));
            }else if(i == 10){
                int x = 0;
                bottom.getChildren().add(drawCorner(TILE_LENGTH,x));
            }else if(i < 20){
                int x = 30-i;//Flips the left column, so it is oriented correctly
                left.getChildren().add(drawMid(TILE_LENGTH,TILE_WIDTH,tileColors[game.tiles[x].groupName],x,Orientation.LEFT));
            }else if(i == 20){
                top.getChildren().add(drawCorner(TILE_LENGTH,i));
            }else if(i < 30){
                top.getChildren().add(drawMid(TILE_WIDTH,TILE_LENGTH,tileColors[game.tiles[i].groupName],i,Orientation.DOWN));
            }else if(i == 30){
                top.getChildren().add(drawCorner(TILE_LENGTH,i));
            }else {
                right.getChildren().add(drawMid(TILE_LENGTH,TILE_WIDTH,tileColors[game.tiles[i].groupName],i,Orientation.RIGHT));
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

            switch (orientation){
                case UP:
                    image.setRotate(0);
                    image.setFitHeight(wid*3/5);
                    break;
                case LEFT:
                    image.setRotate(90);
                    image.setFitHeight(height*3/5);
                    break;
                case DOWN:
                    image.setRotate(180);
                    image.setFitHeight(wid*3/5);
                    break;
                case RIGHT:
                    image.setRotate(270);
                    image.setFitHeight(height*3/5);
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

        //TODO Text rotation
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
                showPropertyFX((Property)game.tiles[i],false);
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

    private ScrollPane generatePlayerPane(Player p){
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinWidth(BOARD_SIZE /2.5 + 30);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox temp = new VBox(10);
        temp.setMinWidth((double) BOARD_SIZE /2.5);
        temp.setPadding(new Insets(30,0,0,10));
        temp.setMouseTransparent(false);

        //Name
        Text name = new Text(p.getName());

        //Balance
        Text balance = new Text("Balance: $"+p.getBalance());

        Text inventory = new Text("Inventory: ");

        temp.getChildren().addAll(name,balance,inventory);

        p.getInventory().sort((o1, o2) -> {
            if(o1.groupName < o2.groupName){
                return -1;
            }else if(o1.groupName > o2.groupName){
                return 1;
            }else {
                return 0;
            }


        });

        for (Property prop:p.getInventory()) {
            temp.getChildren().add(generatePropertyView(prop));
        }

        if(p.hasJailCard()){
            temp.getChildren().add(new Text("\t Get Out Of Jail Free Card x"+ p.getNumberOfJailCards()));
        }

        scrollPane.setContent(temp);

        return scrollPane;
    }

    private StackPane generatePropertyView(Property prop){
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        Rectangle back = new Rectangle((double) BOARD_SIZE /2.5 - 40,30, tileColors[prop.groupName]);
        back.setStroke(Color.BLACK);

        Text name = new Text(prop.getName());

        stackPane.getChildren().addAll(back,name);

        //When the property is clicked, display its card
        stackPane.setOnMouseClicked(event -> {
            showPropertyFX(prop,false);
        });


    return stackPane;
    }

    public void updatePlayerPane(Player p){
        //Don't show visual for NPC
        if(p.getType() == Player.Type.NPC){
            return;
        }

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

        int t = outValue;
        outValue = -1;
        return t;
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
                    pane.getChildren().add(buttonBuilder("End Turn",4,pane));
                    break;
                case 5:
                    pane.getChildren().add(buttonBuilder("You can buy this property for $" + prop.getCost(),5,pane));
                    break;
                case 6:
                    pane.getChildren().add(buttonBuilder("You can build a house here for $" + prop.getCost(),6,pane));
                    break;
                case 7:
                    pane.getChildren().add(buttonBuilder("You can sell this property for $" + prop.propertySalePrice(),7,pane));
                    break;
                case 8:
                    pane.getChildren().add(buttonBuilder("You can sell a house for $" + prop.houseSalePrice(),8,pane));
                    break;
                case 9:
                    pane.getChildren().add(buttonBuilder("You can pay $150",9,pane));
                    break;
                case 10:
                    pane.getChildren().add(buttonBuilder("You can pay 10% of your wallet",10,pane));
                    break;
                case 11:
                    pane.getChildren().add(buttonBuilder("Property Manager",11,pane));
                    break;
            }
        }
        gameBoard.setCenter(pane);
    }

    @Override
    public String propertyManagerPrompt(String[] buttonsToDisplay) {
        if(semaphore.availablePermits() != 0){
            try{
                throw new Exception ("Invalid Permit Count");
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        if(buttonsToDisplay.length == 0){
            return "-1:-1";
        }

        Platform.runLater(() ->{
            propertyManagerPromptFX(buttonsToDisplay);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String s = outString;
        outString = "";
        return s;
    }

    private void propertyManagerPromptFX(String[] buttonsToDisplay){
        Text text = new Text("Property Manager");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


        VBox pane = new VBox(15);
        pane.getChildren().add(text);
        pane.setAlignment(Pos.CENTER);

        int currentGroup = 001;
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        for (String s:buttonsToDisplay) {
            int groupName = game.tiles[Integer.parseInt(s.split(":")[0])].groupName;

            if(groupName == currentGroup){
                hBox.getChildren().add(propertyManagerGroupBuilder(s,pane));
            }else {
                pane.getChildren().add(hBox);
                currentGroup++;
                hBox = new HBox(10);
                hBox.setAlignment(Pos.CENTER);
                hBox.getChildren().add(propertyManagerGroupBuilder(s,pane));
            }
        }

        Button backToGame = new Button("Back To Game");
        backToGame.setOnAction(event -> {
            setOutString("-1:-1");
            pane.getChildren().clear();
            semaphore.release(1);
        });
        pane.getChildren().add(backToGame);

        scrollPane.setContent(pane);

        gameBoard.setCenter(scrollPane);
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

    private StackPane propertyManagerGroupBuilder(String s, Pane parent){//TODO CLEAN ME / FIXME (House not functional)
        //The input parsed into 4 ints
        int[] parsed = new int[4];
        //The initial input
        String[] split = s.split(":");
        for (int i = 0; i < 4; i++) {
            parsed[i] = Integer.parseInt(split[i]);
        }

        Property prop = (Property)game.tiles[parsed[0]];

        StackPane stackPane = new StackPane();

        //The background rectangle
        Rectangle back = new Rectangle(150,100,tileColors[prop.groupName]);
        back.setStroke(Color.BLACK);

        Text name = new Text(prop.name);

        Button buyHouse = new Button("Buy a house for $"+prop.getCost());
        buyHouse.setOnAction(event -> {
            setOutString(parsed[0]+":6");
            parent.getChildren().clear();
            semaphore.release(1);

        });

        Button sellHouse = new Button("Sell a house for $"+prop.houseSalePrice());
        sellHouse.setOnAction(event -> {
            setOutString(parsed[0]+":8");
            parent.getChildren().clear();
            semaphore.release(1);
        });

        Button sellProperty = new Button("Sell Property for $"+prop.propertySalePrice());
        sellProperty.setOnAction(event -> {
            setOutString(parsed[0]+":7");
            parent.getChildren().clear();
            semaphore.release(1);
        });

        VBox column = new VBox();
        column.getChildren().add(name);

        //If they can buy a house
        if(parsed[1] != -1){
            column.getChildren().add(buyHouse);
        }

        //If they can sell a house
        if(parsed[3] != -1){
            column.getChildren().add(sellHouse);
        }

        //They can always sell a property they own
        column.getChildren().addAll(sellProperty);
        column.setAlignment(Pos.CENTER);


        stackPane.getChildren().addAll(back,column);
        stackPane.setAlignment(Pos.CENTER);
        return stackPane;
    }

    /**
    The value to be returned to prompt
     */
    private void setOutValue(int outValue) {
        this.outValue = outValue;
    }

    private void setOutString(String outString){this.outString = outString;}

    public void movePlayer(Player p){}//TODO Player token animation

    public void showProperty(Property p, boolean show){
        //Don't show visual for NPC
        if(!show){
            return;
        }

        if(semaphore.availablePermits() != 0) {
            try {
                throw new Exception("Invalid Permit Count");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        Platform.runLater(() -> {
            showPropertyFX(p, true);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showPropertyFX(Property p, boolean releaseSemaphore){
        int wid = 350;
        int height = 500;

        //The window
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);//No window border
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);//Can't access below windows

        //The background of the card
        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        //The owner
        String s;
        if(p.hasOwner()){
            s = "Owner: "+p.getOwner().getName();
        }else {
            s = "Available";
        }

        Text owner = new Text(s);

        //A button which closes the card, and returns to the game
        Button close = new Button("Return to game");
        close.setOnAction(event -> {
            popup.close();

            if(releaseSemaphore){
                semaphore.release();
            }
        });
        close.setFont(Font.font("Futura",12));

        //A vbox containing the column of nodes
        VBox vBox = new VBox(15);
        vBox.setMaxSize(wid,height);
        vBox.setMinSize(wid,height);


        if(p.groupName == 002){//Railroad
            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/Railroad.png")));
            image.setPreserveRatio(true);
            image.setFitHeight(wid/2);

            Text name = new Text(p.name);

            Text text = new Text("Rent $20"+
                    "\nIf 2 Railroads Are Owned: $50"+
                    "\nIf 3 Railroads Are Owned: $100"+
                    "\nIf 4 Railroads Are Owned: $200"+
                    "\nMortgage Value $"+p.propertySalePrice());

            vBox.getChildren().addAll(image,name,owner,text,close);

        }else if(p.groupName == 005){//Utilities
            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/"+p.name+".png")));
            image.setPreserveRatio(true);
            image.setFitHeight(wid/2);

            Text name = new Text(p.name);

            Text text = new Text("If ONE Utility is owned," +
                    "\nRent is 4x number shown on the dice" +
                    "\nIf BOTH Utilities are owned," +
                    "\nRent is 10x the amount shown on the dice."+
                    "\nMortgage Value $"+p.propertySalePrice());

            vBox.getChildren().addAll(image,name,owner,text,close);
        }else{
            StackPane coloredStack = new StackPane();
            Rectangle coloredRec = new Rectangle(wid,height/4,tileColors[p.groupName]);

            VBox textColumn = new VBox(10);
            Text header = new Text("TITLE DEED");
            Text name = new Text(p.name);
            textColumn.getChildren().addAll(header,name);
            textColumn.setAlignment(Pos.CENTER);
            header.setFont(Font.font("Copperplate",15));
            name.setFont(Font.font("Copperplate",30));

            coloredStack.getChildren().addAll(coloredRec,textColumn);

            Text values = new Text(
                    "Rent $"+p.getRents()[0]+
                            "\nWith 1 house: $"+p.getRents()[1]+
                            "\nWith 2 houses: $"+p.getRents()[2]+
                            "\nWith 3 houses: $"+p.getRents()[3]+
                            "\nWith 4 houses: $"+p.getRents()[4]+
                            "\nWith 1 hotel: $"+p.getRents()[5]+
                            "\nMortgage Value: $"+p.propertySalePrice()
            );
            values.setTextAlignment(TextAlignment.CENTER);
            values.setFont(Font.font("Futura",15));

            vBox.getChildren().addAll(coloredStack,owner,values,close);
        }

        vBox.setAlignment(Pos.TOP_CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(card,vBox);

        Scene scene = new Scene(stackPane,wid,height);

        popup.setScene(scene);
        popup.showAndWait();
    }

    public void message(String message,boolean show){
        //Don't show visual for NPC
        if(!show){
            return;
        }

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
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);

        Text text = new Text(message);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("Futura",25));

        //Sets the width to that of the text
        int wid =  getFontWidth(message,text.getFont())+ 40;
        int height = 150;

        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        Button close = new Button("Return to game");
        close.setOnAction(event -> {
            popup.close();
            semaphore.release(1);
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(text,close);
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

    public void showChance(String title, String message,boolean show){
        //Don't show visual for NPC
        if(!show){
            return;
        }

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
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);


        Text ti = new Text(title);
        ti.setTextAlignment(TextAlignment.CENTER);

        Text mes = new Text(message);
        mes.setTextAlignment(TextAlignment.CENTER);

        int wid;
        if(message.length() > title.length()){
            wid = getFontWidth(message,mes.getFont())+ 40;
        }else {
            wid = getFontWidth(title,ti.getFont())+ 40;
        }
        int height = 150;

        Rectangle card = new Rectangle(wid,height,Color.WHITE);

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

    private int getFontWidth(String s, Font f){
        return (int)com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(s,f);
    }

    private enum Orientation {UP,DOWN,LEFT,RIGHT}
}