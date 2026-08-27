package ua.com.javarush.jsquad.m1.island;

import ua.com.javarush.jsquad.m1.config.Settings;

/** Крутить такти, поки не спрацює умова зупинки з налаштувань. */
public class Simulation {

    private final Settings settings;
    private final View view;
    private final Island island;

    public Simulation(Settings settings, View view) {
        this.settings = settings;
        this.view = view;
        this.island = new Island(settings);
    }

    public void run() {
        while (true) {
            island.tick();                               // одне завдання = один такт (легко віддати пулу потоків)
            view.show(island);
            String reason = stopReason();
            if (reason != null) {
                System.out.println("Симуляція завершена: " + reason);
                return;
            }
            sleep(settings.tickMillis());
        }
    }

    public Island island() {
        return island;
    }

    private String stopReason() {
        if (settings.stopWhenNoAnimals() && island.totalAnimals() == 0) return "усі тварини загинули";
        if (settings.maxTicks() > 0 && island.tickNumber() >= settings.maxTicks()) {
            return "минуло " + settings.maxTicks() + " тактів";
        }
        return null;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
