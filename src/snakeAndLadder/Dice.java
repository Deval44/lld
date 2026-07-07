package snakeAndLadder;

import java.util.Random;

public class Dice {
    static Random rand = new Random();
    private final Integer max;

    public Dice(Integer max) {
        this.max = max;
    }

    public Integer roll() {
        return rand.nextInt(1, max + 1);
    }

    @Override
    public String toString() {
        return "Dice[" +
                "max=" + max + ']';
    }

}
