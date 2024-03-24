package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.data.packets.SetColorPacket;
import me.redstoner2019.main.serverstuff.ClientMain;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Vector;

class ColorName{
    public String colorName;
    public Color color;

    public ColorName(String colorName, Color color) {
        this.colorName = colorName;
        this.color = color;
    }
}

public class ChooseColorPopup extends JDialog {
    public static void main(String[] args) {
        new ChooseColorPopup(null);
    }
    public ChooseColorPopup(JFrame parent) {
        super(parent,"Choose Color",true);

        // Create text fields
        JList list = new JList(new Vector<ColorName>() {
            {
                add(new ColorName("RED", Color.RED));
                add(new ColorName("GREEN", Color.GREEN));
                add(new ColorName("ORANGE", Color.ORANGE));
                add(new ColorName("BLUE", Color.BLUE));
            }
        });
        list.setFont(new Font("Arial", Font.PLAIN,30));

        list.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ColorName) {
                    ColorName colorName = (ColorName) value;
                    setText(colorName.colorName);
                    setForeground(colorName.color);
                    if (isSelected) {
                        setForeground(getForeground().darker());
                    }
                } else {
                    setText("whatdiscolor?");
                }
                return c;
            }

        });

        // Create panel and add components
        JPanel panel = new JPanel();
        panel.add(list);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ClientMain.sendObject(new SetColorPacket(((ColorName)list.getSelectedValue()).colorName));
                dispose();
            }
        });
        // Set frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().add(panel);

        setPreferredSize(new Dimension(200,200));

        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }
}
