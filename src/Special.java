/**
 * A child of Tile.
 * It is any tile, which cannot be purchased and thus has a 'special' effect
 * @author Shafran
 */
public class Special extends Tile{
    /**
     * The constructor for Special
     * @param location The location on the board
     * @param name The name of the tile
     * @param myBoard The Board which this Special is stored in
     */
    public Special(int location, String name, Board myBoard) {
        this.type = Tile.Type.SPECIAL;
        this.name = name;
        this.location = location;
        this.myBoard = myBoard;
    }

    /**
     * @param p The player who landed on this Tile
     * @param rollSum The sum of the die roll, which resulted on this tile being landed on
     * @param show A boolean stating whether a popup should be displayed
     */
    public void landedOn(Player p, int rollSum, boolean show){
        switch (name){
                //If the player lands on GO
            case "Go":
                //Double pass go, if you land on the go tile
                p.addMoney(200);
                break;

                //Sends the player to jail due to landing on the Go To Jail tile
            case "Go To Jail":
                myBoard.gameDisplay.message("Sorry Pal, off to the clink with you",show);
                myBoard.sendToJail(p);
                myBoard.gameDisplay.updateGameBoard();
                break;

                //Has the player draw a card due to landing on the Chance tile
            case "Chance":
                myBoard.drawCard(p, show);
                break;

                //Has the player take all the money from the money pot due to landing on the Free Parking tile
            case "Free Parking":
                //If there is money in the pot, then give it to the player
                if(myBoard.getCashPot() > 0){
                    int received = myBoard.emptyCashPot();
                    p.addMoney(received);

                    myBoard.gameDisplay.message("You lucky duck. You just got $"+received,show);
                }
                break;

                //Has the player pay either $150 or 10% of his wallet due to landing on the Income Tax tile
            case "Income Tax":
                int[] options = new int[2];

                //If the player has $150+ they are given the option to pay $150
                if(p.getBalance()>=150){
                    options[0] = 9;
                }else {
                    options[0] = -1;
                }

                //You can pay %10 of your wallet
                options[1] = 10;

                int choice;
                if(p.getType() == Player.Type.PC) {
                    choice = myBoard.gameDisplay.prompt("You have to pay your taxes pal. Here are your options ...",options);
                }else {
                    NPC npc = (NPC)p;
                    choice = npc.makeDecisionTaxes(options);
                }

                //Handles the user's choice
                switch (choice){
                    case 9:
                        p.removeMoney(150);
                        myBoard.addToCashPot(150);
                        break;
                    case 10:
                        myBoard.addToCashPot((int)(p.getBalance()*0.1));
                        p.removeMoney((int)(p.getBalance()*0.1));
                        break;
                }
                break;
        }
    }

    /**
     * A ToString() method for Special
     * @return A string displaying all needed information about the Special
     */
    @Override
    public String toString() {
        return "NULL";
    }
}