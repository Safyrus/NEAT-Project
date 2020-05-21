package main;

import java.awt.Graphics;
import javax.swing.JPanel;

import entity.Creature;
import entity.Food;
import nn.NeuralNetwork;
import world.World;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;

    private NeuralNetwork nn;
    private World world;

    public Canvas()
    {
        nn = new NeuralNetwork();
        world = new World(400, 400);
        for (int i = 0; i < 10; i++) {
            Food f = new Food();
            f.setX(Math.random()*world.getW());
            f.setY(Math.random()*world.getH());
            world.addEntity(f);
        }
        world.addEntity(new Creature());
    }

    public void step()
    {
        //nn.step();
        //nn.mutate();
        world.step();
    }

    public void paint(Graphics g) {
        super.paint(g);

        world.display(g);
        //nn.display(g);
    }
}