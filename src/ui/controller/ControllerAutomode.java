package ui.controller;

import domain.MainController;
import domain.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.App;
import ui.controller.model.Statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ControllerAutomode {
    private App app;
    private MainController mainController;
    private List<Statistic> statisticList;

    @FXML
    private TableView<Statistic> table;
    @FXML
    private TableColumn<?, ?> objectColumn;
    @FXML
    private TableColumn<?, ?> statNameColumn;
    @FXML
    private TableColumn<?, ?> statColumn;


    public void provideApp(App app, MainController mainController) {
        this.app = app;
        this.mainController = mainController;
        statisticList = new ArrayList<>();
        objectColumn.setCellValueFactory(new PropertyValueFactory<>("object"));
        statNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statColumn.setCellValueFactory(new PropertyValueFactory<>("stat"));
        loadStat();
    }

    private void loadStat() {
        Map<Integer, Integer> sourceRequestsNumbers = mainController.getSourceRequestsCount();
        sourceRequestsNumbers.keySet().forEach(key -> {
            statisticList.add(new Statistic("Источник " + key, "Количество сгенерированных заявок",
                    sourceRequestsNumbers.getOrDefault(key, 0)));
        });

        Map<Integer, Integer> sourceRejectedCount = mainController.getSourceRejectedCount();
        System.out.println(sourceRejectedCount);
        sourceRejectedCount.keySet().forEach(key -> {
            double stat = (double) sourceRejectedCount.getOrDefault(key, 0) / sourceRequestsNumbers.getOrDefault(key, 0);
            int denominator = sourceRequestsNumbers.getOrDefault(key, 0);
            int numerator = sourceRejectedCount.getOrDefault(key, 0);
            if (denominator == 0 || numerator == 0) {
                statisticList.add(new Statistic("Источник " + key, "Вероятность отказа", 0));
            } else {
                statisticList.add(new Statistic("Источник " + key, "Вероятность отказа", stat));
            }
        });
        if (sourceRejectedCount.size() == 0) {
            sourceRequestsNumbers.keySet().forEach(key -> {
                statisticList.add(new Statistic("Источник " + key, "Вероятность отказа", 0));
            });
        }

        List<Pair<Integer, Double>> systemTime = new ArrayList<>();
        Map<Integer, Double> totalSystemTime = mainController.getTotalSystemTime();
        totalSystemTime.keySet().forEach(key -> {
            systemTime.add(new domain.Pair<>(key,
                    totalSystemTime.getOrDefault(key, 0.0) / sourceRequestsNumbers.getOrDefault(key, 0)));
            statisticList.add(new Statistic("Источник " + key, "Среднее время заявок в системе",
                    totalSystemTime.getOrDefault(key, 0.0) / sourceRequestsNumbers.getOrDefault(key, 0)));
        });

        List<Pair<Integer, Double>> waitingTime = new ArrayList<>();
        Map<Integer, Double> totalWaitingTime = mainController.getSourceWaitingTime();
        totalWaitingTime.keySet().forEach(key -> {
            waitingTime.add(new domain.Pair<>(key,
                    totalWaitingTime.getOrDefault(key, 0.0) / sourceRequestsNumbers.getOrDefault(key, 0)));
            statisticList.add(new Statistic("Источник " + key, "Среднее время ожидания заявок каждого источника в системе",
                    totalWaitingTime.getOrDefault(key, 0.0) / sourceRequestsNumbers.getOrDefault(key, 0)));
        });

        Map<Integer, Double> timeOnDevice = mainController.getTotalTimeOnDevice();
        timeOnDevice.keySet().forEach(key -> {
            statisticList.add(new Statistic("Источник " + key, "Среднее время обслуживания заявок источника",
                    timeOnDevice.getOrDefault(key, 0.0) / sourceRequestsNumbers.getOrDefault(key, 0)));
        });

        // дисперсии
        double matWait = 0.0;
        for (Pair<Integer, Double> pair : systemTime) {
            matWait += pair.getFirst() * pair.getSecond();
        }
        double disp = 0.0;
        for (Pair<Integer, Double> pair : systemTime) {
            disp += pair.getFirst() * pair.getFirst() * pair.getSecond() - matWait * matWait;
        }

        statisticList.add(new Statistic("Система", "Дисперсия среднего времени ожидания заявок", disp));

        matWait = 0.0;
        disp = 0.0;
        for (Pair<Integer, Double> pair : waitingTime) {
            matWait += pair.getFirst() * pair.getSecond();
        }
        for (Pair<Integer, Double> pair : waitingTime) {
            disp += pair.getFirst() * pair.getFirst() * pair.getSecond() - matWait * matWait;
        }

        statisticList.add(new Statistic("Система", "Дисперсия среднего времени обслуживания заявок", disp));

        Map<Integer, Double> deviceWorkTime = mainController.getDevicesTime();
        deviceWorkTime.keySet().forEach(key -> {
            statisticList.add(new Statistic("Прибор " + key, "Коэффициент использования",
                    deviceWorkTime.getOrDefault(key, 0.0) / BuildConfig.TIME_LIMIT));
        });


        table.getItems().addAll(statisticList);
    }
}
