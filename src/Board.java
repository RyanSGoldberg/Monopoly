import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * PURPOSE OF CLASS
 * @author Day
 * @author Goldberg
 */
public class Board{
    public GameDisplay gameDisplay;

    public Tile[] tiles;
    public static ArrayList<Player> players;
    public int currentPlayer;

    private int cashPot;

    private int numDoubleRollsOnTurn;

    public int numPlayers;

    public int[] monopolies;

    public Board(boolean newGame, Display gameDisplay){
        tiles = new Tile[40];
        monopolies = new int[]{2,4,3,3,2,3,3,3,3,2};
        this.gameDisplay = gameDisplay;
        loadTiles();
        players = new ArrayList<>();
        if(newGame){
            //TODO Shuffle players array
            currentPlayer = 0;

        }else{
            //TODO Load board
            loadBoard();
        }
        setNumPlayers();
    }

    public void loadTiles(){
         //The path to the file containing the tile data
         Path tileData = Paths.get("src/Data/tiles.csv");

         //Checks to make sure the file exists
         if(!Files.exists(tileData)){
             System.err.println("The file cannot be found");
             System.exit(1);
         }

        //Loading the files lines into a List
        List<String> lines = null;
        try {
            lines = Files.readAllLines(tileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Parses the file, and saves each line as a tile
        for (int i = 0; i < 40; i++) {
            String[] parsed =lines.get(i).split(",");

            String name = parsed[1];
            if(parsed[0].equals("PROPERTY")){
                int group = Integer.parseInt(parsed[2]);

                int[] rent;
                boolean canBuild;
                int[] cost;

                //Checks if the tile is a non-buildable property
                if(group == 002 || group == 005){
                    canBuild = false;
                    rent = new int[]{0,0,0,0,0,0};
                    cost = new int[]{Integer.parseInt(parsed[3]),0};
                }else {
                    canBuild = true;

                    rent = new int[6];
                    for (int j = 3; j < 9; j++) {
                        rent[j-3] = Integer.parseInt(parsed[j]);
                    }

                    cost = new int[2];
                    for (int j = 9; j < 11; j++) {
                        cost[j-9] = Integer.parseInt(parsed[j]);
                    }
                }
                tiles[i] = new Property(i,name,group,rent,canBuild,cost,this);
            }else{
                tiles[i] = new Special(i,name,this);
            }
        }
    }

    public void play(){
        while (players.size() > 1) {
            numDoubleRollsOnTurn = 0;

            boolean show;
            if(getCurrentPlayer().type == Player.Type.PC){
                show = true;
            }else {
                show = false;
            }
            handleTurn(getCurrentPlayer(),show);
            currentPlayer++;
            if(currentPlayer  == numPlayers){
                currentPlayer = 0;
            }
        }
        gameDisplay.message(getCurrentPlayer().getName()+" won the game",true);
    }

    public void handleTurn(Player p, boolean show){
        boolean doubleRoll;
        do {
            gameDisplay.message("It is " + p.getName() + "'s turn",show);

            gameDisplay.updatePlayerPane(p);

            //If it is a playable character
            if(p.getType() == Player.Type.PC){
                gameDisplay.prompt("Roll the die", new int[]{0});
            }

            //Rolls the dice
            int die1 = Utilities.roll();
            int die2 = Utilities.roll();
            gameDisplay.message(p.getName() + " rolled a " + die1 + " and a " + die2,show);

            //If a double is rolled
            doubleRoll = false;
            if (die1 == die2) {
                doubleRoll = true;
                numDoubleRollsOnTurn++;
                gameDisplay.message("WOW: You got a double",show);
            }

            //If a double is rolled 3 times, then end the turn and send to jail
            if (numDoubleRollsOnTurn == 3) {
                gameDisplay.message("That's 3 doubles in a row, sorry friend off to jail with you",show);
                sendToJail(p);
                return;
            }
            //Deals with player being in Jail
            if (p.isInJail()) {
                //If the player rolled a double, free them
                if (doubleRoll) {
                    p.decreaseJail(true);
                    gameDisplay.message("Lucky for you, that double will set you free",show);
                } else {
                    int[] options = new int[3];
                    options[0] = 1;

                    //Buy out of jail
                    if (p.getBalance() > 150) {
                        options[1] = 2;
                    } else {
                        options[1] = -1;
                    }

                    //Use a get out of jail free card
                    if (p.hasJailCard()) {
                        options[2] = 3;
                    } else {
                        options[2] = -1;
                    }

                    int choice;
                    if(p.getType() == Player.Type.PC) {
                        choice = gameDisplay.prompt("You're still in jail. Here are your choices", options);
                    }else {
                        NPC npc = (NPC)p;
                        choice = npc.makeDecisionJail(options);
                    }

                    switch (choice) {
                        case 1:
                            p.decreaseJail(false);
                            return;
                        case 2:
                            p.removeMoney(150);
                            p.decreaseJail(true);
                            break;
                        case 3:
                            p.useJailCard();
                            p.decreaseJail(true);
                            break;
                    }
                }
            }

            //Move the player
            move(p, die1 + die2);
            gameDisplay.updateGameBoard();


            //The tile the player is currently on
            Tile tile = tiles[p.position];

            //gameDisplay.movePlayer(p);//FIXME

            //Calls the tile's basic function
            tile.landedOn(p, die1 + die2, show);

            //The player's choices
            int[] options = new int[]{-1,-1,-1};

            //End Turn option
            options[0] = 4;

            //Declares prop as the property (If it is a property)
            Property prop = null;
            if (tile.type == Tile.Type.PROPERTY) {
                prop = (Property) tile;
                if(!p.equals(prop.getOwner())){
                    gameDisplay.showProperty(prop,show);
                }
            }

            //Loop until the user has ended their turn (choice == 4)
            int choice = -1;

            int numActionsThisTurn = 0;
            boolean boughtProperty = false;
            boolean boughtHouse = false;

            while (choice != 4){
                gameDisplay.updatePlayerPane(p);
                //If the player landed on a property, they are given option of what to do with it
                if (tile.type == Tile.Type.PROPERTY) {
                    //Buy property option
                    if (!prop.hasOwner() && p.getBalance() >= prop.getCost()) {
                        options[1] = 5;
                    } /*else if(p.getBalance() < prop.getCost()){
                        options[1] = -1;

                    }*/ else {
                        options[1] = -1;
                    }
                }
                //Property Manager option
                if(p.getInventory().size() > 0){
                    options[2] = 11;
                }else {
                    options[2] = -1;
                }

                if(p.getType() == Player.Type.PC) {
                    choice = gameDisplay.prompt("Ok friend, here are your choices...", options);
                }else {
                    //TODO BENNY Z
                    NPC npc = (NPC)p;
                    numActionsThisTurn++;
                    choice = npc.makeDecisionLandedOn(options,numActionsThisTurn, boughtProperty, boughtHouse);
                    if (choice == 5){
                        boughtProperty = true;
                    }
                    else if(choice == 6){
                        boughtHouse = true;
                    }
                }

                switch (choice) {
                    case 4:
                        gameDisplay.message("Ok. See you next turn",show);
                        break;
                    case 5:
                        gameDisplay.message(prop.name + " is now yours",show);
                        prop.buy(p);
                        break;
                    case 11:
                        //Property Manager
                        String[] optionsString = new String[p.getInventory().size()];
                        int i = 0;

                        //Builds the inut string for the display prompt
                        for (Property property:p.getInventory()) {
                            String s = property.location+":";

                            //Build house option
                            try {
                                if (p.getBalance() >= property.getCost() && property.canBuild() && property.getNumberHouses() < 6 && property.playerHasMonopoly(prop.groupName)) {
                                    s+="6:";
                                } else {
                                    s+="-1:";
                                }
                            } catch (NullPointerException e) {
                                s+="-1:";
                            }

                            //Sell Property (Always an option)
                            s+="7:";

                            //Sell house
                            try {
                                if (property.getNumberHouses() > 0) {
                                    s+="8";
                                } else {
                                    s+="-1";
                                }
                            } catch (NullPointerException e) {
                                s+="-1";
                            }

                            optionsString[i] = s;
                            i++;
                        }

                        String[] propertyManagerChoice = gameDisplay.propertyManagerPrompt(optionsString).split(":");

                        //The tile location of the button clicked
                        int posParsed = Integer.parseInt(propertyManagerChoice[0]);
                        //The command of the of the button clicked(6-8)
                        int choiceParsed = Integer.parseInt(propertyManagerChoice[1]);

                        if(posParsed == -1 || choiceParsed == -1){
                            break;
                        }

                        Property property = (Property) tiles[posParsed];

                        switch (choiceParsed){
                            case 6:
                                property.buildHouse();
                                gameDisplay.message("Wow, now have " + prop.getNumberHouses() + " houses built here. Quite the estate",show);
                                gameDisplay.updatePlayerPane(p);
                                gameDisplay.updateGameBoard();
                                break;
                            case 7:
                                property.sellProperty();
                                gameDisplay.message("With today's market, I don't blame you for selling",show);
                                gameDisplay.updatePlayerPane(p);
                                gameDisplay.updateGameBoard();
                                break;
                            case 8:
                                property.sellHouse();
                                gameDisplay.message("Too bad, I was just starting to like the old place. You now have " + prop.getNumberHouses() + "houses",show);
                                gameDisplay.updatePlayerPane(p);
                                gameDisplay.updateGameBoard();
                                break;
                        }


                }
            }
        } while (doubleRoll);
    }

    public void move(Player p, int numberOfSpaces){
        gameDisplay.movePlayer(p);
        p.position += numberOfSpaces;
        if(p.position > 39){
            p.position = p.position%40;
            passGo(p);
        }
    }

    public void moveTo(Player p, int newPos){
        if(newPos < p.position){
            move(p,(40-p.position)+newPos);
            passGo(p);
        }else {
            move(p,newPos-p.position);
        }
    }

    public void passGo(Player p){
        p.addMoney(200);
    }

    public void sendToJail(Player p){
        p.setJail();
        moveTo(p,10);
    }

    public void drawCard(Player p, boolean show) {
        int number = Utilities.generateNumber(0, 5);
        String cardName = "";
        String cardMessage = "";
        int randomAmount = Utilities.generateNumber(10, 250);
        int randomLocation = Utilities.generateNumber(1, 7);

        switch (number) {
            case 1:
                cardName = "Collect Cash!";
                cardMessage = "You have gained $"+randomAmount;

                p.addMoney(randomAmount);
                break;
            case 2:
                cardName = "Pay Tax";
                cardMessage = "You have to pay $"+randomAmount+" in taxes";

                p.removeMoney(randomAmount);
                addToCashPot(randomAmount);
                break;
            case 3:
                cardName = "Move Token";
                cardMessage = "You now must move "+randomLocation+" tiles forward";

                move(p, randomLocation);
                handleTurn(getCurrentPlayer(),show);

                break;
            case 4:
                cardName = "Get Out Of Jail Free Card";

                p.getJailCard();
                break;
        }

        gameDisplay.showChance(cardName,cardMessage,show);
        gameDisplay.updatePlayerPane(p);
        gameDisplay.updateGameBoard();
        //TODO Update Player location
    }

    public void addToCashPot(int amount){
        cashPot+=amount;
    }

    public int emptyCashPot(){
        int c = cashPot;
        cashPot = 0;
        return c;
    }

    public int getCashPot() {
        return cashPot;
    }

    public void mortgageMode(Player p, int debt){
        if(p.netWorth() < debt){
            gameDisplay.message("Sorry pal, looks like your gambling days are over: You're OUT",true);
            players.remove(p);
        }

        while (debt != 0){
            System.out.println("Debt entered");
            //TODO Force sell of stuff
        }
    }

    public Player getCurrentPlayer(){
        return players.get(currentPlayer);
    }

    public void setNumPlayers() {
        this.numPlayers = players.size();
    }

    @Override
    public String toString() {
        return "";
        //TODO
    }

    public void loadBoard(){
        //TODO
    }

    public void saveBoard(){

        FileWriter fw;
        PrintWriter pw;

        try{
            System.out.println("Enter the name of your game");
            Scanner sc = new Scanner (System.in);
            String game = (sc.nextLine())+".txt";

            fw = new FileWriter(game);
            pw = new PrintWriter(fw);

            for (int i=0; i<players.size(); i++){
                pw.println();
            }
            pw.println();

            pw.close();
        }catch(Exception e){
            System.err.println("Error 404");
        }

    }

}
