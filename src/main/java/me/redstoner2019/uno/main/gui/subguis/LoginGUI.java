package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;

public class LoginGUI extends GUI {
    @Override
    public String getGUIName() {
        return "login-account-gui";
    }

    @Override
    public GUI init() {
        Design.register(this);
        return this;
    }
}
