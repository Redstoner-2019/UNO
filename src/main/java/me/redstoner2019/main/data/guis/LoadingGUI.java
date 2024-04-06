package me.redstoner2019.main.data.guis;

import javax.swing.*;
import java.awt.*;

public class LoadingGUI<d> {

    private JFrame frame;
    private int width = 500;
    private int height = 100;
    private JProgressBar progressBar = new JProgressBar();
    public void setMax(int max){
        this.progressBar.setMaximum(max);
    }
    public void setValue(int value){
        this.progressBar.setValue(value);
        this.progressBar.setString(value + "/" + progressBar.getMaximum());
        this.progressBar.setStringPainted(true);
    }
    public void increaseValue(){
        this.progressBar.setValue(progressBar.getValue()+1);
        this.progressBar.setString(progressBar.getValue() + "/" + progressBar.getMaximum());
        this.progressBar.setStringPainted(true);
    }
    public void dispose(){
        frame.dispose();
    }
    public LoadingGUI() {
        initialize();
    }
    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("Loading...");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        progressBar.setBounds((frame.getWidth() - 400) / 2,(frame.getHeight() - 80) / 2,400,40);
        panel.add(progressBar);
    }
}
