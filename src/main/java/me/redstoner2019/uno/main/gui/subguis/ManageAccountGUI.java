package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageAccountGUI extends GUI {
    public static JButton backButton = new JButton("BACK");
    @Override
    public String getGUIName() {
        return "manage-account-gui";
    }

    @Override
    public GUI init() {
        int buttons = 20;

        JPanel pa = new JPanel();
        pa.setLayout(new GridLayout(0,buttons));

        for (int i = 0; i < buttons; i++) {

            JButton button = new JButton("Button " + (i + 1));

            button.setMinimumSize(new Dimension(400, (int) button.getMinimumSize().height));

            pa.add(button);

        }

        JScrollPane scrollPane = new JScrollPane(pa);


        register(scrollPane,new Setting(0,0,1,.2));

        register(backButton,new Setting(.3,.475,.4,.05));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("settings-menu");
            }
        });

        Design.register(this);
        return this;
    }
}
