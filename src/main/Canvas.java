package main;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
     * Default constructor
     */
    public Canvas() {

        //creations of the world
        world = new World(800, 800);
        for (int i = 0; i < 10; i++) {
            Creature c = new Creature(world);
            c.setX(Math.random() * world.getW());
            c.setY(Math.random() * world.getH());
            world.addEntity(c);
        }

        //creations of the debug neuralnetwork
        nn = new NeuralNetwork();

        //initializes variables
        this.addMouseListener(this);
        crea = null;
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

        //displays the world
        world.display(g, (int)(-camX), (int)(-camY));
        //hightlights the selected creature and display his information
        if (crea != null) {
            crea.displayHighlight(g, (int)(-camX), (int)(-camY));
            crea.displayNN(g);
        }

        //displays the neuralNetwork
        nn.display(g, getWidth()-(nn.getLayerSize()*40)-20, 0);
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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Entity entity = world.GetEntity(e.getX()+(int)(camX), e.getY()+(int)(camY));
        if (entity != null) {
            if (entity.getClass() == Creature.class) {
                crea = (Creature) entity;
            }
        } else {
            crea = null;
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