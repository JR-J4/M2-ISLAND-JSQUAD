package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Cell;
import ua.com.javarush.jsquad.m1.island.Island;

/** Все, що займає місце в клітинці: рослина або тварина. */
public abstract class Organism {

    private final Species species;
    private final Island island;

    private Cell cell;
    private int slot = -1;                 // місце в списку клітинки: дає видалення за O(1)
    private boolean alive = true;

    protected Organism(Species species, Island island) {
        this.species = species;
        this.island = island;
    }

    public Species species() { return species; }
    public Island island() { return island; }
    public Cell cell() { return cell; }
    public boolean isAlive() { return alive; }

    /**
     * Внутрішній звʼязок з клітинкою: організм памʼятає не тільки ДЕ він, а й НА ЯКОМУ місці
     * у списку. Тому видалення - це "переставити останнього на це місце", без пошуку по списку.
     *
     * @apiNote викликає лише {@link Cell}
     */
    public void placedAt(Cell cell, int slot) {
        this.cell = cell;
        this.slot = slot;
    }

    /** @apiNote викликає лише {@link Cell} */
    public int slot() {
        return slot;
    }

    /** Померти від голоду або бути зʼїденим: зникаємо з клітинки. */
    public void die() {
        if (!alive) return;
        alive = false;
        if (cell != null) cell.remove(this);
    }
}
