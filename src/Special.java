/**
 * PURPOSE OF CLASS
 * @author Shafran
 */
public class Special extends Tile{
    public Special(int location, String name, Board myBoard) {
        type = Tile.Type.SPECIAL;
        this.name = name;
        this.location = location;
        this.myBoard = myBoard;
    }

    public void landedOn(Player p, int rollSum, boolean show){
        switch (name){
            //Has the player pass the Go tile
            case "Go":
                myBoard.passGo(p);
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
                int received = myBoard.emptyCashPot();
                p.addMoney(received);

                if(myBoard.getCashPot() > 0){
                    myBoard.gameDisplay.message("You lucky duck. You just got $"+received,show);
                }
                break;
            //Has the player pay either $150 or 10% of his/her wallet due to landing on the Income Tax tile
            case "Income Tax":
                int[] options = new int[2];

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

                switch (choice){
                    case 9:
                        p.removeMoney(150);
                        myBoard.addToCashPot(150);
                        break;
                    case 10:
                        p.removeMoney((int)(p.getBalance()*0.1));
                        myBoard.addToCashPot((int)(p.getBalance()*0.1));
                        break;
                }
                break;
        }
    }

    @Override
    public String toString(int rollSum) {
        return "";
    }
}