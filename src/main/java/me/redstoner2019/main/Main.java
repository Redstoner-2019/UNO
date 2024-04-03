package me.redstoner2019.main;

import me.redstoner2019.main.data.guis.GUI;

public class Main {
    public static final boolean TEST_MODE = false;
    private static final String VERSION = "v1.4.0-alpha.1";
    public static String getVersion(){
        return VERSION;
    }
    public static void main(String[] args) throws Exception {
        LoggerDump.initialize();
        GUI gui = new GUI();
    }
}