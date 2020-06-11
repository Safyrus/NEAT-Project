package nn;

import java.util.ArrayList;
import java.awt.Graphics;
import java.io.Serializable;

/**
 * Class that represente a neural network
 */
public class NeuralNetwork implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * array of neurons that constitute the network
     */
    private ArrayList<ArrayList<Neuron>> network;
    /**
     * chance for this network to mutate
     */
    private double mutChance;
    /**
     * prints informations to the console
     */
    private static boolean print = false;

    /**
     * Default constructor
     */
    public NeuralNetwork() {
        mutChance = Math.random();

        //adds 3 neurone to 2 layers of the network
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

        //adds 3 connections
        Neuron n = network.get(0).get(0);
        network.get(1).get(0).addConnection(n, 1);
        n = network.get(0).get(1);
        network.get(1).get(1).addConnection(n, Math.random() * 2 - 1);
        n = network.get(0).get(2);
        network.get(1).get(2).addConnection(n, 0.75);
    }

    /**
     * Mutates the network
     */
    public void mutate() {
        if (print)
            System.out.print(".");
        
        // if it mutates
        if (mutChance >= Math.random()) {
            double mutType = Math.random();
            if (print)
                System.out.println("Mutate !(" + mutType + ")");
            if (mutType < 0.3) {
                // add connection
                mutAddLink();
            } else if (mutType < 0.32) {
                // add an input neurone
                mutAddInputNeuron();
            } else if (mutType < 0.34) {
                // add an output neurone
                mutAddOutputNeuron();
            } else if (mutType < 0.35) {
                // add neuron at a layer
                mutAddLayerNeuron();
            } else if (mutType < 0.37) {
                // add neuron at a existing connection
                mutAddNeuron();
            } else if (mutType < 0.375) {
                // add neuron at a existing connection
                mutRemoveNeuron();
            } else if (mutType < 1) {
                // change weight of a connection
                mutChangeWeight();
            }
        }
    }

    /**
     * Sets print
     * @param p boolean
     */
    public void setPrint(boolean p) {
        print = p;
    }

    /**
     * Gets an input neuron
     * @param i index
     * @return neuron
     */
    public Neuron getInputNeuron(int i) {
        if (network.get(0).size() > i) {
            return network.get(0).get(i);
        }
        return null;
    }

    /**
     * Gets the size of the network
     * @return size
     */
    public int getLayerSize() {
        return network.size();
    }

    /**
     * Gets the size of the input layer
     * @return size
     */
    public int getInputSize() {
        return network.get(0).size();
    }

    /**
     * Gets an output neuron
     * @param i index
     * @return neuron
     */
    public Neuron getOutputNeuron(int i) {
        if (network.get(network.size() - 1).size() > i) {
            return network.get(network.size() - 1).get(i);
        }
        return null;
    }

    /**
     * Gets the size of the output layer
     * @return size
     */
    public int getOutputSize() {
        return network.get(network.size() - 1).size();
    }

    /**
     * Makes a copy of the network with a chance to mutate during the process
     * @return copy of the network
     */
    public NeuralNetwork copy() {
        //creates the new network
        NeuralNetwork newNn = new NeuralNetwork();
        newNn.mutChance = this.mutChance + (Math.random() * 0.1 - 0.05);
        newNn.network = new ArrayList<>();

        //copys the neurons and their connection
        for (int i = 0; i < network.size(); i++) {
            newNn.network.add(new ArrayList<>());
            for (int j = 0; j < network.get(i).size(); j++) {
                newNn.network.get(i).add(new Neuron());

                //adds the connections to a neuron
                Neuron nOld = network.get(i).get(j);
                Neuron nNew = newNn.network.get(i).get(j);
                for (int k = 0; k < nOld.getLinkNumber(); k++) {
                    int index = -1;
                    for (int l = 0; l < i; l++) {
                        //checks if the neuron exists
                        index = network.get(l).indexOf(nOld.getNeuron(k));
                        if (index != -1) {
                            nNew.addConnection(newNn.network.get(l).get(index), nOld.getWeight(k));
                            break;
                        }
                    }
                }
            }
        }

        //mutate or not the new network
        newNn.mutate();

        return newNn;
    }

    /**
     * Calculates the state of all neuron
     */
    public void step() {
        for (int i = network.size() - 1; i >= 1; i--) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.operation();
            }
        }
    }

    /**
     * Displays the neural network
     * @param g
     * @param x x-position
     * @param y y-position
     */
    public void display(Graphics g, int x, int y) {

        //sets the position of all neurons
        for (int i = 0; i < network.size(); i++) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.setX(x + i * 40 + 40);
                n.setY(y + j * 40 + 40);
            }
        }

        //displays all neurons
        for (int i = network.size() - 1; i >= 0; i--) {
            ArrayList<Neuron> layer = network.get(i);
            for (int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                n.display(g);
            }
        }
    }

    /**
     * Gets the number of neurons in the network
     * @return number of neurons
     */
    public int neuronCount() {
        int res = 0;
        for (int i = 0; i < network.size(); i++) {
            res += network.get(i).size();
        }
        return res;
    }

    /**
     * Mutation that adds a neuron to a new layer
     */
    private void mutAddLayerNeuron() {
        if (print)
            System.out.print("add free neuron...");
        if (network.size() <= 2) {
            if (print)
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
        if (print)
            System.out.println("yes !");
    }

    /**
     * Mutation that adds an input neuron
     */
    private void mutAddInputNeuron() {
        if (print)
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
                if (print)
                    System.out.println("yes !");
            }
            ite++;
        }
    }

    /**
     * Mutation that adds an output neuron
     */
    private void mutAddOutputNeuron() {
        if (print)
            System.out.print("add output neuron...");
        ArrayList<Neuron> l = network.get(network.size() - 1);
        Neuron nOut = new Neuron();
        l.add(nOut);

        int ranLayer = (int) (Math.random() * (network.size() - 1));
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        Neuron n = network.get(ranLayer).get(ranNeuron);
        nOut.addConnection(n, Math.random());
        if (print)
            System.out.println("yes !");
    }

    /**
     * Mutation that changes the weight of a connection
     */
    private void mutChangeWeight() {
        if (print)
            System.out.print("mutate weight...");
        int ranLayer = (int) (Math.random() * network.size());
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        int ranLink = (int) (Math.random() * network.get(ranLayer).get(ranNeuron).getLinkNumber());
        double ranWeight = (Math.random() - 0.5) + network.get(ranLayer).get(ranNeuron).getWeight(ranLink);
        if (print)
            System.out.println(ranWeight);
        network.get(ranLayer).get(ranNeuron).setWeight(ranLink, ranWeight);
    }

    /**
     * Mutation that adds a new connection
     */
    private void mutAddLink() {
        if (print)
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
                    if (print)
                        System.out.println("yes !");
                }
            }
            ite++;
        }
        if (!done && print)
            System.out.println("nope !");
    }

    /**
     * Mutation that adds a neuron to a connection
     */
    private void mutAddNeuron() {
        if (print)
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
                if (print)
                    System.out.println("yes !");
            }
            ite++;
        }
        if (!done && print)
            System.out.println("nope !");
    }

    /**
     * Mutation that remove a neuron
     */
    private void mutRemoveNeuron() {
        if (print)
            System.out.println("remove neuron");
        int ranLayer = (int) (Math.random() * (network.size()));
        ArrayList<Neuron> l = network.get(ranLayer);
        int ranNeuron = (int) (Math.random() * network.get(ranLayer).size());
        Neuron n = l.get(ranNeuron);

        for (int i = ranLayer; i < network.size(); i++) {
            for (int j = 0; j < network.get(i).size(); j++) {
                Neuron nc = network.get(i).get(j);
                nc.removeConnection(n);
            }
        }
        network.get(ranLayer).remove(n);
        if(network.get(ranLayer).size() == 0) {
            network.remove(network.indexOf(network.get(ranLayer)));
        }
    }

    /**
     * Gets the layer index of a neuron
     * @param n neuron
     * @return index
     */
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