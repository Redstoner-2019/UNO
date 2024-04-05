package me.redstoner2019.serverhandling;

import me.redstoner2019.main.serverstuff.ServerMain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private String username;
    private boolean connected = true;
    public String currentlySelectedChat = null;

    public ClientHandler(ObjectInputStream in, ObjectOutputStream out, Socket socket, String username) {
        this.in = in;
        this.out = out;
        this.socket = socket;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public void disconnect(){
        try {
            socket.close();
            Client.isConnected = false;
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPacketListener(final PacketListener listener){
        System.out.println("Started Packet Listener");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                while (run){
                    Object o = null;
                    if(socket.isClosed()){
                        Server.getClients().remove(this);
                        Util.log("Client disconnected"); //8008135
                        run = false;
                    }
                    try {
                        o = getIn().readObject();
                        ServerMain.packetsrecieved++;
                    } catch (ClassNotFoundException ignored) {
                        System.out.println("ClassNotFoundExeption");
                    } catch (SocketException e) {
                        System.out.println(e.getLocalizedMessage());
                        if(e.getLocalizedMessage().equals("Connection reset")){
                            Server.getClients().remove(this);
                            Util.log("Client disconnected"); //8008135
                            run = false;
                            break;
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                        /*Server.getClients().remove(this);
                        Util.log("Client disconnected"); //8008135
                        run = false;
                        break;*/
                    }
                    listener.packetRecievedEvent((Packet) o);
                }
            }
        });
        t.start();
    }

    public void sendObject(Object packet){
        ServerMain.packetsSent++;
        if(socket.isClosed()){
            disconnect();
            return;
        }
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            disconnect();
            Util.log("Error sending Object.");
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
