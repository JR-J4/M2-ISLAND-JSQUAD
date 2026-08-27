package ua.com.javarush.jsquad.m1.organism;

import ua.com.javarush.jsquad.m1.config.Settings;
import ua.com.javarush.jsquad.m1.island.Cell;
import ua.com.javarush.jsquad.m1.island.Dice;
import ua.com.javarush.jsquad.m1.island.Direction;
import ua.com.javarush.jsquad.m1.island.Island;

/**
 * Спільна поведінка ВСІХ тварин: поїсти -> розмножитися -> перейти -> зголодніти.
 * Нащадки перевизначають тільки те, що в них особливе.
 */
public abstract class Animal extends Organism {

    /** values() щоразу віддає НОВУ копію масиву, тому тримаємо одну на всіх. */
    private static final Direction[] DIRECTIONS = Direction.values();

    private double satiety;                  // скільки кг їжі "в животі"; 0 - смерть від голоду
    private int lastTick;                    // у якому такті вже ходила (щоб не сходити двічі)

    protected Animal(Species species, Island island) {
        super(species, island);
        this.satiety = species.fullMeal() * island.settings().newbornSatiety();
    }

    /** Один хід тварини. Порядок дій видно з першого погляду - і легко змінити. */
    public void liveOneTick() {
        eat();
        reproduce();
        move();
        getHungry();
    }

    /**
     * Поїсти. Ніякого пошуку їжі немає: у виду вже лежить готове меню в порядку переваги,
     * а клітинка вміє порахувати і видати випадкову особину виду за O(1).
     * Кожна сусідня особина - один кидок кубика, як і вимагає таблиця ТЗ.
     * <p>
     * Спроби обмежені: полювання теж вимагає сил. Без ліміту вовк, щоб набити 8 кг,
     * винищував би за один такт півтори сотні мишей - і миші зникали б з острова.
     */
    public void eat() {
        if (!isHungry()) return;                                   // сита тварина не полює
        Species me = species();
        double fullMeal = me.fullMeal();
        Cell cell = cell();
        Dice dice = island().dice();
        int attemptsLeft = settings().huntAttempts();
        for (Prey prey : me.diet()) {
            int nearby = cell.count(prey.species());
            for (int i = 0; i < nearby && attemptsLeft > 0 && satiety < fullMeal; i++) {
                attemptsLeft--;
                if (!dice.roll(chanceToCatch(prey))) continue;     // здобич утекла
                Organism victim = cell.anyOf(prey.species(), dice);
                if (victim == null) break;                         // усіх зʼїли
                satiety = Math.min(fullMeal, satiety + prey.species().weight());
                island().statistics().registerEaten();
                victim.die();
            }
            if (satiety >= fullMeal || attemptsLeft == 0) return;
        }
    }

    /** Шанс схопити здобич, %. Вид може його змінити - див. {@link Wolf}. */
    protected int chanceToCatch(Prey prey) {
        return prey.chance();
    }

    /** Розмножитися: потрібна пара в клітинці, ситість і трохи щастя. */
    public void reproduce() {
        Species me = species();
        Cell cell = cell();
        if (satiety < me.fullMeal() * settings().breedAbove()) return;   // голодній не до дітей
        if (cell.count(me) < 2) return;                                  // немає пари
        if (!cell.hasRoom(me)) return;                                   // немає місця для дитинчат
        if (!island().dice().roll(settings().breedChance())) return;
        for (int i = 0; i < me.cubs() && cell.hasRoom(me); i++) {
            Animal cub = (Animal) me.create(island());
            cub.markLived(island().tickNumber());     // дитинча ходить уже з наступного такту
            cell.add(cub);
            island().statistics().registerBirth();
        }
        satiety /= 2;                                                    // діти забирають половину сил
    }

    /** Перейти - не більше ніж speed клітинок за такт. */
    public void move() {
        Species me = species();
        for (int step = 0; step < me.speed(); step++) {
            Direction direction = chooseDirection();
            if (direction == null) return;                               // вирішила залишитись
            Cell next = island().neighbour(cell(), direction);
            if (next == null || !next.hasRoom(me)) continue;             // море або тісно
            cell().remove(this);
            next.add(this);
        }
    }

    /** Куди йти. Базово - навмання; хижак і травоїдне вирішують по-своєму. */
    protected Direction chooseDirection() {
        return randomDirection();
    }

    /** Дешевий варіант: один кидок кубика, без огляду сусідів. */
    protected final Direction randomDirection() {
        return DIRECTIONS[island().dice().next(DIRECTIONS.length)];
    }

    /**
     * Дорогий варіант: оглянути 4 сусідні клітинки і піти в найкращу за {@link #score}.
     * Обхід починаємо з випадкової сторони, тому за однакових оцінок напрямок теж випадковий.
     * Викликати тільки тоді, коли є заради чого - інакше це половина часу всієї симуляції.
     */
    protected final Direction bestNeighbour() {
        Cell here = cell();
        int offset = island().dice().next(DIRECTIONS.length);
        Direction best = null;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < DIRECTIONS.length; i++) {
            Direction direction = DIRECTIONS[(offset + i) % DIRECTIONS.length];
            Cell neighbour = island().neighbour(here, direction);
            if (neighbour == null) continue;
            int score = score(neighbour);
            if (score > bestScore) {
                bestScore = score;
                best = direction;
            }
        }
        return best;
    }

    /** Наскільки приваблива сусідня клітинка. Використовує {@link #bestNeighbour()}. */
    protected int score(Cell neighbour) {
        return 0;
    }

    /** Витратити енергію за такт. Порожній живіт = смерть. */
    private void getHungry() {
        double fullMeal = species().fullMeal();
        if (fullMeal == 0) return;                                       // такі, як гусінь, не голодують
        satiety -= fullMeal * settings().hungerPerTick();
        if (satiety <= 0) {
            island().statistics().registerStarved();                     // рахуємо ДО die()
            die();
        }
    }

    public boolean isHungry() {
        return satiety < species().fullMeal() * settings().hungryBelow();
    }

    /** Чи вже ходила в цьому такті: тварина могла перейти в ще не оброблену клітинку. */
    public boolean hasLivedIn(int tickNumber) {
        return lastTick == tickNumber;
    }

    /** @apiNote викликає {@link Island} перед ходом і {@link #reproduce()} для новонароджених */
    public void markLived(int tickNumber) {
        lastTick = tickNumber;
    }

    protected Settings settings() {
        return island().settings();
    }
}
