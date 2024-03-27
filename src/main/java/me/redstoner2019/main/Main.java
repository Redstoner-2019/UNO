package me.redstoner2019.main;

import me.redstoner2019.main.data.data.Userdata;

public class Main {
    public static final boolean TEST_MODE = true;
    public static final String VERSION = "v1.3.0-alpha.5";
    public static void main(String[] args){
        /*Userdata userdata = new Userdata(5,2,2,"lukas","Lukas","test");
        Userdata.write(userdata);

        userdata = new Userdata(5,2,2,"halil","Halil","test");
        Userdata.write(userdata);*/

        System.out.println(Userdata.read("lukas"));

        Userdata userdata = Userdata.read("lukas");
        userdata.setGamesWon(userdata.getGamesWon()+1);
        Userdata.write(userdata);

        System.out.println(Userdata.read("lukas"));
    }
}