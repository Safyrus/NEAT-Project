package nn;

import java.util.ArrayList;
import java.awt.Graphics;

public class NeuralNetwork {

    private ArrayList<ArrayList<Neuron>> network;
    private double mutChance;

    public NeuralNetwork() {
        mutChance = Math.random();

        network = new ArrayList<>();
        ArrayList<Neuron> layer = new ArrayList<>();
        layer.add(new Neuron());
        layer.add(new Neuron());
        layer.add(new Neuron());
        network.add(layer);
        layer = new ArrayList<>();
        layer.add(new Neuron());
        layer.add(new Neuron());
        layer.add(new Neuron());
        network.add(layer);

        Neuron n = network.get(0).get(0);
        network.get(1).get(0).addConnection(n, 1);
        n = network.get(0).get(1);
        network.get(1).get(1).addConnection(n, Math.random() * 2 - 1);
        n = network.get(0).get(2);
        network.get(1).get(2).addConnection(n, 0.75);
    }

    public void mutate() {
        System.out.print(".");
        if (mutChance >= Math.random()) {
            double mutType = Math.random();
            System.out.println("Mutate !(" + mutType + ")");
            if (mutType < 0.35) {
                // add connection
                mutAddLink();
            } else if (mutType < 0.36) {
                // add an input neurone
                mutAddInputNeuron();
            } else if (mutType < 0.37) {
                // add an output neurone
                mutAddOutputNeuron();
            } else if (mutType < 0.54) {
                // add neuron at a layer
                mutAddLayerNeuron();
            } else if (mutType < 0.6) {
                // add neuron at a existing connection
                mutAddNeuron();
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

    public int getLayerSize() {
        return network.size();
    }

    public int getInputSize() {
        return network.get(0).size();
    }

    public Neuron getOutputNeuron(int i) {
        if (network.get(network.size() - 1).size() > i) {
            return network.get(network.size() - 1).get(i);
        }
        return null;
    }

    public int getOutputSize() {
        return network.get(network.size() - 1).size();
    }

    public NeuralNetwork copy() {
        NeuralNetwork newNn = new NeuralNetwork();
        newNn.mutChance = this.mutChance + (Math.random() * 0.1 - 0.05);
        newNn.network = new ArrayList<>();

        for (int i = 0; i < network.size(); i++) {
            newNn.network.add(new ArrayList<>());
            for (int j = 0; j < network.get(i).size(); j++) {
                newNn.network.get(i).add(new Neuron());
                if (i > 0) {
                    Neuron nOld = network.get(i).get(j);
                    Neuron nNew = newNn.network.get(i).get(j);
                    for (int k = 0; k < nOld.getLinkNumber(); k++) {
                        int index = network.get(i - 1).indexOf(nOld.getNeuron(k));
                        if (index != -1) {
                            nNew.addConnection(newNn.network.get(i - 1).get(index), nOld.getWeight(k));
                        }
                    }
                }
            }
        }
        newNn.mutate();
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

    public void display(Graphics g, int x, int y) {
        for (int i = 0; i < network.size(); i++) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.setX(x + i * 40 + 40);
                n.setY(y + j * 40 + 40);
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

    public int neuronCount() {
        int res = 0;
        for (int i = 0; i < network.size(); i++) {
            res += network.get(i).size();
        }
        return res;
    }

    private void mutAddLayerNeuron() {
        System.out.print("add free neuron...");
        if (network.size() <= 2) {
            System.out.print("nope, change to add neuron");
            mutAddNeuron();
            return;
        }
        int ranLayer = (int) (Math.random() * (network.size() - 2)) + 1;
        ArrayList<Neuron> l = network.get(ranLayer);
        Neuron n = new Neuron();
        l.add(n);
        int ranLayer2 = Math.min((int) (Math.random() * network.size()), ranLayer);
        int ranLayer3 = Math.max((int) (Math.random() * network.size()), ranLayer);
        while (ranLayer3 == ranLayer) {
            ranLayer3 = Math.max((int) (Math.random() * network.size()), ranLayer);
        }
        while (ranLayer2 == ranLayer) {
            ranLayer2 = Math.max((int) (Math.random() * network.size()), ranLayer);
        }
        int ranNeuron2 = (int) (Math.random() * network.get(ranLayer2).size());
        int ranNeuron3 = (int) (Math.random() * network.get(ranLayer3).size());
        Neuron n2 = network.get(ranLayer2).get(ranNeuron2);
        Neuron n3 = network.get(ranLayer3).get(ranNeuron3);
        n.addConnection(n2, Math.random());
        n3.addConnection(n, Math.random());
        System.out.println("yes !");
    }

    private void mutAddInputNeuron() {
        System.out.print("add input neuron...");
        ArrayList<Neuron> l = network.get(0);
        Neuron nIn = new Neuron();
        l.add(nIn);

        boolean done = false;
        int ite = 0;
        while (!done && ite < 50) {
            int ranLayer = (int) (Math.random() * (network.size() - 1)) + 1;
            int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
            Neuron n = network.get(ranLayer).get(ranNeuron);

            boolean alreadyLink = false;
            for (int i = 0; i < n.getLinkNumber(); i++) {
                if (n.getNeuron(i) == nIn) {
                    alreadyLink = true;
                    break;
                }
            }
            if (!alreadyLink) {
                n.addConnection(nIn, Math.random());
                done = true;
                System.out.println("yes !");
            }
            ite++;
        }
    }

    private void mutAddOutputNeuron() {
        System.out.print("add output neuron...");
        ArrayList<Neuron> l = network.get(network.size() - 1);
        Neuron nOut = new Neuron();
        l.add(nOut);

        int ranLayer = (int) (Math.random() * (network.size() - 1));
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        Neuron n = network.get(ranLayer).get(ranNeuron);
        nOut.addConnection(n, Math.random());
        System.out.println("yes !");
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
        System.out.print("add link...");
        boolean done = false;
        int ite = 0;
        while (!done && ite < 50) {
            int ranLayer1 = (int) (Math.random() * (network.size()));
            int ranLayer2 = (int) (Math.random() * (network.size()));
            if (ranLayer1 != ranLayer2) {
                if (ranLayer1 > ranLayer2) {
                    int tmp = ranLayer2;
                    ranLayer2 = ranLayer1;
                    ranLayer1 = tmp;
                }
                int ranNeuron = (int) (Math.random() * network.get(ranLayer2).size());
                Neuron n = network.get(ranLayer2).get(ranNeuron);
                ArrayList<Neuron> possible = new ArrayList<>();
                possible.addAll(network.get(ranLayer1));
                for (int i = 0; i < n.getLinkNumber(); i++) {
                    if (possible.indexOf(n.getNeuron(i)) != -1) {
                        possible.remove(n.getNeuron(i));
                    }
                }
                if (!possible.isEmpty()) {
                    ranNeuron = (int) (Math.random() * possible.size());
                    n.addConnection(possible.get(ranNeuron), Math.random());
                    done = true;
                    System.out.println("yes !");
                }
            }
            ite++;
        }
        if (!done)
            System.out.println("nope !");
    }

    private void mutAddNeuron() {
        System.out.print("add neuron...");
        boolean done = false;
        int ite = 0;
        while (!done && ite < 50) {
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
                done = true;
                System.out.println("yes !");
            }
            ite++;
        }
        if (!done)
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