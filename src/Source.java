import java.util.Random;

// ИБn — бесконечный
// ИЗ1 - пуассоновский для бесконечных,
public class Source {
    private int frequency;
    private int number;
    private int nextRequestNumber;
    private Random random = new Random();

    public Source(int number, int frequency) {
        this.number = number;
        this.frequency = frequency;
        nextRequestNumber = 0;
    }


    public Pair<Double, Request> generate(double currentTime) {
        double nextRequestTime = getNextRequestTime(currentTime);
        return new Pair<>(nextRequestTime, new Request(number, nextRequestNumber++, currentTime));
    }

    private double getNextRequestTime(double currentTime) {
        return currentTime -1.0 / frequency * Math.log(random.nextDouble());
    }

    public int getFrequency() {
        return frequency;
    }
}