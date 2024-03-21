package me.redstoner2019.main.data.guis;

import javax.swing.*;
import java.awt.*;

public class ConsoleGUI<d> {
    private JFrame frame;
    private int width = 700;
    private int height = 700;
    public static JTextArea area = new JTextArea();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConsoleGUI window = new ConsoleGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ConsoleGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("ConsoleGUI");

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JScrollPane sp = new JScrollPane(area);
        sp.setBounds(0,0,width,height);
        area.setEditable(false);
        panel.add(sp);

        area.setBackground(Color.DARK_GRAY);
        area.setForeground(Color.GREEN);
        area.setFont(new Font("Consolas",Font.PLAIN,15));

        sp.setAutoscrolls(true);
    }
}
