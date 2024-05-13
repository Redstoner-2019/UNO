package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class CreateAccountGUI extends GUI {
    public static JLabel title = new JLabel("Create Account");
    public static JTextField username = new JTextField();
    public static JTextField displayname = new JTextField();
    public static JPasswordField password = new JPasswordField();
    public static JPasswordField confirmPassword = new JPasswordField();
    public static JLabel usernameLabel = new JLabel("Username:");
    public static JLabel displaynameLabel = new JLabel("Displayname:");
    public static JLabel passwordLabel = new JLabel("Password:");
    public static JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
    public static JButton createAccountButton = new JButton("CREATE");
    public static JButton backButton = new JButton("BACK");
    public static JLabel createStatus = new JLabel();
    @Override
    public String getGUIName() {
        return "create-account-gui";
    }

    @Override
    public GUI init() {
        register(title,new Setting(0,0,1,.1));

        register(username,new Setting(.5,.2,.2,.05));
        register(displayname,new Setting(.5,.3,.2,.05));
        register(password,new Setting(.5,.4,.2,.05));
        register(confirmPassword,new Setting(.5,.5,.2,.05));

        register(usernameLabel,new Setting(.3,.2,.2,.05));
        register(displaynameLabel,new Setting(.3,.3,.2,.05));
        register(passwordLabel,new Setting(.3,.4,.2,.05));
        register(confirmPasswordLabel,new Setting(.3,.5,.2,.05));

        register(createAccountButton,new Setting(.3,.6,.4,.05));
        register(backButton,new Setting(.3,.675,.4,.05));

        register(createStatus,new Setting(0,.75,1,.25));

        Design.centerText(createStatus);

        Design.centerText(title);
        Design.setFontSize(title,50);

        Design.setFontSize(usernameLabel,20);
        Design.setFontSize(displaynameLabel,20);
        Design.setFontSize(passwordLabel,20);
        Design.setFontSize(confirmPasswordLabel,20);

        Design.register(this);

        createAccountButton.addActionListener(new ActionListener() {
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
        return this;
    }
}
