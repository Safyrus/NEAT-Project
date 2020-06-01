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

    private static final int ACT_NOP = 0;
    private static final int ACT_IN_CST = 1;
    private static final int ACT_IN_CST_NEG = 2;
    private static final int ACT_IN_ENERGY = 7;
    private static final int ACT_OUT_FORWARD = 2;
    private static final int ACT_OUT_ROTATE = 3;
    private static final int ACT_OUT_EAT = 4;
    private static final int ACT_IN_COLLIDE = 5;
    private static final int ACT_OUT_BIRTH = 6;

    public Creature(World world) {
        super(world);
        energy = 100;
        angle = Math.random() * 360;
        size = Math.random()*10+15;
        birthWait = 100;
        age = 0;
        energyMax = size*20;

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

        g.setColor(new Color(0, 0, 200));
        g.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
        g.setColor(new Color(0, 0, 0));
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
        g.fillRect(0, 0, nn.getLayerSize() * 40 + 50, rectH*40 + 40);
        nn.display(g, 0, 0);
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < inputAction.size(); i++) {
            g.drawString("" + inputAction.get(i), 5, 20 + i * 10);
        }
        for (int i = 0; i < outputAction.size(); i++) {
            g.drawString("" + outputAction.get(i), 15, 20 + i * 10);
        }
        g.drawString("" + energy, 30, 10);
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
            if (e.getClass() == Food.class) {
                double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
                if (dist < (size / 2) + (e.size / 2)) {
                    energy += e.getEnergy();
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
                return act_in_collide();
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
        }
    }

    private int randomActIn() {
        int ran = (int) (Math.random() * 4);
        switch (ran) {
            case 0:
                return ACT_IN_CST;
            case 1:
                return ACT_IN_CST_NEG;
            case 2:
                return ACT_IN_COLLIDE;
            case 3:
                return ACT_IN_ENERGY;
            default:
                return ACT_NOP;
        }
    }

    private int randomActOut() {
        int ran = (int) (Math.random() * 4);
        switch (ran) {
            case 0:
                return ACT_OUT_FORWARD;
            case 1:
                return ACT_OUT_ROTATE;
            case 2:
                return ACT_OUT_BIRTH;
            case 3:
                return ACT_OUT_EAT;
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

    private double act_in_collide() {
        energy -= 0.005;
        ArrayList<Entity> list = world.getLocalEntity((int) x, (int) y);

        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (e != this && collide(e)) {
                return 1;
            }
        }
        return 0;
    }

    private void act_out_forward(double res) {
        energy -= 0.02 * Math.abs(res);
        x += Math.cos(angle) * res * (10/size);
        y += Math.sin(angle) * res * (10/size);
    }

    private void act_out_rotate(double res) {
        energy -= 0.02 * Math.abs(res);
        angle += res * (10/size);
    }

    private void act_out_eat(double res) {
        energy -= 0.1 * Math.max(res, 0);
        if (res < 1) {
            return;
        }
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
        e.size = size + (Math.random()*2-1);
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