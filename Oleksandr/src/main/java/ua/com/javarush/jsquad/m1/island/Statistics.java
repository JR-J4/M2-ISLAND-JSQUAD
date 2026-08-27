package ua.com.javarush.jsquad.m1.island;

/** Що сталося на острові за такт. Сама себе й друкує (див. toString). */
public final class Statistics {

    private int born;
    private int eaten;
    private int starved;
    private long nanos;

    public void reset() {
        born = 0;
        eaten = 0;
        starved = 0;
    }

    public void registerBirth() { born++; }
    public void registerEaten() { eaten++; }
    public void registerStarved() { starved++; }
    public void tookNanos(long value) { nanos = value; }

    public int born() { return born; }
    public int eaten() { return eaten; }
    public int starved() { return starved; }
    public double millis() { return nanos / 1_000_000.0; }

    @Override
    public String toString() {
        return String.format("народилось: %d | зʼїдено: %d | з голоду: %d | такт: %.1f мс",
                born, eaten, starved, millis());
    }
}
