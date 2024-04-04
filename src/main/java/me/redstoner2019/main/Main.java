package me.redstoner2019.main;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.CardColor;
import me.redstoner2019.main.data.CardType;
import me.redstoner2019.main.data.guis.GUI;
import me.redstoner2019.serverhandling.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static me.redstoner2019.main.data.CardColor.*;

public class Main {
    public static final boolean TEST_MODE = false;
    private static final String VERSION = "v1.4.0-alpha.1";
    public static String getVersion(){
        return VERSION;
    }
    static BufferedImage cards;
    public static void main(String[] args) throws Exception {
        LoggerDump.initialize();
        //generateTextures();
        GUI gui = new GUI();
    }

    public static void generateTextures() throws IOException {
        BufferedImage image = ImageIO.read(GUI.class.getResource("/textures/background.png"));
        cards = ImageIO.read(GUI.class.getResource("/cards.png"));

        new File("textures").mkdirs();



        for(CardColor c : new CardColor[]{RED, BLUE, GREEN, YELLOW}){
            new File("textures/"+c.name()).mkdirs();
            for(CardType t : CardType.values()){
                Card card = new Card(c,t);
                card.setOverrideColor(c);
                BufferedImage i = getCard(card);
                ImageIO.write(Util.resize(i,128,192),"PNG",new File("textures/" + c.name() + "/"+t.name()+".png"));
            }
        }

        for(CardColor c : new CardColor[]{SPECIAL}){
            new File("textures/"+c.name()).mkdirs();
            for(CardType t : new CardType[]{CardType.PLUS_4, CardType.CHANGE_COLOR}){
                Card card = new Card(c,t);
                BufferedImage i = getCard(card);
                ImageIO.write(Util.resize(i,128,192),"PNG",new File("textures/" + c.name() + "/"+t.name()+".png"));
                for(CardColor c2 : new CardColor[]{RED, BLUE, GREEN, YELLOW}){
                    card.setOverrideColor(c2);
                    i = getCard(card);
                    ImageIO.write(Util.resize(i,128,192),"PNG",new File("textures/" + c2.name() + "/"+t.name()+".png"));
                }
            }
        }
    }

    public static BufferedImage getCard(Card c){
        int color = 0;
        int number = 0;
        HashMap<CardColor,Integer> colors = new HashMap<>();
        colors.put(RED,0);
        colors.put(GREEN,2);
        colors.put(YELLOW,1);
        colors.put(BLUE,3);
        colors.put(SPECIAL,4);

        HashMap<CardType,Integer> types = new HashMap<>();
        int i = 0;
        for(CardType t : CardType.values()){
            types.put(t,i);
            i++;
        }

        color = colors.get(c.getColor());
        number = types.get(c.getNum());

        if(c == null) return new BufferedImage(1,1,1);

        if(c.getColor() == SPECIAL){
            number = 13;
            if(c.getNum() == CardType.CHANGE_COLOR){
                color = 0;
            }
            if(c.getNum() == CardType.PLUS_4){
                color = 4;
            }
        }

        BufferedImage ca = getCard(color,number);
        if(c.getOverrideColor() != null && c.getColor().equals(SPECIAL)){
            System.out.println(c);
            Graphics2D g = ca.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            switch(c.getOverrideColor()){
                case RED: {
                    g.setColor(Color.RED);
                    break;
                }
                case GREEN: {
                    g.setColor(Color.GREEN);
                    break;
                }
                case BLUE: {
                    g.setColor(Color.BLUE);
                    break;
                }
                case YELLOW: {
                    g.setColor(Color.ORANGE);
                    break;
                }
                default: {
                    return new BufferedImage(1,1,1);
                }
            }
            g.fillOval(90,15,20,20);
            g.fillOval(13,150,20,20);
            g.dispose();
        }
        return ca;
    }

    public static BufferedImage getCard(int color, int number){
        int x0 = 125;
        int y0 = 25;
        int cWidth = 125;
        int cHeight = 187;
        if(cards == null) return new BufferedImage(cWidth,cHeight,1);
        BufferedImage subImage = new BufferedImage(cWidth,cHeight,1);
        for (int x = 0; x < cWidth; x++) {
            for (int y = 0; y < cHeight; y++) {
                subImage.setRGB(x,y,cards.getRGB(x0 + (cWidth * number) + x,y0 + (cHeight * color) + y));
            }
        }
        return subImage;
        //return cards.getSubimage(x0 + (cWidth * number),y0 + (cHeight * color),cWidth,cHeight);
    }
}