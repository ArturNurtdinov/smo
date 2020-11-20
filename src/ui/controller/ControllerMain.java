package ui.controller;

import domain.BuildConfig;
import domain.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import ui.App;


public class ControllerMain {
    private App app;
    private MainController mainController;

    @FXML
    private TextField sourceNumber;
    @FXML
    private TextField deviceNumber;
    @FXML
    private TextField timeLimit;
    @FXML
    private TextField bufferSizeText;
    @FXML
    private TextField alpha;
    @FXML
    private TextField beta;
    @FXML
    private TextField frequency;

    @FXML
    private RadioButton stepmode;
    @FXML
    private RadioButton automode;

    public void provideApp(App app, MainController mainController) {
        this.app = app;
        this.mainController = mainController;

        ToggleGroup modeGroup = new ToggleGroup();
        stepmode.setToggleGroup(modeGroup);
        automode.setToggleGroup(modeGroup);
    }

    @FXML
    public void startModulating() {
        accessField();
    }

    private void accessField() {
        if (sourceNumber.getText().isEmpty() || deviceNumber.getText().isEmpty()
                || timeLimit.getText().isEmpty() || bufferSizeText.getText().isEmpty()
                || alpha.getText().isEmpty() || beta.getText().isEmpty() || frequency.getText().isEmpty()) {
            app.showErrorAlert("Заполните все поля");
            return;
        }

        try {
            BuildConfig.SOURCE_NUMBER = Integer.parseInt(sourceNumber.getText());
            BuildConfig.DEVICE_NUMBER = Integer.parseInt(deviceNumber.getText());
            BuildConfig.TIME_LIMIT = Double.parseDouble(timeLimit.getText());
            BuildConfig.BUFFER_SIZE = Integer.parseInt(bufferSizeText.getText());
            BuildConfig.ALPHA = Double.parseDouble(alpha.getText());
            BuildConfig.BETA = Double.parseDouble(beta.getText());
            BuildConfig.FREQUENCY = Double.parseDouble(frequency.getText());

            mainController.start();
        } catch (Exception e) {
            app.showErrorAlert("Неверно заполнены поля");
        }

        if (stepmode.isSelected()) {
            app.showStepModeWindow();
        } else if (automode.isSelected()) {
            app.showAutoModeWindow();
        }
    }

    public MainController getMainController() {
        return mainController;
    }
}
