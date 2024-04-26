package me.redstoner2019.guiapi;

import me.redstoner2019.guiapi.design.Setting;

import java.awt.*;

public class BoundsCreator {
    public int framewidth;
    public int frameheight;
    public void update(int width, int height){
        this.framewidth = width;
        this.frameheight = height;
    }
    public BoundsCreator(int width, int height) {
        this.framewidth = width;
        this.frameheight = height;
    }
    public Rectangle bounds(double x, double y, double width, double height){
        if(!within(x,0,1)) return new Rectangle(0,0,0,0);
        if(!within(y,0,1)) return new Rectangle(0,0,0,0);
        if(!within(width,0,1)) return new Rectangle(0,0,0,0);
        if(!within(height,0,1)) return new Rectangle(0,0,0,0);
        Rectangle rectangle = new Rectangle((int) (framewidth*x),(int) (frameheight*y),(int) (framewidth*width),(int) (frameheight*height));
        return rectangle;
    }
    public Rectangle bounds(Setting c){
        if(!within(c.getX(),0,1)) return new Rectangle(0,0,0,0);
        if(!within(c.getY(),0,1)) return new Rectangle(0,0,0,0);
        if(!within(c.getWidth(),0,1)) return new Rectangle(0,0,0,0);
        if(!within(c.getHeight(),0,1)) return new Rectangle(0,0,0,0);
        Rectangle rectangle = new Rectangle((int) (framewidth*c.getX()),(int) (frameheight*c.getY()),(int) (framewidth*c.getWidth()),(int) (frameheight*c.getHeight()));
        return rectangle;
    }
    public boolean within(double value, double bound1, double bound2){
        double smaller = Math.min(bound1,bound2);
        double bigger = Math.max(bound1,bound2);
        return value >= smaller && value <=bigger;
    }
}
