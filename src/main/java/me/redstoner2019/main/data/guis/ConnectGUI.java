package me.redstoner2019.main.data.guis;

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

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
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
                Util.writeStringToFile(Util.prettyJSON(object.toString()),new File("client.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            object = new JSONObject(Util.readFile(new File("client.properties")));
        } catch (Exception e) {
            System.out.println("fixing");
            object.put("ip","172.20.150.24");
            object.put("username","lukas");
            object.put("password","test");
            System.out.println(new File("client.properties").delete());
            try {
                new File("client.properties").createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("Server Selector" + " - " + Main.VERSION);
        frame.setLocationRelativeTo(null);
        System.out.println(Main.VERSION);

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

        if(object.has("username")) username.setText(object.getString("username"));
        if(object.has("password")) passwordField.setText(object.getString("password"));
        if(object.has("displayName")) displayNameField.setText(object.getString("displayName"));
        if(object.has("ip")) ipAddress.setText(object.getString("ip"));
        if(setUsername != null) username.setText(setUsername);

        username.setBounds(680,140,200,20);
        passwordField.setBounds(680,180,200,20);
        displayNameField.setBounds(680,220,200,20);
        ipAddress.setBounds(680,260,200,20);
        loginResult.setBounds(680,300,200,20);

        createChangeLabel.setBounds(930,360,200,20);
        accountUsername.setBounds(930,400,200,20);
        displayname.setBounds(930,440,200,20);
        password.setBounds(930,480,200,20);
        createAccountButton.setBounds(930,520,90,20);
        changeDisplayNameButton.setBounds(1040,520,90,20);

        usernameLabel.setBounds(680,400,80,20);
        displayNameLabel.setBounds(680,440,80,20);
        passwordLabel.setBounds(680,480,80,20);

        username.setToolTipText("Username");
        passwordField.setToolTipText("Password");
        displayNameField.setToolTipText("Displayname");
        ipAddress.setToolTipText("IP Address");

        panel.add(username);
        panel.add(passwordField);
        panel.add(displayNameField);
        panel.add(ipAddress);
        panel.add(loginResult);

        panel.add(usernameLabel);
        panel.add(displayNameLabel);
        panel.add(passwordLabel);

        panel.add(displayNameLabel);
        panel.add(accountUsername);
        panel.add(password);
        panel.add(displayname);
        panel.add(changeDisplayNameButton);
        panel.add(createAccountButton);

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
        button.setBounds(680,20,200,40);
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
                            throw new RuntimeException(ex);
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

        connect.setBounds(680,80,200,40);
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
                        try {
                            Util.writeStringToFile(Util.prettyJSON(finalObject.toString()),new File("client.properties"));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
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
