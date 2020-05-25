package entity;

import java.awt.Graphics;

public abstract class Entity {
    protected double x;
    protected double y;

    protected double energy;
    protected double angle;
    protected double size;

    public Entity() {
        energy = 10;
        angle = 0;
        size = 10;

        this.x = 0;
        this.y = 0;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public double getAngle() {
        return angle;
    }

    public double getEnergy() {
        return energy;
    }

    public abstract void display(Graphics g);

    public abstract void step();

    public boolean collide(int xx, int yy) {
        double dist = Math.sqrt(Math.pow(yy - y, 2) + Math.pow(xx - x, 2));
        return dist <= size/2;
    }
}