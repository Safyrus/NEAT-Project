package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

import entity.Creature;
import entity.Entity;
import nn.NeuralNetwork;
import world.World;

/**
 * Class where the whole simulation take place
 */
public class Canvas extends JPanel implements MouseListener, KeyListener {
    private static final long serialVersionUID = 1L;

    /**
     * Debug neuralNetwork
     */
    private NeuralNetwork nn;
    /**
     * World to simulate
     */
    private World world;
    /**
     * Selected creature
     */
    private Creature crea;

    /**
     * if the simulation is pause or not
     */
    private boolean pause;
    /**
     * if the menu is active or not
     */
    private boolean menu;
    /**
     * x-position of the camera
     */
    private double camX;
    /**
     * y-position of the camera
     */
    private double camY;
    /**
     * speed of the camera
     */
    private double camSpd;
    /**
     * keys pressed
     */
    private boolean keys[];
    /**
     * string buffer for menu options
     */
    private String strBuf[];
    /**
     * selected option
     */
    private int selected;
    /**
     * x-position of the menu
     */
    private int menuX;
    /**
     * y-position of the menu
     */
    private int menuY;
    /**
     * width of the menu
     */
    private int menuW;
    /**
     * height of the menu
     */
    private int menuH;

    private boolean save;
    private boolean load;

    /**
     * Default constructor
     */
    public Canvas() {
        // creations of the debug neuralnetwork
        nn = new NeuralNetwork();

        // initializes variables
        this.addMouseListener(this);
        crea = null;
        menu = false;
        selected = 0;
        strBuf = new String[3];
        strBuf[0] = "";
        strBuf[1] = "10";
        strBuf[2] = "worldSave";
        pause = false;
        camX = 0;
        camY = 0;
        camSpd = 4;
        keys = new boolean[4];

        // creations of the world
        createWorld();
    }

    /**
     * Regenerates the world
     */
    private void createWorld() {
        int entityStart = 10;
        try {
            entityStart = Integer.parseInt(strBuf[1]);
        } catch (Exception e) {
        }
        
        world = new World(800, 800);
        for (int i = 0; i < entityStart; i++) {
            Creature c = new Creature(world);
            c.setX(Math.random() * world.getW());
            c.setY(Math.random() * world.getH());
            world.addEntity(c);
        }
    }

    /**
     * Calculates one step of the simulation
     */
    public void step() {
        if (save) {
            if (strBuf[2] == "") {
                strBuf[2] = "worldSave";
            }
            save(strBuf[2]);
            save = false;
        }

        if (load) {
            if (strBuf[2] == "") {
                strBuf[2] = "worldSave";
            }
            load(strBuf[2]);
            load = false;
        }

        if (!pause) {
            world.step();
        }

        keysAction();
    }

    /**
     * Displays the simulation
     */
    public void paint(Graphics g) {
        super.paint(g);

        // displays the world
        world.display(g, (int) (-camX), (int) (-camY));

        // hightlights the selected creature and display his information
        if (crea != null && !menu) {
            crea.displayHighlight(g, (int) (-camX), (int) (-camY));
            crea.displayNN(g);
        }

        // displays the menu
        if (menu) {
            // changes the font to a bigger one
            Font font = g.getFont();
            Font bigFont = font.deriveFont(font.getSize() * 1.4F);
            g.setFont(bigFont);

            int opacity = 160;
            menuX = getWidth() / 4;
            menuY = getHeight() / 4;
            menuW = getWidth() / 2;
            menuH = getHeight() / 2;

            Color black = new Color(0, 0, 0, opacity);
            Color white = new Color(255, 255, 255, opacity);
            Color grey = new Color(200, 200, 200, opacity);
            Color yellow = new Color(255, 255, 100, opacity);
            g.setColor(black);
            g.fillRect(menuX, menuY, menuW, menuH);

            // displays buttons
            g.setColor(white);
            g.fillRect(menuX + 10, menuY + 10, 60, 30);
            g.fillRect(menuX + 10, menuY + 50, 60, 30);
            g.fillRect(menuX + 10, menuY + 90, 60, 30);
            if (selected == 1) {
                g.setColor(yellow);
            } else {
                g.setColor(grey);
            }
            g.fillRect(menuX + 90, menuY + 90, 40, 30);
            if (selected == 2) {
                g.setColor(yellow);
            } else {
                g.setColor(grey);
            }
            g.fillRect(menuX + 90, menuY + 30, 120, 30);

            // displays texts
            g.setColor(new Color(0, 0, 0, opacity));
            g.drawString("SAVE", menuX + 20, menuY + 30);
            g.drawString("LOAD", menuX + 20, menuY + 70);
            g.drawString("REGEN", menuX + 12, menuY + 110);
            g.drawString(strBuf[1], menuX + 90, menuY + 110);
            g.drawString(strBuf[2], menuX + 90, menuY + 50);

            // resets the font
            g.setFont(font);
        } else {
            // displays the neuralNetwork
            nn.display(g, getWidth() - (nn.getLayerSize() * 40) - 20, 0);
        }
    }

    /**
     * Actions performed by different keys
     */
    private void keysAction() {
        if (keys[0]) {
            camX += camSpd;
        }
        if (keys[1]) {
            camX -= camSpd;
        }
        if (keys[2]) {
            camY += camSpd;
        }
        if (keys[3]) {
            camY -= camSpd;
        }
    }

    /**
     * Saves the world into a file
     */
    private void save(String file) {
        try {
            File f = new File(file);
            FileOutputStream fo = new FileOutputStream(f);
            ObjectOutputStream o = new ObjectOutputStream(fo);

            o.writeObject(world);

            o.close();
            fo.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }
    }

    /**
     * Loads the world from a file
     */
    private void load(String file) {
        try {
            FileInputStream fi = new FileInputStream(new File(file));
            ObjectInputStream oi = new ObjectInputStream(fi);

            world = (World) oi.readObject();

            oi.close();
            fi.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!menu) {
            Entity entity = world.GetEntity(e.getX() + (int) (camX), e.getY() + (int) (camY));
            if (entity != null) {
                if (entity.getClass() == Creature.class) {
                    crea = (Creature) entity;
                }
            } else {
                crea = null;
            }
        } else {
            if (e.getX() > menuX + 10 && e.getX() < menuX + 70 && e.getY() > menuY + 10 && e.getY() < menuY + 40) {
                save = true;
            } else if (e.getX() > menuX + 10 && e.getX() < menuX + 70 && e.getY() > menuY + 50
                    && e.getY() < menuY + 80) {
                load = true;
            } else if (e.getX() > menuX + 10 && e.getX() < menuX + 70 && e.getY() > menuY + 90
                    && e.getY() < menuY + 120) {
                createWorld();
            } else if (e.getX() > menuX + 90 && e.getX() < menuX + 130 && e.getY() > menuY + 90
                    && e.getY() < menuY + 120) {
                selected = 1;
            } else if (e.getX() > menuX + 90 && e.getX() < menuX + 210 && e.getY() > menuY + 30
                    && e.getY() < menuY + 60) {
                selected = 2;
            } else {
                selected = 0;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        int keyCode = e.getKeyCode();

        if (selected == 0) {
            if (key == 'p') {
                pause = !pause;
            } else if (key == 'm') {
                nn.mutate();
            } else if (key == 'c') {
                nn = nn.copy();
            } else if (key == '+') {
                camSpd *= 2;
            } else if (key == '-') {
                camSpd /= 2;
            }
        } else {
            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                if (strBuf[selected].length() > 0) {
                    strBuf[selected] = strBuf[selected].substring(0, strBuf[selected].length() - 1);
                }
            } else if (keyCode > 0x1F && keyCode < 0x7F || keyCode > 0x9F) {
                strBuf[selected] += key;
            }
        }
        if (!menu) {
            if (keyCode == KeyEvent.VK_UP) {
                keys[3] = true;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                keys[2] = true;
            } else if (keyCode == KeyEvent.VK_LEFT) {
                keys[1] = true;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                keys[0] = true;
            }
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
            menu = !menu;
            selected = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP) {
            keys[3] = false;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            keys[2] = false;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            keys[1] = false;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            keys[0] = false;
        }
    }
}