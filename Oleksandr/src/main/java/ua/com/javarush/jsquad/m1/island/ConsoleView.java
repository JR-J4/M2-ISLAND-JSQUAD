package ua.com.javarush.jsquad.m1.island;

import ua.com.javarush.jsquad.m1.config.Settings;
import ua.com.javarush.jsquad.m1.organism.Ecosystem;
import ua.com.javarush.jsquad.m1.organism.Species;

/** Статистика і псевдографіка в консолі. */
public class ConsoleView implements View {

    /** ANSI-команда "очистити екран і піднятись у верхній кут". 27 - це код символу ESC. */
    private static final String CLEAR = (char) 27 + "[H" + (char) 27 + "[2J";

    private final Settings settings;

    public ConsoleView(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void show(Island island) {
        Ecosystem ecosystem = island.ecosystem();
        StringBuilder out = new StringBuilder();
        if (settings.clearScreen()) out.append(CLEAR);
        out.append(String.format("=== ТАКТ %d ===%n", island.tickNumber()));
        if (settings.showMap()) out.append(map(island));
        for (Species species : ecosystem.byImportance()) {
            out.append(String.format("%s %-8s %6d%n",
                    species.icon(), species.title(), island.population(species)));
        }
        out.append(String.format("Разом тварин: %d | %s%n", island.totalAnimals(), island.statistics()));
        System.out.print(out);
    }

    /** Карта: в кожній клітинці - іконка найголовнішого мешканця. */
    private String map(Island island) {
        Ecosystem ecosystem = island.ecosystem();
        StringBuilder map = new StringBuilder();
        for (Cell[] row : island.cells()) {
            for (Cell cell : row) {
                Species species = cell.dominant(ecosystem);
                map.append(species == null ? "· " : species.icon());
            }
            map.append(System.lineSeparator());
        }
        return map.toString();
    }
}
