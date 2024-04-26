package me.redstoner2019.guiapi.design;

import me.redstoner2019.guiapi.Util;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Setting {
    private double x = 0;
    private double y = 0;
    private double width = 0;
    private double height = 0;
    private boolean customDesign = true;
    private String defaultDesign = "DARK";
    private Color foreground = Color.WHITE;
    private Color background = Color.DARK_GRAY;
    private File file;
    private String gui;
    private String componentID;
    public Setting loadFromFile(File file, String gui, String componentID){
        try {
            file = new File("design.json");
            if(!file.exists()){
                file.createNewFile();
                new Setting().saveToFile(file,gui,componentID);
            }
            String json = Util.readFile(file);
            JSONObject data = new JSONObject(json);
            if(!data.has(gui)){
                new Setting().saveToFile(file,gui,componentID);
                return null;
            }
            JSONObject guiData = data.getJSONObject(gui);
            if(!guiData.has(componentID)){
                new Setting().saveToFile(file,gui,componentID);
                return null;
            }
            JSONObject designData = guiData.getJSONObject(componentID);
            this.x = designData.getDouble("x");
            this.y = designData.getDouble("y");
            this.width = designData.getDouble("width");
            this.height = designData.getDouble("height");
            this.customDesign = designData.getBoolean("custom-design");
            if(customDesign){
                JSONObject foregroundColor = designData.getJSONObject("foreground");
                this.foreground = new Color(foregroundColor.getInt("r"),foregroundColor.getInt("g"),foregroundColor.getInt("b"),foregroundColor.getInt("a"));
                JSONObject backgroundColor = designData.getJSONObject("background");
                this.background = new Color(backgroundColor.getInt("r"),backgroundColor.getInt("g"),backgroundColor.getInt("b"),backgroundColor.getInt("a"));
            } else {
                this.defaultDesign = designData.getString("default-design");
            }
            this.file = file;
            this.gui = gui;
            this.componentID = componentID;
        } catch (Exception e) {
            //System.out.println("Failed to load Setting: " + e.getLocalizedMessage());
        }
        return this;
    }
    public void reload(){
        loadFromFile(file,gui,componentID);
    }
    public void saveToFile(File file, String gui, String componentID){
        try {
            JSONObject data = new JSONObject(Util.readFile(file));
            if(!data.has(gui)) data.put(gui,new JSONObject());
            JSONObject guiData = data.getJSONObject(gui);
            JSONObject designData = new JSONObject();

            designData.put("x",x);
            designData.put("y",y);
            designData.put("width",width);
            designData.put("height",height);
            designData.put("custom-design",customDesign);
            designData.put("default-design",defaultDesign);
            if(customDesign){
                JSONObject color = new JSONObject();
                color.put("r",foreground.getRed());
                color.put("g",foreground.getGreen());
                color.put("b",foreground.getBlue());
                color.put("a",foreground.getAlpha());
                designData.put("foreground",color);

                color = new JSONObject();
                color.put("r",background.getRed());
                color.put("g",background.getGreen());
                color.put("b",background.getBlue());
                color.put("a",background.getAlpha());
                designData.put("background",color);
            }

            guiData.put(componentID,designData);
            data.put(gui,guiData);
            Util.writeStringToFile(Util.prettyJSON(data.toString()),file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Setting(){

    }
    public Setting(double x, double y, double width, double height, boolean customDesign, String defaultDesign, Color foreground, Color background) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.customDesign = customDesign;
        this.defaultDesign = defaultDesign;
        this.foreground = foreground;
        this.background = background;
    }
    public Setting(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isCustomDesign() {
        return customDesign;
    }

    public void setCustomDesign(boolean customDesign) {
        this.customDesign = customDesign;
    }

    public String getDefaultDesign() {
        return defaultDesign;
    }

    public void setDefaultDesign(String defaultDesign) {
        this.defaultDesign = defaultDesign;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }
}
