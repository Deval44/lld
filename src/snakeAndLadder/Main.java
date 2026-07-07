package snakeAndLadder;

public class Main {
    public static void main(String[] args) {
        //testShortGame();
        testLongGame();
    }

    private static void testLongGame() {
        //long game
        Game game = new Game(50);

        Dice dice = new Dice(10);
        game.addDice(dice);
        game.addDice(new Dice(10));
        game.removeDice(dice);

        game.addPlayer(new Player("red"));
        game.addPlayer(new Player("blue"));
        game.addPlayer(new Player("yellow"));

        game.startGame();

        for(int i=0 ; i<50 && !game.isGameOver(); i++ ) {
            System.out.println("\n ======= \n");
            game.rollDice();
        }

        game.getPositions().forEach((player, position) -> System.out.println("Player " + player + " position: " + position));
    }

    private static void testShortGame() {
        //short game
        Game shortGame = new Game(10);

        shortGame.addDice(new Dice(8));
        shortGame.addDice(new Dice(3));

        shortGame.addPlayer(new Player("Red"));
        shortGame.addPlayer(new Player("Green"));

        shortGame.startGame();

        for(int i=0 ; i<50 && !shortGame.isGameOver(); i++ ) {
            System.out.println("\n ======= \n");
            shortGame.rollDice();
        }
    }
}
