package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends GUI {
    public static JLabel title = new JLabel("Login Account");
    public static JTextField username = new JTextField();
    public static JPasswordField password = new JPasswordField();
    public static JLabel usernameLabel = new JLabel("Username:");
    public static JLabel passwordLabel = new JLabel("Password:");
    public static JButton loginButton = new JButton("CREATE");
    public static JButton backButton = new JButton("BACK");
    public static JLabel createStatus = new JLabel();
    @Override
    public String getGUIName() {
        return "login-account-gui";
    }

    @Override
    public GUI init() {
        register(title,new Setting(0,0,1,.1));

        register(username,new Setting(.5,.2,.2,.05));
        register(password,new Setting(.5,.3,.2,.05));

        register(usernameLabel,new Setting(.3,.2,.2,.05));
        register(passwordLabel,new Setting(.3,.3,.2,.05));

        register(loginButton,new Setting(.3,.4,.4,.05));
        register(backButton,new Setting(.3,.475,.4,.05));

        register(createStatus,new Setting(0,.55,1,.45));

        Design.centerText(createStatus);

        Design.centerText(title);
        Design.setFontSize(title,50);

        Design.setFontSize(usernameLabel,20);
        Design.setFontSize(passwordLabel,20);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

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
