package me.redstoner2019.main.serverstuff;

import java.sql.*;
import java.util.*;

public class SQL {

    public static Connection connection = null;
    public static int SHORT = 15;
    public static int NORMAL = 50;
    public static List<Integer> shorts = List.of(1,6,7,8,9,10);

    public static void main(String[] args) {
        connect("192.168.178.24","3306","myDatabase","Redstoner","OD2023");
        try {
            recreateUserTable();
            for(String username : List.of("redstoner_2019","halulzen","nicocruw","phillipgiovannielberfeld","sprayD","myuutivated","asfalt","angefraggt","phillikulli")){
                Random random = new Random(username.hashCode());
                newEntry("username",username);
                setString(username,"password",Password.hashPassword("Eisenbahn.24"));
                setString(username,"displayname",username);
                setString(username,"gamesPlayed","0");
                setString(username,"gamesWon","0");
                setString(username,"specialFOUR","0");
                setString(username,"specialCHOOSE","0");
                setString(username,"totalPlaced","0");
                setString(username,"uuid",new UUID(random.nextLong(),random.nextLong()).toString());
            }
            printDatabase();
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.println("Query?");
                String query = scanner.nextLine();
                if(query.equals("stop")) break;
                print(query);
            }
            stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("\n");
            System.err.println(e.getLocalizedMessage());
        }
    }

    public static void connect(String address, String port, String database, String username, String password){
        //default port 3306
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);
            String url = "jdbc:mysql://" + address + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void dropTable(String table){
        try{
            String query = "DROP TABLE IF EXISTS " + table + ";";
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void recreateUserTable(){
        try{
            dropTable("users");
            String query = "CREATE TABLE users (userID INT KEY AUTO_INCREMENT, uuid varchar(255), username varchar(255), displayname varchar(255), password varchar(255),gamesPlayed varchar(255),gamesWon varchar(255),specialFOUR varchar(255),specialCHOOSE varchar(255),totalPlaced varchar(255), UNIQUE (username));";
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static boolean newEntry(String column, String data){
        try{
            String query = "INSERT INTO users (" + column + ") VALUES ('" + data + "');";
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
            return true;
        }catch (SQLIntegrityConstraintViolationException e){
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static String getString(String username, String column){
        try {
            String query = "SELECT * FROM users WHERE users.username = '" + username + "';";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String result = rs.getString(column);
            rs.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setString(String username, String column, String value){
        try {
            String query = "UPDATE users SET " + column + " = '" + value + "' WHERE username = '" + username + "';";
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printDatabase(){
        print("SELECT * FROM users");
    }

    public static void print(String query){
        int w;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            List<String> columnNames = new ArrayList<>();
            String divider = "#";

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if(shorts.contains(i)) w = SHORT;else w = NORMAL;
                divider+=String.format("%" + (w + 1) + "s#","").replaceAll(" ", "-");
            }

            System.out.println(divider);
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if(shorts.contains(i)) w = SHORT;else w = NORMAL;
                columnNames.add(rs.getMetaData().getColumnName(i));
                System.out.printf("| %-" + (w) + "s",rs.getMetaData().getColumnName(i));
            }
            System.out.println("|");
            System.out.println(divider);

            while (rs.next()){
                int i = 1;
                for(String s : columnNames){
                    if(shorts.contains(i)) w = SHORT;else w = NORMAL;
                    i++;
                    String data = String.format("| %-" + (w) + "s",rs.getString(s));
                    if(data.length() >= w+2) data = data.substring(0,w+1) + " ";
                    System.out.print(data);
                }
                System.out.println("|");
                System.out.println(divider);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void stop(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
