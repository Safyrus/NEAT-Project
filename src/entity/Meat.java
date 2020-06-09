package entity;

import java.awt.Color;
import java.awt.Graphics;

import world.World;

/**
 * Class that represente Meat
 */
public class Meat extends Entity {

    /**
     * Default constructor
     * 
     * @param world the world in which the entity is in
     */
    public Meat(World world) {
        super(world);
        energy = Math.random()*9 + 1;
    }
    /**
     * Display the Meat
     * 
     * @param g
     * @param offx the x coordinate offset
     * @param offy the x coordinate offset
     */
    @Override
    public void display(Graphics g, int offx, int offy) {
        double tmpX = x;
        double tmpY = y;
        x += offx;
        y += offy;

        int r = (energy > 10)?255:(int)(150 + 10*energy);
        g.setColor(new Color(r, 0, 0));
        size = (int)(5+energy);
        g.fillOval((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);

        x = tmpX;
        y = tmpY;
    }

    /**
     * Action made by the Meat
     */
    @Override
    public void step() {
        energy -= 0.01;
    }
    
}