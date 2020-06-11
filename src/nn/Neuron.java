package nn;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.awt.BasicStroke;

/**
 * Class that represents a neuron
 */
public class Neuron implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The result or output signal
     */
    private double res;
    /**
     * Other connected neurons
     */
    private ArrayList<Neuron> inputs_n;
    /**
     * the weight of the connections
     */
    private ArrayList<Double> inputs_w;

    /**
     * x-position for display
     */
    private int x;
    /**
     * y-position for display
     */
    private int y;

    /**
     * Default constructor
     */
    public Neuron() {
        inputs_n = new ArrayList<>();
        inputs_w = new ArrayList<>();
        res = 0;
        x = 0;
        y = 0;
    }

    /**
     * Gets the result
     * @return res
     */
    public double getRes() {
        return res;
    }

    /**
     * Sets the result
     * @param res
     */
    public void setRes(double res) {
        this.res = res;
    }

    /**
     * Gets the x-position
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-position
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the y-position
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-position
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the number of connections
     * @return number of connections
     */
    public int getLinkNumber() {
        return inputs_w.size();
    }

    /**
     * Gets the weight of a specific connection
     * @param i connection index
     * @return connection weight
     */
    public double getWeight(int i) {
        if (inputs_w.size() > i)
            return inputs_w.get(i);
        return 0;
    }

    /**
     * Sets the weight of a specific connection
     * @param i connection index
     * @param w connection weight
     */
    public void setWeight(int i, double w) {
        if (inputs_w.size() > i)
            inputs_w.set(i, w);
    }

    /**
     * Gets a connected neuron
     * @param i connection index
     * @return connected neuron
     */
    public Neuron getNeuron(int i) {
        if (inputs_n.size() > i)
            return inputs_n.get(i);
        return null;
    }

    /**
     * Sets a connected neuron
     * @param i connection index
     * @param i connection weight
     */
    public void addConnection(Neuron n, double w) {
        if (inputs_n.contains(n)) {
            inputs_n.set(inputs_n.indexOf(n), n);
            inputs_w.set(inputs_n.indexOf(n), w);
        } else {
            inputs_n.add(n);
            inputs_w.add(w);
        }
    }

    /**
     * Removes a connected neuron
     * @param n the neuron to remove
     */
    public void removeConnection(Neuron n) {
        if (inputs_n.contains(n)) {
            inputs_w.remove(inputs_n.indexOf(n));
            inputs_n.remove(n);
        }
    }

    /**
     * Calculates neuron output
     * @return the output
     */
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

    /**
     * Calculates an activation function with a given value
     * @param v value
     * @param type type of activation function
     */
    private double activationFunction(double v, int type) {
        double r = 0;
        switch (type) {
            default:
                r = v;
                break;
        }
        return r;
    }

    /**
     * Display the neuron
     * @param g
     */
    public void display(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        //draw all connection with other neurons
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
        }

        //draws the neuron
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