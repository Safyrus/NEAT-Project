package main;

import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;

import nn.NeuralNetwork;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;

    private NeuralNetwork nn;
    private int width;
    private int height;

    public Canvas(int width, int height)
    {
        nn = new NeuralNetwork();
    }

    public void step()
    {
        nn.step();
        nn.mutate();
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0, 100, 100));
        g.clearRect(0, 0, width, height);
        nn.display(g);
    }
}