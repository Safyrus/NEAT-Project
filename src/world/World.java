package world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.awt.Color;
import java.awt.Graphics;

import entity.Creature;
import entity.Entity;
import entity.Food;

public class World {
    private int x;
    private int y;
    private int w;
    private int h;

    private ArrayList<Entity> entities;
    private ArrayList<ArrayList<Entity>> grid;
    private int cellSize;

    public World(int w, int h) {
        this.x = 0;
        this.y = 0;
        this.w = w;
        this.h = h;

        this.entities = new ArrayList<>();
        cellSize = 40;
    }

    public void display(Graphics g) {
        g.setColor(new Color(128, 128, 128));
        g.fillRect(x, y, w, h);
        for (Entity entity : entities) {
            entity.display(g);
        }
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < h / cellSize; i++) {
            for (int j = 0; j < w / cellSize; j++) {
                if (grid.get(i * w / cellSize + j) != null) {
                    g.drawString("" + grid.get(i * w / cellSize + j).size(), j * cellSize + cellSize / 2,
                            i * cellSize + cellSize / 2);
                } else {
                    g.drawString("0", j * cellSize + cellSize / 2, i * cellSize + cellSize / 2);
                }
            }
        }
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public void step() {

        grid = new ArrayList<>(Collections.nCopies(h / cellSize * w / cellSize, null));

        for (Entity entity : entities) {
            int cx = (int) (entity.getX() / cellSize);
            int cy = (int) (entity.getY() / cellSize);
            int i = cy * (w / cellSize) + cx;
            ArrayList<Entity> list = grid.get(i);
            if (list == null) {
                grid.set(i, new ArrayList<>());
            }
            grid.get(i).add(entity);
        }
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.step();

            double ex = entity.getX();
            double ey = entity.getY();
            int cx = (int) (ex / cellSize);
            int cy = (int) (ey / cellSize);

            if (ex < 0)
                entity.setX(ex + w);
            if (ex > w)
                entity.setX(ex - w);
            if (ey < 0)
                entity.setY(ey + h);
            if (ey > h)
                entity.setY(ey - h);
            if (entity.getClass() == Creature.class) {
                Creature crea = (Creature) entity;
                ArrayList<Entity> list = getLocalEntity(cx, cy);
                ArrayList<Entity> tmp = crea.eat(list);
                list.removeAll(tmp);
                entities.removeAll(list);
                i = entities.indexOf(crea);
                if (crea.getEnergy() <= 0) {
                    entities.remove(crea);
                    i--;
                }
            }
        }
        if (Math.random() < 1) {
            Food f = new Food();
            f.setX(Math.random() * w);
            f.setY(Math.random() * h);
            entities.add(f);
        }
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Entity MouseGetEntity(int mx, int my) {
        for (Entity entity : entities) {
            if (entity.collide(mx, my)) {
                return entity;
            }
        }
        return null;
    }

    private ArrayList<Entity> getLocalEntity(int lx, int ly) {
        ArrayList<Entity> res = new ArrayList<>();

        int gw = w / cellSize;
        int gh = h / cellSize;
        if(lx >= gw) {
            lx--;
        }
        if(ly >= gh) {
            ly--;
        }
        int lowY = (ly - 1 < 0) ? (ly - 1 + gh) : (ly - 1);
        int lowX = (lx - 1 < 0) ? (lx - 1 + gw) : (lx - 1);
        int higY = (ly + 1 >= gh) ? (ly + 1 - gh) : (ly + 1);
        int higX = (lx + 1 >= gw) ? (lx + 1 - gw) : (lx + 1);
        //System.out.println(lx+","+ly+" "+lowX+","+lowY+" "+higX+","+higY);
        if (grid.get(lowY * gw + lowX) != null)
            res.addAll(grid.get(lowY * gw + lowX));
        if (grid.get(lowY * gw + lx) != null)
            res.addAll(grid.get(lowY * gw + lx));
        if (grid.get(lowY * gw + higX) != null)
            res.addAll(grid.get(lowY * gw + higX));
        if (grid.get(ly * gw + lowX) != null)
            res.addAll(grid.get(ly * gw + lowX));
        if (grid.get(ly * gw + lx) != null)
            res.addAll(grid.get(ly * gw + lx));
        if (grid.get(ly * gw + higX) != null)
            res.addAll(grid.get(ly * gw + higX));
        if (grid.get(higY * gw + lowX) != null)
            res.addAll(grid.get(higY * gw + lowX));
        if (grid.get(higY * gw + lx) != null)
            res.addAll(grid.get(higY * gw + lx));
        if (grid.get(higY * gw + higX) != null)
            res.addAll(grid.get(higY * gw + higX));

        return res;
    }
}