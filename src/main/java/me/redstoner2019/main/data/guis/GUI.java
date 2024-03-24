package me.redstoner2019.main.data.guis;

import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.packets.*;
import me.redstoner2019.main.serverstuff.ClientMain;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.awt.Color.*;

public class GUI<d> {

    public static JFrame frame;
    private int width = 1280;
    private int height = 720;
    public static BufferedImage cards = null;
    public static List<Card> playerCardStack = new ArrayList<>();
    public static Card lastPlaced = null;
    public static boolean isCurrentTurn = false;
    public static String currentPlayer = "";
    public static JButton drawButton = new JButton("DRAW");
    public static JButton skipButton = new JButton("SKIP");
    public static JButton unoButton = new JButton("UNO");
    public static JTextArea nextUp = new JTextArea();
    public static HashMap<Integer,Integer> lift = new HashMap<>();
    public static HashMap<Integer,Integer> modifier = new HashMap<>();
    public static boolean preGame = true;
    public static HashMap<String,Boolean> prePlayers = new HashMap<>();
    public static int countdown = 0;
    public static int minPlayers = 0;
    public static int cardsPerPlayer = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) throws Exception {
        System.out.println(Main.class.getResource("/cards.png"));
        cards = ImageIO.read(GUI.class.getResource("/cards.png"));
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI window = new GUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     * //@param <d>
     */
    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0, 0, width, height);
        frame.setTitle("UNO");

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);


        /**
         * ingame visuals
         */
        JLabel label = new JLabel();
        label.setBounds(50,350,width,287);
        panel.add(label);

        JLabel lastPlacedCard = new JLabel();
        lastPlacedCard.setBounds((width - 125) / 2,((height - 187) / 2) - 150,125,187);
        panel.add(lastPlacedCard);

        JLabel turn = new JLabel("Turn: ");
        turn.setBounds(40,40,1000,20);
        turn.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(turn);

        nextUp.setBounds(50,90,300,350-90);
        nextUp.setFont(new Font("Arial", Font.PLAIN, 15));
        nextUp.setEditable(false);
        nextUp.setForeground(Color.BLACK);
        panel.add(nextUp);

        drawButton.setBounds(1000,140,200,40);
        drawButton.setEnabled(false);
        panel.add(drawButton);
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientMain.sendObject(new DrawCard());
            }
        });

        skipButton.setBounds(1000,190,200,40);
        skipButton.setEnabled(false);
        panel.add(skipButton);
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientMain.sendObject(new SkipTurnPacket());
            }
        });

        unoButton.setBounds(1000,90,200,40);
        unoButton.setEnabled(false);
        panel.add(unoButton);
        unoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientMain.sendObject(new UNOPacket());
            }
        });

        JButton readyButton = new JButton("Ready");
        panel.add(readyButton);
        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readyButton.setEnabled(false);
                ClientMain.sendObject(new ReadyPacket());
            }
        });

        if(!ClientMain.isConnected()) System.exit(0);

        frame.setLocation((1920-width)/2,(1080-height)/2);

        frame.setVisible(true);

        /**
         * pregame visuals
         */

        JTextArea playerList = new JTextArea();
        playerList.setBounds(30,150,width-500,height-270);
        playerList.setEditable(false);
        playerList.setFont(new Font("Arial",Font.PLAIN,30));
        panel.add(playerList);

        JLabel countdownLabel = new JLabel("Test");
        countdownLabel.setBounds(50,20,width-100,100);
        countdownLabel.setFont(new Font("Arial",Font.PLAIN,50));
        panel.add(countdownLabel);

        JTextArea info = new JTextArea();
        info.setBounds(width-400,150,350,height-270);
        info.setEditable(false);
        info.setFont(new Font("Arial",Font.PLAIN,20));
        panel.add(info);

        readyButton.setBounds(30,height-100,width-80,50);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (frame.isVisible()){
                    if(preGame){
                        playerList.setVisible(true);
                        countdownLabel.setVisible(true);
                        info.setVisible(true);
                        readyButton.setVisible(true);

                        unoButton.setVisible(false);
                        drawButton.setVisible(false);
                        skipButton.setVisible(false);
                        nextUp.setVisible(false);
                        turn.setVisible(false);
                        lastPlacedCard.setVisible(false);
                        label.setVisible(false);
                    } else {
                        playerList.setVisible(false);
                        countdownLabel.setVisible(false);
                        info.setVisible(false);
                        readyButton.setVisible(false);

                        unoButton.setVisible(true);
                        drawButton.setVisible(true);
                        skipButton.setVisible(true);
                        nextUp.setVisible(true);
                        turn.setVisible(true);
                        lastPlacedCard.setVisible(true);
                        label.setVisible(true);
                    }

                    int i = 1;
                    String string = "";
                    for(String s : prePlayers.keySet()){
                        string+=i + ". " + s;
                        if(prePlayers.get(s)) string+="(READY)\n"; else string+="\n";
                        i++;
                    }
                    playerList.setText(string);

                    if(countdown == 10) {
                        if(prePlayers.size() >= minPlayers) countdownLabel.setText("Game Starting... Waiting for players to be ready"); else countdownLabel.setText("Game Starting... Waiting for players to join");
                    }else countdownLabel.setText("Game Starting... " + countdown + " Seconds");

                    String infoString = "Settings\n\nMinimum players: " + minPlayers + "\nCards/Player: " + cardsPerPlayer;
                    info.setText(infoString);



                    if(isCurrentTurn){
                        turn.setText("Current Turn: " + "you");
                    } else {
                        turn.setText("Current Turn: " + currentPlayer);
                    }
                    synchronized (this) {
                        if(playerCardStack.isEmpty()) continue;
                        int w = 1200 / playerCardStack.size();

                        BufferedImage image = new BufferedImage(w*playerCardStack.size() + (125 - w),287,1);

                        Graphics2D g = image.createGraphics();
                        g.setColor(new Color(226, 234, 236));
                        g.fillRect(0,0,width,height);

                        int xOffset = 0;

                        int index = 0;

                        int defaultModifier = 13;

                        for(Card c : playerCardStack){
                            Point mouse = label.getMousePosition();
                            lift.put(index,lift.getOrDefault(index,0));
                            modifier.put(index,modifier.getOrDefault(index,defaultModifier));
                            if(mouse!=null) {
                                if(mouse.x > xOffset && mouse.x <= xOffset + w) {
                                    lift.put(index,lift.get(index) - modifier.get(index));
                                    lift.put(index,-100);
                                   if(modifier.get(index) > 0) {
                                       modifier.put(index, modifier.get(index) - 1);
                                   }
                                } else {
                                    if(lift.get(index) < 0){
                                        lift.put(index,lift.get(index) + 10);
                                    }
                                    modifier.put(index, defaultModifier);
                                }
                            } else {
                                if(lift.get(index) < 0){
                                    lift.put(index,lift.get(index) + 10);
                                }
                                modifier.put(index,defaultModifier);
                            }

                            BufferedImage cardImage = getCard(c);

                            Graphics2D g2 = cardImage.createGraphics();
                            g2.setColor(BLACK);
                            g2.drawString((lift.get(index)+100) + " ", 10,10);
                            g2.dispose();

                            g.drawImage(getCard(c),xOffset,lift.get(index)+100,null);
                            xOffset+=w;
                            index++;
                        }
                        g.dispose();

                        label.setIcon(new ImageIcon(image));

                        BufferedImage lastCard = new BufferedImage(lastPlacedCard.getWidth(),lastPlacedCard.getHeight(),1);

                        g = lastCard.createGraphics();
                        if(lastPlaced!=null) g.drawImage(getCard(lastPlaced),0,0,null);
                        g.dispose();

                        lastPlacedCard.setIcon(new ImageIcon(lastCard));
                    }
                }
            }
        });
        t.start();

        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int xOffset = 0;

                if(playerCardStack.isEmpty()) return;

                int w = 1200 / playerCardStack.size();

                for(Card c : playerCardStack){
                    if(c.getColor().contains(" - ")) c.setColor("BLACK");
                    Point mouse = label.getMousePosition();
                    if(mouse!=null) {
                        if(mouse.x > xOffset && mouse.x <= xOffset + w){
                            ClientMain.sendObject(new PutCardPacket(c));
                            if(c.getNum() == 'W' && !lastPlaced.getColor().equals("BLACK")){
                                new ChooseColorPopup(frame);
                            }
                            if(c.getNum() == 'D' && c.getColor().equals("BLACK") && !lastPlaced.getColor().equals("BLACK")){
                                new ChooseColorPopup(frame);
                            }
                            break;
                        }
                    }
                    xOffset+=w;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public static BufferedImage getCard(Card c){
        int color = 0;
        int number = 0;
        if(c == null) return new BufferedImage(1,1,1);
        switch (c.getNum()){
            case 'S':{
                number = 10;
                break;
            }
            case 'R':{
                number = 11;
                break;
            }
            case 'D':{
                number = 12;
                break;
            }
            case 'W':{
                break;
            }
            default: {
                number = Integer.parseInt(c.getNum() + "");
            }
        }

        switch (c.getColor()){
            case "RED": {
                color = 0;
                break;
            }
            case "YELLOW": {
                color = 1;
                break;
            }
            case "GREEN": {
                color = 2;
                break;
            }
            case "BLUE": {
                color = 3;
                break;
            }
        }

        if(c.getColor().equals("BLACK")){
            number = 13;
            switch (c.getNum()){
                case 'D': {
                    color = 4;
                    break;
                }
                case 'W': {
                    color = 0;
                    break;
                }
                default: {
                    return new BufferedImage(1,1,1);
                }
            }
        }
        Color extraColor = null;
        if(c.getColor().startsWith("BLACK - ")){
            number = 13;
            switch (c.getNum()){
                case 'D': {
                    color = 4;
                    break;
                }
                case 'W': {
                    color = 0;
                    break;
                }
                default: {
                    return new BufferedImage(1,1,1);
                }
            }
            switch (c.getColor().substring("BLACK - ".length())){
                case "RED": {
                    extraColor = new Color(252, 85, 84);
                    break;
                }
                case "GREEN": {
                    extraColor = new Color(84, 169, 84);
                    break;
                }
                case "BLUE": {
                    extraColor = new Color(83, 84, 251);
                    break;
                }
                case "YELLOW": {
                    extraColor = new Color(253, 169, 1);
                    break;
                }
            }
        }
        BufferedImage ca = getCard(color,number);
        if(extraColor != null){
            /*for (int x = 0; x < ca.getWidth(); x++) {
                for (int y = 0; y < ca.getHeight(); y++) {
                    if((new Color(ca.getRGB(x,y)).getRed() < 30) && (new Color(ca.getRGB(x,y)).getGreen() < 30) && (new Color(ca.getRGB(x,y)).getBlue() < 30)){
                        ca.setRGB(x,y,extraColor.getRGB());
                    }
                }
            }*/
            Graphics2D g = ca.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);


            g.setColor(extraColor);
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

//TODO: switch to LWJGL
/*

TextureAtlas textureAtlas = new TextureAtlas();
textureAtlas.loadFromStream(new FileInputStream("path/to/your/font.tga"), "TGA");

TextureAtlasBuilder from the org.newdawn.slick.imageout

String text = "Hello, LWJGL!";
fontHeight = 12;

        float x = 100;
        float y = 100;

        float spaceWidth = 3;

        for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        int offset = (c - 32) * 16;

        // position
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + 16, y);
        GL11.glVertex2f(x + 16, y + 16);
        GL11.glVertex2f(x, y + 16);

        // texture
        GL11.glTexCoord2f(offset % 128 / 256.0f, offset / 128 / 256.0f);
        GL11.glTexCoord2f((offset + 16) % 128 / 256.0f, offset / 128 / 256.0f);
        GL11.glTexCoord2f((offset + 16) % 128 / 256.0f, (offset + 16) / 128 / 256.0f);
        GL11.glTexCoord2f(offset % 128 / 256.0f, (offset + 16) / 128 / 256.0f);

        x += 16 + spaceWidth;
        }

// reset OpenGL state
        GL11.glColor4f(1, 1, 1, 1);*/
