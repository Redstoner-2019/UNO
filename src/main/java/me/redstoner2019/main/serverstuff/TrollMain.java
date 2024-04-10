package me.redstoner2019.main.serverstuff;

import me.redstoner2019.serverhandling.Client;
import me.redstoner2019.serverhandling.LocalNetworkScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrollMain extends Client {
    public static void main(String[] args) throws IOException {
        List<String> servers = new ArrayList<>();
        LocalNetworkScanner.scan(servers,45678);
        System.out.println(servers);
        if(servers.isEmpty()) return;
        System.out.println(servers.get(0));
        connect(servers.get(0),45678);
        sendObject("Hallo Philip!");
    }
}
