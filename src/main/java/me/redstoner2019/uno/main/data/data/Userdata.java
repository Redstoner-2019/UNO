package me.redstoner2019.uno.main.data.data;

import me.redstoner2019.server.defaultpackets.Packet;
import me.redstoner2019.server.util.Util;
import org.json.JSONObject;

import java.io.File;

public class Userdata extends Packet {
    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private int plus4Placed = 0;
    private String username = "";
    private String displayName = "";
    private String password = "";
    public Userdata(){

    }

    public Userdata(int gamesPlayed, int gamesWon, int plus4Placed, String username, String displayName, String password) {
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.plus4Placed = plus4Placed;
        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        object.put("games-played",gamesPlayed);
        object.put("games-won",gamesWon);
        object.put("plus-4-placed",plus4Placed);
        object.put("username",username);
        object.put("displayname",displayName);
        object.put("password",password);
        return object;
    }
    public static Userdata read(String username){
        Userdata data = new Userdata();
        File dataFile = new File("playerdata.json");
        if(!dataFile.exists()){
            try {
                dataFile.createNewFile();
                Util.writeStringToFile(new JSONObject().toString(),dataFile);
            } catch (Exception e) {
                return null;
            }
        }
        try {
            JSONObject jsonData = new JSONObject(Util.readFile(new File("playerdata.json")));
            if(!jsonData.has(username)) return null;
            jsonData = jsonData.getJSONObject(username);
            data.setDisplayName(jsonData.getString("displayname"));
            data.setGamesPlayed(jsonData.getInt("games-played"));
            data.setGamesWon(jsonData.getInt("games-won"));
            data.setPassword(jsonData.getString("password"));
            data.setPlus4Placed(jsonData.getInt("plus-4-placed"));
            data.setUsername(username);
        } catch (Exception e) {
            return null;
        }
        return data;
    }
    public static void write(Userdata data){
        try {
            JSONObject object = new JSONObject(Util.readFile(new File("playerdata.json")));
            File dataFile = new File("playerdata.json");

            object.put(data.username,data.toJSON());

            System.out.println(object.toString());

            Util.writeStringToFile(Util.prettyJSON(object.toString()),dataFile);
        } catch (Exception e) {
            System.out.println("Error writing ");
        }
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getPlus4Placed() {
        return plus4Placed;
    }

    public void setPlus4Placed(int plus4Placed) {
        this.plus4Placed = plus4Placed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Userdata{" +
                "gamesPlayed=" + gamesPlayed +
                ", gamesWon=" + gamesWon +
                ", plus4Placed=" + plus4Placed +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}