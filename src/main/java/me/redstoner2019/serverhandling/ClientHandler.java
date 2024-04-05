package me.redstoner2019.serverhandling;

import me.redstoner2019.main.serverstuff.ServerMain;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ClientHandler {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private String username;
    private boolean connected = true;
    public String currentlySelectedChat = null;
    private Queue<Packet> toSend = new ArrayDeque<>();

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
                        connected = false;
                        run = false;
                        break;
                    }
                    try {
                        o = getIn().readObject();
                        ServerMain.packetsrecieved++;
                    } catch (ClassNotFoundException ignored) {
                        System.out.println("ClassNotFoundExeption");
                    } catch (EOFException ignored) {
                        System.out.println("EOFException");
                        try {
                            getIn().reset();
                        } catch (IOException e) {
                            System.out.println("Reset unsuccesful");
                        }
                    } catch (SocketException e) {
                        System.out.println(e.getLocalizedMessage());
                        if(e.getLocalizedMessage().equals("Connection reset")){
                            Server.getClients().remove(this);
                            Util.log("Client disconnected"); //8008135
                            connected = false;
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
        toSend.add((Packet) packet);
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
