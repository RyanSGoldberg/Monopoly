import java.io.IOException;
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
    Tile[] tiles;
    private ArrayList<Player> players;
    private int currentPlayer;

    private int cashPot;

    private int numDoubleRollsOnTurn;
    //Number of players (2-6)
    private int numPlayers;

    public int[] monopolies;

    public Board(boolean newGame) {
        tiles = new Tile[40];
        monopolies = new int[]{2,4,3,3,2,3,3,3,3,2};
        loadTiles();
        if(newGame){
            //TODO
            players = new ArrayList<>();
        }else{
            //TODO Load board
        }
    }

    public void loadTiles(){
         /*
        PROPERTY,name,group(001, 002,003...)rent(5 numbers seperated by commas),canBuild,costs(2 numbers seperated by commas)
        SPECIAL, name
        */

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

                //002 - rail, 005 - electric

                int[] rent;
                boolean canBuild;
                int[] cost;

                if(group == 002 || group == 005){
                    rent = new int[]{-1,-1,-1,-1,-1};
                    cost = new int[]{-1,-1};
                    canBuild = false;
                }else {
                    rent = new int[6];
                    for (int j = 3; j < 9; j++) {
                        rent[j-3] = Integer.parseInt(parsed[j]);
                    }

                    canBuild = true;

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
        Scanner sc = new Scanner(System.in);
        System.out.println("How many players are there?");
        numPlayers = sc.nextInt(); //Place restrictions later

        for (int i = 0; i < numPlayers; i++) {
            sc = new Scanner(System.in);
            System.out.println("What is player "+(i+1)+"'s name");
            String name = sc.next();
            players.add(new Player(name,this));
        }

        currentPlayer = 0;
        while (players.size() > 1){
            handleTurn(players.get(currentPlayer));
            currentPlayer++;
            if(currentPlayer == numPlayers){
                currentPlayer = 0;
            }
        }

        System.out.println("WINNER!");
        System.out.println("WOW "+players.get(0)+" you won the game");

    }

    public void handleTurn(Player p) {
        System.out.println("________________________________________________");
        System.out.println("It is " + p.getName() + "'s turn");

        boolean doubleRoll;
        numDoubleRollsOnTurn = 0;
        Scanner sc = new Scanner(System.in);

        do {
            //Rolls the dice
            int die1 = Utilities.roll();
            int die2 = Utilities.roll();
            System.out.println(p.getName() + " rolled a " + die1 + " and a " + die2);

            //If a double is rolled
            doubleRoll = false;
            if (die1 == die2) {
                doubleRoll = true;
                numDoubleRollsOnTurn++;
                System.out.println("WOW: You got a double");
            }

            //If a double is rolled 3 times, then end the turn and send to jail
            if (numDoubleRollsOnTurn == 3) {
                System.out.println("That's 3 doubles in a row, sorry friend off to jail with you");
                sendToJail(p);
                return;
            }
            //Deals with player being in Jail
            if (p.isInJail()) {
                //If the player rolled a double, free them
                if (doubleRoll) {
                    p.decreaseJail(true);
                    System.out.println("Lucky for you, that double will set you free");
                } else {
                    System.out.println("You have a few options friend. \n You can...");

                    System.out.println("1: Do your time, and wait another turn");

                    //Buy out of jail
                    if (p.getBalance() > 150) {
                        System.out.println("2: Bribe a guard, and free yourself for $150");
                    }

                    //Use a get out of jail free card
                    if (p.hasJailCard()) {
                        System.out.println("3: Use your get out of jail free card");
                    }

                    int choice = sc.nextInt();

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
                        default:
                            System.out.println("That was not an option, you're gonna have to do your time");
                            p.decreaseJail(false);
                            return;
                    }
                }
            }

            //Move the player
            move(p, die1 + die2);


            //The tile the player is currently on
            Tile tile = tiles[p.position];

            System.out.println("You are now located on " + tile.name);
            System.out.println(tile.toString());
            System.out.println();

            //Calls the tile's basic function
            tile.landedOn(p);//TODO inside landed on have a print out of whats going on

            System.out.println("Your current balance is $"+p.getBalance());

            //If the player landed on a property, they are given option of what to do with it
            if (tile.type == Utilities.Type.PROPERTY) {
                Property prop = (Property) tile;
                System.out.println("Ok friend, here are your choice...");

                System.out.println("1: Just chill here");

                if (!prop.hasOwner() && p.getBalance() >= prop.getCost()) {
                    System.out.println("2: You can buy this property for " + prop.getCost());
                }

                try {
                    if (prop.getOwner().equals(p) && p.getBalance() >= prop.getCost() && prop.canBuild()) {
                        System.out.println("3: You can build a house here for " + prop.getCost());
                    }

                    if (prop.getOwner().equals(p)) {
                        System.out.println("4: You can sell this property for " + prop.propertySalePrice());
                    }

                    if (prop.getOwner().equals(p) && prop.getNumberHouses() > 0) {
                        System.out.println("5: You can sell a house for " + prop.houseSalePrice());
                    }
                }catch (NullPointerException e){}

                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("Ok. See you next turn");
                        return;
                    case 2:
                        System.out.println(prop.name + " is now yours");
                        prop.buy(p);
                        break;
                    case 3:
                        System.out.println("Wow, now have " + prop.getNumberHouses() + " built here. Quite the estate");
                        prop.buildHouse();
                        break;
                    case 4:
                        System.out.println("With today's market, I don't blame you for selling");
                        prop.sellProperty();
                        break;
                    case 5:
                        System.out.println("Too bad, I was just starting to like the old place. You now have " + prop.getNumberHouses() + "houses");
                        prop.sellHouse();
                        break;
                    default:
                        System.out.println("I guess you are confused, you can try again next turn");
                        return;
                }
            }

            System.out.println();
        } while (doubleRoll);

    }

    public void move(Player p, int numberOfSpaces){
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
        String cardName = null;
        int randomAmount = Utilities.generateNumber(10, 250);
        int randomLocation = Utilities.generateNumber(1, 7);

        switch (number) {
            case 1:
                cardName = "Collect Cash!";
                p.addMoney(randomAmount);
                break;
            case 2:
                cardName = "Pay Tax";
                p.removeMoney(randomAmount);
                break;
            case 3:
                cardName = "Move Token";
                move(p, randomLocation);
                break;
        }

        System.out.println("You picked up the following card:" + cardName +"");

    }

    public void addToCashPot(int amount){
        cashPot+=amount;
    }

    public int emptyCashPot(){
        int c = cashPot;
        cashPot = 0;
        return c;
    }

    public void mortgageMode(Player p, int debt){
        if(p.netWorth() < debt){
            System.out.println("Sorry pal, looks like your gambling days are over: You're OUT");
            players.remove(p);
        }

        while (debt != 0){
            //TODO Force sell of stuff
        }
    }

    @Override
    public String toString() {
        return "";
        //TODO
    }
}
