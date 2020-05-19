package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws Exception {
        JFrame fenetre = new JFrame();

        fenetre.setTitle("Test");
        fenetre.setSize(400, 400);
        Canvas canvas = new Canvas(fenetre.getWidth(), fenetre.getHeight());
        fenetre.setLocationRelativeTo(null);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setContentPane(canvas);
        fenetre.setVisible(true);
        
        boolean stop = false;

        while (!stop) {
            canvas.step();
            canvas.repaint();
            Thread.sleep(50);
        }
    }

}