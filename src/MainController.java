import java.util.List;
import java.util.Objects;
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

    public MainController() {
        sourceCount = BuildConfig.SOURCE_NUMBER;
        deviceCount = BuildConfig.DEVICE_NUMBER;
        bufferSize = BuildConfig.BUFFER_SIZE;
        alpha = BuildConfig.ALPHA;
        beta = BuildConfig.BETA;
        currentTime = 0;

        sourceManager = new SourceManager(sourceCount);
        deviceManager = new DeviceManager(deviceCount, alpha, beta);
        buffer = new Buffer(bufferSize);
        deviceManager.setCurrentPackage(2);
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
                Request requestForDevice;
                if (packageNumber != -1) {
                    requestForDevice = buffer.get(packageNumber);
                    if (requestForDevice == null) {
                        Main.print("Пакетная обработка закончилась, нужно получить новый пакет");
                        requestForDevice = buffer.get();
                        deviceManager.setCurrentPackage(requestForDevice.getSourceNumber());
                    }
                } else {
                    requestForDevice = buffer.get();
                    deviceManager.setCurrentPackage(requestForDevice.getSourceNumber());
                }

                int deviceNumber = deviceManager.executeRequest(requestForDevice, currentTime);
                Main.print("Заявка от источника номер " + requestForDevice.getSourceNumber() +
                        " загружена на прибор номер " + deviceNumber + " номер обрабатываемого пакета - " + deviceManager.getCurrentPackage());
            }
        }
    }

    public void start() {
        while (currentTime < BuildConfig.TIME_LIMIT) {
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
}
