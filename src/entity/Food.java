package entity;

import java.awt.Color;
import java.awt.Graphics;

public class Food extends Entity {

    public Food() {
        super();
        energy = Math.random()*9 + 1;
    }

    @Override
    public void display(Graphics g) {
        g.setColor(new Color(0, (int)(150 + 10*energy), 0));
        size = (int)(5+energy);
        g.fillOval((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);
    }

    @Override
    public void step() {

    }
    
}