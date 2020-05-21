package entity;

import java.awt.Graphics;

public abstract class Entity {
    protected double x;
    protected double y;

    public Entity() {
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

    public abstract void display(Graphics g);

    public abstract void step();
}