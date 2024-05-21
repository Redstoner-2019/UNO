package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddServerGUI extends GUI {
    public static JLabel title = new JLabel("Add Server");
    public static JTextField ip = new JTextField();
    public static JButton addServer = new JButton("ADD");
    public static JButton back = new JButton("BACK");
    @Override
    public String getGUIName() {
        return "add-server";
    }

    @Override
    public GUI init() {
        register(title,new Setting(0,0,1,.1));
        register(ip,new Setting(.3,.2,.4,.05));
        register(addServer,new Setting(.3,.3,.4,.05));
        register(back,new Setting(.3,.4,.4,.05));

        Design.centerText(title);
        Design.setFontSize(title,50);

        Design.register(this);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("server-menu");
            }
        });

        addServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerMenu.serversModel.add(0,ip.getText());
                Application.switchGui("server-menu");
            }
        });

        return this;
    }
}
