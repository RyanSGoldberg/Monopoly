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

    private int moneyPot;

    Tile[] tiles = new Tile[40];
    ArrayList<Card> deck;

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
        int die1 = Utilities.roll();
        int die2 = Utilities.roll();

        boolean doubleRoll = false;
        if(die1 == die2){
            doubleRoll = true;
        }

        handleRoll(die1,die2);

        if(p.isInJail()){
            if(doubleRoll){
                p.decreaseJail(true);
            }else{
                p.decreaseJail(false);
            }
        }else{
            move(p,die1+die2);
        }

        //The tile the player is currently on
        Tile tile = tiles[p.position];

        //Calls the tile's basic function
        tile.landedOn(p);

        Scanner sc = new Scanner(System.in);
        sc.next();
        if(tile.type == Utilities.Type.PROPERTY){
            Property x = (Property)tile;

            if(!x.hasOwner()){
                //TODO Buy option
            }else if(x.getOwner().equals(p)){
                //TODO Build house option
            }
        }else{

        }
    }

    public void handleRoll(int a, int b){
        //TODO
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

    public void shuffleNewDeck(){
        //TODO
        //Load in all the possible cards and then
        //If possible make a shuffle arraylist algorithm in utilities, so we can use it elsehwere
    }

    public void drawCard(){
        //TODO
        //Run the fxn of the drawn card(pop the card and run its fxn)
    }

    public void saveBoard(){
        //TODO
        //Save a board as a text file, and all of the other info
    }

    public void loadBoard(){
        //TODO
        //load in the saved board
        //Load in saved players
    }
}
