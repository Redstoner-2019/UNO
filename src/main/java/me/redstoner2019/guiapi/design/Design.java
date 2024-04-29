package me.redstoner2019.guiapi.design;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Design {
    public static List<Component> componentList = new ArrayList<>();
    public static DesignColor design = DesignColor.DARK;
    public static void update(){
        for(Component c : componentList){
            switch (design){
                case DARK -> {
                    c.setForeground(Color.WHITE);
                    c.setBackground(Color.DARK_GRAY);
                }
                case LIGHT -> {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                }
            }
        }
    }
    public static void register(Component c){
        componentList.add(c);
    }
    public static void centerText(JLabel c){
        c.setHorizontalAlignment(JLabel.CENTER);
        c.setVerticalAlignment(JLabel.CENTER);
    }
    public static void rightAlignment(JLabel c){
        c.setHorizontalAlignment(JLabel.RIGHT);
        c.setVerticalAlignment(JLabel.CENTER);
    }
    public static void setFontSize(Component c, int size){
        Font f = new Font(c.getFont().getFontName(),c.getFont().getStyle(),size);
        c.setFont(f);
    }
    public static void setFontStyle(Component c, int style){
        Font f = new Font(c.getFont().getFontName(),style,c.getFont().getSize());
        c.setFont(f);
    }
}
enum DesignColor{
    DARK,
    LIGHT
}

