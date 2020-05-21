package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import nn.NeuralNetwork;

public class Creature extends Entity {

    private double energy;
    private double angle;
    private NeuralNetwork nn;
    private ArrayList<Integer> inputAction;
    private ArrayList<Integer> outputAction;

    private static final int ACT_NOP = 0;
    private static final int ACT_IN_CST = 1;
    private static final int ACT_IN_CST_NEG = 2;
    private static final int ACT_OUT_FORWARD = 2;

    public Creature() {
        energy = 100;
        angle = Math.random() * 360;
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
        g.fillOval((int) x - 10, (int) y - 10, 20, 20);
        g.setColor(new Color(0, 0, 0));
        g.drawLine((int) x, (int) y, (int) (x + (Math.cos(angle) * 10)), (int) (y + (Math.sin(angle) * 10)));
        nn.display(g);
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