package uk.co.notori.gol;

import com.amazon.kindle.booklet.AbstractBooklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;

/**
 * Kindle Booklet entry for application
 */
public class KGOLBooklet extends AbstractBooklet implements ActionListener {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.logFile","/mnt/us/kgol.log");
        System.setProperty("org.slf4j.simpleLogger.showDateTime","true");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName","true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat","yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        log = LoggerFactory.getLogger(KGOLBooklet.class);
        Util.setKindle(true);
    }

    private static final Logger log;
    private Container rootContainer = null;

    public KGOLBooklet() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    public void run() {
                        KGOLBooklet.this.longStart();
                    }
                },
                1000
        );
    }

    public void start(URI uri) {
        log.info("start called with {} ", uri);
        super.start(uri);
    }

    // Because this got obfuscated...
    private Container getUIContainer() {
        // Check our cached value, first
        if (rootContainer != null) {
            return rootContainer;
        } else {
            try {
                Container container = Util.getUIContainer(this);
                if (container == null) {
                    log.error("Failed to find getUIContainer method, abort!");
                    endBooklet();
                    return null;
                }
                rootContainer = container;
                return container;
            } catch (Throwable t) {
                throw new RuntimeException(t.toString());
            }
        }
    }

    private void endBooklet() {
        try {
            log.info("Ending Booklet");
            Runtime.getRuntime().exec("lipc-set-prop com.lab126.appmgrd stop app://uk.co.notori.gol");
        } catch (IOException e) {
            log.error("Failed when terminating ", e);
        }
    }

    private void longStart() {
        try {
            initializeUI();
        } catch (Throwable t) {
            log.error(t.getMessage(), new RuntimeException(t));
            endBooklet();
            throw new RuntimeException(t);
        }
    }

    private void initializeUI() {
        log.debug("Starting Up");
        Container root = getUIContainer();

        log.debug("Got UI container: {}", root);
        assert root != null;

        // clear the container first
        root.removeAll();
        
        Font rootFont = new Font("SansSerif", Font.PLAIN, 12);
        root.setFont(rootFont);

        final KGOLBooklet booklet = this;
        
        MainScreen mainScreen = new MainScreen(
            root, 
            new MainScreen.ExitHook() {
                public void exit() {
                    booklet.endBooklet();
                }
            }
        );
        
        // force a repaint
        try {
            root.requestFocus();
            
            mainScreen.start();
            
        } catch (Exception e) {
            log.error("Error during UI initialization", e);
        }
    }

    public void destroy() {
        // Try to cleanup behind us on exit...
        try {
            // NOTE: This can be a bit racey with stop(),
            //   so sleep for a tiny bit so our commandToRunOnExit actually has a chance to run...
            Thread.sleep(175);
            Util.updateCCDB("Game of Life", "/mnt/us/documents/GameOfLife.kgol");
        } catch (Exception ignored) {
            // Avoid the framework shouting at us
        }

        super.destroy();
    }

    public void actionPerformed(ActionEvent e) {
        log.debug("Action Performed {} ", e);
    }
}