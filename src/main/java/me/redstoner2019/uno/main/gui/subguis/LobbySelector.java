package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbySelector extends GUI {
    public static JLabel title = new JLabel("UNO-Lobbies");
    public static JLabel globalChatTitle = new JLabel("Global Chat");
    public static JLabel lobbyTitle = new JLabel("Lobby");
    public static JTextField lobbyCode = new JTextField();
    public static JButton joinLobby = new JButton("JOIN");

    public static JLabel lobbyInfo = new JLabel();

    public static JButton disconnectButton = new JButton("Disconnect");

    public static JTextArea messageArea = new JTextArea();
    public static JScrollPane messagePane = new JScrollPane(messageArea);
    public static JTextField inputField = new JTextField();
    public static JButton sendButton = new JButton("SEND");

    @Override
    public String getGUIName() {
        return "lobby-selector";
    }

    @Override
    public GUI init() {
        register(title,new Setting(0,0,1,.1));
        register(globalChatTitle,new Setting(.5,.1,.5,.05));
        register(lobbyTitle,new Setting(0,.1,.5,.05));
        register(lobbyCode,new Setting(.1,.2,.3,.05));
        register(joinLobby,new Setting(.1,.3,.3,.05));
        register(lobbyInfo,new Setting(0,.4,.5,.5));
        register(disconnectButton,new Setting(.3,.9,.4,.05));

        register(messagePane, new Setting(.55,.2,.4,.55));
        register(inputField, new Setting(.55,.8,.25,.05));
        register(sendButton, new Setting(.85,.8,.1,.05));

        Design.centerText(title);
        Design.centerText(globalChatTitle);
        Design.centerText(lobbyTitle);

        Design.setFontSize(title,50);
        Design.setFontSize(globalChatTitle,30);
        Design.setFontSize(lobbyTitle,30);

        Design.register(this);
        Design.register(messageArea);

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("server-menu");
            }
        });

        joinLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("lobby-gui");
            }
        });

        return this;
    }
}
