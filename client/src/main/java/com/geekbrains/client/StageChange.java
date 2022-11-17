package com.geekbrains.client;

import javafx.stage.Stage;

public class StageChange {
    private static StageChange instance;
    private Stage currentStage;

    public static StageChange getInstance() {
        if (instance == null) {
            instance = new StageChange();
        }
        return instance;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentScene(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
