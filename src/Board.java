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
    //Number of players (2-6)
    int numPlayers;
    private ArrayList<Player> players;
    private int currentPlayer;

    private int cashPot;

    private int numDoubleRollsOnTurn;

    Tile[] tiles = new Tile[40];

    public Board(boolean newGame) {
        loadTiles();

        if(newGame){
            //TODO
            //Make a new game
            players = new ArrayList<>();
        }else{
            loadBoard();
        }
    }

    public void loadTiles(){
         /*
        PROPERTY,name,group(001, 002,003...)rent(5 numbers seperated by commas),canBuild,costs(5 numbers seperated by commas)
        SPECIAL, name
         */

         List<String> lines = null;
         Path tileData = Paths.get("Data/tiles.csv");
        try {
            lines = Files.readAllLines(tileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < lines.size(); i++) {
            String[] parsed =lines.get(i).split(",");

            String name = parsed[1];
            if(parsed[0].equals("PROPERTY")){
                int group = Integer.parseInt(parsed[2]);
                int[] rent = new int[5];
                for (int j = 3; j < 8; j++) {
                    rent[j-3] = Integer.parseInt(parsed[j]);
                }

                boolean canBuild = Boolean.parseBoolean(parsed[8]);

                int[] cost = new int[2];

                if(canBuild){
                    for (int j = 9; j < 11; j++) {
                        cost[j-9] = Integer.parseInt(parsed[j]);
                    }
                }else{
                    cost = null;
                }
                tiles[i] = new Property(i,name,group,rent,canBuild,cost);
            }else{
                tiles[i] = new Special(i,name);
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
            players.add(new Player(name));
        }

        currentPlayer = 0;
        while (players.size() != 1){
            handleTurn(players.get(currentPlayer));
            currentPlayer = (currentPlayer++)%numPlayers;
        }
    }

    public void handleTurn(Player p){
        boolean doubleRoll;
        do {
            int die1 = Utilities.roll();
            int die2 = Utilities.roll();

            doubleRoll = false;
            if (die1 == die2) {
                doubleRoll = true;
                numDoubleRollsOnTurn++;
            }

            Scanner sc = new Scanner(System.in);
            if(numDoubleRollsOnTurn == 3){
                sendToJail(p);
                doubleRoll = false;
            }else {
                if (p.isInJail()) {
                    if (doubleRoll) {
                        p.decreaseJail(true);
                    }else {
                        if(p.hasJailCard()){
                            if(p.hasJailCard()){
                                System.out.println("Do you want to use a get out of jail free card?");
                                boolean use = sc.nextBoolean();
                                if(use){
                                    p.useJailCard();
                                    p.decreaseJail(true);
                                }
                            }
                            if(p.getBalance() > 50){
                                System.out.println("Do you want PAY $50 to buy your way out?");
                                boolean pay = sc.nextBoolean();
                                if(pay){
                                    p.removeMoney(50);
                                    p.decreaseJail(true);
                                }
                                p.decreaseJail(true);
                            }else{
                                p.decreaseJail(false);
                            }
                        }
                    }
                } else {
                    move(p, die1 + die2);
                }

                //The tile the player is currently on
                Tile tile = tiles[p.position];

                //Calls the tile's basic function
                tile.landedOn(p);

                sc.next();
                if (tile.type == Utilities.Type.PROPERTY) {
                    Property x = (Property) tile;
                    if (!x.hasOwner()) {
                        if (p.getBalance() <= x.getCost()) {
                            x.buy(p);
                        } else {
                            System.out.println("You can't afford that");
                        }
                    } else if (x.getOwner().equals(p)) {
                        if (p.getBalance() <= x.getCost()) {
                            x.buildHouse();
                        } else {
                            System.out.println("You can't afford that");
                        }
                    }
                }
                System.out.println("Next player's turn");
                System.out.println();
            }
        }while (doubleRoll);
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

    public void drawCard(Player p){
        //TODO
        //Run the fxn of the drawn card(pop the card and run its fxn)
    }

    public void saveBoard(){
        //TODO
        //Save a board as a text file, and all of the other info
    }

    public void addToCashPot(int amount){
        cashPot+=amount;
    }

    public int emptyCashPot(){
        int c = cashPot;
        cashPot = 0;
        return c;
    }

    public void loadBoard(){
        //TODO
        //load in the saved board
        //Load in saved players
    }
}
