package com.geekbrains.client;

import com.geekbrains.util.FilesUtils;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.net.Socket;

public class Network {
    private static Network instance;


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

    public boolean connect() {
        if (socket != null && !socket.isClosed()) {
            return true;
        }
        try {

            return true;
        } catch (Exception e) {
            showError("Невозможно подключиться к серверу");
            return false;
        }
    }

    private void showError(String message) {

    }
}
