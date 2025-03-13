package uk.co.notori.gol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main screen manager
 */
public class MainScreen {

    private static final Logger log = LoggerFactory.getLogger(MainScreen.class);
        
    private JPanel currentUI;
    private final Container root;
    private final ExitHook mainExitHook;

    public interface ExitHook {
        void exit();
    }
    
    public interface NewGameHook {
        void newGame();
    }

    public MainScreen(Container root, ExitHook exitHook) {
        this(root, exitHook, null);
    }
    
    public MainScreen(Container root, ExitHook exitHook, String savePath) {
        this.root = root;
        this.mainExitHook = exitHook;
                
        if (!Util.isKindle()) {
            root.setSize(new Dimension(800, 600));
        }
		// start new game grid on load
        newGame();
    }
    
	public void start() {
		log.info("Starting Game of Life application");
		
		if (currentUI != null) {
			currentUI.setVisible(true);
			
			// force a complete refresh
			root.invalidate();
			root.validate();
			root.repaint();
			
			// very ugly fix for weird component visibility behaviour
			if (Util.isKindle()) {
				scheduleRefresh(300);
				scheduleRefresh(600);
				scheduleRefresh(1200);
				scheduleRefresh(2000);
				scheduleRefresh(3000);
				
				new java.util.Timer().schedule(
					new java.util.TimerTask() {
						public void run() {
							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										refreshButtonsOnly(root);
									}
								});
							} catch (Exception e) {
								log.error("Error during button refresh", e);
							}
						}
					},
					3500
				);
			}
		}
		
		log.info("Application started");
	}
	
	// another very ugly fix for weird component visibility behaviour
	private void scheduleRefresh(final int delay) {
		new java.util.Timer().schedule(
			new java.util.TimerTask() {
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								refreshComponentsRecursively(root);
								
								currentUI.invalidate();
								currentUI.validate();
								currentUI.repaint();
								
								root.invalidate();
								root.validate();
								root.repaint();
								
								log.info("Completed UI refresh after " + delay + "ms");
							}
						});
					} catch (Exception e) {
						log.error("Error during UI refresh", e);
					}
				}
			},
			delay
		);
	}

	// another very ugly fix for weird component visibility behaviour
	private void refreshComponentsRecursively(Container container) {
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			component.invalidate();
			component.validate();
			component.repaint();
			
			// handling for buttons to ensure visibility
			if (component instanceof JButton) {
				JButton button = (JButton)component;
				button.setVisible(true);
			}
			
			if (component instanceof Container) {
				refreshComponentsRecursively((Container)component);
			}
		}
	}

	private void refreshButtonsOnly(Container container) {
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			
			if (component instanceof JButton) {
				JButton button = (JButton)component;
				button.setVisible(true);
				button.invalidate();
				button.validate();
				button.repaint();
				log.info("Refreshed button: " + button.getText());
			}
			
			if (component instanceof Container) {
				refreshButtonsOnly((Container)component);
			}
		}
	}

    public void newGame() {
        log.info("Starting new game");
        
        // remove current UI if it exists
        if (currentUI != null) {
            root.remove(currentUI);
        }
        
        // create main UI with new panel
        Panel gamePanel = new Panel(root.getSize());
        currentUI = new MainUI(
            new ExitHook() {
                public void exit() {
                    mainExitHook.exit();
                }
            },
            gamePanel
        );
        
        currentUI.setSize(root.getSize());
        currentUI.setPreferredSize(root.getSize());
        
        root.add(currentUI, BorderLayout.CENTER);
        
        currentUI.setVisible(true);
        gamePanel.setVisible(true);
        
        root.validate();
        root.repaint();
    }
}
