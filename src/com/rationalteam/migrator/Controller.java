package com.rationalteam.migrator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import javax.management.timer.Timer;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import static com.rationalteam.migrator.Main.showError;

public class Controller implements Initializable {
    public Button cmdexit;
    public TextField txtsource;
    public TextField txttarget;
    public Button cmdstop;
    public Button cmdstart;
    public Button cmdtarget;
    public Button cmdsource;
    public TextArea txtoutput;
    public Circle indicator;
    private Migrator migrator;
    private Thread migratorThread;

    public void onexit(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("هل أنت متأكد من رغبتك في إيقاف الخدمة و الخروج من النظام؟");
        Optional<ButtonType> bt = alert.showAndWait();
        if (bt.get() == ButtonType.OK) {
            //save properties
            OutputStream proppath = null;
            try {
                String confpath = System.getProperty("user.dir");
                proppath = new FileOutputStream(confpath + "/rtmigrator.properties");
                Properties prop = new Properties();
                if (migrator.getSource() != null && !migrator.getSource().isEmpty()) {
                    prop.put("migrator.source", migrator.getSource());
                    prop.store(proppath, "Updated on: " + LocalDateTime.now().toString());
                }
                if (migrator.getTarget() != null && !migrator.getTarget().isEmpty()) {
                    prop.put("migrator.target", migrator.getTarget());
                    prop.store(proppath, "Updated on: " + LocalDateTime.now().toString());
                }
            } catch (IOException exp) {
                showError(exp.getMessage());
            }
            migrator.interrupt();
            Platform.exit();
            System.exit(0);
        }
    }

    public void onStartService(ActionEvent e) {
        if (Objects.isNull(txtsource) || Objects.isNull(txttarget)
                || txtsource.getText().isEmpty() || txttarget.getText().isEmpty()) {
            showError("لا بد من إختيار المجلد المصدر ، والمجلد المراد النسخ إليه قبل تشغيل الخدمة");
        } else {
            Migrator migrator = new Migrator();
            migrator.setSource(Paths.get(txtsource.getText()).toString());
            migrator.setTarget(Paths.get(txttarget.getText()).toString());
            migrator.setOnMessage(msg -> Platform.runLater(() -> txtoutput.appendText(msg)));
            migrator.setOnCreate(path ->
                    Platform.runLater(() -> txtoutput.appendText(path.toString())));
            migratorThread = new Thread() {
                @Override
                public void run() {
                    migrator.startMigrator();
                }

                @Override
                public void interrupt() {
                    migrator.interrupt();
                }
            };
            migratorThread.setDaemon(true);
            migratorThread.start();
            if (migratorThread.isAlive()) {
                cmdstop.setDisable(false);
                cmdstart.setDisable(true);
                txtsource.setDisable(true);
                txttarget.setDisable(true);
                cmdsource.setDisable(true);
                cmdtarget.setDisable(true);
                indicator.setFill(Color.GREEN);
            }
        }
    }

    public void onStopService(ActionEvent e) {
        if (migratorThread.isAlive()) {
            migrator.interrupt();
            migratorThread.interrupt();
            indicator.setFill(Color.GRAY);
        }
        cmdstop.setDisable(true);
        cmdstart.setDisable(false);
        txtsource.setDisable(false);
        txttarget.setDisable(false);
        cmdsource.setDisable(false);
        cmdtarget.setDisable(false);
    }

    public void onTarget(ActionEvent e) {
        try {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Select target server folder");
            File dir = dc.showDialog(null);
            if (dir != null) {
                txttarget.setText(dir.getPath());
                System.setProperty("migrator.target", dir.getPath());
            }
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    public void onSource(ActionEvent e) {
        try {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Select source folder");
            File dir = dc.showDialog(null);
            if (dir != null) {
                txtsource.setText(dir.getPath());
                System.setProperty("migrator.source", dir.getPath());
            }
        } catch (Exception exception) {
            showError(exception.getMessage());
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //read environment first
        migrator = new Migrator();
        migrator.loadConfig();
        txtsource.setText(migrator.getSource());
        txttarget.setText(migrator.getTarget());
    }

}
