package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.guis.ConsoleGUI;
import me.redstoner2019.main.data.packets.*;
import me.redstoner2019.main.data.Player;
import me.redstoner2019.serverhandling.Server;
import me.redstoner2019.serverhandling.Util;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ServerMain extends Server {
    public static List<Player> players = new ArrayList<>();
    public static final int MIN_PLAYERS = 2;
    public static int CARDS_PLAYER = 8;
    public static Card lastCardPlaced = null;
    public static boolean DIRECTION = false;
    public static boolean GAME_RUNNING = false;
    public static boolean skipNextTurn = false;
    public static String overridenColor = "RED";
    public static List<Card> deck = new ArrayList<>();

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        if(!new File("server.properties").exists()){
            try {
                new File("server.properties").createNewFile();
                object.put("cards",8);
                Util.writeStringToFile(Util.prettyJSON(object.toString()),new File("server.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            object = new JSONObject(Util.readFile(new File("server.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CARDS_PLAYER = object.getInt("cards");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ConsoleGUI.main(args);
            }
        });
        t.start();
        setup(8008);
        setClientConnectEvent(h -> {
            Player player = new Player();
                players.add(player);
                startPacketListener((p, handler) -> {
                    if(p instanceof JoinPacket packet){
                        player.setUsername(packet.getUsername());
                        player.setLoginComplete(true);
                        player.getCards().clear();
                        player.handler = handler;
                        Util.log("Player " + packet.getUsername() + " joined successfully");
                        if(GAME_RUNNING){
                            if(deck.size() < 50){
                                deck.addAll(shuffleList(Card.getDECK()));
                            }
                            for (int i = 0; i < 10; i++) {
                                player.addCard(popTop(deck));
                            }

                        }
                    } else if(p instanceof ReadyPacket){
                        player.ready = true;
                        Util.log("Player " + player.getUsername() + " is now ready");
                    } else if(p instanceof PutCardPacket packet){
                        Card cardPlayed = packet.card;
                        if(GAME_RUNNING){
                            if(players.get(0).equals(player)){
                                if(lastCardPlaced.canBePlayed(cardPlayed)){
                                    Card toRemove = null;
                                    for(Card c : player.getCards()){
                                        if(c.getColor().equals(cardPlayed.getColor()) && c.getNum() == cardPlayed.getNum()){
                                            toRemove = c;
                                            break;
                                        }
                                    }
                                    player.getCards().remove(toRemove);
                                    if(lastCardPlaced.getColor().startsWith("BLACK - ")) lastCardPlaced.setColor("BLACK");

                                    for(Card c : List.copyOf(deck)) {
                                        if(c.getColor().contains(" - ")){
                                            Util.log("Found broken card " + c);
                                        }
                                    }

                                    lastCardPlaced = cardPlayed;
                                    deck.add(cardPlayed);
                                    if(cardPlayed.getNum() == 'R'){
                                        DIRECTION = true;
                                    }
                                    if(cardPlayed.getNum() == 'S'){
                                        skipNextTurn = true;
                                    }
                                    if(cardPlayed.getNum() == 'D' && !cardPlayed.getColor().startsWith("BLACK")){
                                        Util.log(player.getUsername() + " " + cardPlayed + " Draw 2");
                                        players.get(1).addCard(popTop(deck));
                                        players.get(1).addCard(popTop(deck));
                                    }
                                    if(cardPlayed.getNum() == 'D' && cardPlayed.getColor().startsWith("BLACK")){
                                        Util.log(player.getUsername() + " " + cardPlayed + " Draw 4");
                                        try{
                                            players.get(1).addCard(popTop(deck));
                                            players.get(1).addCard(popTop(deck));
                                            players.get(1).addCard(popTop(deck));
                                            players.get(1).addCard(popTop(deck));
                                        } catch (Exception e){

                                        }

                                    }
                                    if(player.getCards().size() == 1){
                                        if(!player.UNO){
                                            Util.log(player.getUsername() + " " + cardPlayed + " Forgot UNO, Draw 2");
                                            player.addCard(popTop(deck));
                                            player.addCard(popTop(deck));
                                        }
                                    }
                                    if(player.UNO && player.getCards().size() > 2){
                                        Util.log(player.getUsername() + " " + cardPlayed + " Draw 2 - Uno with too many Cards");
                                        player.addCard(popTop(deck));
                                        player.addCard(popTop(deck));
                                    }
                                    if(cardPlayed.getNum() == 'W' || (cardPlayed.getNum() == 'D' && cardPlayed.getColor().startsWith("BLACK"))){

                                    } else {
                                        manageNextPlayer();
                                    }
                                    player.UNO = false;
                                }
                                if(player.getCards().isEmpty()) {
                                    for(Player pl : players){
                                        pl.handler.sendObject(new PlayerHasWonPacket(player.getUsername() + " has won! You had " + pl.getCards().size() + " cards left."));
                                        GAME_RUNNING = false;
                                    }
                                }
                            }
                        }
                    } else if(p instanceof DrawCard){
                        if(GAME_RUNNING){
                            Util.log(player.getUsername() + " has drawn a card");
                            if(players.get(0).equals(player)){
                                if(!player.hasDrawnCardThisRound){
                                    player.hasDrawnCardThisRound = true;
                                    player.addCard(popTop(deck));
                                }
                            }
                        }
                    } else if(p instanceof SkipTurnPacket){
                        if(GAME_RUNNING){
                            Util.log(player.getUsername() + " has skipped their turn");
                            if(players.get(0).equals(player)){
                                if(player.hasDrawnCardThisRound){
                                    manageNextPlayer();
                                }
                            }
                        }
                    } else if (p instanceof UNOPacket){
                        Util.log(player.getUsername() + " UNO!");
                        player.UNO = true;
                    } else if (p instanceof SetColorPacket packet){
                        overridenColor = packet.color;
                        lastCardPlaced.setColor("BLACK - " + overridenColor);
                        Util.log(lastCardPlaced.getColor());
                        manageNextPlayer();
                    }
                    },h);
        });

        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                    while (true){
                        boolean allAgreed = true;
                        synchronized (this){
                            if(players.size() < MIN_PLAYERS) continue;
                            for (Player p: List.copyOf(players)) {
                                if(!p.ready) {
                                    allAgreed = false;
                                    break;
                                }
                            }
                        }

                        if(!allAgreed) continue;

                        Util.log("Starting a game");

                        int decks = players.size()/4;
                        decks++;

                        List<Card> unshuffled = new ArrayList<>();

                        Util.log(players.size() + " Players, using " + decks + " Deck(s)");

                        for (int i = 0; i < decks; i++) {
                            unshuffled.addAll(Card.getDECK());
                        }

                        deck = shuffleList(List.copyOf(unshuffled));

                        Util.log("Deck contains " + deck.size() + " cards");
                        //initializing game
                        GAME_RUNNING = true;
                        for (Player p: players) {
                            for (int i = 0; i < CARDS_PLAYER; i++) {
                                p.addCard(popTop(deck));
                            }
                            Util.log("Player: " + p.getUsername() + " " + p.getCards().size());
                        }

                        Util.log("Cards left " + deck.size());

                        lastCardPlaced = deck.get(0); //TODO: first card might be broken

                        while (GAME_RUNNING){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if(players.isEmpty()){
                                GAME_RUNNING = false;
                                break;
                            }
                            try{
                                List<Player> toRemove = new ArrayList<>();
                                for(Player p : List.copyOf(players)){
                                    if(!p.handler.getSocket().isClosed()) {
                                        List<String> nextUp = new ArrayList<>();
                                        int c = 1;
                                        for(Player pl : List.copyOf(players)){
                                            if(pl.UNO)nextUp.add(c + ". " + pl.getUsername() + " (" + pl.getCards().size() + " cards left) UNO!");
                                            else nextUp.add(c + ". " + pl.getUsername() + " (" + pl.getCards().size() + " cards left)");
                                            c++;
                                        }
                                        p.handler.sendObject(new ClientDataPacket(List.copyOf(p.getCards()), new Card(lastCardPlaced), players.get(0).equals(p),players.get(0).getUsername(),!p.hasDrawnCardThisRound,p.hasDrawnCardThisRound,List.copyOf(nextUp),p.placement));
                                    } else {
                                        toRemove.add(p);
                                    }
                                }
                                for(Player p : toRemove){
                                    Util.log(p.getUsername() + " has left");
                                    players.remove(p);
                                }
                            }catch (Exception ignored){

                            }
                        }
                        players.clear();
                        Util.log("Reset Server");
                    }

            }
        });
        gameThread.start();

        start();
    }

    public static List<Card> shuffleList(List<Card> list) {
        Random rand = new Random();
        List<Card> cardsShuffled = new ArrayList<>();
        for(Card c : list){
            int index = 0;
            if(!cardsShuffled.isEmpty()){
                index = rand.nextInt(cardsShuffled.size());
            }
            cardsShuffled.add(index,c);
        }
        return cardsShuffled;
    }
    public static Card popTop(List<Card> deck){
        Card c = deck.get(0);
        deck.remove(0);
        return c;
    }
    public static Card popBottom(List<Card> deck){
        Card c = deck.get(deck.size() - 1);
        deck.remove(deck.size() - 1);
        return c;
    }
    public static void manageNextPlayer(){
        Util.log("Next Player");
        Player current = players.get(0);
        current.hasDrawnCardThisRound = false;
        players.remove(0);
        players.add(current);
        if(skipNextTurn){
            skipNextTurn = false;
            manageNextPlayer();
        }
        if(DIRECTION){
            Collections.reverse(players);
            DIRECTION = false;
            manageNextPlayer();
        }
    }



}
