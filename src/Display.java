import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class Display extends Application implements GameDisplay{
    private Stage window;
    private BorderPane screen;
    private BorderPane gameBoard;

    private Board game;

    private int BOARD_SIZE;
    private int TILE_LENGTH;
    private int TILE_WIDTH;

    private Color tileColors[] = new Color[]{null,Color.SADDLEBROWN,Color.LIGHTGREY,Color.LIGHTBLUE,Color.DEEPPINK,null,Color.ORANGE,Color.RED,Color.YELLOW,Color.GREEN,Color.MEDIUMBLUE};
    private Color tileBaseColor = Color.PALEGREEN;
    private Color houseColour[] = new Color[]{Color.DARKGREEN, Color.DARKRED};

    private ArrayList<String> tokens;

    private Semaphore semaphore = new Semaphore(0);
    private int outValue;
    private String outString;

    private Font defaultFont=Font.font("Futura",15);

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

        Text newGame = new Text("New Game");
        newGame.setFont(Font.font("Futura", 30));
        newGame.setFill(Color.BLACK);
        newGame.setOnMouseClicked(event -> {
            startPlayerCreatorPane();
        });

        newGame.setOnMouseEntered(event -> {
            newGame.setFill(Color.BLUE);
        });

        newGame.setOnMouseExited(event -> {
            newGame.setFill(Color.BLACK);
        });

        Text loadGame = new Text("Load Game");
        loadGame.setFont(Font.font("Futura", 30));
        loadGame.setFill(Color.BLACK);
        loadGame.setOnMouseClicked(event -> {

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("GAME files (*.game)", "*.game");
            fileChooser.getExtensionFilters().add(extension);
            File file = fileChooser.showOpenDialog(window);

            System.out.println("Load Game");
            if(file != null){
                game = new Board(false,this, file);
            }

            System.out.println("TODO");

        });

        loadGame.setOnMouseEntered(event -> {
            loadGame.setFill(Color.BLUE);
        });

        loadGame.setOnMouseExited(event -> {
            loadGame.setFill(Color.BLACK);
        });

        centre.getChildren().addAll(newGame,loadGame);
        centre.setAlignment(Pos.CENTER_LEFT);

        mainMenu.setCenter(centre);

        Scene scene = new Scene(stack);
        window.setScene(scene);
    }

    private void initializeToolbar(){
        HBox hBox = new HBox(10);

        Button saveGame = new Button("Save Game");
        saveGame.setFont(defaultFont);
        saveGame.setOnAction(event -> {
            System.out.println("Load Clicked");
            game.saveBoard();
        });

        Button returnToMain = new Button("Main Menu");
        returnToMain.setFont(defaultFont);
        returnToMain.setOnAction(event -> {
            startMainMenu();
        });

        hBox.getChildren().addAll(returnToMain,saveGame);
        hBox.setAlignment(Pos.CENTER);

        screen.setTop(hBox);
    }

    private void startPlayerCreatorPane(){
        //Makes a new instance of game
        game = new Board(true,this, null);

        //All the possible tokens
        tokens =  new ArrayList<String>();
        tokens.clear();
        tokens.addAll(Arrays.asList("car","dog","hat","ship","trolly"));

        //The stack of background and players
        StackPane playerMaker = new StackPane();
        playerMaker.setMaxSize(BOARD_SIZE*1.5, BOARD_SIZE);
        playerMaker.setMinSize(BOARD_SIZE*1.5, BOARD_SIZE);

        HBox playersHBox = new HBox(15);
        playersHBox.setAlignment(Pos.CENTER);

        //The original number of players cannot be less than 2
        game.numPlayers = 2;
        //Adds the first 2 players
        for (int i = 0; i < game.numPlayers; i++) {
            playersHBox.getChildren().addAll(playerCreationBox(false));
        }

        //Sets the background image
        ImageView background = new ImageView(new Image(Display.class.getResourceAsStream("Images/PlayerMakerBackground.jpg")));
        background.setFitHeight(BOARD_SIZE);
        background.setFitWidth(BOARD_SIZE*1.5);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        //Play button (when clicked checks all the inputted data for errors)
        Button play = new Button("PLAY");
        play.setFont(defaultFont);
        play.setOnAction(event -> {
            for (Object o:playersHBox.getChildren().toArray()) {
                PlayerCreatorPane temp = (PlayerCreatorPane) o;

                //Ensure names are not empty
                if(temp.name.isEmpty()){
                    messageFX("Invalid Input. Names may not be empty",false);

                    //Clears the players so they when 1 is rejected, it must start again
                    game.players.clear();
                    return;
                }

                //Ensures the name is not an empty string of spaces
                boolean isNotSpace = false;
                for (char c:temp.name.toCharArray()){
                    if(c != ' '){
                        isNotSpace = true;
                    }
                }
                if(!isNotSpace){
                    messageFX("Invalid Input. Names may not be empty",false);

                    //Clears the players so they when 1 is rejected, it must start again
                    game.players.clear();
                    return;
                }


                //Ensures names are alphanum(So they can be saved in csv files)
                if(!temp.name.matches("[a-zA-Z0-9]+")){
                    messageFX("Invalid Input. Names must be alphanumeric",false);

                    //Clears the players so they when 1 is rejected, it must start again
                    game.players.clear();
                    return;
                }

                //Ensures no names are duplicated
                for (Player p:game.players) {
                    if(temp.name.equalsIgnoreCase(p.getName())){
                        messageFX("Invalid Input. Names may not be repeated",false);

                        //Clears the players so they when 1 is rejected, it must start again
                        game.players.clear();
                        return;
                    }
                }

                //Ensures a token was selected
                if(temp.token.isEmpty()){
                    messageFX("You must pick a token",false);

                    //Clears the players so they when 1 is rejected, it must start again
                    game.players.clear();
                    return;
                }

                //Ensures a type was selected
                if(temp.type == null){
                    messageFX("You must pick a player type",false);

                    //Clears the players so they when 1 is rejected, it must start again
                    game.players.clear();
                    return;
                }

                //Ensures at least 1 player is a human
                boolean PC = false;
                for (Player p:game.players) {
                    if (p.type == Player.Type.PC) {
                        PC = true;
                    }
                }
                if(temp.type == Player.Type.PC){
                    PC = true;
                }
                if(!PC){
                    messageFX("You must have at least 1 playable character", false);

                    //Clears the players so they when 1 is rejected, it must start again
                    game.players.clear();
                    break;
                }

                int spriteSize = ((TILE_LENGTH-(5*game.numPlayers))/game.numPlayers);

                //Adds the player
                if(temp.type == Player.Type.PC){
                    game.players.add(new Player(temp.name,game,temp.token,spriteSize));
                }else {
                    game.players.add(new NPC(temp.name,game,temp.token,spriteSize));
                }
            }

            if(game.players.size() == 0){
                return;
            }

            startGame();
        });

        Button addPlayer = new Button("ADD PLAYER");
        addPlayer.setFont(defaultFont);
        addPlayer.setOnAction(event -> {
            if(game.numPlayers < 5){
                playersHBox.getChildren().add(playerCreationBox(true));
                game.numPlayers++;
            }else{
                messageFX("You cannot have more than 5 players",false);
            }
        });

        Button back = new Button("BACK");
        back.setFont(defaultFont);
        back.setOnAction(event -> {
            startMainMenu();
        });

        Button dev = new Button("Dev Players");
        dev.setFont(defaultFont);
        dev.setOnAction(event -> {
            game.players.add(new Player("DevA",game,"hat",40));
            game.players.add(new Player("DevB",game,"trolly",40));
            startGame();
        });



        buttons.getChildren().addAll(dev,back,addPlayer,play);


        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(playersHBox,buttons);

        playerMaker.getChildren().addAll(background,vBox);

        Scene scene = new Scene(playerMaker);
        window.setScene(scene);
        //startGame();
    }

    private PlayerCreatorPane playerCreationBox(boolean showRemove){
        PlayerCreatorPane playerCreatorPane = new PlayerCreatorPane();

        double wid = BOARD_SIZE/3;
        double height = BOARD_SIZE/2.5;

        //Background Colored rectangle
        Rectangle back = new Rectangle(wid,height,Color.DARKTURQUOISE);
        back.setStroke(Color.DARKGREEN);
        back.setStrokeWidth(2);
        back.setArcHeight(15);
        back.setArcWidth(15);

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        //Radio Buttons which control the player type
        VBox playerType = new VBox();
        playerType.setAlignment(Pos.CENTER);
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton radioButton = new RadioButton("HUMAN PLAYER");
        radioButton.setFont(defaultFont);
        radioButton.setUserData(Player.Type.PC);
        radioButton.setToggleGroup(toggleGroup);
        RadioButton radioButton2 = new RadioButton("COMPUTER PLAYER");
        radioButton2.setFont(defaultFont);
        radioButton2.setUserData(Player.Type.NPC);
        radioButton2.setToggleGroup(toggleGroup);
        playerType.getChildren().addAll(radioButton,radioButton2);

        //When the buttons are toggled, set the type
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(toggleGroup.getSelectedToggle() != null){
                playerCreatorPane.type = (Player.Type)toggleGroup.getSelectedToggle().getUserData();
            }
        });

        //text field for username input
        TextField userName = new TextField();
        userName.setFont(defaultFont);
        userName.setAlignment(Pos.CENTER);
        userName.setPromptText("Enter A Name");
        userName.setMaxWidth(wid-10);

        //Sets the username when the text field is updated
        userName.setOnKeyReleased(event -> {
            playerCreatorPane.name = userName.getText();
        });

        StackPane image = new StackPane();
        image.setPrefSize(100,100);

        //The colored rectangle back of the image
        Rectangle baseImage = new Rectangle(100,100,Color.MEDIUMTURQUOISE);
        baseImage.setStroke(Color.DARKGREEN);
        baseImage.setStrokeWidth(2);
        baseImage.setArcHeight(15);
        baseImage.setArcWidth(15);

        //The token image
        ImageView tokenImage = new ImageView(new Image(Display.class.getResourceAsStream("Images/clickMe.png")));
        tokenImage.setPreserveRatio(true);
        tokenImage.setFitHeight(80);
        tokenImage.setMouseTransparent(true);

        image.getChildren().addAll(baseImage,tokenImage);

        //Highlights token image
        baseImage.setOnMouseEntered(event -> {
            baseImage.setStroke(Color.PALEGREEN);
            baseImage.setFill(Color.PALETURQUOISE);
        });

        //De-highlights token image
        baseImage.setOnMouseExited(event -> {
            baseImage.setStroke(Color.DARKGREEN);
            baseImage.setFill(Color.MEDIUMTURQUOISE);
        });

        //Opens token picker when clicked
        baseImage.setOnMouseClicked(event -> {
            //If the user had previously chosen a token and wants to replace it, put that one back in options
            if(!playerCreatorPane.token.isEmpty()) {
                tokens.add(playerCreatorPane.token);
            }

            tokenPicker();

            //Sets the new token image
            playerCreatorPane.token = outString;
            tokenImage.setImage(new Image(Display.class.getResourceAsStream("Images/"+playerCreatorPane.token+".png")));

            //Removes the token from the options
            tokens.remove(playerCreatorPane.token);

            outString = "";
        });
        vBox.getChildren().addAll(playerType, userName,image);

        //If the player is 3-5 it can be removed
        if(showRemove){
            Button removePlayer = new Button("REMOVE PLAYER");
            removePlayer.setFont(defaultFont);
            removePlayer.setDefaultButton(true);
            removePlayer.setOnAction(event -> {
                if(!playerCreatorPane.token.isEmpty()){
                    tokens.add(playerCreatorPane.token);
                }

                if(playerCreatorPane.getParent() instanceof HBox){
                    ((HBox)playerCreatorPane.getParent()).getChildren().remove(playerCreatorPane);
                    game.numPlayers--;
                }

            });

            vBox.getChildren().add(removePlayer);
        }

        playerCreatorPane.getChildren().addAll(back,vBox);

        return playerCreatorPane;
    }

    public void tokenPicker(){
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);//Can't access below windows
        stage.setOnCloseRequest(Event::consume);

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(Color.DARKTURQUOISE,CornerRadii.EMPTY,Insets.EMPTY)));

        Text text = new Text("Pick a token");

        //Displays all the available tokens
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        for (String s:tokens) {
            StackPane image = new StackPane();
            image.setPrefSize(100,100);

            Rectangle baseImage = new Rectangle(100,100,Color.MEDIUMTURQUOISE);
            baseImage.setStroke(Color.DARKGREEN);
            baseImage.setStrokeWidth(2);
            baseImage.setArcHeight(15);
            baseImage.setArcWidth(15);

            ImageView tokenImage = new ImageView(new Image(Display.class.getResourceAsStream("Images/"+s+".png")));
            tokenImage.setPreserveRatio(true);
            tokenImage.setFitHeight(80);
            tokenImage.setMouseTransparent(true);

            image.getChildren().addAll(baseImage,tokenImage);

            baseImage.setOnMouseEntered(event -> {
                baseImage.setStroke(Color.PALEGREEN);
                baseImage.setFill(Color.PALETURQUOISE);
            });

            baseImage.setOnMouseExited(event -> {
                baseImage.setStroke(Color.DARKGREEN);
                baseImage.setFill(Color.MEDIUMTURQUOISE);
            });

            baseImage.setOnMouseClicked(event -> {
                outString = s;
                stage.close();
            });

            hBox.getChildren().add(image);
        }

        vBox.getChildren().addAll(text,hBox);


        Scene scene = new Scene(vBox, 700,150);
        stage.setScene(scene);
        stage.showAndWait();
    }

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
        screen.setBackground(new Background(new BackgroundFill(tileBaseColor,CornerRadii.EMPTY, Insets.EMPTY)));
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
            Property p = (Property) game.tiles[i];

            int houseSize = TILE_WIDTH/10;
            int hotelSize = TILE_WIDTH/2;

            StackPane coloredStack = new StackPane();
            //The houses
            HBox HHouses = new HBox(3);
            VBox VHouses = new VBox(3);

            if(p.getNumberHouses() == 5){
                HHouses.getChildren().add(new Rectangle(hotelSize,hotelSize/1.5,houseColour[1]));
                VHouses.getChildren().add(new Rectangle(hotelSize/1.5,hotelSize,houseColour[1]));
            }else {
                for (int j = 0; j < p.getNumberHouses(); j++) {
                    HHouses.getChildren().add(new Circle(houseSize,houseColour[0]));
                    VHouses.getChildren().add(new Circle(houseSize,houseColour[0]));
                }
            }


            //The colored rectangle
            Rectangle coloredRec;
            switch (orientation){
                case UP:
                    coloredRec = new Rectangle(wid-1,height/3,c);
                    base.setAlignment(Pos.TOP_CENTER);

                    HHouses.setAlignment(Pos.TOP_CENTER);

                    coloredStack.getChildren().addAll(coloredRec,HHouses);
                    coloredStack.setAlignment(Pos.TOP_CENTER);
                    break;
                case LEFT:
                    coloredRec = new Rectangle(wid/3,height-1,c);
                    base.setAlignment(Pos.CENTER_RIGHT);

                    VHouses.setAlignment(Pos.CENTER_RIGHT);

                    coloredStack.getChildren().addAll(coloredRec,VHouses);
                    coloredStack.setAlignment(Pos.CENTER_RIGHT);
                    break;
                case DOWN:
                    coloredRec = new Rectangle(wid-1,height/3,c);
                    base.setAlignment(Pos.BOTTOM_CENTER);

                    HHouses.setAlignment(Pos.BOTTOM_CENTER);

                    coloredStack.getChildren().addAll(coloredRec,HHouses);
                    coloredStack.setAlignment(Pos.BOTTOM_CENTER);
                    break;
                case RIGHT:
                    coloredRec = new Rectangle(wid/3,height-1,c);
                    base.setAlignment(Pos.CENTER_LEFT);

                    VHouses.setAlignment(Pos.CENTER_LEFT);

                    coloredStack.getChildren().addAll(coloredRec,VHouses);
                    coloredStack.setAlignment(Pos.CENTER_LEFT);
                    break;
            }
            base.getChildren().addAll(baseRec,coloredStack);
        }

        //TODO Text rotation
        Text text = new Text(Integer.toString(game.tiles[i].location));
        text.setRotate(0);

        //The pane, players are stored on
        if(orientation == Orientation.UP || orientation == Orientation.DOWN){
            VBox players = new VBox(5);
            players.setAlignment(Pos.CENTER);
            for (Player p:game.players) {
                if(p.getPosition() == i){
                    players.getChildren().add(p.sprite);
                }
            }

            tempTile.getChildren().addAll(base,text,players);
        }else {
            HBox players = new HBox(5);
            players.setAlignment(Pos.CENTER);
            for (Player p:game.players) {
                if(p.getPosition() == i){
                    players.getChildren().add(p.sprite);
                }
            }

            tempTile.getChildren().addAll(base,text,players);
        }

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
        scrollPane.setMinWidth(BOARD_SIZE /2.5 + 20);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox temp = new VBox(10);
        temp.setMinWidth((double) BOARD_SIZE /2.5);


        temp.setMinHeight(BOARD_SIZE + 10);

        temp.setPadding(new Insets(30,0,0,10));
        temp.setMouseTransparent(false);
        temp.setBackground(new Background(new BackgroundFill(tileBaseColor,CornerRadii.EMPTY, Insets.EMPTY)));

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
        text.setFont(defaultFont);

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

    public void diceRoll(int die1, int die2, boolean show){
        if(semaphore.availablePermits() != 0){
            try{
                throw new Exception ("Invalid Permit Count");
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        if(!show){
            return;
        }

        Platform.runLater(() ->{
            diceRollFX(die1,die2);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    public void diceRollFX(int die1, int die2){
        Stage stage = new Stage();
        stage.toFront();
        stage.initStyle(StageStyle.UNDECORATED);

        //The size of the dice
        int DIE_SIZE = 150;

        //The 2 dice, originally set to random values
        ImageView dieA = new ImageView(new Image(Display.class.getResourceAsStream("Images/Dice/"+Utilities.roll()+".png")));
        dieA.setPreserveRatio(true);
        dieA.setFitWidth(DIE_SIZE);
        ImageView dieB = new ImageView(new Image(Display.class.getResourceAsStream("Images/Dice/"+Utilities.roll()+".png")));
        dieB.setPreserveRatio(true);
        dieB.setFitWidth(DIE_SIZE);

        HBox root = new HBox(30);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(dieA, dieB);

        Rectangle back = new Rectangle(500,400,Color.PALEGREEN);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(back,root);

        //Animate a random assortment of values while the roll is 'happening'
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), event -> {
            dieA.setImage(new Image(Display.class.getResourceAsStream("Images/Dice/"+Utilities.roll()+".png")));
            dieB.setImage(new Image(Display.class.getResourceAsStream("Images/Dice/"+Utilities.roll()+".png")));
        }));
        timeline.setCycleCount(6);
        timeline.play();

        //When the roll finishes, set the dice to the correct values
        timeline.setOnFinished(event -> {
            dieA.setImage(new Image(Display.class.getResourceAsStream("Images/Dice/"+die1+".png")));
            dieB.setImage(new Image(Display.class.getResourceAsStream("Images/Dice/"+die2+".png")));

            Button close = new Button("CLOSE");
            close.setFont(defaultFont);
            close.setOnAction(clicked ->{
                stage.close();
                semaphore.release();
            });

            root.getChildren().add(close);
        });

        Scene scene = new Scene(stackPane,500,400);
        stage.setScene(scene);

        stage.show();

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
        text.setFont(defaultFont);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


        VBox vBox = new VBox(15);
        vBox.setMinWidth(BOARD_SIZE-2*TILE_LENGTH + 8);
        vBox.setMinHeight(BOARD_SIZE-2*TILE_LENGTH + 8);
        vBox.getChildren().add(text);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(tileBaseColor,CornerRadii.EMPTY, Insets.EMPTY)));

        int currentGroup = 001;
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < buttonsToDisplay.length; i++) {
            int groupName = game.tiles[Integer.parseInt(buttonsToDisplay[i].split(":")[0])].groupName;

            if(i == 0){
                currentGroup = groupName;
            }

            if(groupName == currentGroup){
                hBox.getChildren().add(propertyManagerGroupBuilder(buttonsToDisplay[i],vBox));
            }else {
                currentGroup = groupName;

                if(!hBox.getChildren().isEmpty()){
                    vBox.getChildren().add(hBox);

                    hBox = new HBox(5);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().add(propertyManagerGroupBuilder(buttonsToDisplay[i],vBox));
                }
            }
        }
        vBox.getChildren().add(hBox);

        Button backToGame = new Button("Back To Game");
        backToGame.setFont(defaultFont);
        backToGame.setOnAction(event -> {
            setOutString("-1:-1");
            vBox.getChildren().clear();
            semaphore.release(1);
        });
        vBox.getChildren().add(backToGame);

        scrollPane.setContent(vBox);

        gameBoard.setCenter(scrollPane);
    }

    private Button buttonBuilder(String text, int returnValue, Pane parent){
        Button b = new Button(text);
        b.setFont(defaultFont);

        //b.setGraphic(Node); //Adds an image to the right

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
        Rectangle back = new Rectangle(160,100,tileColors[prop.groupName]);
        back.setStroke(Color.BLACK);

        Text name = new Text(prop.name);
        name.setFont(defaultFont);

        Button buyHouse = new Button();
        buyHouse.setFont(defaultFont);
        buyHouse.setOnAction(event -> {
            setOutString(parsed[0]+":6");
            parent.getChildren().clear();
            semaphore.release(1);

        });

        Button sellHouse = new Button();
        sellHouse.setFont(defaultFont);
        sellHouse.setOnAction(event -> {
            setOutString(parsed[0]+":8");
            parent.getChildren().clear();
            semaphore.release(1);
        });

        Button sellProperty = new Button("Sell Property for $"+prop.propertySalePrice());
        sellProperty.setFont(defaultFont);
        sellProperty.setOnAction(event -> {
            setOutString(parsed[0]+":7");
            parent.getChildren().clear();
            semaphore.release(1);
        });

        VBox column = new VBox();
        column.getChildren().add(name);

        //If they can buy a house
        if(parsed[1] != -1){
            buyHouse.setText("Buy a house for $"+prop.getHouseCost());
            column.getChildren().add(buyHouse);
        }

        //If they can sell a house
        if(parsed[3] != -1){
            sellHouse.setText("Sell a house for $"+prop.houseSalePrice());
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
        owner.setFont(defaultFont);

        //A button which closes the card, and returns to the game
        Button close = new Button("Return to game");
        close.setFont(defaultFont);
        close.setOnAction(event -> {
            popup.close();

            if(releaseSemaphore){
                semaphore.release();
            }
        });

        //A vbox containing the column of nodes
        VBox vBox = new VBox(15);
        vBox.setMaxSize(wid,height);
        vBox.setMinSize(wid,height);


        if(p.groupName == 002){//Railroad
            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/Railroad.png")));
            image.setPreserveRatio(true);
            image.setFitHeight(wid/2);

            Text name = new Text(p.name);
            name.setFont(defaultFont);

            Text text = new Text("Rent $20"+
                    "\nIf 2 Railroads Are Owned: $50"+
                    "\nIf 3 Railroads Are Owned: $100"+
                    "\nIf 4 Railroads Are Owned: $200"+
                    "\nMortgage Value $"+p.propertySalePrice());
            text.setFont(defaultFont);

            vBox.getChildren().addAll(image,name,owner,text,close);

        }else if(p.groupName == 005){//Utilities
            ImageView image = new ImageView(new Image(Display.class.getResourceAsStream("Images/"+p.name+".png")));
            image.setPreserveRatio(true);
            image.setFitHeight(wid/2);

            Text name = new Text(p.name);
            name.setFont(defaultFont);

            Text text = new Text("If ONE Utility is owned," +
                    "\nRent is 4x number shown on the dice" +
                    "\nIf BOTH Utilities are owned," +
                    "\nRent is 10x the amount shown on the dice."+
                    "\nMortgage Value $"+p.propertySalePrice());
            text.setFont(defaultFont);

            vBox.getChildren().addAll(image,name,owner,text,close);
        }else{
            StackPane coloredStack = new StackPane();
            Rectangle coloredRec = new Rectangle(wid,height/4,tileColors[p.groupName]);

            VBox textColumn = new VBox(10);
            Text header = new Text("TITLE DEED");
            header.setFont(Font.font("Copperplate",15));
            Text name = new Text(p.name);
            name.setFont(Font.font("Copperplate",30));
            textColumn.getChildren().addAll(header,name);
            textColumn.setAlignment(Pos.CENTER);

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
            values.setFont(defaultFont);

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
            messageFX(message,true);
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void messageFX(String message, boolean releaseSemaphore){
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);

        Text text = new Text(message);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(defaultFont);

        //Sets the width to that of the text
        int wid =  getFontWidth(message,text.getFont())+ 40;
        int height = 150;

        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        Button close = new Button("Return to game");
        close.setFont(defaultFont);
        close.setOnAction(event -> {
            popup.close();
            if(releaseSemaphore){
                semaphore.release(1);
            }
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

    public void winScreen(Player p){
        Platform.runLater(() ->{
            winScreenFX(p);
        });
    }


    public void winScreenFX(Player p){
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);

        Text text = new Text(p.getName()+" Wins!");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("Futura",25));

        ImageView token = p.getSprite();

        //Sets the width to that of the text
        int wid =  300;
        int height = 150;

        Rectangle card = new Rectangle(wid,height,Color.WHITE);

        Button close = new Button("Return to main menu");
        close.setFont(defaultFont);
        close.setOnAction(event -> {
            popup.close();
            startMainMenu();
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(text,token,close);
        vBox.setAlignment(Pos.CENTER);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(card,vBox);

        Scene scene = new Scene(stackPane,wid,height);

        scene.setOnKeyPressed(event -> {
            popup.close();
        });

        popup.setScene(scene);
        popup.showAndWait();
    }

    private enum Orientation {UP,DOWN,LEFT,RIGHT}
}