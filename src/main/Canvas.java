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

    public Canvas() {
        this.addMouseListener(this);
        nn = new NeuralNetwork();
        world = new World(400, 400);
        for (int i = 0; i < 4; i++) {
            Creature c = new Creature(world);
            c.setX(Math.random() * world.getW());
            c.setY(Math.random() * world.getH());
            world.addEntity(c);
        }
        crea = null;
        pause = false;
    }

    public void step() {
        if (!pause) {
            world.step();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        world.display(g);
        if (crea != null) {
            crea.displayHighlight(g);
            crea.displayNN(g);
        }
        nn.display(g, 400, 0);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Entity entity = world.MouseGetEntity(e.getX(), e.getY());
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
        }
        if (e.getKeyChar() == 'm') {
            nn.mutate();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}