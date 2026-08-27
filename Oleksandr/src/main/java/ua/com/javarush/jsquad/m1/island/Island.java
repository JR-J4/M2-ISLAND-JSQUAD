package ua.com.javarush.jsquad.m1.island;

import ua.com.javarush.jsquad.m1.config.Settings;
import ua.com.javarush.jsquad.m1.organism.Animal;
import ua.com.javarush.jsquad.m1.organism.Ecosystem;
import ua.com.javarush.jsquad.m1.organism.Species;

import java.util.ArrayList;
import java.util.List;

/** Острів: сітка клітинок, свої правила, свій кубик і своя статистика. */
public final class Island {

    private final Settings settings;
    private final Ecosystem ecosystem;
    private final Dice dice;
    private final Statistics statistics = new Statistics();

    private final Cell[][] cells;
    private final List<Cell> order;                  // порядок обходу клітинок, тасується щотакту
    private final List<Animal> buffer = new ArrayList<>(1024);   // один на весь такт

    private int tickNumber;

    public Island(Settings settings) {
        this.settings = settings;
        this.ecosystem = settings.ecosystem();
        this.dice = Dice.forSeed(settings.seed());
        this.cells = new Cell[settings.rows()][settings.cols()];
        this.order = new ArrayList<>(settings.rows() * settings.cols());
        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {
                cells[row][col] = new Cell(row, col, ecosystem);
                order.add(cells[row][col]);
            }
        }
        for (Species species : ecosystem.all()) settle(species);
    }

    /** Один такт: виросли рослини -> кожна тварина прожила свій хід. */
    public void tick() {
        long started = System.nanoTime();
        tickNumber++;
        statistics.reset();
        growPlants();
        liveOneTick();
        statistics.tookNanos(System.nanoTime() - started);
    }

    /**
     * Обходимо клітинки у випадковому порядку - жодного списку "всі тварини острова".
     * Тварина, яка перейшла в ще не оброблену клітинку, могла б сходити двічі:
     * від цього рятує позначка про такт.
     * <p>
     * Тварин у клітинці теж тасуємо. Без цього вони ходили б групами за видами: миші завжди
     * першими виїдали б траву в кролика з-під носа, а вовк ходив би останнім - коли здобич
     * уже розбіглася. Заміряно: без тасування вовк і кролик вимирають до 20-го такту.
     */
    private void liveOneTick() {
        dice.shuffle(order);
        for (int i = 0; i < order.size(); i++) {
            Cell cell = order.get(i);
            buffer.clear();
            cell.collectAnimals(ecosystem, buffer);
            dice.shuffle(buffer);                    // інакше види ходили б завжди в одному порядку
            for (int j = 0; j < buffer.size(); j++) {
                Animal animal = buffer.get(j);
                if (!animal.isAlive() || animal.hasLivedIn(tickNumber)) continue;
                animal.markLived(tickNumber);
                animal.liveOneTick();
            }
        }
    }

    /** Ростуть усі види рослин, скільки б їх не було в екосистемі. */
    private void growPlants() {
        int perTick = settings.plantsPerTick();
        List<Species> plants = ecosystem.plants();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                for (int p = 0; p < plants.size(); p++) {
                    Species plant = plants.get(p);
                    for (int i = 0; i < perTick && cell.hasRoom(plant); i++) {
                        cell.add(plant.create(this));
                    }
                }
            }
        }
    }

    /** Розселити вид на старті у випадкові клітинки. */
    private void settle(Species species) {
        for (int i = 0; i < species.startCount(); i++) {
            for (int attempt = 0; attempt < 10; attempt++) {
                Cell cell = cells[dice.next(rows())][dice.next(cols())];
                if (cell.hasRoom(species)) {
                    cell.add(species.create(this));
                    break;
                }
            }
        }
    }

    // [][][]
    // [][][]
    // [][][]

    /** Сусідня клітинка або null, якщо там уже море. */
    public Cell neighbour(Cell from, Direction direction) {
        int row = from.row() + direction.dRow();
        int col = from.col() + direction.dCol();
        boolean outside = row < 0 || row >= rows() || col < 0 || col >= cols();
        return outside ? null : cells[row][col];
    }

    public int population(Species species) {
        int total = 0;
        for (Cell[] row : cells) {
            for (Cell cell : row) total += cell.count(species);
        }
        return total;
    }

    public int totalAnimals() {
        int total = 0;
        for (Species species : ecosystem.animals()) total += population(species);
        return total;
    }

    public Settings settings() { return settings; }
    public Ecosystem ecosystem() { return ecosystem; }
    public Dice dice() { return dice; }
    public Statistics statistics() { return statistics; }
    public Cell[][] cells() { return cells; }
    public int rows() { return cells.length; }
    public int cols() { return cells[0].length; }
    public int tickNumber() { return tickNumber; }
}
