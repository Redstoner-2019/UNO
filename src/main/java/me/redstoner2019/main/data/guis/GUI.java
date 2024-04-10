package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.BoundsCheck;
import me.redstoner2019.main.CustomButton;
import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.CardColor;
import me.redstoner2019.main.data.CardType;
import me.redstoner2019.main.data.packets.gamepackets.*;
import me.redstoner2019.main.data.packets.generalpackets.ProfilerUpdate;
import me.redstoner2019.main.data.packets.lobbypackets.*;
import me.redstoner2019.main.data.packets.loginpackets.*;
import me.redstoner2019.serverhandling.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.zip.ZipFile;

import static me.redstoner2019.main.data.CardColor.*;
import static me.redstoner2019.main.data.CardType.*;

public class GUI extends Client {
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
    public GUI() throws Exception {
        initialize();
    }
    private void initialize() throws Exception {
        final LoadingGUI[] loadingGUI = {new LoadingGUI()};
        loadingGUI[0].setMax(10);
        loadingGUI[0].setValue(0);
        //gui = "game-main-game-end";


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
        frame.setBackground(Color.WHITE);
        frame.setForeground(Color.WHITE);

        JPanel pan = new JPanel();
        frame.getContentPane().add(pan, BorderLayout.CENTER);
        pan.setLayout(null);

        JLabel panel = new JLabel();
        panel.setBounds(0,0,width,height);
        pan.add(panel);

        final BufferedImage[] image = {new BufferedImage(1920, 1080, 1)};

        try {
            System.out.println("Loading texture");
            image[0] = ImageIO.read(GUI.class.getResource("/background.png"));
            System.out.println("Loaded texture");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Textures loaded");

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
            Util.writeStringToFile(Util.prettyJSON(freshData.toString()),new File("client.properties"));
        }

        clientData = new JSONObject(Util.readFile(new File("client.properties")));

        if(Main.password.isEmpty()) Main.password = clientData.getString("password");
        if(Main.username.isEmpty()) Main.username = clientData.getString("username");

        JSONArray serverList;
        if(clientData.has("servers")) serverList = clientData.getJSONArray("servers"); else serverList = new JSONArray();
        if(clientData.has("dark-mode")) darkMode = clientData.getBoolean("dark-mode");
        String texturePack = "";
        if(clientData.has("texturepack")) texturePack = clientData.getString("texturepack");

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

        /**
         * Main Menu
         */
        JLabel mainMenuTitleLabel = new JLabel("UNO");
        JLabel mainMenuSubTitleLabel = new JLabel("Version " + Main.getVersion());
        JButton mainMenuPlayButton = new JButton("PLAY");
        JButton mainMenuSettingsButton = new JButton("SETTINGS");

        String latestVersion = Main.getLatestVersion();
        if(!latestVersion.equals(Main.getVersion())){
            mainMenuSubTitleLabel.setText(mainMenuSubTitleLabel.getText() + " (Latest on github " + latestVersion + ")");
        }

        mainMenuTitleLabel.setBounds((frame.getWidth()-400)/2,30,400,80);
        mainMenuSubTitleLabel.setBounds((frame.getWidth()-800)/2,100,800,50);
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

        settingsTitleLabel.setBounds((frame.getWidth()-400)/2,30,400,80);
        settingsSubTitleLabel.setBounds((frame.getWidth()-300)/2,100,300,50);
        settingsMainMenuButton.setBounds((frame.getWidth()-300)/2,frame.getHeight()-100,300,40);
        startPerformanceProfiler.setBounds((frame.getWidth()-300)/2,150,300,40);
        toggleDarkMode.setBounds((frame.getWidth()-300)/2,200,300,40);
        resourcePackTextField.setBounds((frame.getWidth()-300)/2,300,300,40);
        applyTexturepackButton.setBounds((frame.getWidth()-300)/2,350,300,40);
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

        cardPreview.setIcon(new ImageIcon(getCard(new Card(RED,CardType.ZERO))));
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
        JButton serverGuiCreateAccount = new JButton("CREATE ACCOUNT");
        JButton serverGuiManageAccount = new JButton("MANAGE ACCOUNT");
        JButton serverGuiSearchLocalServersButton = new JButton("SEARCH LOCAL");

        serverGuiManageAccount.setEnabled(false);

        JList<String> serversJList = new JList<>();
        JScrollPane serverScrollPane = new JScrollPane(serversJList);
        JTextArea serverConnectionInfo = new JTextArea();
        JTextField usernameField = new JTextField(Main.username);
        JPasswordField passwordField = new JPasswordField(Main.password);

        serverSelectorLabel.setBounds(frame.getWidth()-500,50,500,40);

        serverGuiAddServer.setBounds(50,frame.getHeight()-200,200,40);
        serverGuiSearchLocalServersButton.setBounds(580,frame.getHeight()-200,200,40);
        serverGuiMenuButton.setBounds(580,frame.getHeight()-130,200,40);
        serverGuiJoinServerButton.setBounds(frame.getWidth()-450,frame.getHeight()-200,400,110);
        serverGuiDeleteServer.setBounds(50,frame.getHeight()-130,200,40);
        serverGuiCreateAccount.setBounds(315,frame.getHeight()-130,200,40);
        serverGuiManageAccount.setBounds(315,frame.getHeight()-200,200,40);

        serverScrollPane.setBounds(50,50,frame.getWidth()-550,height-280);
        serverConnectionInfo.setBounds(frame.getWidth()-450,100,400,250);
        usernameField.setBounds(frame.getWidth()-450,frame.getHeight()-340,400,40);
        passwordField.setBounds(frame.getWidth()-450,frame.getHeight()-270,400,40);

        serverSelectorLabel.setHorizontalAlignment(JLabel.CENTER);
        serverSelectorLabel.setVerticalAlignment(JLabel.CENTER);
        serverConnectionInfo.setEditable(false);
        serverConnectionInfo.setLineWrap(true);

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
                forceUpdate = true;
            }
        });

        serverGuiJoinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect(serversJList.getSelectedValue(),8008);
                LoginPacket o = new LoginPacket(usernameField.getText(),passwordField.getText(),null);
                sendObject(o);
                sendObject(new Ping(System.currentTimeMillis()));
                sendObject(new RequestLobbiesPacket());
                clientData.put("username",usernameField.getText());
                clientData.put("password",passwordField.getText());
                clientData.put("servers",serverList);
                try {
                    Util.writeStringToFile(Util.prettyJSON(clientData.toString()),new File("client.properties"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
                if(serverList.isEmpty()) serverList.put("localhost");
                serversJList.setSelectedIndex(0);
                forceUpdate = true;
            }
        });
        serverGuiCreateAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "create-account";
            }
        });
        serverGuiManageAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "manage-account";
                System.out.println("gui");
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
        panel.add(serverGuiDeleteServer);
        panel.add(serverGuiCreateAccount);
        panel.add(serverGuiManageAccount);
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

        lobbyStacking.setOpaque(true);

        PlainDocument doc1 = (PlainDocument) lobbyCardsPerPlayer.getDocument();
        doc1.setDocumentFilter(new IntFilter());

        PlainDocument doc2 = (PlainDocument) lobbyDecks.getDocument();
        doc2.setDocumentFilter(new IntFilter());

        lobbyCode.setBounds(0,10,frame.getWidth(),40);
        lobbyPlayersScrollPane.setBounds(50,50,(frame.getWidth()/2)-50,frame.getHeight()-140);
        lobbyStart.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-140,400,50);
        leaveLobby.setBounds((frame.getWidth()/2) + 100,frame.getHeight()-220,400,50);
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
        leaveLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendObject(new LeaveLobbyPacket());
                gui = "server-main";
            }
        });

        /*lobbyDecks.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("Test");
                //sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));
            }
        });*/

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
        loadingGUI[0].increaseValue();

        /**
         * game-main
         */

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
                sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));
            }
        });
        draw.add(backButton);
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

        int yOffset = 100;

        createAccountTitleLabel.setBounds(((width-1000)/2),50,1000,50);
        createAccountUsername.setBounds(((width-400)/2) + 210,100 + yOffset,190,30);
        createAccountDisplayName.setBounds(((width-400)/2) + 210,140 + yOffset,190,30);
        createAccountPasswordField.setBounds(((width-400)/2) + 210,180 + yOffset,190,30);
        createAccountConfirmPassword.setBounds(((width-400)/2) + 210,220 + yOffset,190,30);
        createAccountCreateButton.setBounds(((width-400)/2),260 + yOffset,400,30);
        createAccountBackButton.setBounds(((width-400)/2),300 + yOffset,400,30);
        createAccountLabel.setBounds(((width-400)/2),100 + yOffset,190,150);

        createAccountTitleLabel.setHorizontalAlignment(JLabel.CENTER);
        createAccountTitleLabel.setVerticalAlignment(JLabel.CENTER);

        createAccountTitleLabel.setFont(new Font("Arial", Font.PLAIN,40));
        createAccountLabel.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountUsername.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountDisplayName.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountPasswordField.setFont(new Font("Arial", Font.PLAIN,16));
        createAccountConfirmPassword.setFont(new Font("Arial", Font.PLAIN,16));

        createAccountBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui = "server-selector";
            }
        });
        createAccountCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(createAccountPasswordField.getText().equals(createAccountConfirmPassword.getText())) {
                    connect(serversJList.getSelectedValue(),8008);
                    sendObject(new CreateAccountPacket(createAccountUsername.getText(), createAccountPasswordField.getText(), createAccountDisplayName.getText()));
                    createAccountTitleLabel.setText("CREATE ACCOUNT (Success)");
                } else {
                    createAccountTitleLabel.setText("CREATE ACCOUNT (Passwords dont match)");
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
                while (true) {
                    long renderStart = System.currentTimeMillis();

                    if(reloadTextures){
                        blurredBackground = convolutionOp.filter(image[0], null);
                        previousMode = !darkMode;
                        reloadTextures = false;
                    }

                    if(previousMode != darkMode){
                        if(darkMode){
                            for(Component c : panel.getComponents()){
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
                            List<Component> components = List.of(mainMenuTitleLabel,mainMenuPlayButton,mainMenuSettingsButton,mainMenuSubTitleLabel);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "settings-gui": {
                            List<Component> components = List.of(cardSwitchRight,cardSwitchLeft,cardPreview,texturePackLoadResult,applyTexturepackButton,resourcePackTextField,settingsSubTitleLabel,settingsTitleLabel,settingsMainMenuButton,startPerformanceProfiler,toggleDarkMode);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "server-selector": {
                            List<Component> components = List.of(serverSelectorLabel,serverGuiMenuButton,serverScrollPane,serverGuiJoinServerButton,serverGuiAddServer,serverGuiSearchLocalServersButton,serverConnectionInfo,usernameField,passwordField,serverGuiDeleteServer,serverGuiCreateAccount,serverGuiManageAccount);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }

                            if(forceUpdate){
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
                            List<Component> components = List.of(statsField,serverConnectedTo,joinLobby,joinResult,createLobby,codeField,serverMainDisconnectButton,lobbiesScrollPane);
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
                            List<Component> components = List.of(lobbyDecks,lobbyStacking,lobbySevenSwap,lobbyJumpIn,lobbyCardsPerPlayer,lobbySettingsLabel,lobbyCode,lobbyPlayersScrollPane,lobbyStart,leaveLobby);
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
                                int spacing = (frame.getWidth() - 200) / deck.size();

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
                            List<Component> components = List.of(draw);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }
                            break;
                        }
                        case "create-account" :
                            List<Component> components = List.of(createAccountTitleLabel, createAccountUsername, createAccountDisplayName, createAccountPasswordField, createAccountConfirmPassword, createAccountCreateButton, createAccountBackButton, createAccountLabel);
                            for(Component c : panel.getComponents()){
                                c.setVisible(components.contains(c));
                            }{
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
                        /*lobbyStacking.setEnabled(p.isOwner());
                        lobbySevenSwap.setEnabled(p.isOwner());
                        lobbyJumpIn.setEnabled(p.isOwner());*/

                            lobbyStacking.setEnabled(false);
                            lobbySevenSwap.setEnabled(false);
                            lobbyJumpIn.setEnabled(false);



                            if(!p.isOwner()) lobbyCardsPerPlayer.setText(p.getCardsPerPlayer() + "");
                            if(!p.isOwner()) lobbyDecks.setText(p.getDecks() + "");
                            //if(!p.isOwner()) lobbyStacking.setSelected(p.isStacking());
                            //if(!p.isOwner()) lobbySevenSwap.setSelected(p.isSevenSwap());
                            //if(!p.isOwner()) lobbyJumpIn.setSelected(p.isJumpIn());

                            Thread.sleep(100);
                            if(p.isOwner()){
                                sendObject(new UpdateLobbyPacket(Integer.parseInt(lobbyCardsPerPlayer.getText()),Integer.parseInt(lobbyDecks.getText()), lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));
                            } /*else sendObject(new UpdateLobbyPacket(0,0, lobbyStacking.isSelected(), lobbySevenSwap.isSelected(), lobbyJumpIn.isSelected()));*/
                        }catch (Exception e){
                            sendObject(new UpdateLobbyPacket(7,2, false, false, false));
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
                        System.out.println(Arrays.toString(p.getLobbies()));
                    }
                    if(packet instanceof GameStartPacket p){
                        System.out.println("Game start");
                        gui = "game-main";
                    }
                    if(packet instanceof ProfilerUpdate p){
                        System.out.println(p);
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

        System.out.println(customTexture);

        if(c.getColor().equals(SPECIAL) && c.getOverrideColor() == null){
            filename += "/textures/SPECIAL/" + c.getNum() + ".png";
        } else if (c.getColor().equals(SPECIAL) && c.getOverrideColor() != null){
            filename += "/textures/" + c.getOverrideColor() + "/" + c.getNum().name() + ".png";
        } else if(!c.getColor().equals(SPECIAL)) {
            filename += "/textures/" + c.getColor().name() + "/" + c.getNum().name() + ".png";
        } else {
            System.out.println("Broken Card " + c);
        }
        System.out.println(filename);
        try {
            if(customTexture.isEmpty()) {
                BufferedImage finalImage = ImageIO.read(GUI.class.getResource(filename));
                buffer.put(c.getExact() + "-" + customTexture, finalImage);
                return finalImage;
            } else if(!new File(filename).exists()){
                System.out.println(filename.substring((customTexture).length()));
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
        System.out.println("Default path " + filename.substring((customTexture).length()));
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