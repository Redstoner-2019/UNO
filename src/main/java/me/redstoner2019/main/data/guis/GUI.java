package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.packets.gamepackets.GameStartPacket;
import me.redstoner2019.main.data.packets.lobbypackets.*;
import me.redstoner2019.main.data.packets.loginpackets.DisconnectPacket;
import me.redstoner2019.main.data.packets.loginpackets.LoginPacket;
import me.redstoner2019.main.data.packets.loginpackets.LoginSuccessPacket;
import me.redstoner2019.main.data.packets.loginpackets.Ping;
import me.redstoner2019.serverhandling.*;
import org.json.JSONArray;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GUI extends Client {
    public static JFrame frame;
    private final int width = 1280;
    private final int height = 720;
    public static String gui = "main-menu";
    public static JSONArray serverList = new JSONArray();
    public static boolean forceUpdate = false;
    public GUI() {
        initialize();
    }
    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("UNO - " + Main.getVersion());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setForeground(Color.WHITE);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        serverList.put("localhost");
        serverList.put("cruw-community.de");

        /**
         * Main Menu
         */
        JLabel mainMenuTitleLabel = new JLabel("UNO");
        JLabel mainMenuSubTitleLabel = new JLabel("Version " + Main.getVersion());
        JButton mainMenuPlayButton = new JButton("PLAY");
        JButton mainMenuSettingsButton = new JButton("SETTINGS");

        mainMenuTitleLabel.setBounds((frame.getWidth()-400)/2,30,400,80);
        mainMenuSubTitleLabel.setBounds((frame.getWidth()-300)/2,100,300,50);
        mainMenuPlayButton.setBounds(frame.getWidth()-500,300,300,80);
        mainMenuSettingsButton.setBounds(200,300,300,80);

        mainMenuTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainMenuTitleLabel.setVerticalAlignment(JLabel.CENTER);
        mainMenuSubTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainMenuSubTitleLabel.setVerticalAlignment(JLabel.CENTER);

        mainMenuTitleLabel.setFont(new Font("Arial", Font.BOLD,50));
        mainMenuSubTitleLabel.setFont(new Font("Arial", Font.PLAIN,25));
        mainMenuPlayButton.setFont(new Font("Arial", Font.PLAIN,40));
        mainMenuSettingsButton.setFont(new Font("Arial", Font.PLAIN,40));

        panel.add(mainMenuTitleLabel);
        panel.add(mainMenuSubTitleLabel);
        panel.add(mainMenuPlayButton);
        panel.add(mainMenuSettingsButton);

        mainMenuSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "settings-gui";
            }
        });

        mainMenuPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "server-selector";
            }
        });

        /**
         * Settings GUI
         */

        JLabel settingsTitleLabel = new JLabel("UNO");
        JLabel settingsSubTitleLabel = new JLabel("Version " + Main.getVersion());
        JButton settingsMainMenuButton = new JButton("MAIN MENU");

        settingsTitleLabel.setBounds((frame.getWidth()-400)/2,30,400,80);
        settingsSubTitleLabel.setBounds((frame.getWidth()-300)/2,100,300,50);
        settingsMainMenuButton.setBounds((frame.getWidth()-300)/2,frame.getHeight()-100,300,40);

        settingsTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        settingsTitleLabel.setVerticalAlignment(JLabel.CENTER);
        settingsSubTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        settingsSubTitleLabel.setVerticalAlignment(JLabel.CENTER);

        settingsTitleLabel.setFont(new Font("Arial", Font.BOLD,50));
        settingsSubTitleLabel.setFont(new Font("Arial", Font.PLAIN,25));
        settingsMainMenuButton.setFont(new Font("Arial", Font.PLAIN,20));

        panel.add(settingsTitleLabel);
        panel.add(settingsSubTitleLabel);
        panel.add(settingsMainMenuButton);

        settingsMainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "main-menu";
            }
        });

        /**
         * Server Selector GUI
         */

        JLabel serverSelectorLabel = new JLabel("Select Server");
        JButton serverGuiMenuButton = new JButton("MAIN MENU");
        JButton serverGuiJoinServerButton = new JButton("JOIN SELECTED SERVER");
        JButton serverGuiAddServer = new JButton("ADD SERVER");
        JButton serverGuiSearchLocalServersButton = new JButton("SEARCH LOCAL");
        JList<String> serversJList = new JList<>();
        JScrollPane serverScrollPane = new JScrollPane(serversJList);
        JTextArea serverConnectionInfo = new JTextArea();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        serverSelectorLabel.setBounds(frame.getWidth()-500,50,500,40);
        serverGuiMenuButton.setBounds(frame.getWidth()-450,frame.getHeight()-130,190,40);
        serverGuiJoinServerButton.setBounds(frame.getWidth()-450,frame.getHeight()-200,190,40);
        serverGuiAddServer.setBounds(frame.getWidth()-240,frame.getHeight()-130,190,40);
        serverGuiSearchLocalServersButton.setBounds(frame.getWidth()-240,frame.getHeight()-200,190,40);
        serverScrollPane.setBounds(50,50,frame.getWidth()-550,height-140);
        serverConnectionInfo.setBounds(frame.getWidth()-450,100,400,250);
        usernameField.setBounds(frame.getWidth()-450,frame.getHeight()-340,400,40);
        passwordField.setBounds(frame.getWidth()-450,frame.getHeight()-270,400,40);

        serverSelectorLabel.setHorizontalAlignment(JLabel.CENTER);
        serverSelectorLabel.setVerticalAlignment(JLabel.CENTER);
        serverConnectionInfo.setEditable(false);

        serverSelectorLabel.setFont(new Font("Arial", Font.BOLD,50));
        serverConnectionInfo.setFont(new Font("Arial", Font.BOLD,18));
        usernameField.setFont(new Font("Arial", Font.BOLD,30));
        passwordField.setFont(new Font("Arial", Font.BOLD,30));

        serverGuiMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "main-menu";
            }
        });

        serverGuiAddServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = JOptionPane.showInputDialog("Enter IP");
                serverList.put(ip);
            }
        });

        serverGuiJoinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect(serversJList.getSelectedValue(),8008);
                LoginPacket o = new LoginPacket(usernameField.getText(),passwordField.getText(),null);
                sendObject(o);
            }
        });

        serverGuiSearchLocalServersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> addresses = new ArrayList<>();
                try {
                    LocalNetworkScanner.scan(addresses);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                for(String s : addresses){
                    Iterator<Object> i = serverList.iterator();
                    boolean found = false;
                    while (i.hasNext()) {
                        if (((String) i.next()).equals(s)) {
                            found = true;
                            break;
                        }
                    }
                    if(!found) serverList.put(s);
                    forceUpdate = true;
                }
            }
        });

        panel.add(serverSelectorLabel);
        panel.add(serverGuiMenuButton);
        panel.add(serverScrollPane);
        panel.add(serverGuiAddServer);
        panel.add(serverGuiJoinServerButton);
        panel.add(serverGuiSearchLocalServersButton);
        panel.add(serverConnectionInfo);
        panel.add(usernameField);
        panel.add(passwordField);

        /**
         * server-main
         */

        JLabel serverConnectedTo = new JLabel();
        JButton joinLobby = new JButton("JOIN");
        JTextField codeField = new JTextField();
        JLabel joinResult = new JLabel();
        JButton createLobby = new JButton("CREATE LOBBY");
        JButton serverMainDisconnectButton = new JButton("DISCONNECT");

        serverConnectedTo.setBounds(0,0,frame.getWidth(),50);
        joinLobby.setBounds((frame.getWidth()-400) / 2, 200, 400,40);
        codeField.setBounds((frame.getWidth()-400) / 2, 260, 400,40);
        joinResult.setBounds((frame.getWidth()-400) / 2, 320, 400,40);
        createLobby.setBounds((frame.getWidth()-400) / 2, 380, 400,40);
        serverMainDisconnectButton.setBounds((frame.getWidth()-400) / 2, 440, 400,40);

        serverConnectedTo.setFont(new Font("Arial", Font.BOLD,40));
        joinLobby.setFont(new Font("Arial", Font.BOLD,30));
        codeField.setFont(new Font("Arial", Font.BOLD,30));
        joinResult.setFont(new Font("Arial", Font.BOLD,30));
        createLobby.setFont(new Font("Arial", Font.BOLD,30));
        serverMainDisconnectButton.setFont(new Font("Arial", Font.BOLD,30));

        serverConnectedTo.setHorizontalAlignment(JLabel.CENTER);
        serverConnectedTo.setVerticalAlignment(JLabel.CENTER);

        joinLobby.setHorizontalAlignment(JLabel.CENTER);
        joinLobby.setVerticalAlignment(JLabel.CENTER);
        joinResult.setHorizontalAlignment(JLabel.CENTER);
        joinResult.setVerticalAlignment(JLabel.CENTER);
        createLobby.setHorizontalAlignment(JLabel.CENTER);
        createLobby.setVerticalAlignment(JLabel.CENTER);
        serverMainDisconnectButton.setHorizontalAlignment(JLabel.CENTER);
        serverMainDisconnectButton.setVerticalAlignment(JLabel.CENTER);


        DefaultStyledDocument doc = new DefaultStyledDocument();
        doc.setDocumentFilter(new DocumentSizeFilter(4));
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        codeField.setDocument(doc);
        codeField.setHorizontalAlignment(JTextField.CENTER);

        joinLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(codeField.getText().length() == 4) sendObject(new JoinLobbyPacket(codeField.getText()));
                gui = "game-lobby";
            }
        });

        createLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new CreateLobbyPacket());
                sendObject(new Ping(System.currentTimeMillis()));
                gui = "game-lobby";
            }
        });

        serverMainDisconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();
                gui = "server-selector";
            }
        });

        panel.add(serverConnectedTo);
        panel.add(joinLobby);
        panel.add(joinResult);
        panel.add(createLobby);
        panel.add(codeField);
        panel.add(serverMainDisconnectButton);

        /**
         * game-lobby
         */
        JLabel lobbyCode = new JLabel();
        JList<String> lobbyPlayers = new JList<>();
        JScrollPane lobbyPlayersScrollPane = new JScrollPane(lobbyPlayers);
        JButton lobbyStart = new JButton("START GAME");
        JLabel lobbySettingsLabel = new JLabel("Settings");
        JTextField lobbyCardsPerPlayer = new JTextField();
        JTextField lobbyDecks = new JTextField();
        JCheckBox lobbyStacking = new JCheckBox("Stacking");
        JCheckBox lobbySevenSwap = new JCheckBox("Seven Swap");
        JCheckBox lobbyJumpIn = new JCheckBox("Jump-In");

        PlainDocument doc1 = (PlainDocument) lobbyCardsPerPlayer.getDocument();
        doc1.setDocumentFilter(new IntFilter());

        PlainDocument doc2 = (PlainDocument) lobbyDecks.getDocument();
        doc2.setDocumentFilter(new IntFilter());

        lobbyCode.setBounds(0,10,frame.getWidth(),40);
        lobbyPlayersScrollPane.setBounds(50,50,(frame.getWidth()/2)-50,frame.getHeight()-140);
        lobbyStart.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-140,400,50);
        lobbySettingsLabel.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-620,400,50);
        lobbyCardsPerPlayer.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-540,400,50);
        lobbyDecks.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-480,400,50);
        lobbyStacking.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-420,400,50);
        lobbySevenSwap.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-360,400,50);
        lobbyJumpIn.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-310,400,50);

        lobbyCode.setFont(new Font("Arial", Font.BOLD,30));
        lobbyPlayers.setFont(new Font("Arial", Font.BOLD,30));
        lobbySettingsLabel.setFont(new Font("Arial", Font.BOLD,50));
        lobbyCardsPerPlayer.setFont(new Font("Arial", Font.BOLD,30));
        lobbyDecks.setFont(new Font("Arial", Font.BOLD,30));
        lobbyStacking.setFont(new Font("Arial", Font.BOLD,30));
        lobbySevenSwap.setFont(new Font("Arial", Font.BOLD,30));
        lobbyJumpIn.setFont(new Font("Arial", Font.BOLD,30));

        Font font = lobbySettingsLabel.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lobbySettingsLabel.setFont(font.deriveFont(attributes));

        lobbyCode.setHorizontalAlignment(JLabel.CENTER);
        lobbyCode.setVerticalAlignment(JLabel.CENTER);

        lobbySettingsLabel.setHorizontalAlignment(JLabel.CENTER);
        lobbySettingsLabel.setVerticalAlignment(JLabel.CENTER);

        lobbyStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new GameStartPacket());
            }
        });

        panel.add(lobbyCode);
        panel.add(lobbyPlayersScrollPane);
        panel.add(lobbyStart);
        panel.add(lobbySettingsLabel);
        panel.add(lobbyCardsPerPlayer);
        panel.add(lobbyDecks);
        panel.add(lobbyStacking);
        panel.add(lobbySevenSwap);
        panel.add(lobbyJumpIn);

        /**
         * game-main
         */

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                while (true) {
                    switch (gui){
                        case "main-menu": {
                            List<Component> components = List.of(mainMenuTitleLabel,mainMenuPlayButton,mainMenuSettingsButton,mainMenuSubTitleLabel);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "settings-gui": {
                            List<Component> components = List.of(settingsSubTitleLabel,settingsTitleLabel,settingsMainMenuButton);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "server-selector": {
                            List<Component> components = List.of(serverSelectorLabel,serverGuiMenuButton,serverScrollPane,serverGuiJoinServerButton,serverGuiAddServer,serverGuiSearchLocalServersButton,serverConnectionInfo,usernameField,passwordField);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }

                            if(System.currentTimeMillis() - lastUpdate > 100 || forceUpdate){
                                forceUpdate = false;

                                int selectedIndex = Math.max(serversJList.getSelectedIndex(),0);

                                int len = serverList.length();

                                String[] data = new String[len];

                                for (int i = 0; i < len; i++) {
                                    data[i] = (serverList.getString(i));
                                }

                                serversJList.setListData(data);
                                serversJList.setSelectedIndex(selectedIndex);

                                lastUpdate = System.currentTimeMillis();
                            }
                            break;
                        }
                        case "server-main": {
                            List<Component> components = List.of(serverConnectedTo,joinLobby,joinResult,createLobby,codeField,serverMainDisconnectButton);
                            serverConnectedTo.setText("Server connected to: " + serversJList.getSelectedValue());
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "game-lobby": {
                            List<Component> components = List.of(lobbyDecks,lobbyStacking,lobbySevenSwap,lobbyJumpIn,lobbyCardsPerPlayer,lobbySettingsLabel,lobbyCode,lobbyPlayersScrollPane,lobbyStart);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "game-main": {
                            List<Component> components = List.of(mainMenuTitleLabel,mainMenuPlayButton,mainMenuSettingsButton,mainMenuSubTitleLabel);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        default: {

                        }
                    }
                }
            }
        });
        t.start();
        final boolean[] scheduled_disconnect = {false};
        final long[] lastPingUpdate = {System.currentTimeMillis()};
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Packet packet) {
                if(packet instanceof DisconnectPacket p){
                    scheduled_disconnect[0] = true;
                    serverConnectionInfo.setForeground(Color.RED);
                    serverConnectionInfo.setText(p.getDisconnectReason());
                }
                if(packet instanceof LoginSuccessPacket){
                    gui = "server-main";
                }
                if(packet instanceof LobbyInfoPacket p){
                    gui = "game-lobby";
                    lobbyCode.setText("Lobby: " + p.getCode());
                    String[] players = new String[p.getPlayers().size()];
                    int i = 0;
                    for(String s : p.getPlayers().keySet()){
                        players[i] = p.getPlayers().get(s);
                        i++;
                    }
                    lobbyPlayers.setListData(players);
                    lobbyStart.setEnabled(p.isOwner());

                    lobbyCardsPerPlayer.setEnabled(p.isOwner());
                    lobbyDecks.setEnabled(p.isOwner());
                    lobbyStacking.setEnabled(p.isOwner());
                    lobbySevenSwap.setEnabled(p.isOwner());
                    lobbyJumpIn.setEnabled(p.isOwner());

                    if(lobbyCardsPerPlayer.getText().isEmpty()) lobbyCardsPerPlayer.setText(p.getCardsPerPlayer() + "");
                    if(lobbyDecks.getText().isEmpty()) lobbyDecks.setText(p.getDecks() + "");
                    if(!p.isOwner()) lobbyStacking.setSelected(p.isStacking());
                    if(!p.isOwner()) lobbySevenSwap.setSelected(p.isSevenSwap());
                    if(!p.isOwner()) lobbyJumpIn.setSelected(p.isJumpIn());

                    sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));
                }
                if(packet instanceof Ping p){
                    if(System.currentTimeMillis() - lastPingUpdate[0] > 1000) {
                        frame.setTitle("UNO - " + Main.getVersion() + " " + (System.currentTimeMillis() - p.getTime() + " ms ping"));
                        lastPingUpdate[0] = System.currentTimeMillis();
                    }
                    sendObject(new Ping(System.currentTimeMillis()));
                }
            }
        });
        setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception reason) {
                gui = "server-selector";
                serverConnectionInfo.setText("Failed to connect, " +reason.getLocalizedMessage());
                serverConnectionInfo.setForeground(Color.RED);
            }
        });
        setOnConnectionSuccessEvent(new ConnectionSuccessEvent() {
            @Override
            public void onConnectionSuccess() {
                serverConnectionInfo.setText("Connected to " + serversJList.getSelectedValue());
                serverConnectionInfo.setForeground(Color.GREEN);
            }
        });
        setConnectionLostEvent(new ConnectionLostEvent() {
            @Override
            public void onConnectionLostEvent() {
                gui = "server-selector";
                if(scheduled_disconnect[0]){
                    scheduled_disconnect[0] = false;
                    return;
                }
                serverConnectionInfo.setText("Lost connection.");
                serverConnectionInfo.setForeground(Color.RED);
            }
        });
    }
}
