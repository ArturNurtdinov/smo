package domain;

import java.util.Random;

// ИБn — бесконечный
// ИЗ1 - пуассоновский для бесконечных,
public class Source {
    private double frequency;
    private int number;
    private static int nextRequestNumber;
    private Random random = new Random();

    public Source(int number, double frequency) {
        this.number = number;
        this.frequency = frequency;
        nextRequestNumber = 0;
    }


    public Pair<Double, Request> generate(double currentTime) {
        double nextRequestTime = getNextRequestTime(currentTime);
        return new Pair<>(nextRequestTime, new Request(number, nextRequestNumber++, currentTime + nextRequestTime));
    }

    private double getNextRequestTime(double currentTime) {
        return - 1 / frequency * Math.log(random.nextDouble());
    }

    public double getFrequency() {
        return frequency;
    }
}