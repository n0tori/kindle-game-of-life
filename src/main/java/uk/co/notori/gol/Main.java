package uk.co.notori.gol;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for desktop testing
 */
public class Main {
    
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.err");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public static void main(String[] args) {
        Util.setKindle(false);
        
        JFrame frame = new JFrame("Conway's Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        
        new MainScreen(frame, new MainScreen.ExitHook() {
            public void exit() {
                System.exit(0);
            }
        });
        
        frame.setVisible(true);
    }
}
