package me.redstoner2019.guiapi.editorgui;

import me.redstoner2019.guiapi.GUI;
import me.redstoner2019.guiapi.Util;
import me.redstoner2019.guiapi.design.Setting;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Menu extends GUI {
    public static JList<String> guis = new JList<>();
    public static JList<String> components = new JList<>();
    public static JSlider xPos = new JSlider(0,100);
    public static JSlider yPos = new JSlider(0,100);
    public static JSlider width = new JSlider(0,100);
    public static JSlider height = new JSlider(0,100);
    @Override
    public GUI init(String guiname) {
        register(guis,new Setting(0,0,.25,1,false,"DARK",null,null));
        register(components,new Setting(.26,0,.24,1,false,"DARK",null,null));
        register(xPos,new Setting(.51,0,.48,.05,false,"DARK",null,null));
        register(yPos,new Setting(.51,.05,.48,.05,false,"DARK",null,null));
        register(width,new Setting(.51,.1,.48,.05,false,"DARK",null,null));
        register(height,new Setting(.51,.15,.48,.05,false,"DARK",null,null));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int selectedIndex = guis.getSelectedIndex();
                    JSONObject jsonObject = new JSONObject(Util.readFile(new File("design.json")));
                    String[] guiA = new String[jsonObject.keySet().size()];
                    int i = 0;
                    for(String s : jsonObject.keySet()){
                        guiA[i] = s;
                        i++;
                    }
                    guis.setListData(guiA);
                    if(selectedIndex == -1) selectedIndex = 0;
                    guis.setSelectedIndex(selectedIndex);

                    selectedIndex = components.getSelectedIndex();
                    jsonObject = jsonObject.getJSONObject(guis.getSelectedValue());
                    guiA = new String[jsonObject.keySet().size()];
                    i = 0;
                    for(String s : jsonObject.keySet()){
                        guiA[i] = s;
                        i++;
                    }
                    components.setListData(guiA);
                    if(selectedIndex == -1) selectedIndex = 0;
                    components.setSelectedIndex(selectedIndex);

                    Setting setting = new Setting().loadFromFile(new File("design.json"),guis.getSelectedValue(),components.getSelectedValue());

                    xPos.setValue((int) (setting.getX()*100.0));
                    yPos.setValue((int) (setting.getY()*100.0));
                    width.setValue((int) (setting.getWidth()*100.0));
                    height.setValue((int) (setting.getHeight()*100.0));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                while (true){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        int selectedIndex = guis.getSelectedIndex();
                        JSONObject jsonObject = new JSONObject(Util.readFile(new File("design.json")));
                        String[] guiA = new String[jsonObject.keySet().size()];
                        int i = 0;
                        for(String s : jsonObject.keySet()){
                            guiA[i] = s;
                            i++;
                        }
                        guis.setListData(guiA);
                        if(selectedIndex == -1) selectedIndex = 0;
                        guis.setSelectedIndex(selectedIndex);

                        selectedIndex = components.getSelectedIndex();
                        jsonObject = jsonObject.getJSONObject(guis.getSelectedValue());
                        guiA = new String[jsonObject.keySet().size()];
                        i = 0;
                        for(String s : jsonObject.keySet()){
                            guiA[i] = s;
                            i++;
                        }
                        components.setListData(guiA);
                        if(selectedIndex == -1) selectedIndex = 0;
                        components.setSelectedIndex(selectedIndex);

                        Setting setting = new Setting().loadFromFile(new File("design.json"),guis.getSelectedValue(),components.getSelectedValue());

                        setting.setX(xPos.getValue()/100.0);
                        setting.setY(yPos.getValue()/100.0);
                        setting.setWidth(width.getValue()/100.0);
                        setting.setHeight(height.getValue()/100.0);

                        setting.saveToFile(new File("design.json"),guis.getSelectedValue(),components.getSelectedValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.start();

        return this;
    }
}
