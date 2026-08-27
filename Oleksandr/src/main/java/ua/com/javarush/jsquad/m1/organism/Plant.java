package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Island;

/** Рослина: не ходить, не їсть, не голодує. Просто виростає (див. Island#growPlants). */
public class Plant extends Organism {

    public Plant(Species species, Island island) {
        super(species, island);
    }
}
