package me.redstoner2019.uno.main.data.guis;

import me.redstoner2019.authserver.client.AuthenticatorClient;
import me.redstoner2019.server.events.ConnectionFailedEvent;
import me.redstoner2019.server.events.ConnectionLostEvent;
import me.redstoner2019.server.events.ConnectionSuccessEvent;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.odclient.*;
import me.redstoner2019.server.util.ConnectionProtocol;
import me.redstoner2019.server.util.LocalNetworkScanner;
import me.redstoner2019.server.util.Util;
import me.redstoner2019.uno.main.util.BoundsCheck;
import me.redstoner2019.uno.main.Main;
import me.redstoner2019.uno.main.data.*;
import me.redstoner2019.uno.main.data.data.ActionMessage;
import me.redstoner2019.uno.main.data.packets.gamepackets.*;
import me.redstoner2019.uno.main.data.packets.generalpackets.ProfilerUpdate;
import me.redstoner2019.uno.main.data.packets.lobbypackets.*;
import me.redstoner2019.uno.main.data.packets.loginpackets.DisconnectPacket;
import me.redstoner2019.uno.main.data.packets.loginpackets.LoginPacket;
import me.redstoner2019.uno.main.data.packets.loginpackets.LoginSuccessPacket;
import me.redstoner2019.uno.main.data.packets.loginpackets.Ping;
import me.redstoner2019.uno.main.data.packets.remoteconsole.NewConsoleLinePacket;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

import static me.redstoner2019.uno.main.data.CardColor.*;
import static me.redstoner2019.uno.main.data.CardType.*;

public class GUI extends ODClient {
    public static JFrame frame;
    private final int width = 1280;
    private final int height = 720;
    public static String gui = "main-menu";
    public static JSONObject clientData = new JSONObject();
    public static boolean forceUpdate = false;
    public static BufferedImage cards = null;
    public static Card lastCardPut = new Card(SPECIAL, PLUS_4, RED);
    public static List<Card> deck = new ArrayList<>();
    public static int indexSelected = -1;
    public static boolean canDraw = false;
    public static boolean canSkip = false;
    public static boolean canUNO = false;
    public static String winner = "";
    public static List<String> leaderboard = new ArrayList<>();
    public static int maxFPS = 1000;
    public static int minMS = 1000/maxFPS;
    public static boolean darkMode = false;
    public static String customTexture = "";
    public static boolean reloadTextures = false;
    public static int viewCard = 0;
    public static List<String> chatMessages = new ArrayList<>();
    public static List<ActionMessage> actionMessages = new ArrayList<>();
    public static String TOKEN = "";
    public static AuthenticatorClient authenticatorClient = new AuthenticatorClient();
    public GUI() throws Exception {
        initialize();
    }
    private void initialize() throws Exception {
        final LoadingGUI[] loadingGUI = {new LoadingGUI()};
        loadingGUI[0].setMax(14);
        loadingGUI[0].setValue(0);
        loadingGUI[0].increaseValue();
        //gui = "game-main-game-end";


        System.out.println(Main.class.getResource("/textures/texture.png"));
        cards = ImageIO.read(GUI.class.getResource("/textures/texture.png"));
        try{
            if(!customTexture.isEmpty()) cards = ImageIO.read(new File(customTexture));
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Couldnt read custom Texture. Defaulting.");
            cards = ImageIO.read(GUI.class.getResource("/cards.png"));
        }

        authenticatorClient.setConnectionFailedEvent(new ConnectionFailedEvent() {
            @Override
            public void onConnectionFailedEvent(Exception reason) {
                JOptionPane.showMessageDialog(null,"Failed to connect to auth Server! \nExiting.");
                Util.log("Connection to auth server failed");
                System.exit(0);
            }
        });
        final boolean[] overrideConnectionLost = {false};
        authenticatorClient.setConnectionLostEvent(new ConnectionLostEvent() {
            @Override
            public void onConnectionLostEvent(String s) {
                if(overrideConnectionLost[0]) {
                    overrideConnectionLost[0] = false;
                    return;
                }
                JOptionPane.showMessageDialog(null,"Lost connection to auth Server! \nExiting.");
                Util.log("Lost connection to auth server failed");
                System.exit(0);
            }
        });

        if(!new File("client.properties").exists()) {
            new File("client.properties").createNewFile();
            JSONObject freshData = new JSONObject();
            freshData.put("username","");
            freshData.put("password","");
            JSONArray servers = new JSONArray();
            servers.put("localhost");
            freshData.put("servers",servers);
            freshData.put("dark-mode",false);
            freshData.put("texturepack","");
            freshData.put("auth-server","45.93.249.98");
            Util.writeStringToFile(Util.prettyJSON(freshData.toString()),new File("client.properties"));
        }

        clientData = new JSONObject(Util.readFile(new File("client.properties")));

        String authServer = "45.93.249.98";

        if(clientData.has("token")) TOKEN = clientData.getString("token");
        if(!clientData.has("version")){
            clientData.put("version",Main.getVersion());
        } else {
            if(!clientData.getString("version").equals(Main.getVersion())){
                clientData.put("token","");
                clientData.put("auth-server",authServer);
            }
        }
        if(!clientData.has("auth-server")) clientData.put("auth-server",authServer);
        authServer = clientData.getString("auth-server");
        System.out.println(authServer);

        authenticatorClient.authenticationServerIp = authServer;
        authenticatorClient.setup();

        final JSONObject[] tokenData = {authenticatorClient.getTokenInfo(TOKEN)};

        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("UNO - " + Main.getVersion());
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.WHITE);
        frame.setForeground(Color.WHITE);

        JPanel pan = new JPanel();
        frame.getContentPane().add(pan, BorderLayout.CENTER);
        pan.setLayout(null);

        JLabel panel = new JLabel();
        panel.setBounds(0,0,width,height);
        pan.add(panel);

        final BufferedImage[] image = {null};

        try {
            System.out.println("Loading texture");
            image[0] = ImageIO.read(GUI.class.getResource("/background.png"));
            System.out.println("Loaded texture");
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingGUI[0].increaseValue();

        System.out.println("Textures loaded");

        tokenData[0] = authenticatorClient.getTokenInfo(TOKEN);

        JSONArray serverList;
        if(clientData.has("servers")) serverList = clientData.getJSONArray("servers"); else serverList = new JSONArray();
        if(clientData.has("dark-mode")) darkMode = clientData.getBoolean("dark-mode");
        String texturePack = "";
        if(clientData.has("texturepack")) texturePack = clientData.getString("texturepack");

        loadingGUI[0].increaseValue();

        File resourcePack = new File("texturepacks/" + texturePack + "/pack.properties");

        try{
            if(resourcePack.exists()){
                JSONObject packData = new JSONObject(Util.readFile(resourcePack));
                String packVersion = "";
                if(packData.has("version")) packVersion = packData.getString("version");
                if(packVersion.equals(Main.getPackVersion())) customTexture = "texturepacks/" + texturePack;
                System.out.println("Loading Background");
                image[0] = Util.resize(ImageIO.read(new File("texturepacks/" + texturePack + "/textures/background.png")),1280,720);
                System.out.println("Loaded Background");
                Util.writeStringToFile(Util.prettyJSON(packData.toString()),resourcePack);
            }
        }catch (Exception ignored){
        }

        forceUpdate = true;
        loadingGUI[0].increaseValue();

        /**
         * Main Menu
         */
        JLabel mainMenuTitleLabel = new JLabel("UNO");
        JLabel mainMenuSubTitleLabel = new JLabel("Version " + Main.getVersion());
        JButton mainMenuPlayButton = new JButton("PLAY");
        JButton mainMenuSettingsButton = new JButton("SETTINGS");
        JLabel loggedInAs = new JLabel("");

        final String[] latestVersion = {""};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latestVersion[0] = Main.getLatestVersion();
                    if(!latestVersion[0].equals(Main.getVersion())){
                        mainMenuSubTitleLabel.setText(mainMenuSubTitleLabel.getText() + " (Latest on github " + latestVersion[0] + ")");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();



        mainMenuTitleLabel.setBounds((frame.getWidth()-400)/2,30,400,80);
        mainMenuSubTitleLabel.setBounds((frame.getWidth()-800)/2,100,800,50);
        mainMenuPlayButton.setBounds(frame.getWidth()-500,300,300,80);
        mainMenuSettingsButton.setBounds(200,300,300,80);
        loggedInAs.setBounds(frame.getWidth()-430,frame.getHeight()-70,400,30);

        mainMenuTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainMenuTitleLabel.setVerticalAlignment(JLabel.CENTER);
        mainMenuSubTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainMenuSubTitleLabel.setVerticalAlignment(JLabel.CENTER);
        loggedInAs.setHorizontalAlignment(JLabel.RIGHT);

        mainMenuTitleLabel.setFont(new Font("Arial", Font.BOLD,50));
        mainMenuSubTitleLabel.setFont(new Font("Arial", Font.PLAIN,25));
        mainMenuPlayButton.setFont(new Font("Arial", Font.PLAIN,40));
        mainMenuSettingsButton.setFont(new Font("Arial", Font.PLAIN,40));
        loggedInAs.setFont(new Font("Arial", Font.PLAIN,20));

        panel.add(mainMenuTitleLabel);
        panel.add(mainMenuSubTitleLabel);
        panel.add(mainMenuPlayButton);
        panel.add(mainMenuSettingsButton);
        panel.add(loggedInAs);

        if(tokenData[0].get("available").equals("true")){
            loggedInAs.setText("Logged in as " + tokenData[0].get("username"));
            loggedInAs.setForeground(Color.GREEN);
            Util.log("logged in");
        } else {
            loggedInAs.setText("Not logged in");
            loggedInAs.setForeground(Color.RED);
            Util.log("not logged in");
        }

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

        mainMenuSubTitleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/Redstoner-2019/UNO/releases/tag/" + latestVersion[0]));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        loadingGUI[0].increaseValue();

        /**
         * Settings GUI
         */

        JLabel settingsTitleLabel = new JLabel("UNO");
        JLabel settingsSubTitleLabel = new JLabel("Version " + Main.getVersion());
        JButton settingsMainMenuButton = new JButton("MAIN MENU");
        JButton startPerformanceProfiler = new JButton("Start Performance Profiler");
        JButton toggleDarkMode = new JButton("Toggle Dark Mode");
        JTextField resourcePackTextField = new JTextField();
        JButton applyTexturepackButton = new JButton("Apply Texturepack");
        JLabel texturePackLoadResult = new JLabel();
        JLabel cardPreview = new JLabel();
        JButton cardSwitchLeft = new JButton("-");
        JButton cardSwitchRight = new JButton("+");
        JButton accountGUI = new JButton("Account...");
        JButton changeAuthServer = new JButton("change auth server");

        settingsTitleLabel.setBounds((frame.getWidth()-400)/2,30,400,80);
        settingsSubTitleLabel.setBounds((frame.getWidth()-300)/2,100,300,50);
        settingsMainMenuButton.setBounds((frame.getWidth()-300)/2,frame.getHeight()-100,300,40);
        startPerformanceProfiler.setBounds((frame.getWidth()-300)/2,150,300,40);
        toggleDarkMode.setBounds((frame.getWidth()-300)/2,200,300,40);
        resourcePackTextField.setBounds((frame.getWidth()-300)/2,300,300,40);
        applyTexturepackButton.setBounds((frame.getWidth()-300)/2,350,300,40);
        accountGUI.setBounds((frame.getWidth()-300)/2,400,300,40);
        changeAuthServer.setBounds((frame.getWidth()-300)/2,450,300,40);
        texturePackLoadResult.setBounds((frame.getWidth()-300)/2,400,300,40);
        cardPreview.setBounds((frame.getWidth()-200),170,128,200);
        cardSwitchLeft.setBounds(frame.getWidth()-200,392,64,20);
        cardSwitchRight.setBounds(frame.getWidth()-200+64,392,64,20);

        settingsTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        settingsTitleLabel.setVerticalAlignment(JLabel.CENTER);
        settingsSubTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        settingsSubTitleLabel.setVerticalAlignment(JLabel.CENTER);

        settingsTitleLabel.setFont(new Font("Arial", Font.BOLD,50));
        settingsSubTitleLabel.setFont(new Font("Arial", Font.PLAIN,25));
        settingsMainMenuButton.setFont(new Font("Arial", Font.PLAIN,20));
        startPerformanceProfiler.setFont(new Font("Arial", Font.PLAIN,20));
        toggleDarkMode.setFont(new Font("Arial", Font.PLAIN,20));
        resourcePackTextField.setFont(new Font("Arial", Font.PLAIN,20));
        applyTexturepackButton.setFont(new Font("Arial", Font.PLAIN,20));
        texturePackLoadResult.setFont(new Font("Arial", Font.PLAIN,20));
        accountGUI.setFont(new Font("Arial", Font.PLAIN,20));
        changeAuthServer.setFont(new Font("Arial", Font.PLAIN,20));

        panel.add(settingsTitleLabel);
        panel.add(settingsSubTitleLabel);
        panel.add(settingsMainMenuButton);
        panel.add(startPerformanceProfiler);
        panel.add(toggleDarkMode);
        panel.add(resourcePackTextField);
        panel.add(applyTexturepackButton);
        panel.add(texturePackLoadResult);
        panel.add(cardPreview);
        panel.add(cardSwitchLeft);
        panel.add(cardSwitchRight);
        panel.add(accountGUI);
        panel.add(changeAuthServer);

        cardPreview.setIcon(new ImageIcon(getCard(new Card(RED,CardType.ZERO))));

        changeAuthServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame,"Changing the auth server may cause you to not be able to login into a Server if the server doesnt have the same auth server. Do you wish to continue?");
                if(choice == JOptionPane.OK_OPTION){
                    String newAuthServer = JOptionPane.showInputDialog("Address?");
                    overrideConnectionLost[0] = true;
                    authenticatorClient.disconnect();
                    authenticatorClient.authenticationServerIp = newAuthServer;
                    authenticatorClient.setup();
                    clientData.put("auth-server",newAuthServer);
                }
            }
        });
        cardSwitchLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCard--;
                List<Card> cards = new ArrayList<>();
                cards.addAll(Card.getFromOneColor(CardColor.RED));
                cards.addAll(Card.getFromOneColor(GREEN));
                cards.addAll(Card.getFromOneColor(BLUE));
                cards.addAll(Card.getFromOneColor(YELLOW));
                cards.add(new Card(SPECIAL,PLUS_4));
                cards.add(new Card(SPECIAL,PLUS_4,RED));
                cards.add(new Card(SPECIAL,PLUS_4,GREEN));
                cards.add(new Card(SPECIAL,PLUS_4,BLUE));
                cards.add(new Card(SPECIAL,PLUS_4,YELLOW));
                cards.add(new Card(SPECIAL,CHANGE_COLOR));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,RED));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,GREEN));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,BLUE));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,YELLOW));
                if(viewCard < 0){
                    viewCard = cards.size()-1;
                }
                cardPreview.setIcon(new ImageIcon(getCard(cards.get(viewCard))));
            }
        });
        cardSwitchRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCard++;
                List<Card> cards = new ArrayList<>();
                cards.addAll(Card.getFromOneColor(CardColor.RED));
                cards.addAll(Card.getFromOneColor(GREEN));
                cards.addAll(Card.getFromOneColor(BLUE));
                cards.addAll(Card.getFromOneColor(YELLOW));
                cards.add(new Card(SPECIAL,PLUS_4));
                cards.add(new Card(SPECIAL,PLUS_4,RED));
                cards.add(new Card(SPECIAL,PLUS_4,GREEN));
                cards.add(new Card(SPECIAL,PLUS_4,BLUE));
                cards.add(new Card(SPECIAL,PLUS_4,YELLOW));
                cards.add(new Card(SPECIAL,CHANGE_COLOR));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,RED));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,GREEN));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,BLUE));
                cards.add(new Card(SPECIAL,CHANGE_COLOR,YELLOW));
                if(viewCard >= cards.size()){
                    viewCard = 0;
                }
                cardPreview.setIcon(new ImageIcon(getCard(cards.get(viewCard))));
            }
        });

        applyTexturepackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientData.put("texturepack",resourcePackTextField.getText());

                String texturePack = clientData.getString("texturepack");

                System.out.println("Loading texturepack");

                File resourcePack = new File("texturepacks/" + texturePack + "/pack.properties");

                try{
                    if(resourcePack.exists()){
                        JSONObject packData = new JSONObject(Util.readFile(resourcePack));
                        String packVersion = "";
                        if(packData.has("version")) packVersion = packData.getString("version");
                        if(packVersion.equals(Main.getPackVersion())) customTexture = "texturepacks/" + texturePack; else {
                            texturePackLoadResult.setText("Invalid pack version");
                            return;
                        }
                        System.out.println("Loading Background");
                        image[0] = Util.resize(ImageIO.read(new File("texturepacks/" + texturePack + "/textures/background.png")),1280,720);
                        System.out.println("Loaded Background");
                        Util.writeStringToFile(Util.prettyJSON(packData.toString()),resourcePack);
                        texturePackLoadResult.setText("Success");
                        clientData.put("texturepack",texturePack);
                        reloadTextures = true;
                    } else {
                        texturePackLoadResult.setText("Couldnt find texturepack");
                        GUI.customTexture = "";
                        System.out.println("Loading texture");
                        image[0] = ImageIO.read(GUI.class.getResource("/background.png"));
                        System.out.println("Loaded texture");
                        reloadTextures = true;
                    }
                }catch (Exception ex){
                    texturePackLoadResult.setText(ex.getLocalizedMessage());
                }

                try {
                    Util.writeStringToFile(clientData.toString(),new File("client.properties"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        startPerformanceProfiler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startPerformanceProfiler.setEnabled(false);
                Thread profilerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PerformanceProfiler performanceProfiler = new PerformanceProfiler("Client");
                            performanceProfiler.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                profilerThread.start();
            }
        });

        toggleDarkMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                darkMode = !darkMode;
                clientData.put("dark-mode",darkMode);
                try {
                    Util.writeStringToFile(Util.prettyJSON(clientData.toString()),new File("client.properties"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        accountGUI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "login-account";
            }
        });



        settingsMainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "main-menu";
            }
        });
        loadingGUI[0].increaseValue();

        /**
         * Server Selector GUI
         */

        JLabel serverSelectorLabel = new JLabel("Select Server");

        JButton serverGuiMenuButton = new JButton("MAIN MENU");
        JButton serverGuiJoinServerButton = new JButton("JOIN SELECTED SERVER");
        JButton serverGuiAddServer = new JButton("ADD SERVER");
        JButton serverGuiDeleteServer = new JButton("DELETE SERVER");
        JButton serverGuiSearchLocalServersButton = new JButton("SEARCH LOCAL");

        JList<String> serversJList = new JList<>();
        JScrollPane serverScrollPane = new JScrollPane(serversJList);
        JTextArea serverConnectionInfo = new JTextArea();

        serverSelectorLabel.setBounds(frame.getWidth()-500,50,500,40);

        serverGuiSearchLocalServersButton.setBounds(480-50,frame.getHeight()-200,350,40);
        serverGuiMenuButton.setBounds(480-50,frame.getHeight()-130,350,40);
        serverGuiAddServer.setBounds(50,frame.getHeight()-200,350,40);
        serverGuiDeleteServer.setBounds(50,frame.getHeight()-130,350,40);
        serverGuiJoinServerButton.setBounds(frame.getWidth()-450,frame.getHeight()-200,400,110);

        serverScrollPane.setBounds(50,50,frame.getWidth()-550,height-280);
        serverConnectionInfo.setBounds(frame.getWidth()-450,100,400,400);

        serverSelectorLabel.setHorizontalAlignment(JLabel.CENTER);
        serverSelectorLabel.setVerticalAlignment(JLabel.CENTER);
        serverConnectionInfo.setEditable(false);
        serverConnectionInfo.setLineWrap(true);

        serverSelectorLabel.setFont(new Font("Arial", Font.BOLD,50));
        serverConnectionInfo.setFont(new Font("Arial", Font.BOLD,18));

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
                forceUpdate = true;
            }
        });

        serverGuiJoinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverGuiJoinServerButton.setEnabled(false);
                serverGuiJoinServerButton.setText("Connecting...");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connect(serversJList.getSelectedValue(),8008, ConnectionProtocol.UDP);
                        LoginPacket o = new LoginPacket(TOKEN);
                        sendObject(o);
                        /**
                         * TODO: Redo Login
                         */
                        sendObject(new Ping(System.currentTimeMillis()));
                        sendObject(new RequestLobbiesPacket());
                        clientData.put("token",TOKEN);
                        clientData.put("servers",serverList);
                        try {
                            Util.writeStringToFile(Util.prettyJSON(clientData.toString()),new File("client.properties"));
                        } catch (IOException ex) {
                            serverGuiJoinServerButton.setEnabled(true);
                            serverGuiJoinServerButton.setText("JOIN SERVER");
                            throw new RuntimeException(ex);
                        }
                        serverGuiJoinServerButton.setEnabled(true);
                        serverGuiJoinServerButton.setText("JOIN SERVER");
                    }
                });
                t.start();
            }
        });

        serverGuiSearchLocalServersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> addresses = new ArrayList<>();
                try {
                    LocalNetworkScanner.scan(addresses,8008);
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

        serverGuiDeleteServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverList.remove(serversJList.getSelectedIndex());
                if(serverList.isEmpty()) serverList.put("45.93.249.98");
                serversJList.setSelectedIndex(0);
                forceUpdate = true;
            }
        });

        panel.add(serverSelectorLabel);
        panel.add(serverGuiMenuButton);
        panel.add(serverScrollPane);
        panel.add(serverGuiAddServer);
        panel.add(serverGuiJoinServerButton);
        panel.add(serverGuiSearchLocalServersButton);
        panel.add(serverConnectionInfo);
        panel.add(serverGuiDeleteServer);
        loadingGUI[0].increaseValue();

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
        JLabel statsField = new JLabel();

        serverConnectedTo.setBounds(0,0,frame.getWidth(),50);
        joinLobby.setBounds((frame.getWidth()-400) / 2, 200, 400,40);
        codeField.setBounds((frame.getWidth()-400) / 2, 260, 400,40);
        joinResult.setBounds((frame.getWidth()-800) / 2, 320, 800,40);
        createLobby.setBounds((frame.getWidth()-400) / 2, 380, 400,40);
        serverMainDisconnectButton.setBounds((frame.getWidth()-400) / 2, 440, 400,40);
        lobbiesScrollPane.setBounds(50,50,300,frame.getHeight()-150);
        statsField.setBounds(frame.getWidth()-350,50,400,frame.getHeight()-150);

        serverConnectedTo.setFont(new Font("Arial", Font.BOLD,40));
        joinLobby.setFont(new Font("Arial", Font.BOLD,30));
        codeField.setFont(new Font("Arial", Font.BOLD,30));
        joinResult.setFont(new Font("Arial", Font.BOLD,30));
        createLobby.setFont(new Font("Arial", Font.BOLD,30));
        serverMainDisconnectButton.setFont(new Font("Arial", Font.BOLD,30));
        lobbies.setFont(new Font("Arial", Font.BOLD,20));
        statsField.setFont(new Font("Arial", Font.BOLD,30));

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
        doc.setDocumentFilter(new DocumentSizeFilter(5));
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

        lobbies.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                codeField.setText(lobbies.getSelectedValue());
            }
        });

        joinLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(codeField.getText().length() == 5) {
                    sendObject(new JoinLobbyPacket(codeField.getText()));
                    //sendObject(new Ping(System.currentTimeMillis()));
                }
            }
        });

        createLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new CreateLobbyPacket(codeField.getText().toUpperCase()));
                //sendObject(new Ping(System.currentTimeMillis()));
                //gui = "game-lobby";
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
        panel.add(statsField);
        loadingGUI[0].increaseValue();

        /**
         * chat-gui
         */

        JLabel chatArea = new JLabel("");
        JScrollPane chatScroll = new JScrollPane(chatArea);
        JTextArea messageInput = new JTextArea();
        JButton sendMessageButton = new JButton("Send");

        chatArea.setBounds(50,50,panel.getWidth()-100,panel.getHeight()-200);
        messageInput.setBounds(50,panel.getHeight()-120,panel.getWidth()-320,50);
        sendMessageButton.setBounds(panel.getWidth()-250,panel.getHeight()-120,200,50);

        chatArea.setVerticalAlignment(JLabel.TOP);
        chatScroll.setBackground(Color.DARK_GRAY);

        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("test");
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    e.consume();
                    if(messageInput.getText().isEmpty()) return;
                    sendObject(new ChatPacket(messageInput.getText()));
                    messageInput.setText("");
                }
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    if(gui.equals("game-main")){
                        gui = "chat-gui";
                        System.out.println(1);
                    }else if(gui.equals("chat-gui")){
                        gui = "game-main";
                        System.out.println(2);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(messageInput.getText().isEmpty()) return;
                sendObject(new ChatPacket(messageInput.getText()));
                messageInput.setText("");
            }
        });

        panel.add(chatArea);
        panel.add(messageInput);
        panel.add(sendMessageButton);


        /**
         * game-lobby
         */
        JLabel lobbyCode = new JLabel();
        JList<String> lobbyPlayers = new JList<>();
        JScrollPane lobbyPlayersScrollPane = new JScrollPane(lobbyPlayers);
        JButton lobbyStart = new JButton("START GAME");
        JButton leaveLobby = new JButton("Leave Lobby");
        JLabel lobbySettingsLabel = new JLabel("Settings");
        JTextField lobbyCardsPerPlayer = new JTextField("7");
        JTextField lobbyDecks = new JTextField("2");
        JCheckBox lobbyStacking = new JCheckBox("Stacking");
        JCheckBox lobbySevenSwap = new JCheckBox("Seven Swap");
        JCheckBox lobbyJumpIn = new JCheckBox("Jump-In");
        JCheckBox lobbyIngameChat = new JCheckBox("Chat");

        lobbyStacking.setOpaque(true);

        PlainDocument doc1 = (PlainDocument) lobbyCardsPerPlayer.getDocument();
        doc1.setDocumentFilter(new IntFilter());

        PlainDocument doc2 = (PlainDocument) lobbyDecks.getDocument();
        doc2.setDocumentFilter(new IntFilter());

        lobbyCode.setBounds(0,10,frame.getWidth(),40);
        lobbyPlayersScrollPane.setBounds(50,50,(frame.getWidth()/2)-50,frame.getHeight()-140);
        lobbyStart.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-140,400,50);
        leaveLobby.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-210,400,50);
        lobbySettingsLabel.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-620,400,50);
        lobbyCardsPerPlayer.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-540,400,50);
        lobbyDecks.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-480,400,50);
        lobbyStacking.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-430,400,50);
        lobbySevenSwap.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-380,400,50);
        lobbyJumpIn.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-330,400,50);
        lobbyIngameChat.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-280,400,50);

        lobbyCode.setFont(new Font("Arial", Font.BOLD,30));
        lobbyPlayers.setFont(new Font("Arial", Font.BOLD,30));
        lobbySettingsLabel.setFont(new Font("Arial", Font.BOLD,50));
        lobbyCardsPerPlayer.setFont(new Font("Arial", Font.BOLD,30));
        lobbyDecks.setFont(new Font("Arial", Font.BOLD,30));
        lobbyStacking.setFont(new Font("Arial", Font.BOLD,30));
        lobbySevenSwap.setFont(new Font("Arial", Font.BOLD,30));
        lobbyJumpIn.setFont(new Font("Arial", Font.BOLD,30));
        lobbyIngameChat.setFont(new Font("Arial", Font.BOLD,30));

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
        leaveLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new LeaveLobbyPacket());
                gui = "server-main";
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
        panel.add(leaveLobby);
        panel.add(lobbyIngameChat);
        loadingGUI[0].increaseValue();

        /**
         * game-main
         */

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gui.equals("game-main")){
                    gui = "chat-gui";
                    System.out.println(1);
                }else if(gui.equals("chat-gui")){
                    gui = "game-main";
                    System.out.println(2);
                }
            }
        };
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke,"escapeKey");
        panel.getActionMap().put("escapeKey",action);

        JLabel draw = new JLabel();
        JButton drawButton = new JButton("DRAW");
        JButton skipButton = new JButton("SKIP");
        JButton unoButton = new JButton("UNO");
        JButton leaveGameButton = new JButton("Leave Game");
        JLabel nextUpLabel = new JLabel();

        nextUpLabel.setText("<html>Next up: YOU<br/>Lukas<br/>Halil</html>");
        nextUpLabel.setVerticalAlignment(JLabel.TOP);

        draw.setBounds(0,0,frame.getWidth(),frame.getHeight());

        drawButton.setBounds(frame.getWidth()-400,50,300,40);
        skipButton.setBounds(frame.getWidth()-400,100,300,40);
        unoButton.setBounds(frame.getWidth()-400,150,300,40);
        leaveGameButton.setBounds(frame.getWidth()-400,200,300,40);

        drawButton.setBounds(frame.getWidth()-300,frame.getHeight()-300,250,40);
        skipButton.setBounds(frame.getWidth()-300,frame.getHeight()-250,250,40);
        unoButton.setBounds(frame.getWidth()-300,frame.getHeight()-200,250,40);
        leaveGameButton.setBounds(frame.getWidth()-300,frame.getHeight()-150,250,40);

        nextUpLabel.setBounds(50,50,300,600);

        drawButton.setBackground(Color.DARK_GRAY);
        drawButton.setForeground(Color.white);
        skipButton.setBackground(Color.DARK_GRAY);
        skipButton.setForeground(Color.white);
        unoButton.setBackground(Color.DARK_GRAY);
        unoButton.setForeground(Color.white);
        leaveGameButton.setBackground(Color.DARK_GRAY);
        leaveGameButton.setForeground(Color.white);
        nextUpLabel.setForeground(Color.WHITE);
        nextUpLabel.setFont(new Font("Arial",Font.BOLD,30));

        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new DrawCardPacket());
            }
        });
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new SkipTurnPacket());
            }
        });
        unoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new UNOPacket());
            }
        });
        leaveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new LeaveLobbyPacket());
                gui = "server-main";
            }
        });

        draw.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
            CardColor colorChosen = null;
            @Override
            public void mousePressed(MouseEvent e) {
                try{
                    switch (gui){
                        case "game-main" :{
                            if(indexSelected < 0) return;
                            if(deck.size()>0) {
                                Card c = deck.get(indexSelected);
                                if (!c.getColor().equals(SPECIAL)) sendObject(new PlaceCardPacket(c));
                                else gui = "game-main-select-color";
                            }
                            break;
                        }
                        case "game-main-select-color" :{
                            colorChosen = null;
                            if(BoundsCheck.within(e.getX(),e.getY(),200,200,128,192)){
                                colorChosen = RED;
                            }
                            if(BoundsCheck.within(e.getX(),e.getY(),440,200,128,192)){
                                colorChosen = GREEN;
                            }
                            if(BoundsCheck.within(e.getX(),e.getY(),680,200,128,192)){
                                colorChosen = BLUE;
                            }
                            if(BoundsCheck.within(e.getX(),e.getY(),920,200,128,192)){
                                colorChosen = YELLOW;
                            }
                            if(colorChosen != null){
                                Card c = deck.get(indexSelected);
                                c.setOverrideColor(colorChosen);
                                sendObject(new PlaceCardPacket(c));
                                gui = "game-main";
                            }
                            break;
                        }
                        case "game-main-game-end" :{
                            break;
                        }
                    }
                }catch (Exception ex){

                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        panel.add(draw);
        draw.add(drawButton);
        draw.add(skipButton);
        draw.add(unoButton);
        draw.add(nextUpLabel);
        draw.add(leaveGameButton);

        draw.setIcon(new ImageIcon(Util.resize(image[0],frame.getWidth(),frame.getHeight())));

        deck.add(new Card(RED, CardType.ZERO));
        deck.add(new Card(CardColor.GREEN, CardType.ZERO));
        deck.add(new Card(CardColor.BLUE, CardType.ZERO));
        deck.add(new Card(CardColor.YELLOW, CardType.ZERO));
        deck.add(new Card(CardColor.SPECIAL, PLUS_4));
        loadingGUI[0].increaseValue();

        /**
         * end-results
         */

        JButton backButton = new JButton("BACK");
        backButton.setBounds((width-300)/2,500,300,40);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.DARK_GRAY);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "game-lobby";
                sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected(),lobbyIngameChat.isSelected()));
            }
        });
        draw.add(backButton);
        loadingGUI[0].increaseValue();

        /**
         * login-account
         */
        JLabel loginAccountTitleLabel = new JLabel("LOGIN ACCOUNT");
        JTextField loginAccountUsername = new JTextField();
        JPasswordField loginAccountPasswordField = new JPasswordField();
        JLabel loginAccountLabel = new JLabel("<html>Username:<br><br>Password:</html>");
        JButton loginAccountLoginButton = new JButton("Login");
        JButton loginAccountBackButton = new JButton("Back");
        JButton switchToCreate = new JButton("Switch Create");

        int yOffset = 100;

        loginAccountTitleLabel.setBounds(((width-1000)/2),50,1000,50);
        loginAccountUsername.setBounds(((width-400)/2) + 210,100 + yOffset,190,30);
        loginAccountPasswordField.setBounds(((width-400)/2) + 210,140 + yOffset,190,30);
        loginAccountLoginButton.setBounds(((width-400)/2),180 + yOffset,400,30);
        loginAccountBackButton.setBounds(((width-400)/2),220 + yOffset,400,30);
        switchToCreate.setBounds(((width-400)/2),260 + yOffset,400,30);
        loginAccountLabel.setBounds(((width-400)/2),95 + yOffset,190,80);

        loginAccountTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        loginAccountTitleLabel.setVerticalAlignment(JLabel.CENTER);

        loginAccountTitleLabel.setFont(new Font("Arial", Font.PLAIN,40));
        loginAccountLabel.setFont(new Font("Arial", Font.PLAIN,16));
        loginAccountUsername.setFont(new Font("Arial", Font.PLAIN,16));
        loginAccountPasswordField.setFont(new Font("Arial", Font.PLAIN,16));

        switchToCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "create-account";
            }
        });

        loginAccountBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "settings-gui";
            }
        });
        loginAccountLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginAccountLoginButton.setEnabled(false);
                        loginAccountLoginButton.setText("Logging you in...");

                        if(loginAccountPasswordField.getText().isEmpty() || loginAccountUsername.getText().isEmpty()){
                            return;
                        }
                        if(loginAccountPasswordField.getText().equals(loginAccountPasswordField.getText())) {
                            loggedInAs.setText("Logging in...");
                            loggedInAs.setForeground(Color.ORANGE);
                            tokenData[0] = authenticatorClient.login(loginAccountUsername.getText(), loginAccountPasswordField.getText());
                            loggedInAs.setText("Recieved Login Prompt");
                            loggedInAs.setForeground(Color.ORANGE);
                            Util.log("token data " + tokenData[0]);
                            if (tokenData[0].get("header").equals("login-complete")) {
                                TOKEN = tokenData[0].getString("token");
                                try {
                                    Util.writeStringToFile(Util.prettyJSON(clientData.toString()), new File("client.properties"));
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                clientData.put("token", TOKEN);
                                Util.log("logged in");
                                loggedInAs.setText("Logged in as " + loginAccountUsername.getText() + " - " + TOKEN.substring(0,5));
                                loggedInAs.setForeground(Color.GREEN);
                            } else {
                                loggedInAs.setText("Not logged in, login failed");
                                loggedInAs.setForeground(Color.RED);
                                Util.log("not logged in");
                            }
                            loginAccountTitleLabel.setText("LOGIN ACCOUNT");

                            loginAccountLoginButton.setEnabled(true);
                            loginAccountLoginButton.setText("Login");
                        }
                    }
                });
                t.start();
            }
        });

        panel.add(loginAccountTitleLabel);
        panel.add(loginAccountUsername);
        panel.add(loginAccountPasswordField);
        panel.add(loginAccountLoginButton);
        panel.add(loginAccountBackButton);
        panel.add(loginAccountLabel);
        panel.add(switchToCreate);
        loadingGUI[0].increaseValue();

        /**
         * create-account
         */
        JLabel createAccountTitleLabel = new JLabel("CREATE ACCOUNT");
        JTextField createAccountUsername = new JTextField();
        JTextField createAccountDisplayName = new JTextField();
        JPasswordField createAccountPasswordField = new JPasswordField();
        JPasswordField createAccountConfirmPassword = new JPasswordField();
        JLabel createAccountLabel = new JLabel("<html>Username:<br><br>Displayname:<br><br>Password:<br><br>Confirm Password:</html>");
        JButton createAccountCreateButton = new JButton("Create");
        JButton createAccountBackButton = new JButton("Back");
        JButton switchToLogin = new JButton("Switch Login");

        yOffset = 100;

        createAccountTitleLabel.setBounds(((width-1000)/2),50,1000,50);
        createAccountUsername.setBounds(((width-400)/2) + 210,100 + yOffset,190,30);
        createAccountDisplayName.setBounds(((width-400)/2) + 210,140 + yOffset,190,30);
        createAccountPasswordField.setBounds(((width-400)/2) + 210,180 + yOffset,190,30);
        createAccountConfirmPassword.setBounds(((width-400)/2) + 210,220 + yOffset,190,30);
        createAccountCreateButton.setBounds(((width-400)/2),260 + yOffset,400,30);
        createAccountBackButton.setBounds(((width-400)/2),300 + yOffset,400,30);
        switchToLogin.setBounds(((width-400)/2),340 + yOffset,400,30);
        createAccountLabel.setBounds(((width-400)/2),100 + yOffset,190,150);

        createAccountTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        createAccountTitleLabel.setVerticalAlignment(JLabel.CENTER);

        createAccountTitleLabel.setFont(new Font("Arial", Font.PLAIN,40));
        createAccountLabel.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountUsername.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountDisplayName.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountPasswordField.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountConfirmPassword.setFont(new Font("Arial", Font.PLAIN,16));

        switchToLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "login-account";
            }
        });

        createAccountBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "settings-gui";
            }
        });
        createAccountCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(createAccountPasswordField.getText().equals(createAccountConfirmPassword.getText())) {
                    JSONObject response = authenticatorClient.createAccount(createAccountUsername.getText(),createAccountDisplayName.getText(),createAccountConfirmPassword.getText());
                    if(response.getString("header").equals("account-already-exists")) {
                        loggedInAs.setText("Account already exists");
                        loggedInAs.setForeground(Color.RED);
                    } else {
                        loggedInAs.setText("Account created");
                        loggedInAs.setForeground(Color.GREEN);
                    }
                    createAccountTitleLabel.setText("CREATE ACCOUNT");
                } else {
                    createAccountTitleLabel.setText("CREATE ACCOUNT");
                }
            }
        });

        panel.add(createAccountTitleLabel);
        panel.add(createAccountUsername);
        panel.add(createAccountDisplayName);
        panel.add(createAccountPasswordField);
        panel.add(createAccountConfirmPassword);
        panel.add(createAccountCreateButton);
        panel.add(createAccountBackButton);
        panel.add(createAccountLabel);
        panel.add(switchToLogin);
        loadingGUI[0].increaseValue();

        /**
         * manage account
         */
        loadingGUI[0].increaseValue();

        try {
            startSender();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        BufferedImage finalImage = image[0];
        final long[] ping = {0};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                int frames = 0;
                long lastUpdateFPS = 0;
                String baseTitle = frame.getTitle();
                BufferedImage image2 = new BufferedImage(frame.getWidth(), frame.getHeight(), 1);
                float[] gaussianBlurKernel = {
                        0.006f,  0.012f,  0.012f,  0.012f,  0.006f,
                        0.012f,  0.025f,  0.025f,  0.025f,  0.012f,
                        0.012f,  0.025f,  0.039f,  0.025f,  0.012f,
                        0.012f,  0.025f,  0.025f,  0.025f,  0.012f,
                        0.006f,  0.012f,  0.012f,  0.012f,  0.006f
                };
                int kernelSize = 5;

                float[] darkenKernel = {
                        0.5f
                };

                Kernel kernel = new Kernel(kernelSize, kernelSize, gaussianBlurKernel);
                ConvolveOp convolutionOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

                BufferedImage blurredBackground = convolutionOp.filter(finalImage, null);

                boolean previousMode = !darkMode;
                    while (true) try{
                        long renderStart = System.currentTimeMillis();

                        if(reloadTextures){
                            blurredBackground = convolutionOp.filter(image[0], null);
                            previousMode = !darkMode;
                            reloadTextures = false;
                        }

                        if(previousMode != darkMode){
                            if(darkMode){
                                for(Component c : panel.getComponents()){
                                    if(c.equals(loggedInAs)) continue;
                                    if(c != serverConnectionInfo && c != joinResult) c.setForeground(Color.WHITE);
                                    if(!(c instanceof JLabel)) c.setBackground(Color.DARK_GRAY);
                                }

                                serversJList.setForeground(Color.WHITE);
                                serversJList.setBackground(Color.DARK_GRAY);

                                lobbies.setForeground(Color.WHITE);
                                lobbies.setBackground(Color.DARK_GRAY);

                                lobbyPlayers.setForeground(Color.WHITE);
                                lobbyPlayers.setBackground(Color.DARK_GRAY);
                            } else {
                                for(Component c : panel.getComponents()){
                                    if(c.equals(loggedInAs)) continue;
                                    if(c != serverConnectionInfo && c != joinResult) c.setForeground(Color.BLACK);
                                    if(!(c instanceof JLabel)) c.setBackground(Color.WHITE);
                                }

                                serversJList.setForeground(Color.BLACK);
                                serversJList.setBackground(Color.WHITE);

                                lobbies.setForeground(Color.BLACK);
                                lobbies.setBackground(Color.WHITE);

                                lobbyPlayers.setForeground(Color.BLACK);
                                lobbyPlayers.setBackground(Color.WHITE);
                            }

                            if(darkMode) {
                                BufferedImage backgImage = new BufferedImage(width, height, 1);

                                Graphics2D g2 = backgImage.createGraphics();

                                g2.drawImage(blurredBackground, null, -2, -2);

                                g2.dispose();

                                panel.setIcon(new ImageIcon(backgImage));
                            } else {
                                BufferedImage backgImage = new BufferedImage(width, height, 1);

                                Graphics2D g2 = backgImage.createGraphics();

                                g2.setColor(Color.WHITE);

                                g2.fillRect(0,0,width,height);

                                g2.dispose();

                                panel.setIcon(new ImageIcon(backgImage));
                            }
                        }

                        previousMode = darkMode;

                        switch (gui){
                            case "main-menu": {
                                List<Component> components = List.of(mainMenuTitleLabel,mainMenuPlayButton,mainMenuSettingsButton,mainMenuSubTitleLabel,loggedInAs);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }
                                break;
                            }
                            case "settings-gui": {
                                List<Component> components = List.of(changeAuthServer,accountGUI,cardSwitchRight,cardSwitchLeft,cardPreview,texturePackLoadResult,applyTexturepackButton,resourcePackTextField,settingsSubTitleLabel,settingsTitleLabel,settingsMainMenuButton,startPerformanceProfiler,toggleDarkMode,loggedInAs);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }
                                break;
                            }
                            case "chat-gui": {
                                List<Component> components = List.of(chatArea,messageInput,sendMessageButton);
                                messageInput.requestFocus();
                                messageInput.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                                messageInput.setCaretColor(Color.WHITE);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }
                                break;
                            }
                            case "server-selector": {
                                List<Component> components = List.of(serverSelectorLabel,serverGuiMenuButton,serverScrollPane,serverGuiJoinServerButton,serverGuiAddServer,serverGuiSearchLocalServersButton,serverConnectionInfo,serverGuiDeleteServer,loggedInAs);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }

                                if(forceUpdate){
                                    forceUpdate = false;

                                    int selectedIndex = Math.max(serversJList.getSelectedIndex(),0);

                                    int len = serverList.length();

                                    String[] data = new String[len];

                                    for (int i = 0; i < len; i++) {
                                        try{data[i] = (serverList.getString(i));} catch (Exception e){}
                                    }

                                    serversJList.setListData(data);
                                    serversJList.setSelectedIndex(selectedIndex);

                                    lastUpdate = System.currentTimeMillis();
                                }
                                break;
                            }
                            case "server-main": {
                                List<Component> components = List.of(statsField,serverConnectedTo,joinLobby,joinResult,createLobby,codeField,serverMainDisconnectButton,lobbiesScrollPane,loggedInAs);
                                serverConnectedTo.setText("Server connected to: " + serversJList.getSelectedValue());
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }
                                /*if(codeField.getText() != null && codeField.getText().length() == 5){
                                    joinLobby.setEnabled(true);
                                    createLobby.setEnabled(false);
                                } else {
                                    joinLobby.setEnabled(false);
                                    createLobby.setEnabled(true);
                                }*/
                                break;
                            }
                            case "game-lobby": {
                                List<Component> components = List.of(lobbyDecks,lobbyStacking,lobbySevenSwap,lobbyJumpIn,lobbyCardsPerPlayer,lobbySettingsLabel,lobbyCode,lobbyPlayersScrollPane,lobbyStart,leaveLobby,lobbyIngameChat,loggedInAs);
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

                                drawButton.setEnabled(canDraw);
                                skipButton.setEnabled(canSkip);
                                unoButton.setEnabled(canUNO);

                                drawButton.setVisible(true);
                                skipButton.setVisible(true);
                                unoButton.setVisible(true);
                                nextUpLabel.setVisible(true);
                                backButton.setVisible(false);
                                leaveGameButton.setVisible(true);

                                Kernel dKernel = new Kernel(1, 1, new float[]{1f});
                                ConvolveOp dconvolutionOp = new ConvolveOp(dKernel, ConvolveOp.EDGE_NO_OP, null);
                                image2 = dconvolutionOp.filter(finalImage, null);

                                /**
                                 * TODO: Accelerate copy of finalImage
                                 */

                                Graphics2D g = image2.createGraphics();

                                BufferedImage card = getCard(lastCardPut);

                                g.drawImage(card,null,((frame.getWidth() - card.getWidth())/2) - 100,(frame.getHeight() - card.getHeight())/2 - 200);

                                if(!deck.isEmpty()){
                                    int spacing = (draw.getWidth() - 400) / deck.size();

                                    Point p = draw.getMousePosition();

                                    boolean shift = false;

                                    if(p != null){
                                        int posX = p.x -50;
                                        indexSelected = posX / spacing;
                                        if(indexSelected > deck.size()) indexSelected = deck.size()-1;
                                        if(p.y > frame.getHeight() - 300 && p.y < frame.getHeight() - 100){
                                            shift = true;
                                        } else {
                                            indexSelected = -1;
                                        }
                                        boolean hovers = p.y > 50 && p.y < 90;
                                    } else {
                                        indexSelected = -1;
                                    }
                                    int x = 0;
                                    int i = 0;

                                    for(Card c : deck){
                                        card = getCard(c);

                                        int y = 0;
                                        if(i == indexSelected && shift){
                                            y = 100;
                                        }
                                        g.drawImage(card,null,x+50,(frame.getHeight() - 300 - y));
                                        i++;
                                        x+=spacing;
                                    }
                                }
                                if(!actionMessages.isEmpty()){
                                    int MAX_AGE_MS = 8000;
                                    Iterator<ActionMessage> iterator = actionMessages.iterator();
                                    int y0 = 20;
                                    g.setFont(new Font("Arial",Font.BOLD,15));
                                    while (iterator.hasNext()){
                                        ActionMessage msg = iterator.next();
                                        if(System.currentTimeMillis()-msg.getAge() > MAX_AGE_MS){
                                            iterator.remove();
                                        } else {
                                            double age = (System.currentTimeMillis()-msg.getAge());
                                            age-=MAX_AGE_MS-1000;
                                            double alpha = (double) ((age/ 1000.0) * 255.0);
                                            if(alpha<0) alpha = 0; else if(alpha >255) alpha = 255;
                                            g.setColor(new Color(255,0,0, (int) (255.0-alpha)));
                                            if(msg.getOffset() > 0){
                                                msg.setOffset(msg.getOffset() - 1);
                                            }
                                            g.drawString(msg.getText(),width-450,y0+ msg.getOffset());
                                            y0+=20;
                                        }
                                    }
                                }

                                g.dispose();

                                BufferedImage finalImage1 = image2;
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        draw.setIcon(new ImageIcon(finalImage1));
                                    }
                                });
                                t.start();
                                break;
                            }
                            case "game-main-select-color": {
                                List<Component> components = List.of(draw);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }

                                backButton.setVisible(false);

                                image2 = new BufferedImage(frame.getWidth(), frame.getHeight(), 1);

                                Graphics2D g = image2.createGraphics();

                                g.drawImage(blurredBackground,null,-2,-2);

                                if(!deck.isEmpty()){

                                    BufferedImage card;
                                    int spacing = (frame.getWidth() - 200) / deck.size();

                                    boolean shift = false;

                                    int x = 0;
                                    int i = 0;

                                    Kernel dKernel = new Kernel(1, 1, darkenKernel);
                                    ConvolveOp dconvolutionOp = new ConvolveOp(dKernel, ConvolveOp.EDGE_NO_OP, null);

                                    for(Card c : deck){
                                        card = getCard(c);
                                        card = dconvolutionOp.filter(card, null);
                                        int y = 0;
                                        g.drawImage(card,null,x+50,(frame.getHeight() - 300 - y));
                                        i++;
                                        x+=spacing;
                                    }
                                }

                                g.setFont(new Font("Arial", Font.BOLD,50));
                                FontMetrics fm = g.getFontMetrics(g.getFont());
                                g.drawString("Choose Color",(width-fm.stringWidth("Choose Color")) / 2,200-150);

                                Point p = draw.getMousePosition();

                                int selected = 0;

                                if(p != null && BoundsCheck.within(p.getX(),p.getY(),200,200,128,192)){
                                    selected = 1;
                                }
                                if(p != null && BoundsCheck.within(p.getX(),p.getY(),440,200,128,192)){
                                    selected = 2;
                                }
                                if(p != null && BoundsCheck.within(p.getX(),p.getY(),680,200,128,192)){
                                    selected = 3;
                                }
                                if(p != null && BoundsCheck.within(p.getX(),p.getY(),920,200,128,192)){
                                    selected = 4;
                                }

                                if(selected == 1) g.drawImage(Util.resize(getCard(new Card(RED,ZERO)),148,212),null,190,340-150); else g.drawImage(getCard(new Card(RED,ZERO)),null,200,350-150);
                                if(selected == 2) g.drawImage(Util.resize(getCard(new Card(GREEN,ZERO)),148,212),null,430,340-150); else g.drawImage(getCard(new Card(GREEN,ZERO)),null,440,350-150);
                                if(selected == 3) g.drawImage(Util.resize(getCard(new Card(BLUE,ZERO)),148,212),null,670,340-150); else g.drawImage(getCard(new Card(BLUE,ZERO)),null,680,350-150);
                                if(selected == 4) g.drawImage(Util.resize(getCard(new Card(YELLOW,ZERO)),148,212),null,910,340-150); else g.drawImage(getCard(new Card(YELLOW,ZERO)),null,920,350-150);

                                g.dispose();

                                drawButton.setVisible(false);
                                skipButton.setVisible(false);
                                unoButton.setVisible(false);
                                nextUpLabel.setVisible(false);
                                backButton.setVisible(false);
                                leaveGameButton.setVisible(false);

                                draw.setIcon(new ImageIcon(image2));
                                break;
                            }
                            case "game-main-game-end": {
                                List<Component> components = List.of(draw);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }

                                backButton.setVisible(true);
                                drawButton.setVisible(false);
                                skipButton.setVisible(false);
                                unoButton.setVisible(false);
                                nextUpLabel.setVisible(false);
                                leaveGameButton.setVisible(false);

                                image2 = new BufferedImage(frame.getWidth(), frame.getHeight(), 1);

                                Graphics2D g = image2.createGraphics();

                                g.drawImage(blurredBackground,null,-2,-2);

                                g.setFont(new Font("Arial", Font.BOLD,80));
                                FontMetrics fm = g.getFontMetrics(g.getFont());
                                g.drawString("Game Ended",(width-fm.stringWidth("Game Ended")) / 2,100);

                                int y0 = 90;

                                g.setFont(new Font("Arial", Font.BOLD,60));
                                fm = g.getFontMetrics(g.getFont());

                                g.drawString("Winner: " + winner,(width-fm.stringWidth("Winner: " + winner)) / 2,100 + y0);

                                y0+=30;

                                if(!leaderboard.isEmpty()) g.setFont(new Font("Arial", Font.PLAIN,Math.min(270/leaderboard.size(),40)));
                                fm = g.getFontMetrics(g.getFont());

                                for(String s : leaderboard) {
                                    y0+=g.getFont().getSize()+5;
                                    g.drawString(s, (width-fm.stringWidth(s))/2, 100 + y0);
                                }

                                g.setFont(new Font("Arial", Font.BOLD,30));
                                fm = g.getFontMetrics(g.getFont());

                                //g.drawString("Press SPACE to continue",(width-fm.stringWidth("Press SPACE to continue")) / 2,600);


                                g.dispose();

                                drawButton.setVisible(false);
                                skipButton.setVisible(false);
                                unoButton.setVisible(false);
                                nextUpLabel.setVisible(false);

                                draw.setIcon(new ImageIcon(image2));
                                break;
                            }
                            case "manage-account" :{
                                List<Component> components = List.of(draw,loggedInAs);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }
                                break;
                            }
                            case "create-account" : {
                                List<Component> components = List.of(switchToLogin, createAccountTitleLabel, createAccountUsername, createAccountDisplayName, createAccountPasswordField, createAccountConfirmPassword, createAccountCreateButton, createAccountBackButton, createAccountLabel,loggedInAs);
                                for (Component c : panel.getComponents()) {
                                    c.setVisible(components.contains(c));
                                }
                                break;
                            }
                            case "login-account" :{
                                List<Component> components = List.of(switchToCreate,loginAccountTitleLabel, loginAccountUsername, loginAccountPasswordField, loginAccountLoginButton, loginAccountBackButton, loginAccountLabel,loggedInAs);
                                for(Component c : panel.getComponents()){
                                    c.setVisible(components.contains(c));
                                }
                                break;
                            }
                            default: {
                                System.out.println("Invalid gui " + gui);
                            }
                        }
                        if(System.currentTimeMillis() - lastUpdateFPS >= 1000){
                            frame.setTitle(baseTitle + " ping " + ping[0] + " ms " + frames + " FPS");
                            lastUpdateFPS = System.currentTimeMillis();
                            frames = 0;
                            //frame.setIconImage(image2);
                        }
                        frames++;
                        long renderTime = (System.currentTimeMillis())-renderStart;
                        /*if(renderTime < minMS){
                            try {
                                Thread.sleep(minMS-renderTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }*/
                        if(loadingGUI[0] != null){
                            loadingGUI[0].increaseValue();
                            loadingGUI[0].dispose();
                            loadingGUI[0] = null;
                            frame.setVisible(true);
                        }
                } catch (Exception e){

                }
            }
        });
        t.start();
        final boolean[] scheduled_disconnect = {false};
        final long[] lastPingUpdate = {System.currentTimeMillis()};
        Object LOCK = new Object();
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object packet) {
                synchronized (LOCK){
                    String clas = packet.getClass().toString();
                    if(packet instanceof DisconnectPacket p){
                        scheduled_disconnect[0] = true;
                        if(p.getCode() == 200) serverConnectionInfo.setForeground(Color.GREEN); else serverConnectionInfo.setForeground(Color.RED);
                        serverConnectionInfo.setText(p.getDisconnectReason());
                        disconnect();
                        return;
                    }
                    if(packet instanceof StatsPacket p){
                        String stats = "<html>Games Played: " + p.getGamesPlayed() + "<br>Games Lost: " + (p.getGamesPlayed()-p.getGamesWon()) + "<br>Games Won: " + p.getGamesWon() + "<br>+4 Placed: " + p.getPlaced4() + "</html>";
                        statsField.setText(stats);
                    }
                    if(packet instanceof LoginSuccessPacket){
                        gui = "server-main";
                    }
                    if(packet instanceof LobbyJoinResultPacket p){
                        joinResult.setText(p.getMsg());
                        if(p.getCode() == 200) {
                            joinResult.setForeground(Color.GREEN);
                            gui = "game-lobby";
                        } else {
                            joinResult.setForeground(Color.RED);
                            gui = "server-main";
                        }
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
                            lobbyIngameChat.setEnabled(p.isOwner());

                            /*lobbyStacking.setEnabled(false);
                            lobbySevenSwap.setEnabled(false);
                            lobbyJumpIn.setEnabled(false);
                            lobbyIngameChat.setEnabled(false);*/



                            if(!p.isOwner()) lobbyCardsPerPlayer.setText(p.getCardsPerPlayer() + "");
                            if(!p.isOwner()) lobbyDecks.setText(p.getDecks() + "");
                            if(!p.isOwner()) lobbyStacking.setSelected(p.isStacking());
                            if(!p.isOwner()) lobbySevenSwap.setSelected(p.isSevenSwap());
                            if(!p.isOwner()) lobbyJumpIn.setSelected(p.isJumpIn());
                            if(!p.isOwner()) lobbyIngameChat.setSelected(p.isChat());

                            Thread.sleep(100);
                            if(p.isOwner()){
                                sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected(),lobbyIngameChat.isSelected()));
                            } /*else sendObject(new UpdateLobbyPacket(0,0, lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));*/
                        }catch (Exception e){
                            sendObject(new UpdateLobbyPacket(7,2, false, false, false,false));
                            //e.printStackTrace();
                        }


                    }
                    if(packet instanceof Ping p){
                        if(System.currentTimeMillis() - lastPingUpdate[0] > 1000) {
                            ping[0] = System.currentTimeMillis() - p.getTime();
                            lastPingUpdate[0] = System.currentTimeMillis();
                        }
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    sendObject(new Ping(System.currentTimeMillis()));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        thread.start();
                    }
                    if(packet instanceof LobbiesPacket p){
                        lobbies.setListData(p.getLobbies());
                    }
                    if(packet instanceof GameStartPacket p){
                        gui = "game-main";
                    }
                    if(packet instanceof ActionPacket p){
                        actionMessages.add(new ActionMessage(p.getAction(),0,System.currentTimeMillis()));
                    }
                    if(packet instanceof ProfilerUpdate p){
                        //System.out.println(p);
                    }
                    if(packet instanceof ChatPacket p){
                        chatMessages.add(0,p.getMessage());

                        String text = "<html><h1>";

                        for(String msg : chatMessages){
                            text+=msg+"<br>";
                        }

                        text+="</h1></html>";
                        chatArea.setText(text);
                    }
                    if(packet instanceof NewConsoleLinePacket p){
                        System.out.println("[Server Console]" + p);
                    }
                    if(packet instanceof GameEndPacket p){
                        System.out.println("Game end");
                        winner = p.getWinner();
                        leaderboard = p.getPlacement();
                        gui = "game-main-game-end";
                    }
                    if(packet instanceof GameDataPacket p){
                        canSkip = p.isCanSkip();
                        canDraw = p.isCanDraw();
                        canUNO = p.isCanUNO();

                        lastCardPut = p.getLastPlaced();
                        deck = p.getOwnDeck();

                        int spot = 1;

                        String nextPlayersString = "<html>Current turn: ";
                        if(p.isTurn()){
                            nextPlayersString+="YOU<br>";
                        } else {
                            nextPlayersString+=p.getNextPlayers().get(0)+"<br>";
                        }

                        for(String s : p.getNextPlayers()){
                            nextPlayersString+=spot + ". " +s+"<br>";
                            spot++;
                        }
                        nextPlayersString+="</html>";

                        nextUpLabel.setText(nextPlayersString);
                    }
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
            public void onConnectionLostEvent(String reason) {
                gui = "server-selector";
                if(scheduled_disconnect[0]){
                    scheduled_disconnect[0] = false;
                    return;
                }
                serverConnectionInfo.setText(reason);
                serverConnectionInfo.setForeground(Color.RED);
            }
        });
    }
    public static HashMap<String, BufferedImage> buffer = new HashMap<>();

    public static BufferedImage getCard(Card c){
        if(buffer.containsKey(c.getExact() + "-" + customTexture) && !gui.contains("settings")){
            return buffer.get(c.getExact() + "-" + customTexture);
        }
        String filename = "" + customTexture;

        if(c.getColor().equals(SPECIAL) && c.getOverrideColor() == null){
            filename += "/textures/SPECIAL/" + c.getNum() + ".png";
        } else if (c.getColor().equals(SPECIAL) && c.getOverrideColor() != null){
            filename += "/textures/" + c.getOverrideColor() + "/" + c.getNum().name() + ".png";
        } else if(!c.getColor().equals(SPECIAL)) {
            filename += "/textures/" + c.getColor().name() + "/" + c.getNum().name() + ".png";
        } else {
            System.out.println("Broken Card " + c);
        }
        try {
            if(customTexture.isEmpty()) {
                BufferedImage finalImage = ImageIO.read(GUI.class.getResource(filename));
                buffer.put(c.getExact() + "-" + customTexture, finalImage);
                return finalImage;
            } else if(!new File(filename).exists()){
                BufferedImage finalImage = ImageIO.read(GUI.class.getResource(filename.substring((customTexture).length())));
                buffer.put(c.getExact() + "-" + customTexture, finalImage);
                return finalImage;
            }else {
                BufferedImage finalImage = ImageIO.read(new File(filename));
                buffer.put(c.getExact() + "-" + customTexture, finalImage);
                return Util.resize(finalImage,128,192);
            }
        } catch (Exception e) {
            System.out.println("Couldnt read " + filename);
        }
        return cards.getSubimage(0,0,128,192);
    }
    public static BufferedImage toGrayscale(BufferedImage image, float grayscaleFactor) {
        if (grayscaleFactor < 0.0f || grayscaleFactor > 1.0f) {
            throw new IllegalArgumentException("Grayscale factor must be between 0.0 and 1.0");
        }

        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        // Iterate over each pixel in the original image, converting it to grayscale and setting the corresponding pixel in the grayscale image
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = new Color(image.getRGB(j, i));
                grayscaleImage.setRGB(j, i, new Color(color.getRed(), color.getGreen(), color.getBlue(), 200).getRGB());
            }
        }
        return grayscaleImage;
    }
}