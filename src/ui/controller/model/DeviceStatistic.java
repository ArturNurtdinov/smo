package ui.controller.model;

public class DeviceStatistic {
    private final int deviceNumber;
    private final double useCoef;

    public DeviceStatistic(int deviceNumber, double useCoef) {
        this.deviceNumber = deviceNumber;
        this.useCoef = useCoef;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public double getUseCoef() {
        return useCoef;
    }
}
