package domain;

class AcceptedRequest {
    private int deviceNumber;
    private Request acceptedRequest;
    private double timeAccept;

    public AcceptedRequest(int deviceNumber, Request doneRequest, double doneTime) {
        this.deviceNumber = deviceNumber;
        this.acceptedRequest = doneRequest;
        this.timeAccept = doneTime;
    }

    public double getTimeAccept() {
        return timeAccept;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public Request getAcceptedRequest() {
        return acceptedRequest;
    }
}