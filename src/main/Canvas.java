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

    private String strBuf;
    private int selected;

    private int menuX;
    private int menuY;
    private int menuW;
    private int menuH;

    /**
     * Default constructor
     */
    public Canvas() {

        // creations of the world
        world = new World(800, 800);
        for (int i = 0; i < 10; i++) {
            Creature c = new Creature(world);
            c.setX(Math.random() * world.getW());
            c.setY(Math.random() * world.getH());
            world.addEntity(c);
        }

        // creations of the debug neuralnetwork
        nn = new NeuralNetwork();

        // initializes variables
        this.addMouseListener(this);
        crea = null;
        menu = false;
        selected = 0;
        strBuf = "";
        pause = false;
        camX = 0;
        camY = 0;
        camSpd = 4;
        keys = new boolean[4];
    }

    /**
     * Calculates one step of the simulation
     */
    public void step() {
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

        // displays the neuralNetwork
        nn.display(g, getWidth() - (nn.getLayerSize() * 40) - 20, 0);

        if (menu) {
            Font font = g.getFont();
            Font bigFont = font.deriveFont(font.getSize() * 1.4F);
            g.setFont(bigFont);
            int opacity = 160;
            menuX = getWidth() / 4;
            menuY = getHeight() / 4;
            menuW = getWidth() / 2;
            menuH = getHeight() / 2;

            g.setColor(new Color(0, 0, 0, opacity));
            g.fillRect(menuX, menuY, menuW, menuH);

            g.setColor(new Color(255, 255, 255, opacity));
            g.fillRect(menuX + 10, menuY + 10, 60, 30);
            g.fillRect(menuX + 10, menuY + 50, 60, 30);
            g.fillRect(menuX + 10, menuY + 90, 60, 30);

            g.setColor(new Color(0, 0, 0, opacity));
            g.drawString("SAVE", menuX + 20, menuY + 30);
            g.drawString("LOAD", menuX + 20, menuY + 70);
            g.drawString("REGEN", menuX + 12, menuY + 110);

            g.setFont(font);
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

    private void save(String file) {
        try {
            FileOutputStream f = new FileOutputStream(new File(file));
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(world);

            o.close();
            f.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }
    }

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
                System.out.println("save");
                save("worldSave");
            } else if (e.getX() > menuX + 10 && e.getX() < menuX + 70 && e.getY() > menuY + 50
                    && e.getY() < menuY + 80) {
                System.out.println("load");
                load("worldSave");
            } else if (e.getX() > menuX + 10 && e.getX() < menuX + 70 && e.getY() > menuY + 90
                    && e.getY() < menuY + 120) {
                System.out.println("regen");
                world = new World(800, 800);
                for (int i = 0; i < 10; i++) {
                    Creature c = new Creature(world);
                    c.setX(Math.random() * world.getW());
                    c.setY(Math.random() * world.getH());
                    world.addEntity(c);
                }
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
        if (e.getKeyChar() == 'p') {
            pause = !pause;
        } else if (e.getKeyChar() == 'm') {
            nn.mutate();
        } else if (e.getKeyChar() == 'c') {
            nn = nn.copy();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            menu = !menu;
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            keys[3] = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            keys[2] = true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            keys[1] = true;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            keys[0] = true;
        } else if (e.getKeyChar() == '+') {
            camSpd *= 2;
        } else if (e.getKeyChar() == '-') {
            camSpd /= 2;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            keys[3] = false;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            keys[2] = false;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            keys[1] = false;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            keys[0] = false;
        }
    }
}