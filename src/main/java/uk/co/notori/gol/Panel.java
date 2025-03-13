package uk.co.notori.gol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;

/**
 * The grid for Conway's Game of Life
 */
public class Panel extends JPanel implements MouseListener {
    public static Dimension rootSize;
    
    private static final int DEFAULT_WIDTH = 40;
    private static final int DEFAULT_HEIGHT = 40;
    private static final int CELL_SIZE = 15;
    private static final Color ALIVE_COLOR = Color.BLACK;
    private static final Color DEAD_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.GRAY;
    
    private boolean[][] grid;
    private boolean[][] nextGrid;
    private int width;
    private int height;
    private boolean running;
    private javax.swing.Timer timer;
    private int generation;
    private int population;
    
    private int updateCounter = 0;
    // update UI every 7 frames approx. every other second at medium speed
    private static final int UPDATE_FREQUENCY = 7;
    
    private Random random = new Random();
    
    /**
     * Create game Panel
     */
    public Panel(Dimension rootSize) {
        super();
        
        Panel.rootSize = rootSize;
        
        // calculate width and height based on screen size
        // create space for buttons
        this.width = rootSize.width / CELL_SIZE;
        this.height = (rootSize.height - 100) / CELL_SIZE; 
        
        grid = new boolean[width][height];
        nextGrid = new boolean[width][height];
        
        // size of the grid
        setPreferredSize(new Dimension(width * CELL_SIZE, height * CELL_SIZE));
        setLayout(null);
        
        running = false;
        generation = 0;
        population = 0;
        
        // Set up timer for animation
        ActionListener animationListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    evolve();
                    updatePopulation();
                    generation++;
                    repaint();
                    
                    updateCounter++;
                    
                    // update UI counters so it's not visually annoying
                    if (updateCounter >= UPDATE_FREQUENCY) {
                        updateCounter = 0;
                        // update the UI with new generation and population count
                        if (getParent() instanceof MainUI) {
                            ((MainUI) getParent()).updateCounters(generation, population);
                        }
                    }
                }
            }
        };
        timer = new javax.swing.Timer(300, animationListener);
        
        initializeRandomGrid();
        
        addMouseListener(this);
        
        setVisible(true);
        setOpaque(true);
        
        timer.setInitialDelay(1000);
        timer.start();
        running = true;
    }
    
    /**
     * Initialize the grid with random cells
     */
    public void initializeRandomGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = random.nextDouble() < 0.3;
            }
        }
        updatePopulation();
        generation = 0;
        updateCounter = 0;
        if (getParent() instanceof MainUI) {
            ((MainUI) getParent()).updateCounters(generation, population);
        }
        repaint();
    }
    
    public void clearGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = false;
            }
        }
        updatePopulation();
        generation = 0;
        updateCounter = 0;
        if (getParent() instanceof MainUI) {
            ((MainUI) getParent()).updateCounters(generation, population);
        }
        repaint();
    }
    

    private void evolve() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int neighbors = countNeighbors(x, y);
                if (grid[x][y]) {
                    nextGrid[x][y] = neighbors == 2 || neighbors == 3;
                } else {
                    nextGrid[x][y] = neighbors == 3;
                }
            }
        }
        
        boolean[][] temp = grid;
        grid = nextGrid;
        nextGrid = temp;
    }
    
    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                
                int nx = (x + dx + width) % width;
                int ny = (y + dy + height) % height;
                
                if (grid[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private void updatePopulation() {
        population = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y]) {
                    population++;
                }
            }
        }
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y]) {
                    g.setColor(ALIVE_COLOR);
                } else {
                    g.setColor(DEAD_COLOR);
                }
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                
                g.setColor(GRID_COLOR);
                g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
    
    public void play() {
        if (!running) {
            running = true;
            timer.start();
        }
    }

    public void pause() {
        if (running) {
            running = false;
            timer.stop();
            
            // force a final update when pausing
            if (getParent() instanceof MainUI) {
                ((MainUI) getParent()).updateCounters(generation, population);
            }
        }
    }
    
    public void reset() {
        pause();
        initializeRandomGrid();
        generation = 0;
        updatePopulation();
    }
    
    // empty implementations
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    public boolean isRunning() {
        return running;
    }
    
    public int getGeneration() {
        return generation;
    }
    
    public int getPopulation() {
        return population;
    }
    
    public void setSpeed(int delay) {
        timer.setDelay(delay);
    }
    
    public int getDelay() {
        return timer.getDelay();
    }
}