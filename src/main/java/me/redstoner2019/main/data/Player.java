package me.redstoner2019.main.data;

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
    }

    public Player(){

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

    public void setUsername(String username) {
        this.username = username;
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
