package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.serverstuff.ClientMain;
import me.redstoner2019.serverhandling.Util;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class LoginPopup extends JDialog {
    private JTextField textField1;
    private JTextField textField2;
    private JButton confirmButton = new JButton("Connect");
    public void enableConnect(boolean enable){
        confirmButton.setEnabled(enable);
    }
    public LoginPopup(JFrame parent) {
        super(parent,"Connect",true);

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

        // Create text fields
        textField1 = new JTextField(object.getString("ip"), 20);
        textField2 = new JTextField(object.getString("username"), 20);

        // Create confirmation button
        JSONObject finalObject = object;
        confirmButton.addActionListener(e -> {
            String input1 = textField1.getText();
            String input2 = textField2.getText();
            confirmButton.setEnabled(false);
            finalObject.put("ip",input1);
            finalObject.put("username",input2);
            try {
                Util.writeStringToFile(Util.prettyJSON(finalObject.toString()),new File("client.properties"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ClientMain.connect(input1,input2,this);
            //JOptionPane.showMessageDialog(parent, "Input 1: " + input1 + "\nInput 2: " + input2);
        });

        // Create panel and add components
        JPanel panel = new JPanel();
        panel.add(new JLabel("IP:"));
        panel.add(textField1);
        panel.add(new JLabel("Username"));
        panel.add(textField2);
        panel.add(confirmButton);

        // Set frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }
}

