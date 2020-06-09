package entity;

import java.awt.Graphics;

import world.World;

/**
 * Abstract Class that represente an Entity
 */
public abstract class Entity {
    /**
     * the world in which the entity is in
     */
    protected World world;
    /**
     * the x coordinate of the entity
     */
    protected double x;
    /**
     * the y coordinate of the entity
     */
    protected double y;

    /**
     * the energy of the entity
     */
    protected double energy;
    /**
     * the angle of the entity
     */
    protected double angle;
    /**
     * the size of the entity
     */
    protected double size;

    /**
     * Default constructor
     * 
     * @param world the world in which the entity is in
     */
    public Entity(World world) {
        this.world = world;
        energy = 10;
        angle = 0;
        size = 10;

        this.x = 0;
        this.y = 0;
    }

    /**
     * Setter for x
     * 
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Setter for y
     * 
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Setter for x
     * 
     * @return double
     */
    public double getX() {
        return x;
    }

    /**
     * Getter for y
     * 
     * @return double
     */
    public double getY() {
        return y;
    }

    /**
     * Getter for size
     * 
     * @return double
     */
    public double getSize() {
        return size;
    }

    /**
     * Getter for angle
     * 
     * @return double
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Getter for energy
     * 
     * @return double
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * Setter for energy
     * 
     * @param e
     */
    public void setEnergy(double e) {
        energy = e;
    }

    /**
     * Display the entity
     * 
     * @param g
     * @param offx the x coordinate offset
     * @param offy the x coordinate offset
     */
    public abstract void display(Graphics g, int offx, int offy);

    /**
     * Action made by the entity
     */
    public abstract void step();

    /**
     * checks if the entity collide with another entity
     * @param e other entity
     * @return boolean true if the two collide
     */
    public boolean collide(Entity e) {
        double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
        return dist < (size / 2) + (e.size / 2);
    }

    /**
     * checks if the entity collide with a point
     * @param xx the x coordinate of the point
     * @param yy the y coordinate of the point
     * @return boolean true if the two collide
     */
    public boolean collide(int xx, int yy) {
        double dist = Math.sqrt(Math.pow(yy - y, 2) + Math.pow(xx - x, 2));
        return dist < size / 2;
    }
}