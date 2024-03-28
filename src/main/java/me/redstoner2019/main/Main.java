package me.redstoner2019.main;

import me.redstoner2019.main.data.guis.ConnectGUI;
import me.redstoner2019.main.serverstuff.ClientMain;

public class Main {
    public static final boolean TEST_MODE = false;
    public static final String VERSION = "v1.3.0-alpha.8";
    public static void main(String[] args) throws Exception {
        LoggerDump.initialize();
        System.out.println("Hello World");
        System.err.println("Hello World Error");
        ConnectGUI.main(args);
    }
}