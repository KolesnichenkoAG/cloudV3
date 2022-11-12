package com.geekbrains.client;

public class Network {
    private static Network instance;
    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }
}
