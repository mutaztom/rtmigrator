package com.rationalteam.migrator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Rtmigapp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("migrator.fxml"));
        Scene scene = new Scene(root, 450, 350);
        primaryStage.setTitle("RationalTeam Migrator 1.0");

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
