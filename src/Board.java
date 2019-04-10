import java.util.ArrayList;
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
        //Read in 40 lines files
        //TODO
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

            if(numDoubleRollsOnTurn == 3){
                sendToJail(p);
            }else {
                if (p.isInJail()) {
                    if (doubleRoll) {
                        p.decreaseJail(true);
                    } else {
                        p.decreaseJail(false);
                    }
                } else {
                    move(p, die1 + die2);
                }

                //The tile the player is currently on
                Tile tile = tiles[p.position];

                //Calls the tile's basic function
                tile.landedOn(p);

                Scanner sc = new Scanner(System.in);
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
