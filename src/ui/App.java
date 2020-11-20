package ui;

import domain.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ui.controller.ControllerAutomode;
import ui.controller.ControllerMain;

import java.io.IOException;

public class App extends Application {

    private Stage primaryStage;
    private MainController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainController = new MainController(this::infoCollector);
        showMainWindow();
    }

    public void showMainWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/res/main.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("СМО");
            primaryStage.show();

            ControllerMain controller = loader.getController();
            controller.provideApp(this, mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAutoModeWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/res/automode.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Автоматический режим");
            primaryStage.show();

            ControllerAutomode controller = loader.getController();
            controller.provideApp(this, mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showStepModeWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/res/main.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Пошаговый режим");
            primaryStage.show();

            ControllerMain controller = loader.getController();
            controller.provideApp(this, mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showErrorAlert(String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Что-то пошло не так. Обратите внимание на информацию ниже.");
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void infoCollector(Object object) {
        System.out.println(object);
    }
}
