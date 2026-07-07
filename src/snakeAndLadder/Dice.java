package snakeAndLadder;

import java.util.Random;

public record Dice(Integer max) {
    static Random rand = new Random();

    public Integer roll() {
        return rand.nextInt(1, max + 1);
    }
}
