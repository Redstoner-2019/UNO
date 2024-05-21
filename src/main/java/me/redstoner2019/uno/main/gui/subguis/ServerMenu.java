package me.redstoner2019.uno.main.gui.subguis;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;
import me.redstoner2019.server.util.LocalNetworkScanner;
import me.redstoner2019.uno.main.gui.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerMenu extends GUI {
    public static DefaultListModel serversModel = new DefaultListModel();
    public static JList<String> servers = new JList<>(serversModel);
    public static JScrollPane serverScrollPane = new JScrollPane(servers);
    public static JLabel selectServerLabel = new JLabel("Select Server");
    public static JTextArea serverInfo = new JTextArea();
    public static JButton addServer = new JButton("Add Server");
    public static JButton deleteServer = new JButton("Delete Server");
    public static JButton searchLocal = new JButton("Search Local");
    public static JButton mainMenu = new JButton("Main Menu");
    public static JButton joinServer = new JButton("Join Server");

    @Override
    public String getGUIName() {
        return "server-menu";
    }

    @Override
    public GUI init() {
        register(serverScrollPane,new Setting(.05,.1,.575,.65));
        register(serverInfo,new Setting(.675,.1,.25,.7));
        serverInfo.setEditable(false);
        serverInfo.setLineWrap(true);
        register(addServer,new Setting(.05,.80,.25,.05));
        register(deleteServer,new Setting(.05,.90,.25,.05));

        register(searchLocal,new Setting(.375,.80,.25,.05));
        register(mainMenu,new Setting(.375,.90,.25,.05));

        register(joinServer,new Setting(.7,.80,.25,.15));

        register(selectServerLabel,new Setting(.1,0,.8,.1));

        Design.register(this);
        Design.register(servers);
        Design.setFontSize(selectServerLabel,40);

        Design.centerText(selectServerLabel);

        mainMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("main-menu");
            }
        });
        addServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("add-server");
            }
        });
        deleteServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        searchLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> addresses = new ArrayList<>();
                try {
                    LocalNetworkScanner.scan(addresses,8008);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Ips found: " + addresses);
                for(String s : addresses){
                    if(!serversModel.contains(s)){
                        serversModel.add(0,s);
                    }
                }
            }
        });
        joinServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.switchGui("lobby-selector");
            }
        });
        return this;
    }
}
