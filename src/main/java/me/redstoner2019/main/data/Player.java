package me.redstoner2019.main.data;

import me.redstoner2019.main.data.data.Userdata;
import me.redstoner2019.serverhandling.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> cards = new ArrayList<>();
    private boolean loginComplete = false;
    private String username = "";
    public boolean ready = false;
    public ClientHandler handler = null;
    public boolean hasDrawnCardThisRound = false;
    public String placement = "";
    public boolean UNO = false;
    public Userdata userdata;

    @Override
    public String toString() {
        return "Player{" +
                "cards=" + cards +
                ", loginComplete=" + loginComplete +
                ", username='" + username + '\'' +
                ", ready=" + ready +
                ", handler=" + handler +
                ", hasDrawnCardThisRound=" + hasDrawnCardThisRound +
                ", placement='" + placement + '\'' +
                ", UNO=" + UNO +
                '}';
    }

    public Player(List<Card> cards, boolean loginComplete, String username) {
        this.cards = cards;
        this.loginComplete = loginComplete;
        this.username = username;
        this.userdata = Userdata.read(username);
        System.out.println(Userdata.read(username));
    }

    public Player(){

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

    public boolean isLoginComplete() {
        return loginComplete;
    }

    public void setLoginComplete(boolean loginComplete) {
        this.loginComplete = loginComplete;
    }

    public String getUsername() {
        return username;
    }
    public String getDisplayName(){
        if(userdata == null) return "unavailable";
        return userdata.getDisplayName();
    }

    public void setUsername(String username) {
        this.username = username;
        this.userdata = Userdata.read(username);
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
    public void addCards(List<Card> cards){
        this.cards.addAll(cards);
    }
}
