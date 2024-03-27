package me.redstoner2019.main.serverstuff;

import me.redstoner2019.main.Main;
import me.redstoner2019.main.data.Card;
import me.redstoner2019.main.data.data.Userdata;
import me.redstoner2019.main.data.guis.ConsoleGUI;
import me.redstoner2019.main.data.packets.*;
import me.redstoner2019.main.data.Player;
import me.redstoner2019.serverhandling.Server;
import me.redstoner2019.serverhandling.Util;
import org.json.JSONObject;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerMain extends Server {
    public static List<Player> players = new ArrayList<>();
    public static final int MIN_PLAYERS = 2;
    public static int CARDS_PLAYER = 7;
    public static Card lastCardPlaced = null;
    public static boolean DIRECTION = false;
    public static boolean GAME_RUNNING = false;
    public static boolean skipNextTurn = false;
    public static String overridenColor = "RED";
    public static List<Card> deck = new ArrayList<>();
    private static final int COUNTDOWN = 10;
    private static int startingIn = COUNTDOWN;
    private static boolean CANCEL_COUNTDOWN = false;
    public static boolean SERVER_GUI = true;

    public static void main(String[] args) throws Exception {
        if (!new File("playerdata.json").exists()) {
            new File("playerdata.json").createNewFile();
        }
        List<String> arguments = new ArrayList<>(List.of(args));
        if (arguments.contains("nogui")) SERVER_GUI = false;
        if (arguments.contains("resetdata")) {
            Scanner scanner = new Scanner(System.in);
            Util.log("Reset all saved Data? Will delete: ");
            Util.log(" - User accounts");
            Util.log(" - All saved data");
            Util.log("(Y/N))");
            String result = scanner.nextLine().toLowerCase();
            if (result.equals("y")) {
                try {
                    Util.writeStringToFile("{ }", new File("playerdata.json"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        JSONObject object = new JSONObject();
        if (!new File("server.properties").exists()) {
            try {
                new File("server.properties").createNewFile();
                object.put("cards", CARDS_PLAYER);
                Util.writeStringToFile(Util.prettyJSON(object.toString()), new File("server.properties"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            object = new JSONObject(Util.readFile(new File("server.properties")));
        } catch (Exception e) {
            new File("server.properties").delete();
            new File("server.properties").createNewFile();
            Util.writeStringToFile(Util.prettyJSON(object.toString()), new File("server.properties"));
            object = new JSONObject(Util.readFile(new File("server.properties")));
        }

        CARDS_PLAYER = object.getInt("cards");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ConsoleGUI.main(args);
            }
        });
        if (SERVER_GUI) t.start();
        setup(8008);
        setClientConnectEvent(h -> {
            if (GAME_RUNNING) {
                Thread.sleep(100);
                h.sendObject(new DisconnectPacket("There already is a Game running"));
                h.disconnect();
            }
            Player player = new Player();
            startPacketListener((p, handler) -> {
                if (p instanceof JoinPacket packet) {
                    for (Player pl : players) {
                        if (pl.getUsername().equals(packet.getUsername())) {
                            h.sendObject(new DisconnectPacket("User with the name '" + packet.getUsername() + "' is already connected"));
                            h.disconnect();
                            return;
                        }
                    }
                    if (!packet.getVersion().equals(Main.VERSION)) {
                        Util.log("Warning Client " + packet.getUsername() + " joined on wrong Version " + packet.getVersion() + " Server is on " + Main.VERSION);
                        h.sendObject(new DisconnectPacket("You joined with Version '" + packet.getVersion() + "', Server running version " + Main.VERSION));
                        h.disconnect();
                        return;
                    }

                    Userdata data = Userdata.read(packet.getUsername());
                    if (data == null) {
                        h.sendObject(new ConnectionResultPacket(404, "Account not found"));
                        return;
                    }
                    if (!data.getPassword().equals(packet.getPassword())) {
                        h.sendObject(new ConnectionResultPacket(405, "Incorrect Password"));
                        return;
                    } else {
                        h.sendObject(new ConnectionResultPacket(100, "Success"));
                    }

                    Util.log(packet.toString());
                    if (packet.getUsername().isEmpty()) return;
                    player.setUsername(packet.getUsername());
                    player.setLoginComplete(true);
                    player.getCards().clear();
                    player.handler = handler;
                    Util.log("Player " + packet.getUsername() + " joined successfully " + Main.VERSION);
                    players.add(player);
                    startingIn = COUNTDOWN;
                    CANCEL_COUNTDOWN = true;
                    Util.log("Cancelling Countdown: Player joined");
                    if (GAME_RUNNING) {
                        if (deck.size() < 50) {
                            deck.addAll(shuffleList(Card.getDECK()));
                        }
                        for (int i = 0; i < 10; i++) {
                            player.addCard(popTop(deck));
                        }

                    }
                } else if (p instanceof ReadyPacket) {
                    player.ready = true;
                    Util.log("Player " + player.getUsername() + " is now ready");
                } else if (p instanceof PutCardPacket packet) {
                    Card cardPlayed = packet.card;
                    if (GAME_RUNNING) {
                        if (players.get(0).equals(player)) {
                            if (lastCardPlaced.canBePlayed(cardPlayed)) {
                                Card toRemove = null;
                                for (Card c : player.getCards()) {
                                    if (c.getColor().equals(cardPlayed.getColor()) && c.getNum() == cardPlayed.getNum()) {
                                        toRemove = c;
                                        break;
                                    }
                                }
                                player.getCards().remove(toRemove);

                                lastCardPlaced = cardPlayed;
                                deck.add(cardPlayed);
                                if (cardPlayed.getNum() == 'R') {
                                    DIRECTION = true;
                                }
                                if (cardPlayed.getNum() == 'S') {
                                    skipNextTurn = true;
                                }
                                if (cardPlayed.getNum() == 'D' && !cardPlayed.getColor().startsWith("BLACK")) {
                                    Util.log(player.getUsername() + " " + cardPlayed + " Draw 2");
                                    players.get(1).addCard(popTop(deck));
                                    players.get(1).addCard(popTop(deck));
                                }
                                if (cardPlayed.getNum() == 'D' && cardPlayed.getColor().startsWith("BLACK")) {
                                    Util.log(player.getUsername() + " " + cardPlayed + " Draw 4");
                                    try {
                                        players.get(1).addCard(popTop(deck));
                                        players.get(1).addCard(popTop(deck));
                                        players.get(1).addCard(popTop(deck));
                                        players.get(1).addCard(popTop(deck));
                                            /*player.incrementPlus4Placed();
                                            player.save();*/
                                    } catch (Exception e) {

                                    }

                                }
                                if (player.getCards().size() == 1) {
                                    if (!player.UNO) {
                                        Util.log(player.getUsername() + " " + cardPlayed + " Forgot UNO, Draw 2");
                                        player.addCard(popTop(deck));
                                        player.addCard(popTop(deck));
                                    }
                                }
                                if (player.UNO && player.getCards().size() > 2) {
                                    Util.log(player.getUsername() + " " + cardPlayed + " Draw 2 - Uno with too many Cards");
                                    player.addCard(popTop(deck));
                                    player.addCard(popTop(deck));
                                }
                                if (cardPlayed.getNum() == 'W' || (cardPlayed.getNum() == 'D' && cardPlayed.getColor().startsWith("BLACK"))) {

                                } else {
                                    manageNextPlayer();
                                }
                                player.UNO = false;
                            }
                            if (player.getCards().isEmpty()) {
                                List<Player> playerCards = new ArrayList<>(List.copyOf(players));
                                playerCards.sort(new Comparator<Player>() {
                                    @Override
                                    public int compare(Player o1, Player o2) {
                                        return o1.getCards().size() - o2.getCards().size();
                                    }
                                });
                                player.incrementGamesWon();
                                String placement = "";
                                for (Player pla : List.copyOf(players)) {
                                    pla.incrementGamesPlayed();
                                    pla.save();
                                    placement = "";
                                    int place = 0;
                                    int finishPlace = 0;
                                    for (Player pl : playerCards) {
                                        place++;
                                        placement += place + ". " + pl.getDisplayName() + ", " + pl.getCards().size() + " Card(s) left\n";
                                        if (pla.getUsername().equals(player.getUsername())) finishPlace = place;
                                    }
                                    //placement = "Placement: " + finishPlace + ". Platz\n\n" + placement;


                                    Util.log("Player won to " + pla.getDisplayName());
                                    pla.handler.sendObject(new PlayerHasWonPacket(player.getDisplayName() + " has won! \n\nYou had " + pla.getCards().size() + " cards left.\n\n" + placement));
                                }
                                GAME_RUNNING = false;
                            }
                        }
                    }
                } else if (p instanceof DrawCard) {
                    if (GAME_RUNNING) {
                        Util.log(player.getUsername() + " has drawn a card");
                        if (players.get(0).equals(player)) {
                            if (!player.hasDrawnCardThisRound) {
                                player.hasDrawnCardThisRound = true;
                                player.addCard(popTop(deck));
                            }
                        }
                    }
                } else if (p instanceof SkipTurnPacket) {
                    if (GAME_RUNNING) {
                        Util.log(player.getUsername() + " has skipped their turn");
                        if (players.get(0).equals(player)) {
                            if (player.hasDrawnCardThisRound) {
                                manageNextPlayer();
                            }
                        }
                    }
                } else if (p instanceof UNOPacket) {
                    Util.log(player.getUsername() + " UNO!");
                    player.UNO = true;
                } else if (p instanceof SetColorPacket packet) {
                    overridenColor = packet.color;
                    lastCardPlaced.setColor("BLACK - " + overridenColor);
                    Util.log(lastCardPlaced.getColor());
                    manageNextPlayer();
                } else if (p instanceof CreateAccountPacket packet) {
                    Util.log("Account Packet " + packet.getUsername());
                    Userdata userdata = Userdata.read(packet.getUsername());
                    if (userdata == null) {
                        Util.log("Creating account " + packet.getUsername());
                        userdata = new Userdata(0, 0, 0, packet.getUsername(), packet.getDisplayName(), packet.getPassword());
                        Userdata.write(userdata);
                        Util.log("Successfully created account " + userdata.getUsername());
                        handler.disconnect();
                    } else {
                        Util.log("Changing nickname of " + packet.getUsername() + " to " + packet.getDisplayName());
                        if (!userdata.getPassword().equals(packet.getPassword())) {
                            Util.log("Incorrect Password");
                            handler.disconnect();
                        } else {
                            userdata.setDisplayName(packet.getDisplayName());
                            Userdata.write(userdata);
                            Util.log("Successfully changed account nickname of " + userdata.getUsername());
                            handler.disconnect();
                        }
                    }
                }
            }, h);
        });

        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Object LOCK = new Object();
                Thread waiter = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            synchronized (LOCK) {
                                try {
                                    LOCK.wait(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            synchronized (LOCK) {
                                LOCK.notify();
                            }
                        }
                    }
                });
                waiter.start();

                while (true) {
                    boolean allAgreed = true;
                    synchronized (this) {
                        if (players.size() < MIN_PLAYERS) {
                            startingIn = COUNTDOWN;
                            continue;
                        }
                        for (Player p : players) {
                            if (!p.ready) {
                                allAgreed = false;
                                break;
                            }
                        }
                    }

                    if (!allAgreed) continue;
                    CANCEL_COUNTDOWN = false;

                    Util.log("Starting a game in " + startingIn);
                    synchronized (LOCK) {
                        while (startingIn > 0) {
                            if (CANCEL_COUNTDOWN) {
                                break;
                            }
                            try {
                                LOCK.wait();
                                //Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            startingIn--;
                            Util.log("Starting a game in " + startingIn);
                        }
                    }
                    if (CANCEL_COUNTDOWN) {
                        CANCEL_COUNTDOWN = false;
                        continue;
                    }
                    Util.log("Starting a game");

                    int decks = players.size() / 4;
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
                    for (Player p : players) {
                        for (int i = 0; i < CARDS_PLAYER; i++) {
                            p.addCard(popTop(deck));
                        }
                        Util.log("Player: " + p.getUsername() + " " + p.getCards().size());
                    }

                    Util.log("Cards left " + deck.size());

                    lastCardPlaced = deck.get(0); //TODO: first card might be broken

                    while (GAME_RUNNING) {
                        if (players.isEmpty()) {
                            GAME_RUNNING = false;
                            break;
                        }
                        sendClientData();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    for (Player p : players) {
                        p.handler.disconnect();
                    }
                    players.clear();
                    deck.clear();
                    Util.log("Reset Server");
                }

            }
        });
        gameThread.start();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    while (true) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (!GAME_RUNNING) {
                            List<Player> toRemove = new ArrayList<>();
                            for (Player p : List.copyOf(players)) {
                                if (p.handler != null && !p.handler.getSocket().isClosed()) {
                                    HashMap<String, Boolean> prePlayers = new HashMap<>();
                                    for (Player pl : List.copyOf(players)) {
                                        prePlayers.put(pl.getDisplayName(), pl.ready);
                                    }
                                    if (!p.handler.getSocket().isClosed())
                                        p.handler.sendObject(new PreGamePacket(prePlayers, startingIn, MIN_PLAYERS, CARDS_PLAYER,p.userdata));
                                } else if (p.handler != null) {
                                    toRemove.add(p);
                                }
                            }
                            players.removeAll(toRemove);
                        }
                    }
                }
            }
        });
        networkThread.start();
        start();
    }

    public static void sendClientData() {
        List<Player> toRemove = new ArrayList<>();
        for (Player p : List.copyOf(players)) {
            if (p.handler != null && !p.handler.getSocket().isClosed()) {
                List<String> nextUp = new ArrayList<>();
                int c = 1;

                if (startingIn == 0) {
                    for (Player pl : List.copyOf(players)) {
                        if (pl.UNO)
                            nextUp.add(c + ". " + pl.getDisplayName() + " (" + pl.getCards().size() + " cards left) UNO!");
                        else nextUp.add(c + ". " + pl.getDisplayName() + " (" + pl.getCards().size() + " cards left)");
                        c++;
                    }
                    if (p.handler.isConnected() && !players.isEmpty())
                        p.handler.sendObject(new ClientDataPacket(List.copyOf(p.getCards()), new Card(lastCardPlaced), players.get(0).equals(p), players.get(0).getDisplayName(), !p.hasDrawnCardThisRound, p.hasDrawnCardThisRound, List.copyOf(nextUp), p.placement));
                } else {
                    for (Player pl : List.copyOf(players)) {
                        if (pl.ready) nextUp.add(c + ". " + pl.getDisplayName() + " (READY)");
                        else nextUp.add(c + ". " + pl.getDisplayName());
                        c++;
                    }
                    nextUp.add("Starting in " + startingIn + " seconds");
                    if (p.handler.isConnected() && !players.isEmpty())
                        p.handler.sendObject(new ClientDataPacket(new ArrayList<>(), null, false, players.get(0).getDisplayName(), false, false, List.copyOf(nextUp), p.placement));
                }
            } else if (p.handler != null) {
                toRemove.add(p);
            }
        }
        players.removeAll(toRemove);
    }

    public static List<Card> shuffleList(List<Card> list) {
        Random rand = new Random();
        List<Card> cardsShuffled = new ArrayList<>();
        for (Card c : list) {
            int index = 0;
            if (!cardsShuffled.isEmpty()) {
                index = rand.nextInt(cardsShuffled.size());
            }
            cardsShuffled.add(index, c);
        }
        return cardsShuffled;
    }

    public static Card popTop(List<Card> deck) {
        Card c = deck.get(0);
        deck.remove(0);
        return c;
    }

    public static Card popBottom(List<Card> deck) {
        Card c = deck.get(deck.size() - 1);
        deck.remove(deck.size() - 1);
        return c;
    }

    public static void manageNextPlayer() {
        Util.log("Next Player");
        Player current = players.get(0);
        current.hasDrawnCardThisRound = false;
        players.remove(0);
        players.add(current);
        if (skipNextTurn) {
            skipNextTurn = false;
            manageNextPlayer();
        }
        if (DIRECTION) {
            Collections.reverse(players);
            DIRECTION = false;
            manageNextPlayer();
        }
    }
}
