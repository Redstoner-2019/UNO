package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.CardColor;
import me.redstoner2019.main.data.CardType;
import me.redstoner2019.main.data.packets.gamepackets.GameEndPacket;
import me.redstoner2019.main.data.packets.gamepackets.GameStartPacket;
import me.redstoner2019.main.data.packets.lobbypackets.*;
import me.redstoner2019.main.data.packets.loginpackets.DisconnectPacket;
import me.redstoner2019.main.data.packets.loginpackets.LoginPacket;
import me.redstoner2019.main.data.packets.loginpackets.LoginSuccessPacket;
import me.redstoner2019.main.data.packets.loginpackets.Ping;
import me.redstoner2019.serverhandling.*;
import org.json.JSONArray;

import javax.imageio.ImageIO;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import static me.redstoner2019.main.data.CardColor.*;
import static me.redstoner2019.main.data.CardType.*;

public class GUI extends Client {
    public static JFrame frame;
    private final int width = 1280;
    private final int height = 720;
    public static String gui = "game-main";
    public static JSONArray serverList = new JSONArray();
    public static boolean forceUpdate = false;
    public static BufferedImage cards = null;
    public static Card lastCardPut = new Card(CardColor.BLUE, CardType.ZERO);
    public static List<Card> deck = new ArrayList<>();
    public GUI() throws Exception {
        initialize();
    }
    private void initialize() throws Exception {
        String customTexture = "";


        System.out.println(Main.class.getResource("/textures/texture.png"));
        cards = ImageIO.read(GUI.class.getResource("/textures/texture.png"));
        try{
            if(!customTexture.isEmpty()) cards = ImageIO.read(new File(customTexture));
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Couldnt read custom Texture. Defaulting.");
            cards = ImageIO.read(GUI.class.getResource("/cards.png"));
        }

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

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("C:\\Users\\andre\\Downloads/IMG-20200812-WA0006_v2_enhanced (1).jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        serverList.put("localhost");
        serverList.put("cruw-community.de");

        /**
         * Main Menu
         */
        JLabel mainMenuTitleLabel = new JLabel("UNO");
        JLabel mainMenuSubTitleLabel = new JLabel("Version " + Main.getVersion());
        JButton mainMenuPlayButton = new JButton("PLAY");
        JButton mainMenuSettingsButton = new JButton("SETTINGS");frame.setIconImage(new ImageIcon(image).getImage());

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
                sendObject(new RequestLobbiesPacket());
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
        JList<String> lobbies = new JList<>();
        JScrollPane lobbiesScrollPane = new JScrollPane(lobbies);

        serverConnectedTo.setBounds(0,0,frame.getWidth(),50);
        joinLobby.setBounds((frame.getWidth()-400) / 2, 200, 400,40);
        codeField.setBounds((frame.getWidth()-400) / 2, 260, 400,40);
        joinResult.setBounds((frame.getWidth()-400) / 2, 320, 400,40);
        createLobby.setBounds((frame.getWidth()-400) / 2, 380, 400,40);
        serverMainDisconnectButton.setBounds((frame.getWidth()-400) / 2, 440, 400,40);
        lobbiesScrollPane.setBounds(0,0,200,frame.getHeight()-100);

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
                if(codeField.getText().length() == 4) {
                    sendObject(new JoinLobbyPacket(codeField.getText()));
                    //sendObject(new Ping(System.currentTimeMillis()));
                }
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
        panel.add(lobbiesScrollPane);

        /**
         * game-lobby
         */
        JLabel lobbyCode = new JLabel();
        JList<String> lobbyPlayers = new JList<>();
        JScrollPane lobbyPlayersScrollPane = new JScrollPane(lobbyPlayers);
        JButton lobbyStart = new JButton("START GAME");
        JLabel lobbySettingsLabel = new JLabel("Settings");
        JTextField lobbyCardsPerPlayer = new JTextField("7");
        JTextField lobbyDecks = new JTextField("2");
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

        JLabel draw = new JLabel();

        draw.setBounds(0,0,frame.getWidth(),frame.getHeight());

        panel.add(draw);

        draw.setIcon(new ImageIcon(Util.resize(image,frame.getWidth(),frame.getHeight())));

        deck.add(new Card(RED, CardType.ZERO));
        deck.add(new Card(CardColor.GREEN, CardType.ZERO));
        deck.add(new Card(CardColor.BLUE, CardType.ZERO));
        deck.add(new Card(CardColor.YELLOW, CardType.ZERO));
        deck.add(new Card(CardColor.SPECIAL, CardType.DRAW));

        BufferedImage finalImage = image;
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
                            List<Component> components = List.of(serverConnectedTo,joinLobby,joinResult,createLobby,codeField,serverMainDisconnectButton,lobbiesScrollPane);
                            serverConnectedTo.setText("Server connected to: " + serversJList.getSelectedValue());
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            if(codeField.getText().length() == 4){
                                joinLobby.setEnabled(true);
                                createLobby.setEnabled(false);
                            } else {
                                joinLobby.setEnabled(false);
                                createLobby.setEnabled(true);
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
                            List<Component> components = List.of(draw);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            BufferedImage image2 = new BufferedImage(frame.getWidth(), frame.getHeight(), 1);

                            Graphics2D g = image2.createGraphics();

                            BufferedImage card = getCard(lastCardPut);

                            g.drawImage(Util.resize(finalImage,image2.getWidth(),image2.getHeight()),null,0,0);

                            g.drawImage(card,null,(frame.getWidth() - card.getWidth())/2,(frame.getHeight() - card.getHeight())/2 - 200);

                            if(deck.size() > 1){
                                int spacing = (frame.getWidth() - 200) / deck.size();
                                int x = 0;
                                int i = 0;
                                while (x < (frame.getWidth() - 200)){
                                    card = getCard(deck.get(i));
                                    g.drawImage(card,null,x+100,(frame.getHeight() - 300));
                                    i++;
                                    x+=spacing;
                                }
                            }

                            g.dispose();

                            draw.setIcon(new ImageIcon(image2));
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
            public void packetRecievedEvent(Object packet) {
                if(!(packet instanceof Ping) && !(packet instanceof LobbyInfoPacket)){
                    System.out.println(packet.getClass());
                }
                if(packet instanceof DisconnectPacket p){
                    scheduled_disconnect[0] = true;
                    serverConnectionInfo.setForeground(Color.RED);
                    serverConnectionInfo.setText(p.getDisconnectReason());
                }
                if(packet instanceof LoginSuccessPacket){
                    gui = "server-main";
                }
                if(packet instanceof LobbyInfoPacket p){
                    try{
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

                        lobbyCardsPerPlayer.setEditable(p.isOwner());
                        lobbyDecks.setEditable(p.isOwner());
                        lobbyStacking.setEnabled(p.isOwner());
                        lobbySevenSwap.setEnabled(p.isOwner());
                        lobbyJumpIn.setEnabled(p.isOwner());

                        if(!p.isOwner()) lobbyCardsPerPlayer.setText(p.getCardsPerPlayer() + "");
                        if(!p.isOwner()) lobbyDecks.setText(p.getDecks() + "");
                        if(!p.isOwner()) lobbyStacking.setSelected(p.isStacking());
                        if(!p.isOwner()) lobbySevenSwap.setSelected(p.isSevenSwap());
                        if(!p.isOwner()) lobbyJumpIn.setSelected(p.isJumpIn());

                        Thread.sleep(100);

                        if(p.isOwner()){
                            sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));
                        } else sendObject(new UpdateLobbyPacket(0,0, lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));
                    }catch (Exception e){
                        sendObject(new UpdateLobbyPacket(7,2, false, false, false));
                    }


                }
                if(packet instanceof Ping p){
                    if(System.currentTimeMillis() - lastPingUpdate[0] > 1000) {
                        frame.setTitle("UNO - " + Main.getVersion() + " " + (System.currentTimeMillis() - p.getTime() + " ms ping"));
                        lastPingUpdate[0] = System.currentTimeMillis();
                    }
                    sendObject(new Ping(System.currentTimeMillis()));
                }
                if(packet instanceof LobbiesPacket p){
                    lobbies.setListData(p.getLobbies());
                    System.out.println(Arrays.toString(p.getLobbies()));
                    sendObject(new RequestLobbiesPacket());
                }
                if(packet instanceof GameStartPacket p){
                    System.out.println("Game start");
                    gui = "game-main";
                }
                if(packet instanceof GameEndPacket p){
                    System.out.println("Game end");
                    gui = "game-lobby";
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

    /*public static BufferedImage getCard(Card c){
        int color = 0;
        int number = 0;
        HashMap<CardColor,Integer> colors = new HashMap<>();
        colors.put(RED,0);
        colors.put(GREEN,2);
        colors.put(YELLOW,1);
        colors.put(BLUE,3);
        colors.put(SPECIAL,4);

        HashMap<CardType,Integer> types = new HashMap<>();
        int i = 0;
        for(CardType t : CardType.values()){
            types.put(t,i);
            i++;
        }

        color = colors.get(c.getColor());
        number = types.get(c.getNum());

        if(c == null) return new BufferedImage(1,1,1);

        if(c.getColor() == SPECIAL){
            number = 13;
            if(c.getNum() == CardType.CHANGE_COLOR){
                color = 0;
            }
            if(c.getNum() == CardType.PLUS_4){
                color = 4;
            }
        }

        BufferedImage ca = getCard(color,number);
        if(c.getOverrideColor() != null){
            Graphics2D g = ca.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            switch(c.getOverrideColor()){
                case RED: {
                    g.setColor(Color.RED);
                    break;
                }
                case GREEN: {
                    g.setColor(Color.GREEN);
                    break;
                }
                case BLUE: {
                    g.setColor(Color.BLUE);
                    break;
                }
                case YELLOW: {
                    g.setColor(Color.ORANGE);
                    break;
                }
                default: {
                    return new BufferedImage(1,1,1);
                }
            }
            g.fillOval(90,15,20,20);
            g.fillOval(13,150,20,20);
            g.dispose();
        }
        return ca;
    }*/

    public static BufferedImage getCard(Card c){
        BufferedImage b = cards.getSubimage(0,0,128,192);
        return b;
    }

    /*public static BufferedImage getCard(int color, int number){
        int x0 = 125;
        int y0 = 25;
        int cWidth = 125;
        int cHeight = 187;
        if(cards == null) return new BufferedImage(cWidth,cHeight,1);
        BufferedImage subImage = new BufferedImage(cWidth,cHeight,1);
        for (int x = 0; x < cWidth; x++) {
            for (int y = 0; y < cHeight; y++) {
                subImage.setRGB(x,y,cards.getRGB(x0 + (cWidth * number) + x,y0 + (cHeight * color) + y));
            }
        }
        return Util.resize(subImage,128,192);
        //return cards.getSubimage(x0 + (cWidth * number),y0 + (cHeight * color),cWidth,cHeight);
    }*/
}
