package entity;

import java.awt.Color;
import java.awt.Graphics;

import world.World;

public class Food extends Entity {

    public Food(World world) {
        super(world);
        energy = Math.random()*9 + 1;
    }

    @Override
    public void display(Graphics g, int offx, int offy) {
        double tmpX = x;
        double tmpY = y;
        x += offx;
        y += offy;

        g.setColor(new Color(0, (int)(150 + 10*energy), 0));
        size = (int)(5+energy);
        g.fillOval((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);

        x = tmpX;
        y = tmpY;
    }

    @Override
    public void step() {

    }
    
}