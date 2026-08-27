package ua.com.javarush.jsquad.m1;

import ua.com.javarush.jsquad.m1.island.ConsoleView;
import ua.com.javarush.jsquad.m1.config.Settings;
import ua.com.javarush.jsquad.m1.island.Simulation;

public class Main {

    public static void main(String[] args) {
        Settings settings = Settings.defaults();          // усі параметри - у Settings.java

        // Приклад: змінити щось саме для цього запуску, не чіпаючи Settings.java
        // settings.rows(50).cols(100).showMap(false).maxTicks(0).seed(42);

        new Simulation(settings, new ConsoleView(settings)).run();
    }
}
