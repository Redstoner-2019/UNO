package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.BoundsCreator;
import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.Main;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends GUI {
    public static JButton settings = new JButton("Settings");
    public static JButton play = new JButton("Play");
    public static JLabel title = new JLabel("UNO");
    public static JLabel subtitle = new JLabel("Version " + Main.getVersion());
    public static JLabel loginStatus = new JLabel("Not connected");

    @Override
    public String getGUIName() {
        return "main-menu";
    }

    @Override
    public void update(BoundsCreator boundsCreator) {
        super.update(boundsCreator);
        loginStatus.setForeground(Application.loginStatusColor);
        loginStatus.setText(Application.loginStatus);
    }

    @Override
    public GUI init() {
        register(settings,new Setting(.1,.45,.2,.1));
        register(play,new Setting(.7,.45,.2,.1));
        register(title,new Setting(0, .1, 1, .1));
        register(subtitle,new Setting(0, .225, 1, .05));
        registerNoDesign(loginStatus,new Setting(0, .95, .95, .025));
        Design.register(this);

        loginStatus.setForeground(Color.RED);

        Design.centerText(title);
        Design.setFontStyle(title, Font.BOLD);

        Design.centerText(subtitle);
        Design.setFontStyle(subtitle, Font.PLAIN);

        Design.rightAlignment(loginStatus);
        Design.setFontStyle(loginStatus, Font.PLAIN);

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("settings-menu");
            }
        });
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("server-menu");
            }
        });
        return this;
    }
}
