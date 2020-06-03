package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import nn.NeuralNetwork;
import world.World;

public class Creature extends Entity {

    private NeuralNetwork nn;
    private ArrayList<Integer> inputAction;
    private ArrayList<Integer> outputAction;
    private double birthWait;
    private double age;
    private double energyMax;
    private double foodType;

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
    private static final int ACT_OUT_TAKE = 14;
    private static final int ACT_OUT_BE_RELEASE = 15;

    public Creature(World world) {
        super(world);
        energy = 100;
        angle = Math.random() * 360;
        size = Math.random() * 10 + 15;
        birthWait = 100;
        age = 0;
        energyMax = size * 20;
        foodType = 0.8;

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

    @Override
    public void display(Graphics g, int offx, int offy) {
        double tmpX = x;
        double tmpY = y;
        x += offx;
        y += offy;

        int red = 55;
        int green = 55;
        // System.out.println(""+red+" "+green);
        if (foodType < 0.5) {
            red = (int) ((0.5 - foodType) * 400);
        }
        if (foodType > 0.5) {
            green = (int) ((foodType - 0.5) * 400);
        }
        g.setColor(new Color(red, green, 55));
        g.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
        g.setColor(new Color(0, 0, 0));
        g.drawOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
        g.drawLine((int) x, (int) y, (int) (x + (Math.cos(angle) * size / 2)),
                (int) (y + (Math.sin(angle) * size / 2)));

        x = tmpX;
        y = tmpY;
    }

    public void displayHighlight(Graphics g, int offx, int offy) {
        g.setColor(new Color(255, 255, 0));
        g.drawOval((int) (x + offx - size / 2), (int) (y + offy - size / 2), (int) size, (int) size);
    }

    public void displayNN(Graphics g) {
        g.setColor(new Color(255, 255, 255, 128));
        int rectH = Math.max(nn.getInputSize(), nn.getOutputSize());
        g.fillRect(0, 0, nn.getLayerSize() * 40 + 50, rectH * 40 + 40);
        nn.display(g, 0, 0);
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < inputAction.size(); i++) {
            g.drawString("" + inputAction.get(i), 5, 20 + i * 10);
        }
        for (int i = 0; i < outputAction.size(); i++) {
            g.drawString("" + outputAction.get(i), 15, 20 + i * 10);
        }
        g.drawString("" + String.format("%.3f", energy), 30, 10);
        g.drawString("" + birthWait, 30, 22);
        g.drawString("" + String.format("%.3f", energyMax), 80, 10);
        g.drawString("" + String.format("%.2f", foodType), 80, 22);
    }

    @Override
    public void step() {
        for (int i = 0; i < inputAction.size(); i++) {
            int a = inputAction.get(i);
            nn.getInputNeuron(i).setRes(actionIn(a));
        }
        nn.step();
        for (int i = 0; i < outputAction.size(); i++) {
            double res = nn.getOutputNeuron(i).getRes();
            actionOut(outputAction.get(i), res);
        }
        if (birthWait > 0)
            birthWait--;

        energy -= 0.001 * nn.neuronCount() + age;
        age += 0.0001;
        if (energy > energyMax)
            energy = energyMax;
    }

    private void eat(ArrayList<Entity> list) {
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (e.getClass() == Food.class && foodType > 0.25) {
                double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
                if (dist < (size / 2) + (e.size / 2)) {
                    energy += e.getEnergy() * foodType;
                    e.setEnergy(0);
                }
            }
            if (e.getClass() == Meat.class && foodType < 0.75) {
                double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
                if (dist < (size / 2) + (e.size / 2)) {
                    energy += e.getEnergy() * (1 - foodType);
                    e.setEnergy(0);
                }
            }
        }
    }

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
            default:
                return 0;
        }
    }

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
        }
    }

    private int randomActIn() {
        int ran = (int) (Math.random() * 7);
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
            default:
                return ACT_NOP;
        }
    }

    private int randomActOut() {
        int ran = (int) (Math.random() * 5);
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
            default:
                return ACT_NOP;
        }
    }

    private double act_in_cst() {
        energy -= 0.001;
        return 1;
    }

    private double act_in_cstNeg() {
        energy -= 0.001;
        return -1;
    }

    private double act_in_energy() {
        energy -= 0.001;
        return energy / 100;
    }

    private double act_in_collide(Class c) {
        energy -= 0.005;
        ArrayList<Entity> list = world.getLocalEntity((int) x, (int) y);

        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (e != this && (c == e.getClass() || c == null) && collide(e)) {
                return 1;
            }
        }
        return 0;
    }

    private void act_out_forward(double res) {
        energy -= 0.02 * Math.abs(res);
        x += Math.cos(angle) * res * (10 / size);
        y += Math.sin(angle) * res * (10 / size);
    }

    private void act_out_rotate(double res) {
        energy -= 0.02 * Math.abs(res);
        angle += res * (10 / size);
    }

    private void act_out_eat(double res) {
        if (res < 1) {
            return;
        }
        energy -= 0.1 * res;
        ArrayList<Entity> list = world.getLocalEntity((int) x, (int) y);
        eat(list);
    }

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

    private void act_out_atk(double res) {
        if (res < 0) {
            return;
        }
        energy -= 0.2 * res;
        ArrayList<Entity> list = world.getLocalEntity((int) x, (int) y);
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (collide(e) && e.getClass() == Creature.class) {
                e.energy -= res*2;
                energy += res;
                break;
            }
        }
    }

    private Creature copyAction(Creature e) {
        e.inputAction = new ArrayList<>();
        for (int i = 0; i < e.nn.getInputSize(); i++) {
            if (i < inputAction.size()) {
                e.inputAction.add(inputAction.get(i));
            } else {
                e.inputAction.add(randomActIn());
            }
        }
        e.outputAction = new ArrayList<>();
        for (int i = 0; i < e.nn.getOutputSize(); i++) {
            if (i < outputAction.size()) {
                e.outputAction.add(outputAction.get(i));
            } else {
                e.outputAction.add(randomActOut());
            }
        }
        return e;
    }

}