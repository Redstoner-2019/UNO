package me.redstoner2019.serverhandling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    public static Socket socket = null;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;
    public static PacketListener listener;
    public static boolean isConnected = false;
    public static ConnectionFailedEvent connectionFailEvent = null;
    public static ConnectionSuccessEvent connectionSuccessEvent = null;
    public static ConnectionLostEvent connectionLostEvent = null;

    public static void setConnectionLostEvent(ConnectionLostEvent connectionLostEvent) {
        Client.connectionLostEvent = connectionLostEvent;
    }

    public static boolean isConnected(){
        return isConnected;
    }
    public static void connect(String address, int port){
        try {
            socket = new Socket(address,port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            isConnected = true;
            if(connectionSuccessEvent != null) connectionSuccessEvent.onConnectionSuccess();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    while (socket.isConnected()){
                        if(in == null) continue;
                        if(listener == null){
                            continue;
                        }
                        try {
                            listener.packetRecievedEvent(in.readObject(), new ClientHandler(in,out,socket,""));
                        } catch (ClassNotFoundException ignored){
                            System.err.println("Class not found");
                        } catch (StreamCorruptedException ignored){
                            System.err.println("Stream corrupted");
                            break;
                        } catch (SocketException ignored){
                            System.err.println("Socket not connected");
                            System.err.println(ignored.getLocalizedMessage());
                            connectionLostEvent.onConnectionLostEvent();
                            break;
                        } catch (Exception e) {
                            System.err.println("Lukas du hurensohn was hast du getan dass dies ausgegeben wird");
                            System.err.println(e.getLocalizedMessage());
                            if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent();
                            try {
                                out.flush();
                            } catch (IOException ex) {}
                            break;
                        }
                    }
                    disconnect();
                }
            });
            t.start();
        } catch (SocketException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            System.err.println("Couldnt connect, socket exception!");
        } catch (UnknownHostException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            System.err.println("Unknown Host");
        } catch (IOException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            throw new RuntimeException(e);
        }
    }
    public static void sendObject(Object o){
        try {
            out.writeObject(o);
            out.flush();
        } catch (IOException e) {

        }
    }
    public static void setPacketListener(PacketListener packetListener){
        listener = packetListener;
    }
    public static void setConnectionFailedEvent(ConnectionFailedEvent listener){
        connectionFailEvent = listener;
    }
    public static void setOnConnectionSuccessEvent(ConnectionSuccessEvent listener){
        connectionSuccessEvent = listener;
    }
    public static void disconnect(){
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
