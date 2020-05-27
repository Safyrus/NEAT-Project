package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) throws Exception {
        JFrame fenetre = new JFrame();

        fenetre.setTitle("Test");
        fenetre.setSize(640, 480);
        Canvas canvas = new Canvas();
        fenetre.setLocationRelativeTo(null);
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setContentPane(canvas);
        fenetre.setVisible(true);

        boolean stop = false;
        int fps = 20;
        int fpsCount = 0;

        while (!stop) {
            canvas.step();
            canvas.repaint();
            fpsCount++;
            /*if (fpsCount % fps == 0) {
                System.out.println(fpsCount / fps);
            }*/
            Thread.sleep(1000 / fps);
        }
    }

}