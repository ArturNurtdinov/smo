package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// Д1ОЗ1 – заполнение буферной памяти «по кольцу»
// Д1ОО2 — приоритет по номеру источника; постановка в буфер
// Д2П1 — приоритет по номеру прибора; выбор ИСТОЧНИКА по приоритету по номеру прибора
// Д2Б5 — приоритет по номеру источника, заявки в пакете. выбор заявки из буфера на обслуживание
// ОР1 — сводная таблица результатов;
// ОД3 — временные диаграммы, текущее состояние
public class MainController {
    private int sourceCount;
    private int deviceCount;
    private int bufferSize;
    private double alpha;
    private double beta;
    private double currentTime;

    private Buffer buffer;
    private SourceManager sourceManager;
    private DeviceManager deviceManager;
    private Consumer<Object> infoCollector;
    private double lastRequestTime;

    // stats
    private Map<Integer, Integer> sourceRequestsCount;
    private Map<Integer, Double> sourceWaitingTime;
    private Map<Integer, Integer> sourceRejectedCount;
    private Map<Integer, Double> totalSystemTime;
    private Map<Integer, Double> totalTimeOnDevice;
    private Map<Integer, Double> devicesTime;

    public MainController(Consumer<Object> infoCollector) {
        this.infoCollector = infoCollector;
        initialize();

        sourceRequestsCount = new HashMap<>();
        sourceRejectedCount = new HashMap<>();
        totalSystemTime = new HashMap<>();
        sourceWaitingTime = new HashMap<>();
        totalTimeOnDevice = new HashMap<>();
        devicesTime = new HashMap<>();
    }

    private void initialize() {
        sourceCount = BuildConfig.SOURCE_NUMBER;
        deviceCount = BuildConfig.DEVICE_NUMBER;
        bufferSize = BuildConfig.BUFFER_SIZE;
        alpha = BuildConfig.ALPHA;
        beta = BuildConfig.BETA;
        currentTime = 0;
        sourceManager = new SourceManager(sourceCount);
        deviceManager = new DeviceManager(deviceCount, alpha, beta);
        buffer = new Buffer(bufferSize);
        deviceManager.setCurrentPackage(BuildConfig.SOURCE_NUMBER - 1);
    }

    private void checkFreeDevices() {
        List<AcceptedRequest> acceptedRequests = deviceManager.getAcceptedRequests(currentTime);
        for (AcceptedRequest acceptedRequest : acceptedRequests) {
            Request doneRequest = acceptedRequest.getAcceptedRequest();
            if (doneRequest != null) {
                infoCollector.accept("Прибор " + acceptedRequest.getDeviceNumber()
                        + " освободился в " + acceptedRequest.getTimeAccept() +
                        ", номер источника заявки - " + doneRequest.getSourceNumber());
                totalSystemTime.put(doneRequest.getSourceNumber(), totalSystemTime.getOrDefault(doneRequest.getSourceNumber(), 0.0)
                        + acceptedRequest.getTimeAccept() - doneRequest.getGeneratedTime());
            }
            int packageNumber = deviceManager.getCurrentPackage();
            if (!buffer.isEmpty()) {
                Request requestForDevice;
                if (packageNumber != -1) {
                    requestForDevice = buffer.get(packageNumber);
                    if (requestForDevice == null) {
                        infoCollector.accept("Пакетная обработка закончилась, нужно получить новый пакет");
                        requestForDevice = buffer.get();
                        deviceManager.setCurrentPackage(requestForDevice.getSourceNumber());
                    }
                } else {
                    requestForDevice = buffer.get();
                    deviceManager.setCurrentPackage(requestForDevice.getSourceNumber());
                }

                int deviceNumber = deviceManager.executeRequest(requestForDevice, currentTime);
                totalTimeOnDevice.put(requestForDevice.getSourceNumber(), totalTimeOnDevice.getOrDefault(requestForDevice.getSourceNumber(), 0.0)
                        + deviceManager.getDevice(deviceNumber).getTimeFreed() - currentTime);
                sourceWaitingTime.put(requestForDevice.getSourceNumber(),
                        sourceWaitingTime.getOrDefault(requestForDevice.getSourceNumber(), 0.0) + (currentTime - requestForDevice.getGeneratedTime()));
                devicesTime.put(deviceNumber, devicesTime.getOrDefault(deviceNumber, 0.0)
                        + deviceManager.getDevice(deviceNumber).getTimeFreed() - currentTime);
                infoCollector.accept("Заявка от источника номер " + requestForDevice.getSourceNumber() +
                        " загружена на прибор номер " + deviceNumber + " номер обрабатываемого пакета - " + deviceManager.getCurrentPackage() + " в " + currentTime);
            }
        }
    }

    public void start() {
        initialize();
        while (currentTime < BuildConfig.TIME_LIMIT) {
            Pair<Double, Request> nextRequestPair = sourceManager.getNextRequest(currentTime);
            Request nextRequest = nextRequestPair.getSecond();
            lastRequestTime = nextRequestPair.getFirst();
            currentTime += nextRequestPair.getFirst();
            sourceRequestsCount.put(nextRequest.getSourceNumber(), sourceRequestsCount.getOrDefault(nextRequest.getSourceNumber(), 0) + 1);
            infoCollector.accept("Источник номер " + nextRequest.getSourceNumber() + " создал заявку в " + nextRequest.getGeneratedTime());
            checkFreeDevices();

            if (buffer.addToBuffer(nextRequest)) {
                infoCollector.accept("Заявка добавлена без удалений");
            } else {
                sourceRejectedCount.put(nextRequest.getSourceNumber(), sourceRejectedCount.getOrDefault(nextRequest.getSourceNumber(), 0) + 1);
                infoCollector.accept("Заявка либо попала в буфер c замещением, либо ушла в отказ сама");
            }
            infoCollector.accept("На данный момент в буфере заявки от следующих источников:");
            infoCollector.accept(buffer.getRequests().stream().filter(Objects::nonNull).map(Request::getSourceNumber).collect(Collectors.toList()));
        }
        System.out.println(devicesTime);
    }

    public Map<Integer, Double> getSourceWaitingTime() {
        return sourceWaitingTime;
    }

    public Map<Integer, Double> getTotalTimeOnDevice() {
        return totalTimeOnDevice;
    }

    public Map<Integer, Integer> getSourceRejectedCount() {
        return sourceRejectedCount;
    }

    public Map<Integer, Double> getTotalSystemTime() {
        return totalSystemTime;
    }

    public Map<Integer, Integer> getSourceRequestsCount() {
        return sourceRequestsCount;
    }

    public Map<Integer, Double> getDevicesTime() {
        return devicesTime;
    }

    public double getCurrentTime() {
        return currentTime;
    }
}
