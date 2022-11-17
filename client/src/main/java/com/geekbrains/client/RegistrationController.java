package com.geekbrains.client;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class RegistrationController implements Initializable {
    public TextField regLastNameField;
    public TextField regNameField;
    public TextField regEmailField;
    public TextField regLoginField;
    public PasswordField regPasswordField;
    public PasswordField regConfirmPasswordField;
    public Button regRegistrButton;
    public Button regCloseButton;

    private Network network;

    public void registration(ActionEvent actionEvent) throws IOException {
        if (regLastNameField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Укажите фамилию", ButtonType.OK).showAndWait();
            return;
        }
        if (regNameField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Укажите имя", ButtonType.OK).showAndWait();
            return;
        }
        if (regLoginField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Укажите логин", ButtonType.OK).showAndWait();
            return;
        }
        if (regEmailField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Укажите email", ButtonType.OK).showAndWait();
            return;
        }
        if (regPasswordField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Укажите пароль", ButtonType.OK).showAndWait();
            return;
        }
        if (regConfirmPasswordField.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Укажите пароль для подтверждения", ButtonType.OK).showAndWait();
            return;
        }
        if (!regPasswordField.getText().equals(regConfirmPasswordField.getText())) {
            new Alert(Alert.AlertType.WARNING, "Пароли не совпадают", ButtonType.OK).showAndWait();
            return;
        }
        if (network.connect()) {
            String hashed = BCrypt.hashpw(regPasswordField.getText(), BCrypt.gensalt());
            network.sendRegMessageToServer(regLastNameField.getText(), regNameField.getText(),
                    regEmailField.getText(), regLoginField.getText(), hashed);
        }
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        network.openWindow("/cloud-start.fxml", "Авторизация пользователя", "", "", null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
    }
}
