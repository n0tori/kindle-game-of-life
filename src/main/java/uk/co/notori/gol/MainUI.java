package uk.co.notori.gol;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main UI for the Game of Life app
 */
public class MainUI extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(MainUI.class);

    public final Panel panel;
    private final JLabel generationLabel;
    private final JLabel populationLabel;
    
    public MainUI(MainScreen.ExitHook exitHook, Panel panel) {
        super();
        
        this.panel = panel;
        
        setLayout(new BorderLayout());
        
		// Panel area
        add(panel, BorderLayout.CENTER);
        
        // calculate appropriate font sizes based on screen dimensions, 
		//  not tested on multiple devicesbut in theory works.
        Dimension screenSize = getScreenSize();
        int labelFontSize = calculateFontSize(screenSize, 14);
        int buttonFontSize = calculateFontSize(screenSize, 12);
        int speedFontSize = calculateFontSize(screenSize, 10);
        int infoFontSize = calculateFontSize(screenSize, 10);
        
        // info panel (top)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 2));
        
        generationLabel = new JLabel("Generation: 0", JLabel.CENTER);
        generationLabel.setFont(new Font("SansSerif", Font.BOLD, labelFontSize));
        infoPanel.add(generationLabel);
        
        populationLabel = new JLabel("Population: 0", JLabel.CENTER);
        populationLabel.setFont(new Font("SansSerif", Font.BOLD, labelFontSize));
        infoPanel.add(populationLabel);
        
        add(infoPanel, BorderLayout.NORTH);
        
        // control panel (bottom)
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel creditsLabel = new JLabel("Kindle Game Of Life - made by notori :)", JLabel.CENTER);
        creditsLabel.setFont(new Font("SansSerif", Font.ITALIC, infoFontSize));
        creditsLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        bottomPanel.add(creditsLabel, BorderLayout.NORTH);
        
        // main control buttons in a grid
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 3, 10, 10)); // 1 row, 3 columns with gaps
        controlPanel.setOpaque(true);
        
        // speed buttons in their own panel
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
        speedPanel.setOpaque(true);
        speedPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel speedLabel = new JLabel("Speed:", JLabel.CENTER);
        speedLabel.setFont(new Font("SansSerif", Font.BOLD, speedFontSize));
        speedPanel.add(speedLabel);
        
        // declare all buttons
        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");
        JButton slowButton = new JButton("S");
        JButton mediumButton = new JButton("M");
        JButton fastButton = new JButton("F");
        JButton exitButton = new JButton("Exit");
        
        // configure control buttons
        playButton.setFont(new Font("SansSerif", Font.BOLD, buttonFontSize));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.play();
            }
        });
        controlPanel.add(playButton);
        styleControlButton(playButton);
        
        pauseButton.setFont(new Font("SansSerif", Font.BOLD, buttonFontSize));
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.pause();
            }
        });
        controlPanel.add(pauseButton);
        styleControlButton(pauseButton);
        
        resetButton.setFont(new Font("SansSerif", Font.BOLD, buttonFontSize));
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.reset();
            }
        });
        controlPanel.add(resetButton);
        styleControlButton(resetButton);
        
        // configure speed buttons
        slowButton.setFont(new Font("SansSerif", Font.PLAIN, speedFontSize));
        slowButton.setPreferredSize(new Dimension(50, 50));
        slowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setSpeed(500);
                updateSpeedButtonSelection(slowButton, mediumButton, fastButton);
            }
        });
        speedPanel.add(slowButton);
        styleSpeedButton(slowButton);
        
        mediumButton.setFont(new Font("SansSerif", Font.PLAIN, speedFontSize));
        mediumButton.setPreferredSize(new Dimension(50, 50));
        mediumButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setSpeed(300);
                updateSpeedButtonSelection(slowButton, mediumButton, fastButton);
            }
        });
        speedPanel.add(mediumButton);
        styleSpeedButton(mediumButton);
        
        fastButton.setFont(new Font("SansSerif", Font.PLAIN, speedFontSize));
        fastButton.setPreferredSize(new Dimension(50, 50));
        fastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setSpeed(100);
                updateSpeedButtonSelection(slowButton, mediumButton, fastButton);
            }
        });
        speedPanel.add(fastButton);
        styleSpeedButton(fastButton);
        
        // medium speed as default
        updateSpeedButtonSelection(slowButton, mediumButton, fastButton);
        
        // exit button
        exitButton.setFont(new Font("SansSerif", Font.BOLD, buttonFontSize));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitHook.exit();
            }
        });
        styleControlButton(exitButton);
        
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.add(exitButton);
        
        // add control panels to the container
        JPanel controlContainer = new JPanel(new BorderLayout());
        controlContainer.add(controlPanel, BorderLayout.NORTH);
        controlContainer.add(speedPanel, BorderLayout.CENTER);
        
        // add all panels to the bottom panel
        bottomPanel.add(controlContainer, BorderLayout.CENTER);
        bottomPanel.add(exitPanel, BorderLayout.SOUTH);
        
        // add bottom panel to main layout
        add(bottomPanel, BorderLayout.SOUTH);
        
		// another very ugly fix for weird component visibility behaviour
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                public void run() {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                // Force the buttons to be visible
                                refreshButton(playButton);
                                refreshButton(pauseButton);
                                refreshButton(resetButton);
                                refreshButton(slowButton);
                                refreshButton(mediumButton);
                                refreshButton(fastButton);
                                refreshButton(exitButton);
                                
                                controlPanel.invalidate();
                                controlPanel.validate();
                                controlPanel.repaint();
                                
                                speedPanel.invalidate();
                                speedPanel.validate();
                                speedPanel.repaint();
                                
                                bottomPanel.invalidate();
                                bottomPanel.validate();
                                bottomPanel.repaint();
                                
                                creditsLabel.setVisible(true);
                                creditsLabel.invalidate();
                                creditsLabel.validate();
                                creditsLabel.repaint();
                            }
                        });
                    } catch (Exception e) {
                        log.error("Error during button refresh", e);
                    }
                }
            },
            1000
        );
        
        updateCounters(panel.getGeneration(), panel.getPopulation());
        
		// another very ugly fix for weird component visibility behaviour
        new javax.swing.Timer(800, new ActionListener() {
            private int count = 0;
            public void actionPerformed(ActionEvent e) {
                if (count++ < 5) {
                    bottomPanel.invalidate();
                    bottomPanel.validate();
                    bottomPanel.repaint();
                } else {
                    ((javax.swing.Timer)e.getSource()).stop();
                }
            }
        }).start();
        
        log.debug("MainUI initialized with buttons and panels");
    }
    
    /**
     * Helper method to refresh button visibility
     */
    private void refreshButton(JButton button) {
        button.setVisible(true);
        button.invalidate();
        button.validate();
        button.repaint();
    }
    
    /**
     * Helper method to style control buttons
     */
    private void styleControlButton(JButton button) {
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setFocusable(true);
        button.setVisible(true);
        
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
    
    /**
     * Helper method to style speed buttons
     */
    private void styleSpeedButton(JButton button) {
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setFocusable(true);
        button.setVisible(true);
        
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        button.setMargin(new Insets(8, 8, 8, 8));
        button.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * Helper method to update speed button selection
     */
    private void updateSpeedButtonSelection(JButton slowButton, JButton mediumButton, JButton fastButton) {
        // reset all buttons to normal state
        slowButton.setFont(new Font(slowButton.getFont().getName(), Font.PLAIN, slowButton.getFont().getSize()));
        mediumButton.setFont(new Font(mediumButton.getFont().getName(), Font.PLAIN, mediumButton.getFont().getSize()));
        fastButton.setFont(new Font(fastButton.getFont().getName(), Font.PLAIN, fastButton.getFont().getSize()));
        
        slowButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        mediumButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        fastButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        // determine which button is selected
        JButton selectedButton;
        if (panel.getDelay() == 500) {
            selectedButton = slowButton;
        } else if (panel.getDelay() == 100) {
            selectedButton = fastButton;
        } else {
            selectedButton = mediumButton;
        }
        
        // make selected button stand out with border
        selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
    }
    
    private Dimension getScreenSize() {
        if (Panel.rootSize != null) {
            return Panel.rootSize;
        }
        return new Dimension(800, 600); // default fallback
    }
    
    private int calculateFontSize(Dimension screenSize, int defaultSize) {
        if (Util.isKindle()) {
            if (screenSize.width <= 600) {
                return Math.max(defaultSize - 4, 8);
            } else if (screenSize.width <= 800) {
                return Math.max(defaultSize - 2, 10);
            }
        }
        return defaultSize;
    }
    
    public void updateCounters(int generation, int population) {
        generationLabel.setText("Generation: " + String.valueOf(generation));
        populationLabel.setText("Population: " + String.valueOf(population));
    }
}