package me.redstoner2019.main.data;

import me.redstoner2019.main.data.data.Userdata;
import me.redstoner2019.serverhandling.ClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username);
    }

    private List<Card> cards = new ArrayList<>();
    private String username = "";
    private boolean loggedIn = false;
    private boolean canDraw = false;
    private boolean canSkip = false;
    private boolean canUNO = false;
    private boolean UNO = false;
    private Userdata userdata;
    private String displayName;
    private String gameID = "";
    private ClientHandler handler;
    public void reset(){
        this.canDraw = false;
        this.canSkip = false;
        this.canUNO = false;
        this.UNO = false;
        this.cards = new ArrayList<>();
    }

    public ClientHandler getHandler() {
        return handler;
    }

    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return "Player{" +
                "cards=" + cards +
                ", username='" + username + '\'' +
                ", UNO=" + UNO +
                '}';
    }

    public Player(List<Card> cards, String username) {
        this.cards = cards;
        this.username = username;
        this.userdata = Userdata.read(username);
        System.out.println(Userdata.read(username));
    }
    public Player(){

    }

    public Player(List<Card> cards, String username, boolean canDraw, boolean canSkip, boolean canUNO, boolean UNO, Userdata userdata, String displayName) {
        this.cards = cards;
        this.username = username;
        this.canDraw = canDraw;
        this.canSkip = canSkip;
        this.canUNO = canUNO;
        this.UNO = UNO;
        this.userdata = userdata;
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    public boolean isCanSkip() {
        return canSkip;
    }

    public void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip;
    }

    public boolean isCanUNO() {
        return canUNO;
    }

    public void setCanUNO(boolean canUNO) {
        this.canUNO = canUNO;
    }

    public boolean isUNO() {
        return UNO;
    }

    public void setUNO(boolean UNO) {
        this.UNO = UNO;
    }

    public Userdata getUserdata() {
        return userdata;
    }

    public void setUserdata(Userdata userdata) {
        this.userdata = userdata;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void save(){
        Userdata.write(userdata);
    }
    public void incrementGamesPlayed(){
        userdata.setGamesPlayed(userdata.getGamesPlayed()+1);
    }
    public void incrementGamesWon(){
        userdata.setGamesWon(userdata.getGamesWon()+1);
    }
    public void incrementPlus4Placed(){
        userdata.setPlus4Placed(userdata.getPlus4Placed()+1);
    }

    public void setUsername(String username) {
        this.username = username;
        this.userdata = Userdata.read(username);
        if(userdata == null) this.displayName =  "unavailable";
        this.displayName = userdata.getDisplayName();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    public void addCard(Card c){
        cards.add(c);
    }
    public void removeCard(Card c){
        cards.remove(c);
    }
    public void addCards(List<Card> cards){
        this.cards.addAll(cards);
    }
}
