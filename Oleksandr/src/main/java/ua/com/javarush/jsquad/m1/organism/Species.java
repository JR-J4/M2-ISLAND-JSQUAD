package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.island.Island;

import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * Опис виду: характеристики з ТЗ + меню + фабрика особин.
 * <p>
 * Це НЕ enum, і це важливо: види стають даними, а не жорстко зашитим переліком.
 * Завдяки цьому можна зібрати іншу екосистему (наприклад, острів без хижаків для тесту),
 * не чіпаючи жодного існуючого класу. Створює види тільки {@link Ecosystem} - він же
 * видає кожному виду {@link #index()}, за яким клітинка тримає мешканців у масиві.
 * <p>
 * Клас відповідає за ДАНІ виду. За ПОВЕДІНКУ відповідає клас-нащадок {@link Animal}.
 */
public final class Species {

    private static final Prey[] NOTHING = new Prey[0];

    private final int index;
    private final String title;
    private final String icon;
    private final double weight;                 // кг - стільки отримує той, хто зʼїв цю особину
    private final int maxPerCell;                // максимум особин виду в одній клітинці
    private final int speed;                     // клітинок за такт
    private final double fullMeal;               // кг їжі до повного насичення
    private final int cubs;                      // максимум дитинчат за раз
    private final int startCount;                // скільки особин створити на старті
    private final boolean animal;
    private final BiFunction<Species, Island, Organism> factory;

    /** Меню і список ворогів заповнює Ecosystem під час налаштування; далі вони не змінюються. */
    private Prey[] diet = NOTHING;
    private Species[] hunters = new Species[0];

    Species(int index, String title, String icon, double weight, int maxPerCell, int speed,
            double fullMeal, int cubs, int startCount, boolean animal,
            BiFunction<Species, Island, Organism> factory) {
        this.index = index;
        this.title = title;
        this.icon = icon;
        this.weight = weight;
        this.maxPerCell = maxPerCell;
        this.speed = speed;
        this.fullMeal = fullMeal;
        this.cubs = cubs;
        this.startCount = startCount;
        this.animal = animal;
        this.factory = factory;
    }

    /**
     * "Вовк їсть кролика з вірогідністю 60%". Порядок викликів = порядок переваги на полюванні.
     * Викликається один раз під час складання екосистеми.
     */
    public Species eats(Species food, int chance) {
        diet = Arrays.copyOf(diet, diet.length + 1);
        diet[diet.length - 1] = new Prey(food, chance);
        food.hunters = Arrays.copyOf(food.hunters, food.hunters.length + 1);
        food.hunters[food.hunters.length - 1] = this;
        return this;
    }

    /** Нова особина цього виду. Вид передається в конструктор, тому один клас
     *  може обслуговувати кілька видів (наприклад, "сірий вовк" і "полярний вовк"). */
    public Organism create(Island island) {
        return factory.apply(this, island);
    }

    /** Готове меню в порядку переваги - без пошуку і сортування під час такту. */
    public Prey[] diet() { return diet; }

    /** Хто полює на цей вид - зворотний бік меню, теж порахований наперед. */
    public Species[] hunters() { return hunters; }

    public int index() { return index; }
    public String title() { return title; }
    public String icon() { return icon; }
    public double weight() { return weight; }
    public int maxPerCell() { return maxPerCell; }
    public int speed() { return speed; }
    public double fullMeal() { return fullMeal; }
    public int cubs() { return cubs; }
    public int startCount() { return startCount; }
    public boolean isAnimal() { return animal; }

    @Override
    public String toString() {
        return title;
    }
}
