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
        network.add(layer);
        layer = new ArrayList<>();
        layer.add(new Neuron());
        network.add(layer);
        /*layer.add(new Neuron());
        layer.get(0).setRes(0.8);
        layer.add(new Neuron());
        layer.get(1).setRes(-1.2);
        network.add(layer);
        layer = new ArrayList<>();
        layer.add(new Neuron());
        network.add(layer);
        layer = new ArrayList<>();
        layer.add(new Neuron());
        layer.add(new Neuron());
        network.add(layer);*/

        Neuron n = network.get(0).get(0);
        network.get(1).get(0).addConnection(n, Math.random() * 2 - 1);
    }

    public void mutate() {
        System.out.print(".");
        if (mutChance >= Math.random()) {
            double mutType = Math.random();
            System.out.println("Mutate !(" + mutType + ")");
            if (mutType < 0.4) {
                // add connection
                mutAddLink();
            } else if (mutType < 0.43) {
                mutAddInputNeuron();
            } else if (mutType < 0.46) {
                mutAddOutputNeuron();
            } else if (mutType < 0.56) {
                // add neuron at a existing connection
                mutAddNeuron();
            } else if (mutType < 0.6) {
                mutAddLayerNeuron();
            } else if (mutType < 1) {
                // change weight of a connection
                mutChangeWeight();
            }
        }
    }

    public Neuron getInputNeuron(int i) {
        if (network.get(0).size() > i) {
            return network.get(0).get(i);
        }
        return null;
    }

    public int getInputSize() {
        return network.get(0).size();
    }

    public Neuron getOutputNeuron(int i) {
        if (network.get(network.size()-1).size() > i) {
            return network.get(network.size()-1).get(i);
        }
        return null;
    }

    public int getOutputSize() {
        return network.get(network.size()-1).size();
    }

    public NeuralNetwork copy() {
        NeuralNetwork newNn = new NeuralNetwork();
        newNn.mutChance = this.mutChance;
        newNn.network = this.network;
        return newNn;
    }

    public void step() {
        for (int i = network.size() - 1; i >= 1; i--) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.operation();
            }
        }
    }

    public void display(Graphics g) {
        for (int i = 0; i < network.size(); i++) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.setX(i * 40 + 40);
                n.setY(j * 40 + 40);
            }
        }
        for (int i = network.size() - 1; i >= 0; i--) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.display(g);
            }
        }
    }

    private void mutAddLayerNeuron() {
        System.out.print("add free neuron...");
        int ranLayer = (int) (Math.random() * (network.size() - 2)) + 1;
        ArrayList<Neuron> l = network.get(ranLayer);
        l.add(new Neuron());
    }

    private void mutAddInputNeuron() {
        System.out.print("add input neuron...");
        ArrayList<Neuron> l = network.get(0);
        l.add(new Neuron());
    }

    private void mutAddOutputNeuron() {
        System.out.print("add output neuron...");
        ArrayList<Neuron> l = network.get(network.size() - 1);
        l.add(new Neuron());
    }

    private void mutChangeWeight() {
        System.out.print("mutate weight...");
        int ranLayer = (int) (Math.random() * network.size());
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        int ranLink = (int) (Math.random() * network.get(ranLayer).get(ranNeuron).getLinkNumber());
        double ranWeight = (Math.random() - 0.5) + network.get(ranLayer).get(ranNeuron).getWeight(ranLink);
        System.out.println(ranWeight);
        network.get(ranLayer).get(ranNeuron).setWeight(ranLink, ranWeight);
    }

    private void mutAddLink() {
        // TODO be able to link with any neuron in any layer
        System.out.print("add link...");
        int ranLayer = (int) (Math.random() * (network.size() - 1)) + 1;
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        ArrayList<Neuron> l = network.get(ranLayer);
        Neuron n = l.get(ranNeuron);
        ArrayList<Neuron> preLayer = new ArrayList<>();
        ArrayList<Neuron> linked = new ArrayList<>();

        for (int i = 0; i < n.getLinkNumber(); i++) {
            linked.add(n.getNeuron(i));
        }
        for (int i = 0; i < network.get(ranLayer - 1).size(); i++) {
            preLayer.add(network.get(ranLayer - 1).get(i));
        }
        while (!linked.isEmpty()) {
            preLayer.remove(linked.get(0));
            linked.remove(0);
        }
        if (!preLayer.isEmpty()) {
            int ranPreNeuron = (int) (Math.random() * (preLayer.size()));
            n.addConnection(preLayer.get(ranPreNeuron), Math.random() * 2 - 1);
            System.out.println("yes !");
        } else {
            System.out.println("nope !");
        }
    }

    private void mutAddNeuron() {
        System.out.print("add neuron...");
        int ranLayer = (int) (Math.random() * (network.size() - 1)) + 1;
        ArrayList<Neuron> l = network.get(ranLayer);
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        Neuron n = l.get(ranNeuron);
        if (n.getLinkNumber() > 0) {
            int ranLink = (int) (Math.random() * n.getLinkNumber());
            Neuron back = n.getNeuron(ranLink);
            n.removeConnection(back);
            Neuron newOne = new Neuron();
            n.addConnection(newOne, Math.random() * 2 - 1);
            newOne.addConnection(back, Math.random() * 2 - 1);

            int backLayer = getNeuronLayer(back);
            if (ranLink - backLayer <= 1) {
                network.add(ranLayer, new ArrayList<>());
                network.get(ranLayer).add(newOne);
            } else {
                network.get(ranLayer - 1).add(newOne);
            }

            System.out.println("yes !");
        }
        System.out.println("nope !");
    }

    private int getNeuronLayer(Neuron n) {
        for (int i = 0; i < network.size(); i++) {
            for (int j = 0; j < network.get(i).size(); j++) {
                if (network.get(i).get(j) == n)
                    return i;
            }
        }
        return -1;
    }
}