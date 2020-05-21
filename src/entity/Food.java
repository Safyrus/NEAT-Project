package entity;

import java.awt.Color;
import java.awt.Graphics;

public class Food extends Entity {

    private double energy;

    public Food() {
        super();
        energy = Math.random()*9 + 1;
    }

    @Override
    public void display(Graphics g) {
        g.setColor(new Color(0, (int)(150 + 10*energy), 0));
        int size = (int)(5+energy);
        g.fillOval((int)(x-size/2), (int)(y-size/2), size, size);
    }

    public double getEnergy() {
        return energy;
    }

    @Override
    public void step() {

    }
    
}