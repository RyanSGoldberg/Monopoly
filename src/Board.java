import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * PURPOSE OF CLASS
 * @author Day
 * @author Goldberg
 */
public class Board{
    public GameDisplay gameDisplay;

    public Tile[] tiles;
    public ArrayList<Player> players;
    public int currentPlayer;

    private int cashPot;

    private int numDoubleRollsOnTurn;
    //Number of players (2-6)
    private int numPlayers;

    public int[] monopolies;

    public Board(boolean newGame, Display gameDisplay, int numPlayers){
        tiles = new Tile[40];
        monopolies = new int[]{2,4,3,3,2,3,3,3,3,2};
        this.gameDisplay = gameDisplay;
        this.numPlayers = numPlayers;
        loadTiles();
        if(newGame){
            //TODO
            players = new ArrayList<>();
        }else{
            //TODO Load board
        }
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
        /*
        Scanner sc = new Scanner(System.in);
        System.out.println("How many players are there?");
        numPlayers = sc.nextInt();

        for (int i = 0; i < numPlayers; i++) {
            sc = new Scanner(System.in);
            System.out.println("What is player "+(i+1)+"'s name");
            String name = sc.next();
            players.add(new Player(name,this));
        }
        */

        System.out.println("Start");

        long before = System.currentTimeMillis();
        long turns = 0;

        currentPlayer = 0;
        while (players.size() > 1) {
            turns++;
            numDoubleRollsOnTurn = 0;
            handleTurn(getCurrentPlayer());
            currentPlayer++;
            if(currentPlayer  == numPlayers){
                currentPlayer = 0;
            }
        }
        long after = System.currentTimeMillis();

        long time = (after-before)/1000/60;
        System.out.println(getCurrentPlayer().getName()+" won the game");
        System.out.println("The algorithm took "+time+" minutes");
        System.out.println("The algorithm took "+turns+" turns");
    }

    public void handleTurn(Player p){
        boolean doubleRoll;
        do {
            gameDisplay.message("It is " + p.getName() + "'s turn",p);

            gameDisplay.updatePlayerPane(p);

            //If it is a playable character
            if(p.getType() == Player.Type.PC){
                gameDisplay.prompt("Roll the die", new int[]{0});
            }

            //Rolls the dice
            int die1 = Utilities.roll();
            int die2 = Utilities.roll();
            gameDisplay.message(p.getName() + " rolled a " + die1 + " and a " + die2,p);

            //If a double is rolled
            doubleRoll = false;
            if (die1 == die2) {
                doubleRoll = true;
                numDoubleRollsOnTurn++;
                gameDisplay.message("WOW: You got a double",p);
            }

            //If a double is rolled 3 times, then end the turn and send to jail
            if (numDoubleRollsOnTurn == 3) {
                gameDisplay.message("That's 3 doubles in a row, sorry friend off to jail with you",p);
                sendToJail(p);
                return;
            }
            //Deals with player being in Jail
            if (p.isInJail()) {
                //If the player rolled a double, free them
                if (doubleRoll) {
                    p.decreaseJail(true);
                    gameDisplay.message("Lucky for you, that double will set you free",p);
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
            //gameDisplay.movePlayer(p);


            //The tile the player is currently on
            Tile tile = tiles[p.position];

            //gameDisplay.movePlayer(p);//FIXME
            //System.out.println("You are now located on " + tile.name);
            //System.out.println(tile.toString(die1 + die2));//TODO How to do with graphics


            //Calls the tile's basic function
            tile.landedOn(p, die1 + die2);//TODO Add graphical capabilities

            //The player's choices
            int[] options = new int[]{-1,-1,-1,-1,-1,-1};
            //End Turn
            options[0] = 4;
            //Trade option
            options[5] = 11;

            Property prop = null;
            if (tile.type == Tile.Type.PROPERTY) {
                prop = (Property) tile;
                gameDisplay.showProperty(prop,p);

            }

            //Loop until the user has ended their turn (choice == 4)
            int choice = -1;
            int numActionsThisTurn = 0;
            while (choice != 4){
                gameDisplay.updatePlayerPane(p);

                //If the player landed on a property, they are given option of what to do with it
                if (tile.type == Tile.Type.PROPERTY) {
                    //Buy property
                    if (!prop.hasOwner() && p.getBalance() >= prop.getCost()) {
                        options[1] = 5;
                    } else {
                        options[1] = -1;
                    }

                    //Build house
                    try {
                        if (prop.getOwner().equals(p) && p.getBalance() >= prop.getCost() && prop.canBuild() && prop.getNumberHouses() < 6 && prop.playerHasMonopoly(prop.groupName)) {
                            options[2] = 6;
                        } else {
                            options[2] = -1;
                        }
                    } catch (NullPointerException e) {
                        options[2] = -1;
                    }

                    //Sell Property
                    try {
                        if (prop.getOwner().equals(p)) {
                            options[3] = 7;
                        } else {
                            options[3] = -1;
                        }
                    } catch (NullPointerException e) {
                        options[3] = -1;
                    }

                    //Sell house
                    try {
                        if (prop.getOwner().equals(p) && prop.getNumberHouses() > 0) {
                            options[4] = 8;
                        } else {
                            options[4] = -1;
                        }
                    } catch (NullPointerException e) {
                        options[4] = -1;
                    }

                }

                if(p.getType() == Player.Type.PC) {
                    choice = gameDisplay.prompt("Ok friend, here are your choices...", options);
                }else {
                    NPC npc = (NPC)p;
                    numActionsThisTurn++;
                    choice = npc.makeDecisionLandedOn(options,numActionsThisTurn);
                }

                switch (choice) {
                    case 4:
                        gameDisplay.message("Ok. See you next turn",p);
                        break;
                    case 5:
                        gameDisplay.message(prop.name + " is now yours",p);
                        prop.buy(p);
                        break;
                    case 6:
                        prop.buildHouse();
                        gameDisplay.message("Wow, now have " + prop.getNumberHouses() + " houses built here. Quite the estate",p);
                        break;
                    case 7:
                        prop.sellProperty();
                        gameDisplay.message("With today's market, I don't blame you for selling",p);
                        break;
                    case 8:
                        prop.sellHouse();
                        gameDisplay.message("Too bad, I was just starting to like the old place. You now have " + prop.getNumberHouses() + "houses",p);
                        break;
                    case 11:
                        //TODO TRADE
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
            move(p,(39-p.position)+newPos);
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

    public void drawCard(Player p) {
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
                break;
            case 4:
                cardName = "Get Out Of Jail Free Card";
                p.getJailCard();
                break;
        }
        gameDisplay.showChance(cardName,cardMessage,p);
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
            gameDisplay.message("Sorry pal, looks like your gambling days are over: You're OUT",p);
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

    @Override
    public String toString() {
        return "";
        //TODO
    }

    public void loadBoard(){
        //TODO
    }

    public void saveBoard(){
        //TODO
    }
}
