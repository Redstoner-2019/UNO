package me.redstoner2019.guiapi;

import me.redstoner2019.guiapi.design.Design;
import me.redstoner2019.guiapi.design.Setting;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public abstract class GUI extends JPanel {
    public HashMap<Component, Setting> settingHashMap = new HashMap<>();
    public abstract String getGUIName();
    public void update(BoundsCreator boundsCreator){
        for(Component c : settingHashMap.keySet()){
            Setting setting = settingHashMap.get(c);
            if(setting == null) continue;
            c.setBounds(boundsCreator.bounds(setting));
            //if(c instanceof JLabel) Design.setFontSize(c,c.getHeight());
        }
        Design.update();
    }
    public abstract GUI init();
    public void register(Component c, Setting setting){
        settingHashMap.put(c,setting);
        add(c);
        Design.register(c);
    }
    public void registerNoDesign(Component c, Setting setting){
        settingHashMap.put(c,setting);
        add(c);
    }
}

