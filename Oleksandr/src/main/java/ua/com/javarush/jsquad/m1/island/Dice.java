package ua.com.javarush.jsquad.m1.island;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** Кубик острова. Уся випадковість симуляції проходить тільки через цей обʼєкт. */
public class Dice {

    /** null - беремо ThreadLocalRandom (той самий "багатопотоковий random" з ТЗ). */
    private final Random seeded;

    public Dice() {
        this.seeded = null;
    }

    /** З зерном прогін повторюється один в один - зручно для тестів і порівнянь. */
    public Dice(long seed) {
        this.seeded = new Random(seed);
    }

    public static Dice forSeed(long seed) {
        return seed == 0 ? new Dice() : new Dice(seed);
    }

    /** true з вірогідністю percent (0..100). Крайні випадки не турбують генератор дарма. */
    public boolean roll(int percent) {
        if (percent <= 0) return false;
        if (percent >= 100) return true;
        return next(100) < percent;
    }

    public int next(int bound) {
        return random().nextInt(bound);
    }

    public <T> T anyOf(T[] items) {
        return items[next(items.length)];
    }

    public void shuffle(List<?> items) {
        Collections.shuffle(items, random());
    }

    private Random random() {
        return seeded != null ? seeded : ThreadLocalRandom.current();
    }
}
