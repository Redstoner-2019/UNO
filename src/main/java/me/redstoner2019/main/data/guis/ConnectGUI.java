package me.redstoner2019.main.data.guis;

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
import java.util.List;

public class ConnectGUI<d> {

    public static JFrame frame;
    private int width = 1280;
    private int height = 720;
    public static JButton connect = new JButton("CONNECT");
    public static List<String> serverList = new ArrayList<>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        connect.setEnabled(true);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConnectGUI window = new ConnectGUI();
                    window.frame.setVisible(true);
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
                object.put("username","HelloWorld");
                Util.writeStringToFile(Util.prettyJSON(object.toString()),new File("client.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            object = new JSONObject(Util.readFile(new File("client.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("Server Selector");

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JTextField username = new JTextField(object.getString("username"));
        JTextField ipAddress = new JTextField(object.getString("ip"));

        username.setBounds(680,140,200,20);
        ipAddress.setBounds(680,180,200,20);

        panel.add(username);
        panel.add(ipAddress);

        JList<String> list = new JList<String>();
        list.setCellRenderer(new LabelListCellRenderer());

        list.add(new JLabel("Test"));

        JScrollPane IPscrollpane = new JScrollPane(list);
        IPscrollpane.setBounds(20,20,640,640);
        panel.add(IPscrollpane);

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
                        try {
                            Util.writeStringToFile(Util.prettyJSON(finalObject.toString()),new File("client.properties"));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        ClientMain.connect(ipAddress.getText(),username.getText());
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
