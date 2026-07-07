package snakeAndLadder;

public class Main {
    public static void main(String[] args) {
        //short game
        Game shortGame = new Game(10);

        shortGame.addDice(new Dice(8));
        shortGame.addDice(new Dice(3));

        shortGame.addPlayer(new Player("Red"));
        shortGame.addPlayer(new Player("Green"));

        shortGame.startGame();

        for(int i=0 ; i<45 && !shortGame.isGameOver(); i++ ) {
            System.out.println("\n ======= \n");
            shortGame.rollDice();
        }
    }
}
