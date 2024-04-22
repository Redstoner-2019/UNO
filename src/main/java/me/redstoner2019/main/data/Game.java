package me.redstoner2019.main.data;

import me.redstoner2019.main.data.packets.gamepackets.*;
import me.redstoner2019.main.data.packets.lobbypackets.StatsPacket;
import me.redstoner2019.main.data.packets.loginpackets.Ping;
import me.redstoner2019.main.serverstuff.ServerMain;
import me.redstoner2019.serverhandling.Packet;

import java.util.*;

public class Game {
    private String gameCode;
    private List<Player> players = new ArrayList<>();
    private int cardsPerPlayer = 7;
    private int decks = 2;
    private boolean stacking = false;
    private boolean sevenSwap = false;
    private boolean jumpIn = false;
    private Player owner = null;
    private boolean running = false;
    private int cardsDue = 0;
    public Queue<GamePacket> queue = new ArrayDeque<>();
    private String winner;
    private List<String> leaderboard;
    private Queue<Card> DECK;
    private HashMap<String, Integer> cardsPlaced = new HashMap<>();
    private List<String> chats = new ArrayList<>();
    private boolean chatEnabled = false;

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public HashMap<String, String> getPlayerHashMap(){
        HashMap<String, String> players = new HashMap<>();
        for(Player p : getPlayers()){
            players.put(p.getUsername(),p.getDisplayName());
        }
        return players;
    }

    public void updatePlayers(){
        try {
            Iterator<Player> playerIterator = players.iterator();
            while (playerIterator.hasNext()){
                Player p = playerIterator.next();
                if(!p.getHandler().isConnected()) {
                    players.remove(p);
                }
                if(!p.getGameID().equals(gameCode)) {
                    players.remove(p);
                }
            }
        } catch (Exception e){

        }
    }
    public Player getOwner() {
        updatePlayers();
        boolean foundOwner = false;
        for(Player p : players)
            if (p.equals(owner)) {
                if(!p.getHandler().isConnected()){
                    foundOwner = false;
                    break;
                }
                foundOwner = true;
                break;
            }
        if(!foundOwner && !players.isEmpty()) {
            owner = players.get(0);
            System.out.println(gameCode + " has a new owner " + owner.getUsername());
        }
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Game(String code){
        this.gameCode = code;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getCardsPerPlayer() {
        return cardsPerPlayer;
    }

    public void setCardsPerPlayer(int cardsPerPlayer) {
        this.cardsPerPlayer = cardsPerPlayer;
    }

    public int getDecks() {
        return decks;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public boolean isStacking() {
        return stacking;
    }

    public void setStacking(boolean stacking) {
        this.stacking = stacking;
    }

    public boolean isSevenSwap() {
        return sevenSwap;
    }

    public void setSevenSwap(boolean sevenSwap) {
        this.sevenSwap = sevenSwap;
    }

    public boolean isJumpIn() {
        return jumpIn;
    }

    public void setJumpIn(boolean jumpIn) {
        this.jumpIn = jumpIn;
    }
    public void addPlayer(Player p){
        if(!players.contains(p)) players.add(p);
    }
    public void addChat(String msg){
        chats.add(msg);
    }

    public List<String> getChats() {
        return chats;
    }

    public static Game createGame(String forceCode){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        String gameCode = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            gameCode+=chars[random.nextInt(chars.length)];
        }
        if(!forceCode.isEmpty()) gameCode = forceCode;
        Game game = new Game(gameCode);
        return game;
    }
    public String getGameCode() {
        return gameCode;
    }
    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }
    public void start(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(running) return;
                System.out.println("Game Start");
                running = true;
                for(Player p : players){
                    p.reset();
                    p.getHandler().sendObject(new GameStartPacket());
                    System.out.println("Send Game Start");
                }
                /**
                 * Game running
                 */
                boolean gameRunning = true;
                Card lastCardPlaced = null;
                DECK = new ArrayDeque<>();
                for (int i = 0; i < decks; i++) {
                    for(Card c : shuffle(Card.getDECK())){
                        DECK.add(c);
                    }
                }
                while (lastCardPlaced == null || lastCardPlaced.getColor().equals(CardColor.SPECIAL)){
                    lastCardPlaced = DECK.poll();
                    DECK.add(lastCardPlaced);
                }
                System.out.println(cardsPerPlayer + " cards/player");
                for(Player p : players){
                    for (int i = 0; i < cardsPerPlayer; i++) {
                        p.addCard(DECK.poll());
                    }
                }
                chats = new ArrayList<>();

                long lastCardPlacedTimestamp = System.currentTimeMillis();

                while (gameRunning){
                    updatePlayers();
                    while (!queue.isEmpty()){
                        boolean stackingAccepted = System.currentTimeMillis() - lastCardPlacedTimestamp <= 2000;
                        GamePacket gamePacket = queue.poll();
                        Player player = gamePacket.getPlayer();
                        Packet packet = gamePacket.getPacket();
                        boolean isTurn = players.get(0).equals(player);
                        /**
                         * TODO: Manage Packets
                         */
                        if(packet instanceof DrawCardPacket p){
                            if(!isTurn) continue;
                            if(!player.isCanDraw()) continue;
                            if(p.getAmount() == 1){
                                player.setCanSkip(true);
                                player.setCanDraw(false);
                            }
                            if(cardsDue > 0 && isStacking()) {
                                p.setAmount(cardsDue);
                                cardsDue = 0;
                                player.setCanSkip(false);
                                player.setCanDraw(true);
                            }
                            for (int i = 0; i < p.getAmount(); i++) {
                                Card card = DECK.poll();
                                player.addCard(card);
                                System.out.println(player.getDisplayName() + " has drawn " + card);
                                if(DECK.size() <= 10){
                                    DECK.addAll(shuffle(Card.getDECK()));
                                }
                            }
                        }
                        if(packet instanceof SkipTurnPacket p){
                            if(!isTurn) continue;
                            if(!player.isCanSkip()) continue;
                            System.out.println(player.getDisplayName() + " Skipped their turn");
                            player.setCanSkip(false);
                            player.setCanDraw(false);
                            nextPlayer();
                        }
                        if(packet instanceof UNOPacket p){
                            if(!isTurn) continue;
                            System.out.println(player.getDisplayName() + ": UNO!");
                            player.setUNO(true);
                            player.setCanUNO(false);
                        }
                        if(packet instanceof PlaceCardPacket p){
                            if(!isTurn) continue;
                            if(!player.getCards().contains(p.getCard())) {
                                System.out.println(player.getUsername() + " does not have card " + p.getCard());
                                continue;
                            }
                            if(lastCardPlaced.canBePlayed(p.getCard())){
                                System.out.println(player.getDisplayName() + " placed card " + p.getCard());
                                DECK.add(lastCardPlaced);
                                System.out.println(cardsPlaced.get(player.getUsername()) + player.getUsername());
                                cardsPlaced.put(player.getUsername(),cardsPlaced.getOrDefault(player.getUsername(),0)+1);
                                System.out.println(cardsPlaced.get(player.getUsername()) + player.getUsername());
                                lastCardPlaced = p.getCard();
                                player.removeCard(p.getCard());
                                if(player.getCards().size() == 1 && !player.isUNO()){
                                    player.addCard(DECK.poll());
                                    player.addCard(DECK.poll());
                                    System.out.println(player.getUsername() + " forgot to say UNO");
                                }
                                if(player.getCards().size() == 1) player.setUNO(false);
                                if(lastCardPlaced.getOverrideColor() == null) lastCardPlaced.setOverrideColor(CardColor.RED);


                                if(lastCardPlaced.getNum().equals(CardType.PLUS_4)){
                                    player.getUserdata().setPlus4Placed(player.getUserdata().getPlus4Placed()+1);
                                    cardsDue+=4;
                                }else if(lastCardPlaced.getNum().equals(CardType.DRAW)){
                                    cardsDue+=2;
                                } else if(cardsDue > 0 && isStacking()){
                                    System.out.println("Drawing " + cardsDue + " cards");
                                    for (int i = 0; i < cardsDue; i++) {
                                        player.addCard(DECK.poll());
                                    }
                                    cardsDue = 0;
                                }

                                if(lastCardPlaced.getNum().equals(CardType.SKIP)){
                                    nextPlayer();
                                }
                                if(lastCardPlaced.getNum().equals(CardType.REVERSE)){
                                    Collections.reverse(players);
                                    nextPlayer();
                                }
                                nextPlayer();
                                if(!isStacking()){
                                    for (int i = 0; i < cardsDue; i++) {
                                        players.get(0).addCard(DECK.poll());
                                    }
                                    cardsDue = 0;
                                }
                            }
                        }
                    }
                    List<String> nextPlayers = new ArrayList<>();
                    for(Player p : players){
                        String s = p.getDisplayName();
                        if(p.isUNO()) s+= " - UNO!";
                        nextPlayers.add(s);
                    }
                    List<Player> toRemove = new ArrayList<>();
                    Iterator<Player> playerIterator = players.iterator();
                    try{
                        while (playerIterator.hasNext()){
                            Player p = playerIterator.next();
                            if(!p.getHandler().isConnected()) {
                                if(players.size() == 1){
                                    gameRunning = false;
                                    break;
                                }
                                toRemove.add(p);
                                continue;
                            }
                            if(players.isEmpty()){
                                gameRunning = false;
                                break;
                            }
                            //if(packetManaged){
                                boolean isTurn = players.get(0).equals(p);
                                boolean canSkip = isTurn && p.isCanSkip();
                                boolean canDraw = isTurn && p.isCanDraw();
                                boolean canUNO = isTurn && p.isCanUNO();

                                p.getHandler().sendObject(new GameDataPacket(canSkip, canDraw, canUNO, isTurn, nextPlayers, lastCardPlaced, List.copyOf(p.getCards()),cardsDue));
                            //}
                            if(p.getCards().isEmpty()){
                                gameRunning = false;
                                winner = p.getDisplayName();
                                p.getUserdata().setGamesWon(p.getUserdata().getGamesWon()+1);
                                players.sort(new Comparator<Player>() {
                                    @Override
                                    public int compare(Player o1, Player o2) {
                                        return o1.getCards().size()-o2.getCards().size();
                                    }
                                });
                                int place = 1;
                                leaderboard = new ArrayList<>();
                                for(Player pl : players){
                                    leaderboard.add(place + ". " + pl.getDisplayName() + " -> " + pl.getCards().size() + " cards left, " + cardsPlaced.getOrDefault(pl.getUsername(),0) + " cards placed");
                                    place++;
                                }
                                System.out.println(p.getDisplayName() + " has won");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(!toRemove.isEmpty()){
                        System.out.println("Removing player(s) " + toRemove);
                    }
                    players.removeAll(toRemove);
                    cardsPlaced = new HashMap<>();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                /**
                 * Game ended
                 */
                System.out.println("Game end");
                for(Player p : players){
                    p.getUserdata().setGamesPlayed(p.getUserdata().getGamesPlayed()+1);
                    p.getHandler().sendObject(new GameEndPacket(winner, leaderboard));
                    p.getHandler().sendObject(new StatsPacket(p.getUserdata().getGamesPlayed(),p.getUserdata().getGamesWon(),p.getUserdata().getPlus4Placed()));
                    p.save();
                    p.getCards().clear();
                }
                running = false;
            }
        });
        t.start();
    }
    public static List<Card> shuffle(List<Card> list){
        List<Card> listShuffled = new ArrayList<>();
        Random random = new Random();
        for(Card o : list){
            if(listShuffled.isEmpty()){
                listShuffled.add(o);
            } else {
                listShuffled.add(random.nextInt(listShuffled.size()),o);
            }
        }
        return listShuffled;
    }
    private void nextPlayer(){
        Player tempPlayer = players.get(0);
        if(tempPlayer.isUNO()){
            tempPlayer.addCard(DECK.poll());
            tempPlayer.addCard(DECK.poll());
            tempPlayer.setUNO(false);
        }
        tempPlayer.setCanDraw(false);
        tempPlayer.setCanSkip(false);
        tempPlayer.setCanUNO(false);

        players.add(tempPlayer);
        players.remove(0);

        players.get(0).setCanUNO(true);
        players.get(0).setCanSkip(false);
        players.get(0).setCanDraw(true);
    }
}
