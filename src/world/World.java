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

/**
 * This class represents a world with entities that evolve in it
 */
public class World {
    /**
     * the x coordinate of the world
     */
    private int x;
    /**
     * the y coordinate of the world
     */
    private int y;
    /**
     * the width of the world
     */
    private int w;
    /**
     * the height of the world
     */
    private int h;

    /**
     * list of entities in the world
     */
    private ArrayList<Entity> entities;
    /**
     * entity grid used to compute the interaction between entities
     */
    private ArrayList<ArrayList<Entity>> grid;
    /**
     * entities to add at the end of the cycle
     */
    private ArrayList<Entity> toAdd;
    /**
     * the size of cells in the grid
     */
    private int cellSize;

    /**
     * Default constructor
     * 
     * @param w width of the world
     * @param h height of the world
     */
    public World(int w, int h) {
        this.x = 0;
        this.y = 0;
        this.w = w;
        this.h = h;

        toAdd = new ArrayList<>();
        this.entities = new ArrayList<>();
        cellSize = 40;
    }

    /**
     * Displays the world
     * 
     * @param g
     * @param offx the x coordinate offset
     * @param offy the y coordinate offset
     */
    public void display(Graphics g, int offx, int offy) {

        // displays the background
        g.setColor(new Color(128, 128, 128));
        g.fillRect(x + offx, y + offy, w, h);

        // displays all the entities
        int creaCount = 0;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.getClass() == Creature.class) {
                creaCount++;
            }
            entity.display(g, x + offx, y + offy);
        }

        // displays the number of entities in each cell of the grid
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

        // displays info about the world
        g.setColor(new Color(120, 0, 0));
        g.drawString(x + " " + y, 20, 10);
        g.drawString("All:" + entities.size(), 20, 30);
        g.drawString("Crea:" + creaCount, 20, 40);
    }

    /**
     * Adds an entity to the world
     * 
     * @param e Entity to add
     */
    public void addEntity(Entity e) {
        toAdd.add(e);
    }

    /**
     * Removes an entity from the world
     * 
     * @param e Entity to remove
     */
    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    /**
     * Executes one step of the simulation
     */
    public void step() {

        // resets the grid
        grid = new ArrayList<>(Collections.nCopies(h / cellSize * w / cellSize, null));

        // adds all the entities to their corresponding cell in the grid
        for (Entity entity : entities) {

            // calculates the cell coordinate
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

            // adds entity to the grid
            int i = cy * cw + cx;
            ArrayList<Entity> list = grid.get(i);
            if (list == null) {
                grid.set(i, new ArrayList<>());
            }
            grid.get(i).add(entity);
        }

        // executes each action of all entities
        ListIterator<Entity> iter = entities.listIterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();

            // executes the action of the entity
            entity.step();

            // repositions the entity if it get out of the world
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

            // removes the entity if it's energy is 0 or less
            if (entity.getEnergy() <= 0) {
                iter.remove();

                // if the entity is a Creature then create Meat entities
                if (entity.getClass() == Creature.class) {
                    Meat m = new Meat(this);
                    m.setX(entity.getX() + Math.random() * 10 - 5);
                    m.setY(entity.getY() + Math.random() * 10 - 5);
                    m.setEnergy(entity.getSize() / 2);
                    toAdd.add(m);
                }
            }
        }

        // adds the entities created in this step
        for (int i = 0; i < toAdd.size(); i++) {
            entities.add(toAdd.get(i));
        }
        // resets the list
        toAdd = new ArrayList<>();

        // create Food entities
        if (Math.random() < 0.25 && entities.size() < (w * h)) {
            for (int i = 0; i < (w * h) / 40000; i++) {
                Food f = new Food(this);
                f.setX(Math.random() * w);
                f.setY(Math.random() * h);
                entities.add(f);
            }
        }
    }

    /**
     * Gets the width of the world
     * 
     * @return int width
     */
    public int getW() {
        return w;
    }

    /**
     * Gets the height of the world
     * 
     * @return int height
     */
    public int getH() {
        return h;
    }

    /**
     * Gets the Entity at the specified coordinate
     * 
     * @param xx x coordinate to look at
     * @param yy y coordinate to look at
     * @return Entity or null if none at this coordinate
     */
    public Entity GetEntity(int xx, int yy) {
        for (Entity entity : entities) {
            if (entity.collide(xx, yy)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Gets entities that are nearby the coordinates we want to check
     * 
     * @param xx x coordinate to look at
     * @param yy y coordinate to look at
     * @return ArrayList<Entity> entities that are nearby
     */
    public ArrayList<Entity> getLocalEntity(int xx, int yy) {
        ArrayList<Entity> res = new ArrayList<>();

        // calculates which cells we need to check
        int lx = xx / cellSize;
        int ly = yy / cellSize;
        int gw = w / cellSize;
        int gh = h / cellSize;
        if (lx >= gw) {
            lx = gw - 1;
        }
        if (ly >= gh) {
            ly = gh - 1;
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

        // adds all entities from the nearby cells
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