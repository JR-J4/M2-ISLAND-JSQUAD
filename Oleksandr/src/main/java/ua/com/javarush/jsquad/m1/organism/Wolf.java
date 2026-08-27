package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Island;

public class Wolf extends Predator {

    /** Скільки відсотків до полювання додає кожен сусід-вовк і скільки їх максимум враховуємо. */
    private static final int PACK_BONUS = 5;
    private static final int MAX_PACKMATES = 4;

    public Wolf(Species species, Island island) {
        super(species, island);
    }

    /**
     * Приклад поведінки конкретного виду: вовки полюють зграєю - разом ловити легше.
     * Це той самий {@code eat()} з Animal, змінився лише один гачок.
     */
    @Override
    protected int chanceToCatch(Prey prey) {
        int packmates = Math.min(cell().count(species()) - 1, MAX_PACKMATES);
        return prey.chance() + packmates * PACK_BONUS;
    }
}
