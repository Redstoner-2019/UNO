package me.redstoner2019.main.data;

import me.redstoner2019.main.Main;
import me.redstoner2019.serverhandling.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static me.redstoner2019.main.data.CardType.*;
import static me.redstoner2019.main.data.CardColor.*;

public class Card extends Packet implements Comparable<Card>{
    private CardColor color = RED;
    private CardType num = ZERO;
    private CardColor overrideColor = null;
    private static List<Card> generateColor(CardColor color){
        return List.of(new Card(color, ZERO,null),
                new Card(color, ONE,null),
                new Card(color, ONE,null),
                new Card(color, TWO,null),
                new Card(color, TWO,null),
                new Card(color, THREE,null),
                new Card(color, THREE,null),
                new Card(color, FOUR,null),
                new Card(color, FOUR,null),
                new Card(color, FIVE,null),
                new Card(color, FIVE,null),
                new Card(color, SIX,null),
                new Card(color, SIX,null),
                new Card(color, SEVEN,null),
                new Card(color, SEVEN,null),
                new Card(color, EIGHT,null),
                new Card(color, EIGHT,null),
                new Card(color, NINE,null),
                new Card(color, NINE,null),
                new Card(color, SKIP,null), //SKIP
                new Card(color, SKIP,null),
                new Card(color, REVERSE,null), //REVERSE
                new Card(color, REVERSE,null),
                new Card(color, DRAW,null), //DRAW
                new Card(color, DRAW,null));
    }
    public static List<Card> getFromOneColor(CardColor color){
        return List.of(new Card(color, ZERO,null),
                new Card(color, ONE,null),
                new Card(color, TWO,null),
                new Card(color, THREE,null),
                new Card(color, FOUR,null),
                new Card(color, FIVE,null),
                new Card(color, SIX,null),
                new Card(color, SEVEN,null),
                new Card(color, EIGHT,null),
                new Card(color, NINE,null),
                new Card(color, SKIP,null),
                new Card(color, REVERSE,null),
                new Card(color, DRAW,null));
    }

    public Card(CardColor color, CardType num, CardColor overrideColor) {
        this.color = color;
        this.num = num;
        this.overrideColor = overrideColor;
    }
    public Card(CardColor color, CardType num) {
        this.color = color;
        this.num = num;
        this.overrideColor = null;
    }
    public Card(){
        color = CardColor.RED;
        num = ZERO;
        overrideColor = null;
    }
    public Card(Card c){
        if(c!=null){
            this.color = c.getColor();
            this.num = c.getNum();
            this.overrideColor = c.getOverrideColor();
        } else {
            this.color = RED;
            this.num = ZERO;
            this.overrideColor = null;
        }
    }

    public static List<Card> getDECK() {
        List<Card> DECK = new ArrayList<>();
        DECK.addAll(generateColor(RED));
        DECK.addAll(generateColor(BLUE));
        DECK.addAll(generateColor(GREEN));
        DECK.addAll(generateColor(YELLOW));
        DECK.add(new Card(SPECIAL,PLUS_4));
        DECK.add(new Card(SPECIAL,PLUS_4));
        DECK.add(new Card(SPECIAL,PLUS_4));
        DECK.add(new Card(SPECIAL,PLUS_4));
        DECK.add(new Card(SPECIAL,CHANGE_COLOR));
        DECK.add(new Card(SPECIAL,CHANGE_COLOR));
        DECK.add(new Card(SPECIAL,CHANGE_COLOR));
        DECK.add(new Card(SPECIAL,CHANGE_COLOR));
        return List.copyOf(DECK);
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public CardType getNum() {
        return num;
    }

    public void setNum(CardType num) {
        this.num = num;
    }

    public CardColor getOverrideColor() {
        return overrideColor;
    }

    public void setOverrideColor(CardColor overrideColor) {
        this.overrideColor = overrideColor;
    }

    /**
     * Check if card can be played on this card.
     * @param card Card to be checked if it can be played on this card.
     * @return true if the card can be played.
     */
    public boolean canBePlayed(Card card){
        if(Main.TEST_MODE) return true;
        if(color == SPECIAL && card.getColor() == SPECIAL){
            return false;
        }
        if(color == card.getColor() || num == card.getNum()){
            return true;
        }
        if(card.getColor() == SPECIAL)return true;
        if(color == SPECIAL){
            if(card.overrideColor == null && card.getColor() == SPECIAL) card.overrideColor = RED;
            if(overrideColor == card.getColor()){
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Card o) {
        HashMap<CardColor,Integer> colors = new HashMap<>();
        colors.put(RED,1);
        colors.put(GREEN,2);
        colors.put(YELLOW,3);
        colors.put(BLUE,4);
        colors.put(SPECIAL,5);

        HashMap<CardType,Integer> types = new HashMap<>();
        int i = 0;
        for(CardType t : CardType.values()){
            i++;
            types.put(t,i);
        }

        if(colors.get(o.color) - colors.get(this.color) == 0){
            if(types.get(o.getNum())-types.get(this.getNum()) == 0){
                return 1;
            } else {
                return types.get(o.getNum())-types.get(this.getNum());
            }
        } else {
            return colors.get(o.color) - colors.get(this.color);
        }
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color.name() +
                ", num=" + num.name() +
                ", overrideColor=" + overrideColor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return color == card.color && num == card.num;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, num);
    }
    public String getExact(){
        return color + "-" + num + "-" + overrideColor;
    }
}

