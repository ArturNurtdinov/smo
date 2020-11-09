import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
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
    private Random random;
    private double alpha;
    private double beta;
    private int requestsNumber;
    private double currentTime;

    private Buffer buffer;
    private SourceManager sourceManager;
    private DeviceManager deviceManager;

    public MainController() {
        sourceCount = BuildConfig.SOURCE_NUMBER;
        deviceCount = BuildConfig.DEVICE_NUMBER;
        bufferSize = BuildConfig.BUFFER_SIZE;
        random = new Random();
        alpha = BuildConfig.ALPHA;
        beta = BuildConfig.BETA;
        requestsNumber = BuildConfig.REQUESTS_NUMBER;
        currentTime = 0;

        sourceManager = new SourceManager(sourceCount);
        deviceManager = new DeviceManager(deviceCount, alpha, beta);
        buffer = new Buffer(bufferSize);
        deviceManager.setCurrentPackage(2);
    }

    public void modulateWork() {
        for (int i = 0; i < requestsNumber; ++i) {
            Pair<Double, Request> nextRequestPair = sourceManager.getNextRequest(currentTime);
            Request nextRequest = nextRequestPair.getSecond();
            currentTime += nextRequestPair.getFirst();
            checkFreeDevices();
            Main.print("Источник номер " + nextRequest.getSourceNumber() + " создал заявку в " + nextRequest.getGeneratedTime());

            if (buffer.addToBuffer(nextRequest)) {
                Main.print("Заявка добавлена без удалений");
            } else {
                Main.print("Заявка либо попала в буфер c замещением, либо ушла в отказ сама");
            }
            Main.print("На данный момент в буфере заявки от следующих источников:");
            Main.print(buffer.getRequests().stream().filter(Objects::nonNull).map(Request::getSourceNumber).collect(Collectors.toList()));
        }
    }

    private void checkFreeDevices() {
        List<AcceptedRequest> acceptedRequests = deviceManager.getAcceptedRequests(currentTime);
        for (AcceptedRequest acceptedRequest : acceptedRequests) {
            Request doneRequest = acceptedRequest.getAcceptedRequest();
            if (doneRequest != null) {
                Main.print("Прибор " + acceptedRequest.getDeviceNumber()
                        + " освободился в " + acceptedRequest.getTimeAccept() +
                        ", номер источника заявки - " + doneRequest.getSourceNumber());
            }
            int packageNumber = deviceManager.getCurrentPackage();
            if (!buffer.isEmpty()) {

                Request requestFromBuf;
                if (packageNumber != -1) {
                    requestFromBuf = buffer.get(packageNumber);
                    if (requestFromBuf == null) {
                        requestFromBuf = buffer.get();
                        deviceManager.setCurrentPackage(requestFromBuf.getSourceNumber());
                    }
                } else {
                    requestFromBuf = buffer.get();
                    deviceManager.setCurrentPackage(requestFromBuf.getSourceNumber());
                }

                int deviceNumber = deviceManager.executeRequest(requestFromBuf, currentTime);
                Main.print("Заявка от источника номер " + requestFromBuf.getSourceNumber() +
                        " загружена на прибор номер " + deviceNumber);
            }
        }
    }

    public void start() {
        modulateWork();
    }
}
