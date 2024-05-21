package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ManageAccountGUI extends GUI {
    public static JButton backButton = new JButton("BACK");
    @Override
    public String getGUIName() {
        return "manage-account-gui";
    }

    @Override
    public GUI init() {
        List<String> gamesNames = new ArrayList<>(List.of(new String[]{"UNO", "ToeTakTik"}));

        JPanel pa = new JPanel();
        GridLayout layout = new GridLayout(1,0);
        pa.setLayout(layout);

        for(String s : gamesNames){
            JButton button = new JButton(s);

            button.setPreferredSize(new Dimension(200, button.getPreferredSize().height));

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
