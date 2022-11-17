package com.geekbrains.client;

import com.geekbrains.common.*;
import com.geekbrains.util.FilesUtils;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class Network {
    private static Network instance;
    private StageChange stageChange;

    private CloudController controller;
    private Socket socket;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    private FilesUtils filesUtils;
    private String userName;
    private String login;
    private Thread thread;

    private Network() {
    }

    public ObjectEncoderOutputStream getObjectEncoderOutputStream() {
        return os;
    }

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    public StageChange getStageChange() {
        return stageChange;
    }

    public boolean connect() {
        if (socket != null && !socket.isClosed()) {
            return true;
        }
        try {
            socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            filesUtils = new FilesUtils();

            thread = new Thread(this::read);
            thread.start();

            return true;
        } catch (Exception e) {
            showError("Невозможно подключиться к серверу");
            return false;
        }
    }

    private void read() {
        try {
            while (true) {
                Message message = (Message) is.readObject();
                switch (message.getTypeMessage()) {
                    case AUTH_OK:
                        AuthOK authOK = (AuthOK) message;
                        log.debug("Сообщение от сервера: успешная авторизация");
                        userName = authOK.getUsername();
                        login = authOK.getLogin();
                        sendListMessageToServer(authOK.getLogin());
                        break;

                    case FILE_MESSAGE:
                        FileMessage fileMessage = (FileMessage) message;
                        Path dirTmp = Paths.get(Paths.get("").toAbsolutePath().toString(), "client", // возможно переделать название папки
                                "tmp", fileMessage.getLogin());
                        filesUtils.saveFile(dirTmp, Paths.get(fileMessage.getDirDestination()), fileMessage);
                        break;

                    case AUTH_ERROR:
                        showError("Неверные логин или пароль, либо указан не существующий логин!");
                        break;

                    case REG_OK:
                        openWindow("/cloud-start.fxml", "Облачное хранилище", "", "", null);
                        showCONFIRMATION("Регистрация прошла успешно");
                        break;

                    case REG_ERROR:
                        RegError regError = (RegError) message;
                        if (!regError.getLogin().isEmpty()) {
                            showError("Логин " + regError.getLogin() + " уже используеться");
                        } else {
                            showError("Email " + regError.getEmail() + " уже используеться");
                        }
                        break;

                    case LIST_MESSAGE:
                        ListMessage listMessage = (ListMessage) message;
                        if (controller == null) {
                            openWindow("/cloud.fxml", "Облачное хранилище", userName, login, listMessage);
                        } else {
                            updateFileList(listMessage.getListFiles(), controller);
                        }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке сообщений на сервер");
        }
    }

    private void updateFileList(List<String> listFiles, CloudController controller) {
        Platform.runLater(() -> {
            controller.storeServerVeiw.getItems().clear();
            if (listFiles != null) {
                controller.storeServerVeiw.getItems().addAll(listFiles);
            }
        });
    }

    public void sendAuthMessageToServer(String login, String password) {
        try {
            os.writeObject(new AuthAsk(login, password));
            os.flush();
            log.debug("Сообщение от клиента: запрос авторизации пользователя" + login);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке запроса авторизации на сервер");
        }
    }

    public void sendSaveFileAskToServer(String fileName, String dirDestination, String login) {
        try {
            os.writeObject(new FileAsk(fileName, dirDestination, login));
            os.flush();
            log.debug("Сообщение от клиента: запрос о выгрузке файла " + fileName + " с сервера");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке запроса о выгрузке файла на сервер");
        }
    }

    private void sendListMessageToServer(String login) {
        try {
            os.writeObject(new ListAsk(login));
            os.flush();
            log.debug("Сообщение от клиента: запрос списка файлов пользователя " + login);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке запроса о списке файлов на сервер");
        }
    }

    public void sendRegMessageToServer(String lastname, String name, String email, String login, String password) {
        try {
            os.writeObject(new RegAsk(lastname, name, email, login, password));
            os.flush();
            log.debug("Сообщение от клиента: запрос о регистрации нового пользователя " + login);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке запроса о регистрации нового пользователя на сервер");
        }
    }

    public void sendDeleteFileMessageToServer(String fileName, String login) {
        try {
            os.writeObject(new DeleteAsk(fileName, login));
            os.flush();
            log.debug("Сообщение от клиента: запрос на удаление файла " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке запроса на удаление файла на сервер");
        }
    }

    public void sendRenameMessageToServer(String oldName, String newName, String login) {
        try {
            os.writeObject(new RenameAsk(oldName, newName, login));
            os.flush();
            log.debug("Сообщение от клиента: запрос на переименование файла " + oldName + " в " + newName);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Ошибка при отправке запроса на переименование файла на сервер");
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
        });
    }

    public void showCONFIRMATION(String message) {
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK).showAndWait();
        });
    }

    public void openWindow(String fxml, String title, String fio, String login, ListMessage listMessage) throws IOException {
        Platform.runLater(() -> {
            try {
                stageChange = StageChange.getInstance();
                Stage stage = stageChange.getCurrentStage();
                stage.close();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
                Parent root1 = (Parent) fxmlLoader.load();

                if (fxml.equals("/cloud.fxml")) {
                    controller = fxmlLoader.<CloudController>getController();
                    controller.storeUserLabel.setText(fio);    // так как создал один label запишем только имя

                    updateFileList((listMessage.getListFiles()), controller);
                }
                stage = new Stage();
                stageChange.setCurrentScene(stage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle(title);
                stage.setScene(new Scene(root1));
                if (fxml.equals("/registration.fxml")) {
                    stage.setOnCloseRequest(event -> {
                        try {
                            openWindow("/cloud-start.fxml", "Авторизация пользователя", "", "", null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (fxml.equals("/cloud.fxml")) {
                    stage.setOnCloseRequest(event -> closeConnection());
                }
                stage.resizableProperty().set(false);
                stage.show();
            } catch (IOException e) {
                log.error("Ошибка при открытии окна", e);
            }
        });
    }

    public void closeConnection() {
        Platform.runLater(() -> {
            log.debug("Закрытие соединений");
            try {
                if (is != null) {
                    if (thread.isAlive()) {
                        thread.stop();
                    }
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Ошибка при закрытии потока");
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Ошибка при закрытие потока");
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Ошибка при закрытии соединения");
            }
            Platform.exit();
        });
    }
}
