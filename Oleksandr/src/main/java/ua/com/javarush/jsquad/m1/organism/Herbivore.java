package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Cell;
import ua.com.javarush.jsquad.m1.island.Direction;
import ua.com.javarush.jsquad.m1.island.Island;

/**
 * Травоїдне тікає. Але озирається на всі боки лише тоді, коли хижак уже поруч,
 * - інакше просто пасеться і йде навмання. Рослини ростуть скрізь, шукати їх не треба.
 */
public abstract class Herbivore extends Animal {

    protected Herbivore(Species species, Island island) {
        super(species, island);
    }

    @Override
    protected Direction chooseDirection() {
        return inDanger() ? bestNeighbour() : randomDirection();
    }

    /** Чи є хижак у моїй клітинці - одна перевірка замість огляду всіх сусідів. */
    private boolean inDanger() {
        for (Species hunter : species().hunters()) {
            if (cell().count(hunter) > 0) return true;
        }
        return false;
    }

    @Override
    protected int score(Cell neighbour) {
        int danger = 0;
        for (Species hunter : species().hunters()) {
            danger += neighbour.count(hunter);
        }
        return -danger;                       // чим менше хижаків, тим краще
    }
}
