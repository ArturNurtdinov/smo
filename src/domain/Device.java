package domain;

import java.util.Random;

// П32 - равномерный.
public class Device {
    private int number;
    private Request request;
    private double alpha;
    private double beta;
    private double timeFreed;
    private Random random = new Random();

    public Device(int number, double alpha, double beta) {
        this.number = number;
        this.alpha = alpha;
        this.beta = beta;
        request = null;
        timeFreed = 0;
    }

    public void execute(Request request, double currentTime) {
        double timeForExecution = getTimeForExecution();
        this.request = request;
        this.timeFreed = currentTime + timeForExecution;
    }

    public double getTimeFreed() {
        return timeFreed;
    }

    public void free() {
        this.request = null;
        this.timeFreed = 0.0;
    }

    public boolean isBusy() {
        return request != null;
    }

    public Request getRequest() {
        return request;
    }

    public int getNumber() {
        return number;
    }

    private double getTimeForExecution() {
        return Math.random() * (beta - alpha) + alpha;
    }
}
