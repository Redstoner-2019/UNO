package me.redstoner2019.main.data;

import me.redstoner2019.main.serverstuff.ServerMain;
import me.redstoner2019.serverhandling.Packet;

import java.util.ArrayList;
import java.util.List;

public class Card extends Packet {
    private String color = null;
    private char num = 0;
    private static List<Card> generateColor(String color){
        return List.of(new Card(color, '0'),
                new Card(color, '1'),
                new Card(color, '1'),
                new Card(color, '2'),
                new Card(color, '2'),
                new Card(color, '3'),
                new Card(color, '3'),
                new Card(color, '4'),
                new Card(color, '4'),
                new Card(color, '5'),
                new Card(color, '5'),
                new Card(color, '6'),
                new Card(color, '6'),
                new Card(color, '7'),
                new Card(color, '7'),
                new Card(color, '8'),
                new Card(color, '8'),
                new Card(color, '9'),
                new Card(color, '9'),
                new Card(color, 'S'), //SKIP
                new Card(color, 'S'),
                new Card(color, 'R'), //REVERSE
                new Card(color, 'R'),
                new Card(color, 'D'), //DRAW
                new Card(color, 'D'));
    }

    public Card(String color, char num) {
        this.color = color;
        this.num = num;
    }
    public Card(){
        color = "RED";
        num = '0';
    }

    public static List<Card> getDECK() {
        List<Card> DECK = new ArrayList<>();
        DECK.addAll(generateColor("RED"));
        DECK.addAll(generateColor("BLUE"));
        DECK.addAll(generateColor("GREEN"));
        DECK.addAll(generateColor("YELLOW"));
        DECK.add(new Card("BLACK",'D'));
        DECK.add(new Card("BLACK",'D'));
        DECK.add(new Card("BLACK",'D'));
        DECK.add(new Card("BLACK",'D'));
        DECK.add(new Card("BLACK",'W'));
        DECK.add(new Card("BLACK",'W'));
        DECK.add(new Card("BLACK",'W'));
        DECK.add(new Card("BLACK",'W'));
        return List.copyOf(DECK);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public char getNum() {
        return num;
    }

    public void setNum(char num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Card{" +
                "color='" + color + '\'' +
                ", num=" + num +
                '}';
    }
    public boolean canBePlayed(Card card){
        if(card.color.equals("BLACK") && color.equals("BLACK")) return false;
        if(card.color.equals(color) || card.num == num){
            return true;
        }
        if(color.equals("BLACK")){
            if(num == 'W'){
                return card.color.equals(ServerMain.overridenColor);
            }
            return true;
        }else return card.color.equals("BLACK");
    }
}
