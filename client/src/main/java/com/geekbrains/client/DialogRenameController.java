package com.geekbrains.client;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DialogRenameController implements Initializable {
    public Button RenRenameButton;
    public Button RenCancelButton;
    public TextField RenNewNameField;

    private Network network;
    private String login;
    private String fileName;
    private List<String> listFiles;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setListFiles(List<String> listFiles) {
        this.listFiles = listFiles;
    }

    public void renameFile(ActionEvent actionEvent) {
        if (!RenNewNameField.getText().isEmpty()) {
            if (listFiles.indexOf(RenNewNameField.getText()) == -1) {
                network.sendRenameMessageToServer(fileName, RenNewNameField.getText(), login);
                closeWindow();
            } else network.showCONFIRMATION("Файл с таким именем уже существует. Введите другое имя");
        } else {
            network.showCONFIRMATION("Введите новое имя файла " + fileName);
        }
    }

    public void closeWindow() {
        Stage stage = (Stage) RenNewNameField.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
    }
}
