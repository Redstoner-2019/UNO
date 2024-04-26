package me.redstoner2019.guiapi.editorgui;

import me.redstoner2019.guiapi.GUIFrame;

public class EditorMain extends GUIFrame {
    public static void main(String[] args) {
        startGUI();

        register("menu",new Menu().init("menu"));
        switchGui("menu");
    }
}
