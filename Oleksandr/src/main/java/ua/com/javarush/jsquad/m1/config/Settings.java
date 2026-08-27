package ua.com.javarush.jsquad.m1.config;

import ua.com.javarush.jsquad.m1.organism.Ecosystem;

/**
 * Налаштування симуляції - звичайний обʼєкт, а не купа static-констант.
 * <p>
 * Значення за замовчуванням - тут (це і є "пульт керування" симуляцією).
 * Будь-який запуск може перевизначити їх ланцюжком, не чіпаючи цей файл:
 * <pre>Settings.defaults().rows(50).cols(100).showMap(false)</pre>
 */
public class Settings {

    // ---------- Острів ----------
    private int rows = 20;
    private int cols = 40;

    // ---------- Такт і зупинка ----------
    private long tickMillis = 400;
    private int maxTicks = 100;                  // 0 = нескінченно
    private boolean stopWhenNoAnimals = true;

    // ---------- Життя ----------
    private double hungerPerTick = 0.25;         // яку частину повного обіду тварина витрачає за такт
    private double hungryBelow = 0.5;            // ситість (0..1), нижче якої тварина йде їсти
    private double newbornSatiety = 0.5;         // з якою ситістю народжується дитинча
    private double breedAbove = 0.6;             // ситість, вище якої тварина готова до розмноження
    private int breedChance = 10;                // % шанс приплоду за такт, якщо в клітинці є пара
    private int plantsPerTick = 5;               // скільки рослин виростає в клітинці за такт
    private int huntAttempts = 12;               // скільки спроб схопити здобич тварина робить за такт
    // newbornSatiety має бути МЕНШЕ за breedAbove, інакше тварини розмножуються без їжі

    // ---------- Інше ----------
    private long seed = 0;                       // 0 = новий випадковий світ; інше число = однаковий прогін
    private boolean showMap = true;
    private boolean clearScreen = false;         // true - для терміналу з ANSI (в IntelliJ краще false)
    private Ecosystem ecosystem = Ecosystem.standard();   // хто живе на острові і хто кого їсть

    public static Settings defaults() {
        return new Settings();
    }

    // ---------- Прочитати ----------
    public int rows() { return rows; }
    public int cols() { return cols; }
    public long tickMillis() { return tickMillis; }
    public int maxTicks() { return maxTicks; }
    public boolean stopWhenNoAnimals() { return stopWhenNoAnimals; }
    public double hungerPerTick() { return hungerPerTick; }
    public double hungryBelow() { return hungryBelow; }
    public double newbornSatiety() { return newbornSatiety; }
    public double breedAbove() { return breedAbove; }
    public int breedChance() { return breedChance; }
    public int plantsPerTick() { return plantsPerTick; }
    public int huntAttempts() { return huntAttempts; }
    public long seed() { return seed; }
    public boolean showMap() { return showMap; }
    public boolean clearScreen() { return clearScreen; }
    public Ecosystem ecosystem() { return ecosystem; }

    // ---------- Змінити (повертають this, тому можна ланцюжком) ----------
    public Settings rows(int value) { rows = value; return this; }
    public Settings cols(int value) { cols = value; return this; }
    public Settings tickMillis(long value) { tickMillis = value; return this; }
    public Settings maxTicks(int value) { maxTicks = value; return this; }
    public Settings stopWhenNoAnimals(boolean value) { stopWhenNoAnimals = value; return this; }
    public Settings hungerPerTick(double value) { hungerPerTick = value; return this; }
    public Settings hungryBelow(double value) { hungryBelow = value; return this; }
    public Settings newbornSatiety(double value) { newbornSatiety = value; return this; }
    public Settings breedAbove(double value) { breedAbove = value; return this; }
    public Settings breedChance(int value) { breedChance = value; return this; }
    public Settings plantsPerTick(int value) { plantsPerTick = value; return this; }
    public Settings huntAttempts(int value) { huntAttempts = value; return this; }
    public Settings seed(long value) { seed = value; return this; }
    public Settings showMap(boolean value) { showMap = value; return this; }
    public Settings clearScreen(boolean value) { clearScreen = value; return this; }
    public Settings ecosystem(Ecosystem value) { ecosystem = value; return this; }
}
