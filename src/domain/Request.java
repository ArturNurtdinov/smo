package domain;

import java.util.Objects;

public class Request {
    private int sourceNumber;
    private int requestNumber;
    private double generatedTime;

    public Request(int sourceNumber, int requestNumber, double generatedTime) {
        this.sourceNumber = sourceNumber;
        this.generatedTime = generatedTime;
        this.requestNumber = requestNumber;
    }

    public double getGeneratedTime() {
        return generatedTime;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    @Override
    public String toString() {
        return "domain.Request{" +
                "sourceNumber=" + sourceNumber +
                ", generatedTime=" + generatedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return sourceNumber == request.sourceNumber &&
                Double.compare(request.generatedTime, generatedTime) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceNumber, generatedTime);
    }
}
