package nn;

import java.util.ArrayList;
import java.awt.Graphics;

public class NeuralNetwork {

    private ArrayList<ArrayList<Neuron>> network;
    private double mutChance;

    public NeuralNetwork() {
        mutChance = Math.random() * 0.25;

        network = new ArrayList<>();
        ArrayList<Neuron> layer = new ArrayList<>();
        layer.add(new Neuron());
        layer.get(0).setRes(0.8);
        network.add(layer);
        layer = new ArrayList<>();
        layer.add(new Neuron());
        network.add(layer);

        Neuron n = network.get(0).get(0);
        network.get(1).get(0).addConnection(n, Math.random() * 2 - 1);
    }

    public void mutate() {
        System.out.print(".");
        if (mutChance >= Math.random()) {
            System.out.println("Mutate !");
            int mutType = (int) (Math.random() * 3);
            switch (mutType) {
                case 0:
                    // add connection
                    System.out.println("TODO LINK");
                    break;
                case 1:
                    // add neuron at a existing connection
                    System.out.println("TODO NEURON");
                    break;
                case 2:
                    // change weight of a connection
                    System.out.print("mutate weight...");
                    int ranLayer = (int) (Math.random() * network.size());
                    int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
                    int ranLink = (int) (Math.random() * network.get(ranLayer).get(ranNeuron).getLinkNumber());
                    double ranWeight = (Math.random() - 0.5) + network.get(ranLayer).get(ranNeuron).getWeight(ranLink);
                    System.out.println(ranWeight);
                    network.get(ranLayer).get(ranNeuron).setWeight(ranLink, ranWeight);
                    break;
            }
        }
    }

    public NeuralNetwork copy() {
        return null;
    }

    public void step()
    {
        for (int i = network.size()-1; i >= 1; i--) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.operation();
            }
        }
    }

    public void display(Graphics g)
    {
        for (int i = 0; i < network.size(); i++) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.setX(i*40+40);
                n.setY(j*40+40);
            }
        }
        for (int i = network.size()-1; i >= 0; i--) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.display(g);
            }
        }
    }
}