package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Direction;
import ua.com.javarush.jsquad.m1.island.Island;

public class Mouse extends Herbivore {

    public Mouse(Species species, Island island) {
        super(species, island);
    }

    /**
     * Приклад поведінки конкретного виду: миша не бігає без потреби - сита сидить у нірці.
     * Це не косметика: миші, які постійно бігають, надто часто знаходять пару і виїдають острів.
     */
    @Override
    protected Direction chooseDirection() {
        return isHungry() ? super.chooseDirection() : null;
    }
}
