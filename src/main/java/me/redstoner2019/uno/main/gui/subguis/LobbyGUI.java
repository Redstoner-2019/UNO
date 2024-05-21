package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyGUI extends GUI {
    public static JList<String> playerList = new JList<>();
    public static JScrollPane scrollPane = new JScrollPane(playerList);
    public static JButton leaveLobby = new JButton("Leave Lobby");
    public static JLabel lobbyCode = new JLabel("BOOBS");

    public static JTextArea messageArea = new JTextArea();
    public static JScrollPane messagePane = new JScrollPane(messageArea);
    public static JTextField inputField = new JTextField();
    public static JButton sendButton = new JButton("SEND");
    public static JButton startGame = new JButton("START");

    public static JCheckBox stacking = new JCheckBox("Stacking");
    public static JCheckBox sevenSwap = new JCheckBox("Seven Swap");
    public static JSlider cardsPerPlayer = new JSlider(2,12);
    public static JSlider decks = new JSlider(1,10);

    @Override
    public String getGUIName() {
        return "lobby-gui";
    }

    @Override
    public GUI init() {
        register(scrollPane,new Setting(.05,.05,.2,.9));
        register(leaveLobby,new Setting(.3,.9,.3,.05));

        register(stacking, new Setting(.3,.2,.3,.05));
        register(sevenSwap, new Setting(.3,.25,.3,.05));
        register(cardsPerPlayer, new Setting(.3,.3,.3,.05));
        register(decks, new Setting(.3,.35,.3,.05));

        register(startGame,new Setting(.3,.4,.3,.05));

        register(lobbyCode, new Setting(.3,.05,.3,.1));
        Design.setFontSize(lobbyCode,40);
        Design.centerText(lobbyCode);

        register(messagePane, new Setting(.65,.05,.3,.825));
        register(inputField, new Setting(.65,.9,.2,.05));
        register(sendButton, new Setting(.875,.9,.075,.05));

        leaveLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("lobby-selector");
            }
        });

        Design.register(this);
        Design.register(playerList);
        Design.register(messageArea);
        return this;
    }
}
