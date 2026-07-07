package snakeAndLadder;

import java.util.*;

public class Game {
    private final int size;
    private final Set<Dice> dices;
    private final Map<Integer, Integer> snakesAndLadders;
    private final Set<Integer> occupiedCells;

    private final Map<Player, Integer> positions;
    private final List<Player> players;
    private Status status;
    private int currentPlayer;

    public Game(int size) {
        this.size = size;
        dices = new HashSet<>();
        snakesAndLadders = new HashMap<>();
        occupiedCells = new HashSet<>();
        positions = new HashMap<>();
        players = new ArrayList<>();
        status = Status.NEW;
        currentPlayer = 0;
    }

    private void requiresNewStatus() {
        if(status != Status.NEW) {
            throw new IllegalStateException("Game is already in progress.");
        }
    }

    public boolean addDice(Dice dice) {
        requiresNewStatus();
        //check
        if(dices.size() == size) {
            System.out.println("Cannot add dices more than: " + size);
            return false;
        }
        dices.add(dice);
        System.out.println("Added dice: " + dice);
        return true;
    }

    public void removeDice(Dice dice) {
        requiresNewStatus();
        dices.remove(dice);
        System.out.println("Removed dice: " + dice);
    }

    public boolean addSnake(int start, int end) {
        requiresNewStatus();
        if(invalidPlacement(start) || invalidPlacement(end) || start <= end){
            System.out.println("Cannot add snake at : " + start + " to " + end);
            return false;
        }

        System.out.println("Adding snake at: " + start + " to " + end);
        addSnakeOrLadder(start, end);
        return true;
    }

    public boolean addLadder(int start, int end) {
        requiresNewStatus();
        if(invalidPlacement(start) || invalidPlacement(end) || end <= start){
            System.out.println("Cannot add ladder at : " + start + " to " + end);
            return false;
        }
        System.out.println("Adding ladder at: " + start + " to " + end);
        addSnakeOrLadder(start, end);
        return true;
    }

    private void addSnakeOrLadder(int start, int end) {
        snakesAndLadders.put(start, end);
        occupiedCells.add(start);
        occupiedCells.add(end);
    }

    public void removeSnake(int start) {
        requiresNewStatus();
        removeSnakeOrLadder(start);
    }

    public void removeLadder(int start) {
        requiresNewStatus();
        removeSnakeOrLadder(start);
    }

    private void removeSnakeOrLadder(int start) {
        if(snakesAndLadders.containsKey(start)){
            occupiedCells.remove(snakesAndLadders.get(start));
            snakesAndLadders.remove(start);
            occupiedCells.remove(start);
        }

        System.out.println("No snake or ladder with start: " + start);
    }

    private boolean invalidPlacement(int pos) {
        return occupiedCells.contains(pos) || pos < 1 || pos > size;
    }

    public void addPlayer(Player player) {
        requiresNewStatus();
        if(positions.containsKey(player)){
            System.out.println("Player already present. Cannot add player: " + player);
            return;
        }

        positions.put(player, 0);//player will start at positions 0.
    }

    public void removePlayer(Player player) {
        requiresNewStatus();
        positions.remove(player);
    }

    public void startGame() {
        requiresNewStatus();
        if(dices.isEmpty() || positions.isEmpty()){
            System.out.println("No dices or players present. Cannot start game.");
            return;
        }
        status = Status.STARTED;
        players.addAll(positions.keySet());
        currentPlayer = 0;
        System.out.println("Game started. Current player: " + players.get(currentPlayer));
    }

    public Player getCurrentPlayer() {
        requiresStartStatus();
        return players.get(currentPlayer);
    }

    public Map<Player, Integer> getPositions() {
        requiresStartStatus();
        return positions;
    }

    private void nextTurn() {
        requiresStartStatus();
        currentPlayer = (currentPlayer + 1) % players.size();
        System.out.println("Next turn for player: " + getCurrentPlayer());
    }

    public void rollDice(){
        requiresStartStatus();
        int totalMoves = dices.stream().mapToInt(Dice::roll).sum();
        System.out.println("Rolling " + totalMoves + " moves.");
        Player currPlayer = getCurrentPlayer();
        updatePosition(currPlayer, totalMoves);
        boolean ifWinner = checkAndUpdateIfWinner(currPlayer);
        if(!ifWinner){
            nextTurn();
        }
    }

    private boolean checkAndUpdateIfWinner(Player currentPlayer) {
        if(positions.get(currentPlayer) == size){
            System.out.println("Player " + currentPlayer + " has won the game.");
            status = Status.STOPPED;
            return true;
        }
        return false;
    }

    private void updatePosition(Player player, int totalMoves){
        int oldPosition = positions.get(player);
        if(oldPosition + totalMoves > size){
            //be at same position, no update
            System.out.println("Oops! Overstepping not allowed. No change in position for player " + player);
            return;
        }

        int newPosition = oldPosition + totalMoves;
        if(snakesAndLadders.containsKey(newPosition)){
            int updatedNewPosition = snakesAndLadders.get(newPosition);
            if(updatedNewPosition < newPosition){
                System.out.println("Snake Bite!! at newPosition: " + newPosition);
            }else{
                System.out.println("Got a Ladder, at current position: " + newPosition + ". Yeah!!");
            }
            newPosition = updatedNewPosition;
        }
        System.out.println("Player " + player + " moves to final position: " + newPosition);
        positions.put(player, newPosition);
    }

    public void endGame() {
        requiresStartStatus();
        status = Status.STOPPED;
    }

    private void requiresStartStatus() {
        if(status != Status.STARTED) {
            throw new IllegalStateException("Game is not in progress.");
        }
    }

    public boolean isGameOver() {
        return status == Status.STOPPED;
    }
}
