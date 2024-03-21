package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.data.packets.SetColorPacket;
import me.redstoner2019.main.serverstuff.ClientMain;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChooseColorPopup extends JDialog {
    public ChooseColorPopup(JFrame parent) {
        super(parent,"Choose Color",true);

        // Create text fields
        JList<String> list = new JList<>();
        list.setListData(new String[]{"RED","GREEN","BLUE","YELLOW"});

        // Create panel and add components
        JPanel panel = new JPanel();
        panel.add(list);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ClientMain.sendObject(new SetColorPacket(list.getSelectedValue()));
                dispose();
            }
        });
        // Set frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }
}
