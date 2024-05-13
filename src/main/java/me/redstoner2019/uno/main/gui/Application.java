package me.redstoner2019.uno.main.gui;

import me.redstoner2019.guiapi.BoundsCreator;
import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.server.util.Util;
import me.redstoner2019.uno.main.gui.subguis.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Application {
    public static JFrame frame = new JFrame();
    public static HashMap<String, GUI> guis = new HashMap<>();
    public static GUI currentGUI = null;
    public static String loginStatus = "Not connected";
    public static Color loginStatusColor = Color.RED;
    public static void startGUI(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        t.start();
    }
    public static boolean register(GUI gui){
        String name = gui.getGUIName();
        if(guis.containsKey(name)) return false;
        guis.put(name,gui);
        Util.log("Registered " + name);
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
        frame.setTitle("UNO");

        register(new MainMenu().init());
        register(new SettingsMenu().init());
        register(new ServerMenu().init());
        register(new ManageAccountGUI().init());
        register(new LoginGUI().init());
        register(new CreateAccountGUI().init());

        switchGui("main-menu");
        switchGui("create-account-gui");

        frame.setVisible(true);

        BoundsCreator boundsCreator = new BoundsCreator(frame.getWidth(),frame.getHeight());

        while (frame.isVisible()){
            boundsCreator.update(frame.getWidth()-16, frame.getHeight()-39);
            if(currentGUI != null) currentGUI.update(boundsCreator);
        }
    }
}
