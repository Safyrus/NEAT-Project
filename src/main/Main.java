package main;

import javax.swing.JFrame;

/**
 * This is the Main class to launch
 */
public class Main {
    public static void main(String[] args) throws Exception {
        JFrame fenetre = new JFrame();

        //creation of the canvas
        Canvas canvas = new Canvas();

        //window creation
        fenetre.setTitle("Test");
        fenetre.setSize(640, 480);
        fenetre.setLocationRelativeTo(null);
        fenetre.addKeyListener(canvas);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setContentPane(canvas);
        fenetre.setVisible(true);

        boolean stop = false;
        int fps = 60;

        //main loop
        while (!stop) {
            canvas.step();
            canvas.repaint();
            Thread.sleep(1000 / fps);
        }
    }
}