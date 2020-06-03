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

public class Canvas extends JPanel implements MouseListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private NeuralNetwork nn;
    private World world;
    private Creature crea;

    private boolean pause;
    private double camX;
    private double camY;
    private double camSpd;
    private boolean keys[];

    public Canvas() {
        this.addMouseListener(this);
        nn = new NeuralNetwork();
        world = new World(800, 800);
        for (int i = 0; i < 10; i++) {
            Creature c = new Creature(world);
            c.setX(Math.random() * world.getW());
            c.setY(Math.random() * world.getH());
            world.addEntity(c);
        }
        crea = null;

        pause = false;
        camX = 0;
        camY = 0;
        camSpd = 4;
        keys = new boolean[4];
    }

    public void step() {
        if (!pause) {
            world.step();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        world.display(g, (int)(-camX), (int)(-camY));
        if (crea != null) {
            crea.displayHighlight(g, (int)(-camX), (int)(-camY));
            crea.displayNN(g);
        }
        nn.display(g, 400, 0);
        keysAction();
    }

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
        Entity entity = world.MouseGetEntity(e.getX()+(int)(camX), e.getY()+(int)(camY));
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