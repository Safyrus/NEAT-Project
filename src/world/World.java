package world;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;

import entity.Entity;

public class World {
    private int x;
    private int y;
    private int w;
    private int h;

    private ArrayList<Entity> entities;

    public World(int w, int h) {
        this.x = 0;
        this.y = 0;
        this.w = w;
        this.h = h;
        this.entities = new ArrayList<>();
    }

    public void display(Graphics g) {
        g.setColor(new Color(128, 128, 128));
        g.fillRect(x, y, w, h);
        for (Entity entity : entities) {
            entity.display(g);
        }
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public void step() {
        for (Entity entity : entities) {
            entity.step();
            if (entity.getX() < 0)
                entity.setX(entity.getX() + w);
            if (entity.getX() > w)
                entity.setX(entity.getX() - w);
            if (entity.getY() < 0)
                entity.setY(entity.getY() + h);
            if (entity.getY() > h)
                entity.setY(entity.getY() - h);
        }
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}