package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.Main;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsMenu extends GUI {
    public static JLabel title = new JLabel("UNO");
    public static JLabel subtitle = new JLabel("Version " + Main.getVersion());
    public static JButton createAccount = new JButton("CREATE ACCOUNT");
    public static JButton loginAccount = new JButton("LOGIN");
    public static JButton manageAccount = new JButton("MANAGE ACCOUNT");
    public static JLabel currentAuthServer = new JLabel("localhost");
    public static JTextField newAuthServerIp = new JTextField("localhost");
    public static JButton setNewAuthServer = new JButton("Set Auth Server");
    public static JButton connectToAuthServer = new JButton("Connect To Auth Server");
    public static JButton mainMenu = new JButton("Main Menu");
    @Override
    public String getGUIName() {
        return "settings-menu";
    }

    @Override
    public GUI init() {
        register(title,new Setting(0, .1, 1, .1));
        register(subtitle,new Setting(0, .225, 1, .05));

        register(createAccount,new Setting(.1, .5, .2, .05));
        register(loginAccount,new Setting(.1, .6, .2, .05));
        register(manageAccount,new Setting(.1, .7, .2, .05));

        register(currentAuthServer,new Setting(.1, .4, .5, .05));
        register(newAuthServerIp,new Setting(.4, .5, .2, .05));
        register(setNewAuthServer,new Setting(.4, .6, .2, .05));
        register(connectToAuthServer,new Setting(.4, .7, .2, .05));

        register(mainMenu,new Setting(.1, .85, .8, .1));

        Design.register(this);

        Design.centerText(title);
        Design.setFontStyle(title, Font.BOLD);

        Design.centerText(subtitle);
        Design.setFontStyle(subtitle, Font.PLAIN);

        Design.centerText(currentAuthServer);
        Design.setFontStyle(currentAuthServer, Font.PLAIN);

        mainMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("main-menu");
            }
        });
        return this;
    }
}
