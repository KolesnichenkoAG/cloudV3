package com.geekbrains.client;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogDeleteController implements Initializable {
    public Button DelYesButton;
    public Button DelNoButton;

    private Network network;
    private String login;
    private String fileName;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void deleteFile(ActionEvent actionEvent) {
        network.sendDeleteFileMessageToServer(fileName, login);
        closeWindow();
    }

    public void closeWindow() {
        Stage stage = (Stage) DelYesButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
    }
}
