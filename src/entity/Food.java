package entity;

import java.awt.Color;
import java.awt.Graphics;

import world.World;

/**
 * Class that represente Food
 */
public class Food extends Entity {

    /**
     * Default constructor
     * 
     * @param world the world in which the entity is in
     */
    public Food(World world) {
        super(world);
        energy = Math.random()*19 + 1;
    }

    
    /**
     * Displays the Food
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

        int green = (energy > 10)?255:(int)(150 + 10*energy);
        g.setColor(new Color(0, green, 0));
        size = (int)(5+energy);
        g.fillOval((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);

        x = tmpX;
        y = tmpY;
    }

    /**
     * Actions made by the Food
     */
    @Override
    public void step() {
        energy -= 0.01;
    }
    
}