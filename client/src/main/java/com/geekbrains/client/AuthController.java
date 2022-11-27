package com.geekbrains.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class AuthController implements Initializable {

    public TextField authLoginField;
    public PasswordField authPasswordField;
    @FXML
    public Button authEnterButton;
    public Button authRegButton;

    private Network network;
    private StageChange stageChange;

    public void tryToAuth(ActionEvent actionEvent) throws IOException {
        if (authLoginField.getText().isEmpty() || authPasswordField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Введите логин и пароль", ButtonType.OK).showAndWait();
            return;
        }
        if (network.connect()) {
            network.sendAuthMessageToServer(authLoginField.getText(), authPasswordField.getText());
        }
        ;
    }

    public void registrationUser(ActionEvent actionEvent) throws IOException {
        network.openWindow("registration.fxml", "Регистрация нового пользователя",
                "", "", null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
        Platform.runLater(() -> {
            stageChange = StageChange.getInstance();
            stageChange.getCurrentStage().setOnCloseRequest(event -> network.closeConnection());
        });
    }
}
