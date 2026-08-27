package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Cell;
import ua.com.javarush.jsquad.m1.island.Direction;
import ua.com.javarush.jsquad.m1.island.Island;

// T0 [C1] {W = 29} == 30
// T1 [C2] W5 -> C1 == C2 W - 1 ==
// T2 [C3] W7 -> C1 == C3 W - 1 ==

/**
 * Хижак полює: голодний іде в ту сусідню клітинку, де найбільше здобичі.
 * Ситий гуляє навмання - не витрачає сили дарма.
 */
public abstract class Predator extends Animal {

    protected Predator(Species species, Island island) {
        super(species, island);
    }

    @Override
    protected Direction chooseDirection() {
        return isHungry() ? bestNeighbour() : randomDirection();
    }

    @Override
    protected int score(Cell neighbour) {
        int food = 0;
        for (Prey prey : species().diet()) {
            food += neighbour.count(prey.species());
        }
        return food;
    }
}
