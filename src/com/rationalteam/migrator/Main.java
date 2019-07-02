//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rationalteam.migrator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.util.Iterator;
import java.util.StringJoiner;

public class Main {
    private static String USAGEGIDE;
    private static String samsung = "c:/data/samsung/";
    private static String cameraSystem = "c:/data/camerasystem/";
    private static WatchService watcher;
    private static Kind<?> kinds;
    private static int delay = 1000;
    static SplashScreen mySplash;


    public static void main(String[] args) {
        initGuide();
        System.out.println("Rational Team Technology.");
        System.out.println("Rational Conveyor Version 1.0. ");
        System.out.println("Developed by Rational Team for Traffic Police, All rights reserved 20018@");
        System.out.println("Press Control+C to  stop this application.");
        if (args.length == 0) {
            System.out.println(USAGEGIDE);
            return;
        }
        if (args.length == 2) {
            samsung = args[0];
            cameraSystem = args[1];
        }

        if (args.length == 3) {
            samsung = args[0];
            cameraSystem = args[1];
            delay = Integer.valueOf(args[2]);
            delay = Integer.parseInt(args[2]);
        }
        initMigrator();
        Path src = Paths.get(samsung);
        Path dest = Paths.get(cameraSystem);
        if (!src.toFile().exists()) {
            throw new RuntimeException("Source folder path does not exist. please specify correctly.");
        } else if (!src.toFile().exists() && !dest.toFile().exists()) {
            throw new RuntimeException("Camera System folder path does not exist. please specify correctly.");
        } else {
            try {
                kinds = StandardWatchEventKinds.ENTRY_CREATE;
                watcher = src.getFileSystem().newWatchService();
                src.register(watcher, kinds);
                System.out.println("Registered CameraSystem Folder as: " + dest.toString());
                System.out.println("Rational Conveyor is now watching folder: " + src.toString() + " .......");

                boolean valid;
                label61:
                do {
                    WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException var11) {
                        return;
                    }

                    Iterator var4 = key.pollEvents().iterator();

                    while (true) {
                        WatchEvent event;
                        Kind kind;
                        do {
                            if (!var4.hasNext()) {
                                valid = key.reset();
                                continue label61;
                            }

                            event = (WatchEvent) var4.next();
                            kind = event.kind();
                        } while (kind == StandardWatchEventKinds.OVERFLOW);

                        Path filename = (Path) event.context();

                        try {
                            Path child = src.resolve(filename);
                            String dfile = filename.getName(filename.getNameCount() - 1).toString();
                            System.out.println("Noticed new file created=" + dfile);
                            Thread.sleep((long) delay);
                            Files.move(child, Paths.get(dest.toString(), dfile), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException var12) {
                            System.err.println(var12);
                            continue;
                        } catch (InterruptedException var13) {
                            var13.printStackTrace();
                        }

                        System.out.format(">>>> Moving file to : %s : %s%n", dest.toString(), filename);
                    }
                } while (valid);
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }
    }

    private static void initMigrator() {

        mySplash = SplashScreen.getSplashScreen();
        if (mySplash == null) {
            System.out.println("This application has no splash");
            return;
        } else if (mySplash != null) {
            Graphics2D g = mySplash.createGraphics();
            Font font = new Font(Font.SERIF, 1, 16);
            g.setFont(font);
            g.setPaint(Color.WHITE);
            g.drawString("RationalTeam", 10, 20);
            g.drawString("Conveyor 1.0", 10, 40);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 10));
            g.setPaint(Color.BLUE);
            g.drawString("www.rationalteam.com", 10, 180);
            g.dispose();
            mySplash.update();
        }

        try {
            Thread.sleep(9000L);
            mySplash.close();
        } catch (InterruptedException var2) {
            ;
        }
    }

    public static void initGuide() {
        StringBuilder sb = new StringBuilder("**************** USER GUIDE *****************").append(System.lineSeparator());
        sb.append("*********************************************************************************").append(System.lineSeparator());
        sb.append("At least two arguments must be provided to run this application.").append(System.lineSeparator())
                .append("*********************************************************************************").append(System.lineSeparator())
                .append("To Use type the following command: ").append(System.lineSeparator())
                .append("java -jar rtmigrator.jar [sourcefolder] [destinationpath:shoud be camera system folder]").append(System.lineSeparator())
                .append("Example: java -jar rtmigrator.jar c:\\Dahwa \\\\server\\CameraSystem\\general\\")
                .append(System.lineSeparator()).append("If you want to add a delay time between file creation and file move to destination add a third parameter to the " +
                "above command line; by adding integer value (30) for example will delay for thirty seconds").append(System.lineSeparator())
                .append("Example: java -jar rtmigrator.jar c:\\Dahwa \\\\server\\CameraSystem\\general\\ 30");
        USAGEGIDE = new String(sb);
    }

}
