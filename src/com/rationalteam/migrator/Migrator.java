package com.rationalteam.migrator;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.function.Consumer;

import static com.rationalteam.migrator.Main.showError;

public class Migrator extends Thread {
    private String source = "c:/data/samsung/";
    private String target = "c:/data/camerasystem/";
    private static WatchService watcher;
    private static WatchEvent.Kind<?> kinds;
    private static int delay = 1000;
    private boolean ConfigLoaded = false;
    private static StringBuffer messages;
    private Consumer<Path> onCreate;
    private Consumer<String> onMessage;

    public Migrator() {
        messages = new StringBuffer();
    }

    public void setOnCreate(Consumer<Path> onCreate) {
        this.onCreate = onCreate;
    }

    public void setOnMessage(Consumer<String> onmsg) {
        onMessage = onmsg;
    }

    @Override
    public synchronized void start() {
        startMigrator();
    }

    public void startMigrator() {
        if (!ConfigLoaded) loadConfig();
        logMessage("Rational Team Technology.");
        logMessage("Rational Conveyor Version 1.0. ");
        logMessage("Developed by Rational Team for Traffic Police, All rights reserved 20018@");
        logMessage("يتم تشغيل خدمة مراقبة الصور الجديدة......");

        Path src = Paths.get(source);
        Path dest = Paths.get(target);
        if (!src.toFile().exists()) {
            showError("Source folder path does not exist. please specify correctly.");
        } else if (!src.toFile().exists() && !dest.toFile().exists()) {
            showError("Camera System folder path does not exist. please specify correctly.");
        } else {
            try {
                kinds = StandardWatchEventKinds.ENTRY_CREATE;
                watcher = src.getFileSystem().newWatchService();
                src.register(watcher, kinds);
                logMessage("Registered CameraSystem Folder as: ");
                logMessage(dest.toString());
                logMessage("Rational Conveyor is now watching folder: " + src.toString() + " .......");

                boolean valid = true;
                label61:
                do {
                    WatchKey key = null;
                    try {
                        if (watcher != null)
                            key = watcher.take();
                    } catch (InterruptedException | ClosedWatchServiceException var11) {
                        break;
                    }

                    Iterator<?> wevents = key.pollEvents().iterator();

                    while (true) {
                        WatchEvent event;
                        WatchEvent.Kind kind;

                        do {
                            if (!wevents.hasNext()) {
                                valid = key.reset();
                                if (isInterrupted())
                                    return;
                                continue label61;
                            }

                            event = (WatchEvent) wevents.next();
                            kind = event.kind();
                        } while (kind == StandardWatchEventKinds.OVERFLOW);

                        Path filename = (Path) event.context();

                        try {
                            Path child = src.resolve(filename);
                            String dfile = filename.getName(filename.getNameCount() - 1).toString();
                            logMessage("Noticed new file created=" + dfile);
                            Files.move(child, Paths.get(dest.toString(), dfile), StandardCopyOption.REPLACE_EXISTING);
                            if (onCreate != null) {
                                onCreate.accept(child);
                            }
                        } catch (IOException exp) {
                            logMessage(exp.getMessage());
                            continue;
                        }

                        logMessage(String.format(">>>> Moving file to : %s : %s%n", dest.toString(), filename));
                    }
                } while (valid);
            } catch (IOException exp) {
                showError(exp.getMessage());
            }

        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void loadConfig() {
        //load default or latest watch directories
        String confpath = System.getProperty("user.dir");
        Properties p = new Properties();
        File f = new File(confpath + "/rtmigrator.properties");
        if (f.exists()) {
            try {
                p.load(new FileInputStream(f));
                if (p.containsKey("migrator.source"))
                    source = p.getProperty("migrator.source");
                if (p.containsKey("migrator.target"))
                    target = p.getProperty("migrator.target");
            } catch (IOException e) {
                showError(e.getMessage());
            }
        }
        ConfigLoaded = true;
    }

    @Override
    public void interrupt() {
        try {
            logMessage("تم إنهاء خدمة مراقبة الملفات ....");
            if (watcher != null) {
                watcher.close();
            }
        } catch (IOException e) {
            logMessage(e.getMessage());
        }
    }

    private void logMessage(String s) {
        messages.append(s).append("\n");
        if (onMessage != null)
            onMessage.accept(s + "\n");

    }

    public static StringBuffer getMessages() {
        return messages;
    }

}
