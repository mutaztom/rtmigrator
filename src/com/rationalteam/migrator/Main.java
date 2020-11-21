package com.rationalteam.migrator;

import com.sun.javafx.application.LauncherImpl;
import javafx.scene.control.Alert;

public class Main {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Rtmigapp.class, RtSplash.class, args);
    }

    public static void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
