package com.geekbrains.client;

import com.geekbrains.util.FilesUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
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
    public Label storeLoginLabel;

    public Network network;
    public FilesUtils filesUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = Network.getInstance();
        network.connect();
        filesUtils = new FilesUtils();
    }

    public void addFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add file to cloud");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All files ", "*.*");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(network.getStageChange().getCurrentStage());
        if (file != null) {
            filesUtils.sendFile(file.toString(), storeLoginLabel.getText(), "", network.getObjectEncoderOutputStream(),
                    null);
        }
    }

    public void deleteFile(ActionEvent actionEvent) throws IOException {
        if (!storeServerVeiw.getSelectionModel().isEmpty()) {
            String fileName = storeServerVeiw.getSelectionModel().getSelectedItems().toString();
            openDialogWindow("/dialogDelete.fxml", fileName, storeLoginLabel.getText());
        } else {
            network.showCONFIRMATION("Выберите файл для удаления");
        }
    }

    public void renameFile(ActionEvent actionEvent) {
        if (!storeServerVeiw.getSelectionModel().isEmpty()) {
            String fileName = storeServerVeiw.getSelectionModel().getSelectedItems().toString();
            openDialogWindow("/dialogRename.fxml", fileName, storeLoginLabel.getText());
        } else {
            network.showCONFIRMATION("Выберите файл для переименования");
        }
    }

    public void closeWindow(ActionEvent actionEvent) {
        network.closeConnection();
    }

    public void saveFile(ActionEvent actionEvent) {
        if (!storeServerVeiw.getSelectionModel().isEmpty()) {
            String fileName = storeServerVeiw.getSelectionModel().getSelectedItems().toString();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file from cloud");
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All files ", "*.*");
            fileChooser.getExtensionFilters().add(filter);
            fileChooser.setInitialFileName(fileName);
            File file = fileChooser.showSaveDialog((network.getStageChange().getCurrentStage()));
            if (file != null) {
                network.sendSaveFileAskToServer(fileName, file.getParent(), storeLoginLabel.getText());
            }
        } else {
            network.showCONFIRMATION("Выберите файл для выгрузки с сервера");
        }
    }

    public void openDialogWindow(String fxml, String fileName, String login) {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
                Parent root1 = (Parent) fxmlLoader.load();

                if (fxml.equals("/dialogDelete.fxml")) {
                    DialogDeleteController controller = fxmlLoader.<DialogDeleteController>getController();
                    controller.setFileName(fileName);
                    controller.setLogin(login);
                }
                if (fxml.equals("/dialogRename.fxml")) {
                    DialogRenameController controller = fxmlLoader.<DialogRenameController>getController();
                    controller.setFileName(fileName);
                    controller.setLogin(login);
                    controller.setListFiles(storeServerVeiw.getItems());
                }
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Confirmation");
                stage.setScene(new Scene(root1));
                stage.resizableProperty().set(false);
                stage.show();
            } catch (IOException e) {
                log.error("Ошибка при открытии окна", e);
            }
        });
    }
}
