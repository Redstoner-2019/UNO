package me.redstoner2019.serverhandling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static int PORT = 3030;
    private static ClientConnectEvent clientConnectEvent;
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static java.util.List<ClientHandler> getClients() {
        return clients;
    }

    public static void setup(int port){
        PORT = port;
    }
    public static void start(){
        try {
            Util.log("Starting Server on Port " + InetAddress.getLocalHost().getHostAddress() + ":" +  PORT);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Util.log("Couldn't start Server");
            return;
        }
        while (serverSocket.isBound()){
            Util.log("Waiting for connection...");
            try{
                final Socket socket = serverSocket.accept();
                final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ClientHandler handler = new ClientHandler(in,out,socket,"");
                            clients.add(handler);
                            if(clientConnectEvent != null) clientConnectEvent.connectEvent(handler);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }});t.start();
                Util.log("A client has connected! " + socket.getInetAddress());
            }catch (Exception ignored){}
        }
    }

    public static void setClientConnectEvent(ClientConnectEvent e){
        clientConnectEvent = e;
    }
    public static void startPacketListener(final PacketListener listener, final ClientHandler handler){
        System.out.println("Started Packet Listener");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                while (run){
                    Object o = null;
                    try {
                        o = handler.getIn().readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        clients.remove(handler);
                        Util.log("Client disconnected"); //8008135
                        run = false;
                        break;
                    }
                    listener.packetRecievedEvent(o, handler);
                }
            }
        });
        t.start();
    }
    public static void sendObject(Object packet, ObjectOutputStream out, Socket socket){
        if(!socket.isConnected()){
            Util.log("Not Connected, can't send Object.");
            return;
        }
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            Util.log("Error sending Object.");
        }
    }
    public static void broadcastMessage(Object packet){
        for(ClientHandler c : clients){
            c.sendObject(packet);
        }
    }
    public static void broadcastMessage(Object packet, ClientHandler except){
        System.out.println(clients.size() + " Broadcasting " + packet);
        for(ClientHandler c : clients){
            if(c.equals(except)) continue;
            c.sendObject(packet);
        }
    }
}