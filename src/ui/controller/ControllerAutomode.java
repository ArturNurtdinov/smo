package ui.controller;

import domain.MainController;
import domain.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.App;
import ui.controller.model.DeviceStatistic;
import ui.controller.model.SourceStatistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ControllerAutomode {
    private App app;
    private MainController mainController;
    private List<SourceStatistic> statisticList;

    @FXML
    private TableView<SourceStatistic> tableSources;
    @FXML
    private TableColumn<?, ?> sourceColumn;
    @FXML
    private TableColumn<?, ?> countColumn;
    @FXML
    private TableColumn<?, ?> rejectColumn;
    @FXML
    private TableColumn<?, ?> inSystemColumn;
    @FXML
    private TableColumn<?, ?> waitingColumn;
    @FXML
    private TableColumn<?, ?> onDeviceColumn;
    @FXML
    private TableColumn<?, ?> disp4Col;
    @FXML
    private TableColumn<?, ?> disp5Col;

    @FXML
    private TableView<DeviceStatistic> devicesTable;
    @FXML
    private TableColumn<?, ?> deviceColumn;
    @FXML
    private TableColumn<?, ?> coefColumn;

    @FXML
    private TextArea bufferText;


    public void provideApp(App app, MainController mainController) {
        this.app = app;
        this.mainController = mainController;
        statisticList = new ArrayList<>();
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourceNumber"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("requestsCount"));
        rejectColumn.setCellValueFactory(new PropertyValueFactory<>("rejectProb"));
        inSystemColumn.setCellValueFactory(new PropertyValueFactory<>("inSystemTime"));
        waitingColumn.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        onDeviceColumn.setCellValueFactory(new PropertyValueFactory<>("onDeviceTime"));
        disp4Col.setCellValueFactory(new PropertyValueFactory<>("disp4"));
        disp5Col.setCellValueFactory(new PropertyValueFactory<>("disp5"));
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceNumber"));
        coefColumn.setCellValueFactory(new PropertyValueFactory<>("useCoef"));
        loadStat();
    }

    private void loadStat() {
        Map<Integer, Integer> sourceRequestsNumbers = mainController.getSourceRequestsCount();
        Map<Integer, Integer> sourceRejectedCount = mainController.getSourceRejectedCount();
        Map<Integer, Double> totalSystemTime = mainController.getTotalSystemTime();
        Map<Integer, Double> totalWaitingTime = mainController.getSourceWaitingTime();
        Map<Integer, Double> timeOnDevice = mainController.getTotalTimeOnDevice();

        for (int j = 0; j < BuildConfig.SOURCE_NUMBER; j++) {
            statisticList.add(new SourceStatistic(j));
            // total requests
            statisticList.get(j).setRequestsCount(sourceRequestsNumbers.getOrDefault(j, 0));
            // rejected
            double stat = (double) sourceRejectedCount.getOrDefault(j, 0) / sourceRequestsNumbers.getOrDefault(j, 0);
            int denominator = sourceRequestsNumbers.getOrDefault(j, 0);
            int numerator = sourceRejectedCount.getOrDefault(j, 0);
            if (denominator == 0 || numerator == 0 || sourceRejectedCount.size() == 0) {
                statisticList.get(j).setRejectProb(0);
            } else {
                statisticList.get(j).setRejectProb(stat);
            }
            // system time
            statisticList.get(j).setInSystemTime(totalSystemTime.getOrDefault(j, 0.0) / sourceRequestsNumbers.getOrDefault(j, 0));
            // waiting time
            statisticList.get(j).setWaitingTime(totalWaitingTime.getOrDefault(j, 0.0) / sourceRequestsNumbers.getOrDefault(j, 0));
            // device time
            statisticList.get(j).setOnDeviceTime(timeOnDevice.getOrDefault(j, 0.0) / sourceRequestsNumbers.getOrDefault(j, 0));
            // disp4
            statisticList.get(j).setDisp4(totalWaitingTime.getOrDefault(j, 0.0) / totalSystemTime.getOrDefault(j, 0.0));
            // disp5
            statisticList.get(j).setDisp5(timeOnDevice.getOrDefault(j, 0.0) / totalSystemTime.getOrDefault(j, 0.0));
        }


        List<DeviceStatistic> deviceStatistics = new ArrayList<>();
        Map<Integer, Double> deviceWorkTime = mainController.getDevicesTime();
        for (int j = 0; j < BuildConfig.DEVICE_NUMBER; j++) {
            deviceStatistics.add(new DeviceStatistic(j,
                    deviceWorkTime.getOrDefault(j, 0.0) / BuildConfig.TIME_LIMIT));
        }

        tableSources.getItems().addAll(statisticList);
        devicesTable.getItems().addAll(deviceStatistics);

        // buffer
        Buffer buffer = mainController.getBuffer();
        List<String> leftRequests = new ArrayList<>(BuildConfig.BUFFER_SIZE);
        for (int i = 0; i < buffer.getRequests().size(); i++) {
            if (buffer.getAt(i) == null) {
                leftRequests.add("Свободно");
            } else {
                leftRequests.add(buffer.getAt(i).getSourceNumber() + "");
            }
        }
        bufferText.setText(leftRequests.toString());
    }
}
