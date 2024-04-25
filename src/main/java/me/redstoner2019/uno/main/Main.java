package me.redstoner2019.uno.main;

import me.redstoner2019.server.util.Util;
import me.redstoner2019.uno.main.data.Card;
import me.redstoner2019.uno.main.data.CardColor;
import me.redstoner2019.uno.main.data.CardType;
import me.redstoner2019.uno.main.data.guis.GUI;
import me.redstoner2019.uno.main.serverstuff.ServerMain;
import org.json.JSONArray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static me.redstoner2019.uno.main.data.CardColor.*;

public class Main {
    public static final boolean TEST_MODE = false;
    private static final String VERSION = "v1.6.0-alpha.10";
    public static String username = "";
    public static String password = "";
    public static String packVersion = "1";
    public static String getPackVersion(){
        return packVersion;
    }
    public static String getVersion(){
        return VERSION;
    }
    static BufferedImage cards;
    public static String getLatestVersion() throws IOException {
        String repoOwner = "Redstoner-2019";
        String repoName = "UNO";

        URL url = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/tags");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github+json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();

        JSONArray tags = new JSONArray(stringBuilder.toString());

        return tags.getJSONObject(0).getString("name");
    }
    public static void main(String[] args) throws Exception {
        if(args.length == 3){
            if(args[0].equals("client")) {
                username = args[1];
                password = args[2];
            }
        }
        if(args.length == 2){
            if(args[0].equals("server")) {
                ServerMain.main(new String[]{args[1]});
                return;
            }
        }
        if(args.length == 1){
            if(args[0].equals("server")) {
                ServerMain.main(new String[]{});
                return;
            }
        }
        //LoggerDump.initialize();

        File resourcePacks = new File("texturepacks");

        if(!resourcePacks.exists()){
            resourcePacks.mkdirs();
        }
        //generateTextures();

        //ImageIO.write(Util.resize(ImageIO.read(new File("C:\\Projects\\UNO\\src\\main\\resources\\background.png")),1280,720),"PNG",new File("C:\\Projects\\UNO\\src\\main\\resources\\background.png"));

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
    private static int compareVersions(String version1, String version2) {
        /*String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (num1 < num2) {
                return -1;
            } else if (num1 > num2) {
                return 1;
            }
        }*/

        return 0;
    }
}