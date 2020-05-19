package nn;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class Neuron {

    private double res;
    private ArrayList<Neuron> inputs_n;
    private ArrayList<Double> inputs_w;

    private int x;
    private int y;

    public Neuron() {
        inputs_n = new ArrayList<>();
        inputs_w = new ArrayList<>();
        res = 0;
        x = 0;
        y = 0;
    }

    public double getRes() {
        return res;
    }

    public void setRes(double res) {
        this.res = res;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLinkNumber() {
        return inputs_w.size();
    }

    public double getWeight(int i) {
        if (inputs_w.size() > i)
            return inputs_w.get(i);
        return 0;
    }

    public void setWeight(int i, double v) {
        if (inputs_w.size() > i)
            inputs_w.set(i, v);
    }

    public Neuron getNeuron(int i) {
        if (inputs_n.size() > i)
            return inputs_n.get(i);
        return null;
    }

    public void addConnection(Neuron n, double w) {
        if (inputs_n.contains(n)) {
            inputs_n.set(inputs_n.indexOf(n), n);
            inputs_w.set(inputs_n.indexOf(n), w);
        } else {
            inputs_n.add(n);
            inputs_w.add(w);
        }
    }

    public void removeConnection(Neuron n) {
        if (inputs_n.contains(n)) {
            inputs_w.remove(inputs_n.indexOf(n));
            inputs_n.remove(n);
        }
    }

    public double operation() {
        if (!inputs_n.isEmpty()) {
            res = 0;
            for (int i = 0; i < inputs_n.size(); i++) {
                res += inputs_n.get(i).res * inputs_w.get(i);
            }
            res = activationFunction(res, 0);
        }
        return res;
    }

    private double activationFunction(double v, int type) {
        double r = 0;
        switch (type) {
            default:
                r = v;
                break;
        }
        return r;
    }

    public void display(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        for (int i = 0; i < inputs_n.size(); i++) {
            Neuron n = inputs_n.get(i);
            double w = inputs_w.get(i);
            int v = Math.abs((int) (w * 100));
            if (v > 255) {
                v = 255;
            }
            if (w > 0) {
                g.setColor(new Color(0, v, 0));
            } else {
                g.setColor(new Color(v, 0, 0));
            }
            g2.drawLine(x, y, n.x, n.y);
            // g.drawString(String.format ("%.4f", inputs_w.get(i)), x - n.x, y - 10);
        }

        int size = 10;
        int value = Math.abs((int) (res * 100));
        if (value > 255) {
            value = 255;
        }
        if (res < 0) {
            g.setColor(new Color(value, 0, 0));
        } else {
            g.setColor(new Color(0, value, 0));
        }
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }
}