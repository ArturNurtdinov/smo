package ui.controller.model;

public class Statistic {
    private String object;
    private String name;
    private double stat;

    public Statistic(String object, String name, double stat) {
        this.object = object;
        this.name = name;
        this.stat = stat;
    }


    public double getStat() {
        return stat;
    }

    public String getName() {
        return name;
    }

    public String getObject() {
        return object;
    }
}
