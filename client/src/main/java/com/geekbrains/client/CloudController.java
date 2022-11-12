package com.geekbrains.client;

import com.geekbrains.util.FilesUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class CloudController implements Initializable {
    public ListView storeServerVeiw;
    public Button storeAddButton;
    public Button storeSaveButton;
    public Button storeDelButton;
    public Button storeRenameButton;
    public Button storeCloseButton;
    @FXML
    public Label storeUserLabel;

    public Network network;
    public FilesUtils filesUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
        network.connect();
        filesUtils = new FilesUtils();
    }
}
