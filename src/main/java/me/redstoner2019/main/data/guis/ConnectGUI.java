package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.LoggerDump;
import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.packets.CreateAccountPacket;
import me.redstoner2019.main.serverstuff.ClientMain;
import me.redstoner2019.serverhandling.LocalNetworkScanner;
import me.redstoner2019.serverhandling.Util;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectGUI<d> {

    public static JFrame frame;
    private final int width = 1280;
    private final int height = 720;
    public static JButton connect = new JButton("CONNECT");
    public static List<String> serverList = new ArrayList<>();
    public static String setUsername = null;
    public static JLabel loginResult = new JLabel();
    public static JTextField customTexture = new JTextField();

    /**
     * Launch the application.
     */
    public static void main(String[] args) throws Exception {
        LoggerDump.initialize();
        if(args.length == 1) setUsername = args[0];
        connect.setEnabled(true);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConnectGUI window = new ConnectGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ConnectGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     * //@param <d>
     */
    private void initialize() {
        JSONObject object = new JSONObject();
        if(!new File("client.properties").exists()){
            try {
                new File("client.properties").createNewFile();
                object.put("ip","172.20.150.24");
                object.put("username","lukas");
                object.put("password","test");
                object.put("custom-texture","");
                Util.writeStringToFile(Util.prettyJSON(object.toString()),new File("client.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            object = new JSONObject(Util.readFile(new File("client.properties")));
            customTexture.setText(object.getString("custom-texture"));
        } catch (Exception e) {
            System.out.println("fixing");
            object.put("ip","172.20.150.24");
            object.put("username","lukas");
            object.put("password","test");
            object.put("custom-texture","");
            System.out.println(new File("client.properties").delete());
            try {
                new File("client.properties").createNewFile();
            } catch (IOException ex) {
                e.printStackTrace();
            }
        }

        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        System.out.println(Main.getVersion());
        frame.setTitle("Server Selector" + " - " + Main.getVersion());
        System.out.println(frame.getTitle());
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JSeparator separator = new JSeparator();
        separator.setOrientation(JSeparator.HORIZONTAL);
        separator.setBounds(680,340,560,20);
        panel.add(separator);

        /*JSeparator separator2 = new JSeparator();
        separator2.setOrientation(JSeparator.VERTICAL);
        separator2.setBounds(930,340,20,320);
        panel.add(separator2);*/

        JTextField username = new JTextField();
        if(setUsername != null) username.setText(setUsername);
        JPasswordField passwordField = new JPasswordField();
        JTextField displayNameField = new JTextField();
        JTextField ipAddress = new JTextField();

        JLabel createChangeLabel = new JLabel("Create Account");
        JTextField accountUsername = new JTextField();
        JTextField displayname = new JTextField();
        JPasswordField password = new JPasswordField();
        JButton createAccountButton = new JButton("Create");
        JButton changeDisplayNameButton = new JButton("Change");

        JLabel usernameLabel = new JLabel("Username");
        JLabel displayNameLabel = new JLabel("Nickname");
        JLabel passwordLabel = new JLabel("Password");
        JLabel customTextureLabel = new JLabel("Custom Texture");

        JLabel usernameLabelOrig = new JLabel("Username");
        JLabel displayNameLabelOrig = new JLabel("Nickname (optional)");
        JLabel passwordLabelOrig = new JLabel("Password");
        JLabel ipLabelOrig = new JLabel("IP");

        if(object.has("username")) username.setText(object.getString("username"));
        if(object.has("password")) passwordField.setText(object.getString("password"));
        if(object.has("displayName")) displayNameField.setText(object.getString("displayName"));
        if(object.has("ip")) ipAddress.setText(object.getString("ip"));
        if(setUsername != null) username.setText(setUsername);

        username.setBounds(890,140,190,20);
        passwordField.setBounds(890,180,190,20);
        displayNameField.setBounds(890,220,190,20);
        ipAddress.setBounds(890,260,190,20);

        usernameLabelOrig.setBounds(680,140,190,20);
        displayNameLabelOrig.setBounds(680,220,190,20);
        passwordLabelOrig.setBounds(680,180,190,20);
        ipLabelOrig.setBounds(680,260,190,20);

        loginResult.setBounds(680,300,400,40);

        createChangeLabel.setBounds(890,360,200,20);
        accountUsername.setBounds(890,400,200,20);
        displayname.setBounds(890,440,200,20);
        password.setBounds(890,480,200,20);
        createAccountButton.setBounds(890,520,90,20);
        changeDisplayNameButton.setBounds(1000,520,90,20);
        customTexture.setBounds(680,620,420,20);
        customTextureLabel.setBounds(680,600,400,20);

        usernameLabel.setBounds(680,400,80,20);
        displayNameLabel.setBounds(680,440,80,20);
        passwordLabel.setBounds(680,480,80,20);

        username.setToolTipText("Username");
        passwordField.setToolTipText("Password");
        displayNameField.setToolTipText("Displayname");
        ipAddress.setToolTipText("IP Address");
        customTexture.setToolTipText("File path to custom texture");

        panel.add(username);
        panel.add(passwordField);
        panel.add(displayNameField);
        panel.add(ipAddress);
        panel.add(loginResult);
        panel.add(customTexture);
        panel.add(customTextureLabel);

        panel.add(usernameLabel);
        panel.add(displayNameLabel);
        panel.add(passwordLabel);

        panel.add(displayNameLabel);
        panel.add(accountUsername);
        panel.add(password);
        panel.add(displayname);
        panel.add(changeDisplayNameButton);
        panel.add(createAccountButton);

        panel.add(usernameLabelOrig);
        panel.add(displayNameLabelOrig);
        panel.add(passwordLabelOrig);
        panel.add(ipLabelOrig);

        JList<String> list = new JList<String>();
        list.setCellRenderer(new LabelListCellRenderer());

        list.add(new JLabel("Test"));

        JScrollPane IPscrollpane = new JScrollPane(list);
        IPscrollpane.setBounds(20,20,640,640);
        panel.add(IPscrollpane);

        changeDisplayNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newDisplayName = displayname.getText();
                String passwordEntered = password.getText();
                String usernameEntered = accountUsername.getText();

                ClientMain.connect(ipAddress.getText(),8008);
                ClientMain.sendObject(new CreateAccountPacket(usernameEntered,newDisplayName,passwordEntered));
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newDisplayName = displayname.getText();
                String newPassword = password.getText();
                String newUsername = accountUsername.getText();

                ClientMain.connect(ipAddress.getText(),8008);
                ClientMain.sendObject(new CreateAccountPacket(newUsername,newDisplayName,newPassword));
            }
        });

        JButton button = new JButton("SCAN");
        button.setBounds(680,20,400,40);
        panel.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setText("Scanning...");
                button.setEnabled(false);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> ips = new ArrayList<>();
                        try {
                            LocalNetworkScanner.scan(serverList);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        String[] arr = new String[ips.size()];
                        for (int i = 0; i < arr.length; i++) {
                            arr[i] = ips.get(i);
                        }
                        list.setListData(arr);
                        button.setText("SCAN");
                        button.setEnabled(true);
                    }
                });
                t.start();
            }
        });

        connect.setBounds(680,80,400,40);
        panel.add(connect);
        JSONObject finalObject = object;
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connect.setEnabled(false);
                        connect.setText("CONNECTING...");
                        finalObject.put("ip",ipAddress.getText());
                        finalObject.put("username",username.getText());
                        finalObject.put("password",passwordField.getText());
                        finalObject.put("displayName",displayNameField.getText());
                        finalObject.put("custom-texture",customTexture.getText());
                        try {
                            Util.writeStringToFile(Util.prettyJSON(finalObject.toString()),new File("client.properties"));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        ClientMain.connect(ipAddress.getText(),username.getText(), passwordField.getText(),displayNameField.getText());
                        System.out.println("Connection Complete");
                    }
                });
                t.start();

            }
        });

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(list.getSelectedIndex() < 0) return;
                String ip = serverList.get(list.getSelectedIndex());
                ipAddress.setText(ip);
            }
        });

        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        frame.setVisible(true);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (frame.isVisible()){
                    String[] arr = new String[serverList.size()];
                    for (int i = 0; i < arr.length; i++) {
                        arr[i] = serverList.get(i);
                    }
                    list.setListData(arr);
                }
            }
        });
        t.start();
    }
}

class LabelListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBounds(0,0,label.getWidth(),40);
        label.setOpaque(true);
        return label;
    }
}
