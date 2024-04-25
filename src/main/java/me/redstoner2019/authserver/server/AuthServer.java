package me.redstoner2019.authserver.server;

import me.redstoner2019.authserver.data.Token;
import me.redstoner2019.authserver.packets.JSONPacket;
import me.redstoner2019.server.events.ClientConnectEvent;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.odserver.ClientHandler;
import me.redstoner2019.server.odserver.ODServer;
import me.redstoner2019.server.util.ConnectionProtocol;
import me.redstoner2019.server.util.Util;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Handler;

public class AuthServer extends ODServer {
    public static HashMap<String, Token> tokens = new HashMap<>();
    public static HashMap<String, Token> usernameTokens = new HashMap<>();
    public static JSONObject userdata = new JSONObject();
    public static File userdataFile = new File("userdata.json");

    public static void saveData(){
        try {
            Util.writeStringToFile(Util.prettyJSON(userdata.toString()),userdataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        if(!userdataFile.exists()){
            userdataFile.createNewFile();
            Util.writeStringToFile("{ }",userdataFile);
        }
        userdata = new JSONObject(Util.readFile(userdataFile));

        setup(8009, ConnectionProtocol.UDP);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    usernameTokens.keySet().removeIf(username -> !usernameTokens.get(username).isValid());
                    tokens.keySet().removeIf(username -> !tokens.get(username).isValid());
                }
            }
        });
        t.start();

        setClientConnectEvent(new ClientConnectEvent() {
            @Override
            public void connectEvent(ClientHandler clientHandler) throws Exception {
                clientHandler.startPacketSender();
                clientHandler.startPacketListener(new PacketListener() {
                    @Override
                    public void packetRecievedEvent(Object o) {
                        if(!(o instanceof JSONPacket)) return;
                        JSONObject data = new JSONObject(((JSONPacket) o).getJson());
                        Util.log("");
                        Util.log("");
                        Util.log("Recieved request " + data.getString("header") + " - " + data.getString("request"));
                        if(data.has("header")){
                            switch (data.getString("header")){
                                case "client":{
                                    switch (data.getString("request")){
                                        case "token-info":{
                                            if(!tokens.containsKey(data.getString("token"))){
                                                JSONObject result = new JSONObject();
                                                result.put("header","token-info");
                                                result.put("available","false");
                                                clientHandler.sendObject(new JSONPacket(result.toString()));
                                                break;
                                            }
                                            Token token = tokens.get(data.getString("token"));
                                            String username = token.getUsername();
                                            JSONObject user = userdata.getJSONObject(username);
                                            user.put("header","token-info");
                                            user.put("available","true");
                                            user.put("username",username);
                                            clientHandler.sendObject(new JSONPacket(user.toString()));
                                            break;
                                        }
                                        case "account-info":{
                                            String username = data.getString("username");
                                            if(!userdata.has(data.getString("username"))){
                                                JSONObject result = new JSONObject();
                                                result.put("header","account-info");
                                                result.put("available","false");
                                                clientHandler.sendObject(new JSONPacket(result.toString()));
                                                break;
                                            }
                                            JSONObject user = userdata.getJSONObject(username);
                                            user.put("header","account-info");
                                            user.put("available","false");
                                            user.put("username",username);
                                            clientHandler.sendObject(new JSONPacket(user.toString()));
                                            break;
                                        }
                                        case "login": {
                                            String username = data.getString("username");
                                            String password = data.getString("password");

                                            JSONObject response = new JSONObject();
                                            try{
                                                if(userdata.has(username)){
                                                    JSONObject user = userdata.getJSONObject(username);
                                                    Util.log("Checking password");
                                                    if(user.has("password") && user.getString("password").equals(password)){
                                                        response.put("header","login-complete");
                                                        Util.log("Creating response");
                                                        if(!usernameTokens.containsKey(username)){
                                                            Token token = Token.createToken(username);
                                                            Util.log("Token created for " + username + ": " + token.getToken());
                                                            tokens.put(token.getToken(), token);
                                                            usernameTokens.put(token.getUsername(),token);
                                                        }
                                                        response.put("token",usernameTokens.get(username).getToken());
                                                    } else {
                                                        response.put("header","invalid-password");
                                                    }
                                                } else {
                                                    response.put("header","invalid-username");
                                                }
                                            }catch (Exception e){
                                                System.out.println(e.getLocalizedMessage());
                                                response.put("info",e.getLocalizedMessage());
                                                response.put("header","login-incomplete");
                                                e.printStackTrace();
                                            }
                                            clientHandler.sendObject(new JSONPacket(response.toString()));
                                            break;
                                        }
                                        case "create-account": {
                                            try{
                                                String username = data.getString("username");
                                                String displayname = data.getString("displayname");
                                                String password = data.getString("password");
                                                JSONObject response = new JSONObject();
                                                if(userdata.has(username)) {
                                                    response.put("header","account-already-exists");
                                                    clientHandler.sendObject(new JSONPacket(response.toString()));
                                                    return;
                                                }
                                                JSONObject user = new JSONObject();
                                                user.put("displayname",displayname);
                                                user.put("password",password);
                                                userdata.put(username,user);
                                                saveData();
                                                response.put("header","created-account");
                                                clientHandler.sendObject(new JSONPacket(response.toString()));
                                            }catch (Exception e){
                                                Util.log(e.getLocalizedMessage());
                                            }
                                            break;
                                        }
                                        case "delete-account":{
                                            String username = data.getString("username");
                                            userdata.remove(username);
                                            JSONObject response = new JSONObject();
                                            response.put("header","delete-account");
                                            response.put("result","success");
                                            clientHandler.sendObject(new JSONPacket(response.toString()));
                                            break;
                                        }
                                        case "change-password": {
                                            String username = data.getString("username");
                                            String password = data.getString("password");
                                            JSONObject user = userdata.getJSONObject(username);
                                            user.put("password",password);
                                            userdata.put(username,user);
                                            break;
                                        }
                                        case "change-displayname": {
                                            String username = data.getString("username");
                                            String displayname = data.getString("displayname");
                                            JSONObject user = userdata.getJSONObject(username);
                                            user.put("displayname",displayname);
                                            userdata.put(username,user);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case "server":{
                                    switch (data.getString("request")){
                                        case "token-info":{
                                            if(!tokens.containsKey(data.getString("token"))){
                                                JSONObject result = new JSONObject();
                                                result.put("header","token-info");
                                                result.put("available","false");
                                                clientHandler.sendObject(new JSONPacket(result.toString()));
                                                break;
                                            }
                                            Token token = tokens.get(data.getString("token"));
                                            String username = token.getUsername();
                                            JSONObject user = userdata.getJSONObject(username);
                                            user.put("header","token-info");
                                            user.put("available","true");
                                            user.put("username",username);
                                            clientHandler.sendObject(new JSONPacket(user.toString()));
                                            break;
                                        }
                                        case "account-info":{
                                            String username = data.getString("username");
                                            if(!userdata.has(data.getString("username"))){
                                                JSONObject result = new JSONObject();
                                                result.put("header","account-info");
                                                result.put("available","false");
                                                clientHandler.sendObject(new JSONPacket(result.toString()));
                                                break;
                                            }
                                            JSONObject user = userdata.getJSONObject(username);
                                            user.put("header","account-info");
                                            user.put("available","false");
                                            user.put("username",username);
                                            clientHandler.sendObject(new JSONPacket(user.toString()));
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
        start();
    }
}
