package world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.awt.Color;
import java.awt.Graphics;

import entity.Creature;
import entity.Entity;
import entity.Food;
import entity.Meat;

public class World {
    private int x;
    private int y;
    private int w;
    private int h;

    private ArrayList<Entity> entities;
    private ArrayList<ArrayList<Entity>> grid;
    private ArrayList<Entity> toAdd;
    private int cellSize;

    public World(int w, int h) {
        this.x = 0;
        this.y = 0;
        this.w = w;
        this.h = h;

        toAdd = new ArrayList<>();
        this.entities = new ArrayList<>();
        cellSize = 40;
    }

    public void display(Graphics g, int offx, int offy) {

        g.setColor(new Color(128, 128, 128));
        g.fillRect(x + offx, y + offy, w, h);
        int creaCount = 0;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if(entity.getClass() == Creature.class) {
                creaCount++;
            }
            entity.display(g, x + offx, y + offy);
        }
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < h / cellSize; i++) {
            for (int j = 0; j < w / cellSize; j++) {
                if (grid.get(i * w / cellSize + j) != null) {
                    g.drawString("" + grid.get(i * w / cellSize + j).size(), j * cellSize + cellSize / 2 + x + offx,
                            i * cellSize + cellSize / 2 + y + offy);
                } else {
                    g.drawString("0", j * cellSize + cellSize / 2 + x + offx, i * cellSize + cellSize / 2 + y + offy);
                }
            }
        }

        g.setColor(new Color(255, 0, 0));
        g.drawString(x + " " + y, 20, 10);
        g.drawString("All:" + entities.size(), 20, 30);
        g.drawString("Crea:" + creaCount, 20, 40);
    }

    public void addEntity(Entity e) {
        toAdd.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public void step() {

        grid = new ArrayList<>(Collections.nCopies(h / cellSize * w / cellSize, null));

        for (Entity entity : entities) {
            int cx = (int) (entity.getX() / cellSize);
            int cy = (int) (entity.getY() / cellSize);
            int cw = (int) (w / cellSize);
            int ch = (int) (h / cellSize);
            if (cx >= cw) {
                cx = cw - 1;
            }
            if (cy >= ch) {
                cy = ch - 1;
            }
            if (cx < 0) {
                cx = 0;
            }
            if (cy < 0) {
                cy = 0;
            }
            int i = cy * cw + cx;
            ArrayList<Entity> list = grid.get(i);
            if (list == null) {
                grid.set(i, new ArrayList<>());
            }
            grid.get(i).add(entity);
        }
        ListIterator<Entity> iter = entities.listIterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();
            entity.step();

            double ex = entity.getX();
            double ey = entity.getY();

            if (ex < 0)
                entity.setX(ex + w);
            if (ex > w)
                entity.setX(ex - w);
            if (ey < 0)
                entity.setY(ey + h);
            if (ey > h)
                entity.setY(ey - h);

            if (entity.getEnergy() <= 0) {
                iter.remove();
                if(entity.getClass() == Creature.class) {
                    Meat m = new Meat(this);
                    m.setX(entity.getX()+Math.random()*10 - 5);
                    m.setY(entity.getY()+Math.random()*10 - 5);
                    m.setEnergy(entity.getSize()/2);
                    toAdd.add(m);
                }
            }
        }
        for (int i = 0; i < toAdd.size(); i++) {
            entities.add(toAdd.get(i));
        }
        toAdd = new ArrayList<>();
        if (Math.random() < 0.5) {
            for (int i = 0; i < (w * h) / 40000; i++) {
                Food f = new Food(this);
                f.setX(Math.random() * w);
                f.setY(Math.random() * h);
                entities.add(f);
            }
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

    public ArrayList<Entity> getLocalEntity(int xx, int yy) {
        ArrayList<Entity> res = new ArrayList<>();

        int lx = xx / cellSize;
        int ly = yy / cellSize;
        int gw = w / cellSize;
        int gh = h / cellSize;
        if (lx >= gw) {
            lx = gw-1;
        }
        if (ly >= gh) {
            ly = gh-1;
        }
        if (lx < 0) {
            lx = 0;
        }
        if (ly < 0) {
            ly = 0;
        }
        int lowY = (ly - 1 < 0) ? (ly - 1 + gh) : (ly - 1);
        int lowX = (lx - 1 < 0) ? (lx - 1 + gw) : (lx - 1);
        int higY = (ly + 1 >= gh) ? (ly + 1 - gh) : (ly + 1);
        int higX = (lx + 1 >= gw) ? (lx + 1 - gw) : (lx + 1);
        // System.out.println(lx+","+ly+" "+lowX+","+lowY+" "+higX+","+higY);
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