package me.redstoner2019.guiapi;

import javax.swing.*;
import java.util.HashMap;

public class GUIFrame {
    public static JFrame frame = new JFrame();
    public static HashMap<String,GUI> guis = new HashMap<>();
    public static GUI currentGUI = null;
    public static void startGUI(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        t.start();
    }
    public static boolean register(String name, GUI gui){
        if(guis.containsKey(name)) return false;
        guis.put(name,gui);
        return true;
    }
    public static void switchGui(final String gui){
        currentGUI = guis.get(gui);
        frame.setContentPane(currentGUI);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }
    public static void init(){
        frame = new JFrame();
        frame.setSize(1280,720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Test");

        frame.setVisible(true);
        BoundsCreator boundsCreator = new BoundsCreator(100,100);

        while (frame.isVisible()){
            boundsCreator.update(frame.getWidth()-16, frame.getHeight()-39);
            if(currentGUI != null) currentGUI.update(boundsCreator);
        }
    }
}

