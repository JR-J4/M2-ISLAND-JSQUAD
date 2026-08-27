package ua.com.javarush.jsquad.m1.island;

import ua.com.javarush.jsquad.m1.organism.Animal;
import ua.com.javarush.jsquad.m1.organism.Ecosystem;
import ua.com.javarush.jsquad.m1.organism.Organism;
import ua.com.javarush.jsquad.m1.organism.Species;

import java.util.ArrayList;
import java.util.List;

/**
 * Клітинка острова: мешканці розкладені по видах у масив за {@link Species#index()}.
 * <p>
 * Всі три операції, які виконуються мільйони разів за симуляцію, працюють за O(1):
 * <ul>
 *   <li>{@link #count} - просто size() потрібного списку;</li>
 *   <li>{@link #anyOf} - випадкова особина виду, без копіювання і сортування;</li>
 *   <li>{@link #remove} - на місце того, хто пішов, стає останній зі списку.</li>
 * </ul>
 *
 * [][][]
 * [][][]
 * [][][]
 */


public final class Cell {

    private final int row;
    private final int col;
    private final List<List<Organism>> residents;

    private ArrayList<Cell> neighbours;


    public Cell(int row, int col, Ecosystem ecosystem) {
        this.row = row;
        this.col = col;
        this.residents = new ArrayList<>(ecosystem.size());
        for (int i = 0; i < ecosystem.size(); i++) {
            residents.add(new ArrayList<>());
        }
    }

    public int row() { return row; }
    public int col() { return col; }

    public int count(Species species) {
        return residents.get(species.index()).size();
    }

    public boolean hasRoom(Species species) {
        return count(species) < species.maxPerCell();
    }

    /** @return false, якщо в клітинці вже максимум особин цього виду */
    public boolean add(Organism organism) {
        Species species = organism.species();
        List<Organism> group = residents.get(species.index());
        if (group.size() >= species.maxPerCell()) return false;
        group.add(organism);
        organism.placedAt(this, group.size() - 1);
        return true;
    }

    /** Видалення за O(1): останній зі списку переїжджає на звільнене місце. */
    public void remove(Organism organism) {
        if (organism.cell() != this) return;
        List<Organism> group = residents.get(organism.species().index());
        Organism last = group.remove(group.size() - 1);
        if (last != organism) {
            group.set(organism.slot(), last);
            last.placedAt(this, organism.slot());
        }
        organism.placedAt(null, -1);
    }

    /** Випадкова особина виду або null, якщо виду тут немає. */
    public Organism anyOf(Species species, Dice dice) {
        List<Organism> group = residents.get(species.index());
        return group.isEmpty() ? null : group.get(dice.next(group.size()));
    }

    /**
     * Складає тварин клітинки у переданий список. Буфер один на весь такт, тому сміття немає.
     * <p>
     * Копія потрібна, бо під час ходу список змінюється: тварина йде геть, когось зʼїдають,
     * хтось народжується. Прямий обхід із перевіркою "хто зараз на позиції i" теж працює,
     * але заміряно - швидше не стає, а код складніший.
     */
    public void collectAnimals(Ecosystem ecosystem, List<Animal> target) {
        List<Species> animals = ecosystem.animals();
        for (int s = 0; s < animals.size(); s++) {
            List<Organism> group = residents.get(animals.get(s).index());
            for (int i = 0; i < group.size(); i++) {
                target.add((Animal) group.get(i));
            }
        }
    }

    /** Кого показати на карті: найвищий у харчовому ланцюгу з наявних. */
    public Species dominant(Ecosystem ecosystem) {
        for (Species species : ecosystem.byImportance()) {
            if (count(species) > 0) return species;
        }
        return null;
    }
}
