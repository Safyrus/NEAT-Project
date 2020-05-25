package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import nn.NeuralNetwork;

public class Creature extends Entity {

    private NeuralNetwork nn;
    private ArrayList<Integer> inputAction;
    private ArrayList<Integer> outputAction;

    private static final int ACT_NOP = 0;
    private static final int ACT_IN_CST = 1;
    private static final int ACT_IN_CST_NEG = 2;
    private static final int ACT_OUT_FORWARD = 2;
    private static final int ACT_OUT_ROTATE = 3;

    public Creature() {
        super();
        energy = 100;
        angle = Math.random() * 360;
        size = 20;

        nn = new NeuralNetwork();
        inputAction = new ArrayList<>();
        outputAction = new ArrayList<>();
        for (int i = 0; i < nn.getInputSize(); i++) {
            inputAction.add(randomActIn());
        }
        for (int i = 0; i < nn.getOutputSize(); i++) {
            outputAction.add(randomActOut());
        }
    }

    @Override
    public void display(Graphics g) {
        g.setColor(new Color(0, 0, 200));
        g.fillOval((int)( x - size/2), (int)( y - size/2), (int)size, (int)size);
        g.setColor(new Color(0, 0, 0));
        g.drawLine((int) x, (int) y, (int) (x + (Math.cos(angle) * size/2)), (int) (y + (Math.sin(angle) * size/2)));
    }

    public void displayNN(Graphics g) {
        g.setColor(new Color(255, 255, 255, 128));
        g.fillRect(0, 0, nn.getLayerSize()*40+50, 80);
        nn.display(g);
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < inputAction.size(); i++) {
            g.drawString(""+inputAction.get(i), 5, 20+i*10);
        }
        for (int i = 0; i < outputAction.size(); i++) {
            g.drawString(""+outputAction.get(i), 15, 20+i*10);
        }
        g.drawString(""+energy, 30, 10);
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
        energy -= 0.1;
    }

    public ArrayList<Entity> eat(ArrayList<Entity> list) {
        ArrayList<Entity> tmp = new ArrayList<>();
        tmp.addAll(list);
        for (int i = 0; i < tmp.size(); i++) {
            Entity e = tmp.get(i);
            if(e.getClass() == Food.class) {
                double dist = Math.sqrt(Math.pow(e.y - y, 2) + Math.pow(e.x - x, 2));
                if(dist < (size/2) + (e.size/2)) {
                    energy += e.getEnergy();
                    tmp.remove(i);
                    i--;
                }
            }
        }
        return tmp;
    }

    private double actionIn(int a) {
        switch (a) {
            case ACT_IN_CST:
                return act_in_cst();
            case ACT_IN_CST_NEG:
                return act_in_cstNeg();
            default:
                return 0;
        }
    }

    private void actionOut(int a, double res) {
        switch (a) {
            case ACT_OUT_FORWARD:
                act_out_forward(res);
                break;
        }
    }

    private int randomActIn() {
        int ran = (int) (Math.random() * 2);
        switch (ran) {
            case 1:
                return ACT_IN_CST;
            default:
                return ACT_IN_CST_NEG;
        }
    }

    private int randomActOut() {
        int ran = (int) (Math.random() * 1);
        switch (ran) {
            default:
                return ACT_OUT_FORWARD;
        }
    }

    private double act_in_cst() {
        return 1;
    }

    private double act_in_cstNeg() {
        return -1;
    }

    private void act_out_forward(double res) {
        x += Math.cos(angle) * res;
        y += Math.sin(angle) * res;
    }
}