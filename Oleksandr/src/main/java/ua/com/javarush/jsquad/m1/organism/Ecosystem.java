package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Хто живе на острові: список видів, їхні характеристики і меню.
 * <p>
 * Замінює колишній enum Species. Види тепер - звичайні обʼєкти, тому можна зібрати
 * скільки завгодно різних екосистем і передати потрібну в Settings.
 * <p>
 * Види оголошуємо ЗНИЗУ ВГОРУ харчовим ланцюгом (рослини -> травоїдні -> хижаки):
 * тоді меню посилається лише на вже створені види, і кільце "А їсть Б, Б їсть А" неможливе.
 */
public final class Ecosystem {

    private final List<Species> all = new ArrayList<>();
    private final List<Species> animals = new ArrayList<>();
    private final List<Species> plants = new ArrayList<>();
    private List<Species> byImportance = List.of();

    /** Екосистема з ТЗ (поки що 1 хижак + 2 травоїдні + рослини). */
    public static Ecosystem standard() {
        Ecosystem ecosystem = new Ecosystem();
        //                                назва      іконка  вага  макс  швидк  обід  дітей  старт
        Species grass  = ecosystem.plant ("Трава",   "🌿",    1.0,  200,                     3200);
        Species grain  = ecosystem.plant ("Зерно",   "🌾",    0.5,  200,                     1600);
        Species mouse  = ecosystem.animal("Миша",    "🐁",    0.05, 500,    1,   0.01,   6,  1200, Mouse::new);
        Species rabbit = ecosystem.animal("Кролик",  "🐇",    2.0,  150,    2,   0.45,   4,  1600, Rabbit::new);
        Species wolf   = ecosystem.animal("Вовк",    "🐺",   50.0,   30,    3,   8.00,   2,   600, Wolf::new);

        // Таблиця "хто кого їсть" з ТЗ. Порядок = що хапаємо першим.
        // Двом травоїдним на одній траві тісно: сильніший рано чи пізно виживає слабшого
        // з острова (це не баг симуляції, а справжнє екологічне правило). Тому в кожного
        // травоїдного - своя їжа: кролик пасеться на траві, миша живе на зерні.
        mouse.eats(grain, 100);
        rabbit.eats(grass, 100);
        wolf.eats(rabbit, 60).eats(mouse, 80);

        return ecosystem;
    }

    public Species plant(String title, String icon, double weight, int maxPerCell, int startCount) {
        return register(new Species(all.size(), title, icon, weight, maxPerCell,
                0, 0, 0, startCount, false, Plant::new));
    }

    public Species animal(String title, String icon, double weight, int maxPerCell, int speed,
                          double fullMeal, int cubs, int startCount,
                          BiFunction<Species, Island, Organism> factory) {
        return register(new Species(all.size(), title, icon, weight, maxPerCell,
                speed, fullMeal, cubs, startCount, true, factory));
    }

    private Species register(Species species) {
        all.add(species);
        (species.isAnimal() ? animals : plants).add(species);
        List<Species> reversed = new ArrayList<>(all);
        Collections.reverse(reversed);
        byImportance = List.copyOf(reversed);
        return species;
    }

    /** Усі види в порядку оголошення. */
    public List<Species> all() { return all; }

    /** Тільки тварини - рослини не ходять і не їдять, щоб не перебирати їх щотакту. */
    public List<Species> animals() { return animals; }

    /** Тільки рослини - вони ростуть самі, скільки б їх видів не було. */
    public List<Species> plants() { return plants; }

    /** Для карти: спершу верхівка харчового ланцюга (оголошений останнім). */
    public List<Species> byImportance() { return byImportance; }

    /** Скільки різних видів - стільки комірок у кожній клітинці. */
    public int size() { return all.size(); }
}
