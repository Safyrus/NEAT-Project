package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import nn.NeuralNetwork;
import world.World;

/**
 * Class that represents a creature
 */
public class Creature extends Entity {

    /**
     * The neural network of the creature
     */
    private NeuralNetwork nn;
    /**
     * actions that send data to the input layer of the neural network
     */
    private ArrayList<Integer> inputAction;
    /**
     * actions that recieve data from the output layer of the neural network
     */
    private ArrayList<Integer> outputAction;
    /**
     * array of entity nearby
     */
    private ArrayList<Entity> entityLocal;
    /**
     * grabed entity
     */
    private Entity grab;
    /**
     * time before the creature can create another one
     */
    private double birthWait;
    /**
     * the amount of time since that creature has been alive
     */
    private double age;
    /**
     * the maximum energy that this creature can have
     */
    private double energyMax;
    /**
     * the type of food that this creature can eat.
     */
    private double foodType;

    //all types of actions
    private static final int ACT_NOP = 0;
    private static final int ACT_IN_CST = 1;
    private static final int ACT_IN_CST_NEG = 2;
    private static final int ACT_OUT_FORWARD = 3;
    private static final int ACT_OUT_ROTATE = 4;
    private static final int ACT_OUT_EAT = 5;
    private static final int ACT_IN_COLLIDE = 6;
    private static final int ACT_OUT_BIRTH = 7;
    private static final int ACT_IN_ENERGY = 8;
    private static final int ACT_IN_COLLIDE_FOOD = 9;
    private static final int ACT_IN_COLLIDE_MEAT = 10;
    private static final int ACT_IN_COLLIDE_CREA = 11;
    private static final int ACT_IN_SEE = 12;
    private static final int ACT_OUT_ATK = 13;
    private static final int ACT_OUT_GRAB = 14;
    private static final int ACT_OUT_UNGRAB = 15;

    /**
     * Default constructor
     * @param world the world in which the entity is in
     */
    public Creature(World world) {
        super(world);

        //initializes variables
        energy = 100;
        angle = Math.random() * 360;
        size = Math.random() * 10 + 15;
        birthWait = 100;
        age = 0;
        energyMax = size * 20;
        foodType = 0.8;
        grab = null;

        //defines inputs and outputs actions
        nn = new NeuralNetwork();
        inputAction = new ArrayList<>();
        outputAction = new ArrayList<>();
        inputAction.add(ACT_IN_COLLIDE);
        inputAction.add(ACT_IN_CST);
        inputAction.add(ACT_IN_ENERGY);
        outputAction.add(ACT_OUT_EAT);
        outputAction.add(ACT_OUT_FORWARD);
        outputAction.add(ACT_OUT_BIRTH);
    }

    /**
     * Displays the Creature
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

        //sets the color of the creature according to the type of food it eats
        int red = 55;
        int green = 55;
        if (foodType < 0.5) {
            red = (int) ((0.5 - foodType) * 400);
        }
        if (foodType > 0.5) {
            green = (int) ((foodType - 0.5) * 400);
        }
        g.setColor(new Color(red, green, 55));

        //displays the creature
        g.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
        if (outputAction.contains(ACT_OUT_ATK)) {
            g.setColor(new Color(255, 0, 0));
        } else if (outputAction.contains(ACT_OUT_GRAB)) {
            g.setColor(new Color(0, 0, 255));
        } else if (outputAction.contains(ACT_OUT_UNGRAB)) {
            g.setColor(new Color(0, 255, 255));
        } else {
            g.setColor(new Color(0, 0, 0));
        }

        //displays the creature's orientation
        g.drawOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
        g.drawLine((int) x, (int) y, (int) (x + (Math.cos(Math.toRadians(angle)) * size / 2)),
                (int) (y + (Math.sin(Math.toRadians(angle)) * size / 2)));

        //displays what the creature sees if it possesses this ability
        if (inputAction.contains(ACT_IN_SEE)) {
            double angleMin = angle - 20;
            double angleMax = angle + 20;
            g.setColor(new Color(100, 100, 200));
            g.drawLine((int) x, (int) y, (int) (x + (Math.cos(Math.toRadians(angleMin)) * size * 3)),
                    (int) (y + (Math.sin(Math.toRadians(angleMin)) * size * 3)));
            g.drawLine((int) x, (int) y, (int) (x + (Math.cos(Math.toRadians(angleMax)) * size * 3)),
                    (int) (y + (Math.sin(Math.toRadians(angleMax)) * size * 3)));
        }

        x = tmpX;
        y = tmpY;
    }

    /**
     * Displays the highlighted version of the creature
     * @param g
     * @param offx the x coordinate offset
     * @param offy the x coordinate offset
     */
    public void displayHighlight(Graphics g, int offx, int offy) {
        g.setColor(new Color(255, 255, 0));
        g.drawOval((int) (x + offx - size / 2), (int) (y + offy - size / 2), (int) size, (int) size);
    }

    /**
     * Displays the neural network and informations about the creature
     * @param g
     */
    public void displayNN(Graphics g) {
        //displays a rectangle
        g.setColor(new Color(255, 255, 255, 128));
        int rectH = Math.max(nn.getInputSize(), nn.getOutputSize());
        g.fillRect(0, 0, nn.getLayerSize() * 40 + 50, rectH * 40 + 40);

        //displays the neural network
        nn.display(g, 0, 0);

        //displays creature's informations
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < inputAction.size(); i++) {
            g.drawString("" + inputAction.get(i), 5, 20 + i * 10);
        }
        for (int i = 0; i < outputAction.size(); i++) {
            g.drawString("" + outputAction.get(i), 20, 20 + i * 10);
        }
        g.drawString("" + String.format("%.3f", energy), 30, 10);
        g.drawString("" + birthWait, 30, 22);
        g.drawString("" + String.format("%.3f", energyMax), 80, 10);
        g.drawString("" + String.format("%.2f", foodType), 80, 22);
    }

    /**
     * Actions made by the Creature
     */
    @Override
    public void step() {
        //get nearby entity
        entityLocal = world.getLocalEntity((int) x, (int) y);

        //performes all input actions
        for (int i = 0; i < inputAction.size(); i++) {
            int a = inputAction.get(i);
            nn.getInputNeuron(i).setRes(actionIn(a));
        }

        //calculates the neural network
        nn.step();

        //performes all output actions
        for (int i = 0; i < outputAction.size(); i++) {
            double res = nn.getOutputNeuron(i).getRes();
            actionOut(outputAction.get(i), res);
        }

        if (birthWait > 0)
            birthWait--;

        age += 0.0001;

        energy -= 0.001 * nn.neuronCount() + age;
        if (energy > energyMax)
            energy = energyMax;

        //move the entity grabed
        if (grab != null) {
            grab.x = x + (size * Math.cos(Math.toRadians(angle)));
            grab.y = y + (size * Math.sin(Math.toRadians(angle)));
        }
    }

    /**
     * Eat an entity
     * @param e entity
     */
    private void eat(Entity e) {
        double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
        if (dist < (size / 2) + (e.size / 2)) {
            double maxEatEnergy = energyMax - energy;
            if (maxEatEnergy < e.getEnergy()) {
                energy += maxEatEnergy * foodType;
                e.setEnergy(e.getEnergy() - maxEatEnergy);
            } else {
                energy += e.getEnergy() * foodType;
                e.setEnergy(0);
            }
        }
    }

    /**
     * Performs an input action and gets the result
     * @param a action
     * @return result
     */
    private double actionIn(int a) {
        switch (a) {
            case ACT_IN_CST:
                return act_in_cst();
            case ACT_IN_CST_NEG:
                return act_in_cstNeg();
            case ACT_IN_COLLIDE:
                return act_in_collide(null);
            case ACT_IN_COLLIDE_CREA:
                return act_in_collide(Creature.class);
            case ACT_IN_COLLIDE_FOOD:
                return act_in_collide(Food.class);
            case ACT_IN_COLLIDE_MEAT:
                return act_in_collide(Meat.class);
            case ACT_IN_ENERGY:
                return act_in_energy();
            case ACT_IN_SEE:
                return act_in_see();
            default:
                return 0;
        }
    }

    /**
     * sends a result to and performs an output action
     * @param a action
     * @param res result of the neural network
     */
    private void actionOut(int a, double res) {
        switch (a) {
            case ACT_OUT_FORWARD:
                act_out_forward(res);
                break;
            case ACT_OUT_ROTATE:
                act_out_rotate(res);
                break;
            case ACT_OUT_BIRTH:
                act_out_birth(res);
                break;
            case ACT_OUT_EAT:
                act_out_eat(res);
                break;
            case ACT_OUT_ATK:
                act_out_atk(res);
                break;
            case ACT_OUT_GRAB:
                act_out_grab(res);
                break;
            case ACT_OUT_UNGRAB:
                act_out_ungrab(res);
                break;
        }
    }

    /**
     * Gets a random input action
     * @return action
     */
    private int randomActIn() {
        int ran = (int) (Math.random() * 8);
        switch (ran) {
            case 0:
                return ACT_IN_CST;
            case 1:
                return ACT_IN_CST_NEG;
            case 2:
                return ACT_IN_COLLIDE;
            case 3:
                return ACT_IN_ENERGY;
            case 4:
                return ACT_IN_COLLIDE_MEAT;
            case 5:
                return ACT_IN_COLLIDE_FOOD;
            case 6:
                return ACT_IN_COLLIDE_CREA;
            case 7:
                return ACT_IN_SEE;
            default:
                return ACT_NOP;
        }
    }

    /**
     * Gets a random output action
     * @return action
     */
    private int randomActOut() {
        int ran = (int) (Math.random() * 7);
        switch (ran) {
            case 0:
                return ACT_OUT_FORWARD;
            case 1:
                return ACT_OUT_ROTATE;
            case 2:
                return ACT_OUT_BIRTH;
            case 3:
                return ACT_OUT_EAT;
            case 4:
                return ACT_OUT_ATK;
            case 5:
                return ACT_OUT_GRAB;
            case 6:
                return ACT_OUT_UNGRAB;
            default:
                return ACT_NOP;
        }
    }

    /**
     * Input action: constant
     * @return result
     */
    private double act_in_cst() {
        energy -= 0.001;
        return 1;
    }

    /**
     * Input action: negative constant
     * @return result
     */
    private double act_in_cstNeg() {
        energy -= 0.001;
        return -1;
    }

    /**
     * Input action: energy level
     * @return result
     */
    private double act_in_energy() {
        energy -= 0.001;
        return energy / 100;
    }

    /**
     * Input action: collision with a type of entity
     * @param c type of entity
     * @return result
     */
    private double act_in_collide(Class c) {
        energy -= 0.005;
        for (int i = 0; i < entityLocal.size(); i++) {
            Entity e = entityLocal.get(i);
            if (e != this && (c == e.getClass() || c == null) && collide(e)) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Input action: see any entity
     * @return result
     */
    private double act_in_see() {
        double res = 0;
        energy -= 0.005;

        for (int i = 0; i < entityLocal.size(); i++) {
            Entity e = entityLocal.get(i);
            double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
            boolean collide = dist < ((size * 3)) + (e.size / 2);
            if (e != this && collide) {
                double eAngle = Math.toDegrees(Math.atan2(e.y - y, e.x - x));
                if (eAngle < 0)
                    eAngle += 360;
                double angleMin = angle - 20;
                double angleMax = angle + 20;
                if (eAngle <= angleMax && eAngle >= angleMin) {
                    res = 1;
                    break;
                }
            }
        }

        return res;
    }

    /**
     * Output action: move forward
     * @param res result of the output neuron
     */
    private void act_out_forward(double res) {
        energy -= 0.02 * Math.abs(res);
        x += Math.cos(Math.toRadians(angle)) * res * (10 / size);
        y += Math.sin(Math.toRadians(angle)) * res * (10 / size);
    }

    /**
     * Output action: rotate
     * @param res result of the output neuron
     */
    private void act_out_rotate(double res) {
        energy -= 0.02 * Math.abs(res);
        angle += res * (10 / size);
    }

    /**
     * Output action: eat an entity
     * @param res result of the output neuron
     */
    private void act_out_eat(double res) {
        if (res < 1) {
            return;
        }
        energy -= 0.1 * res;
        for (int i = 0; i < entityLocal.size(); i++) {
            Entity e = entityLocal.get(i);
            if (e.getClass() == Food.class && foodType > 0.25) {
                eat(e);
            } else if (e.getClass() == Meat.class && foodType < 0.75) {
                eat(e);
            }
        }
    }

    /**
     * Output action: creates another entity
     * @param res result of the output neuron
     */
    private void act_out_birth(double res) {
        energy -= 0.02 * Math.max(res, 0);
        if (res < 1 || birthWait > 0) {
            return;
        }
        Creature e = new Creature(world);
        e.nn = nn.copy();
        e.x = x;
        e.y = y;
        e.size = size + (Math.random() * 2 - 1);
        e.foodType = foodType + (Math.random() * 0.02 - 0.01);
        e.foodType = Math.max(Math.min(e.foodType, 1), 0);
        e.energyMax = size * 20;
        e = copyAction(e);
        birthWait = 100;
        if (energy < 100) {
            e.energy = energy;
            energy = 0;
        } else {
            energy -= 100;
        }
        world.addEntity(e);
    }

    /**
     * Output action: attacks another entity
     * @param res result of the output neuron
     */
    private void act_out_atk(double res) {
        if (res < 0) {
            return;
        }
        energy -= 0.2 * res;
        for (int i = 0; i < entityLocal.size(); i++) {
            Entity e = entityLocal.get(i);
            if (collide(e) && e.getClass() == Creature.class) {
                e.energy -= res * 2;
                energy += res;
                break;
            }
        }
    }

    /**
     * Output action: grabs another entity
     * @param res result of the output neuron
     */
    private void act_out_grab(double res) {
        if (res < 1) {
            grab = null;
            return;
        }
        energy -= 0.01;
        if (grab == null) {
            for (int i = 0; i < entityLocal.size(); i++) {
                Entity e = entityLocal.get(i);
                if (e != this && collide(e)) {
                    grab = e;
                    break;
                }
            }
        }
    }

    /**
     * Output action: releases itself from another entity
     * @param res result of the output neuron
     */
    private void act_out_ungrab(double res) {
        if (res < 1) {
            return;
        }
        energy -= 0.02;
        for (int i = 0; i < entityLocal.size(); i++) {
            if (entityLocal.get(i).getClass() == Creature.class) {
                Creature c = (Creature) entityLocal.get(i);
                if (c.grab == this) {
                    c.grab = null;
                }
            }
        }
    }

    /**
     * changes a creature's action to this creature's actions with a chance to mutate
     * @param e creature to change
     * @return changed creature
     */
    private Creature copyAction(Creature e) {
        e.inputAction = new ArrayList<>();
        for (int i = 0; i < e.nn.getInputSize(); i++) {
            if (i < inputAction.size() && Math.random() > 0.02) {
                e.inputAction.add(inputAction.get(i));
            } else {
                e.inputAction.add(randomActIn());
            }
        }
        e.outputAction = new ArrayList<>();
        for (int i = 0; i < e.nn.getOutputSize(); i++) {
            if (i < outputAction.size() && Math.random() > 0.02) {
                e.outputAction.add(outputAction.get(i));
            } else {
                e.outputAction.add(randomActOut());
            }
        }
        return e;
    }

}