package me.redstoner2019.server.odserver;

import me.redstoner2019.server.defaultpackets.ACK;
import me.redstoner2019.server.defaultpackets.Packet;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.util.Util;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

import static me.redstoner2019.server.util.ConnectionProtocol.TCP;

public class ClientHandler {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private boolean connected = true;
    private List<Object> toSend = new ArrayList<>();
    private static final Object REFERENCE = new Object();
    public HashMap<String, PacketCache> packetCache = new HashMap<>();

    public ClientHandler(ObjectInputStream in, ObjectOutputStream out, Socket socket) {
        this.in = in;
        this.out = out;
        this.socket = socket;
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
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startPacketSender(){
        final long TIMEOUT = 2000;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    synchronized (REFERENCE){
                        if(!isConnected() || socket.isClosed()) {
                            toSend.clear();
                            break;
                        }

                        Iterator<String> uuids = packetCache.keySet().iterator();

                        if(ODServer.getProtocol().equals(TCP)) while (uuids.hasNext()){
                            try{
                                String uuid = uuids.next();
                                PacketCache pc = packetCache.get(uuid);
                                if(System.currentTimeMillis() - pc.getSendTime() > TIMEOUT){
                                    sendObject(pc.getPacket());
                                    uuids.remove();
                                }
                            } catch (Exception e){

                            }
                        }

                        if(toSend.isEmpty()) {
                            continue;
                        }
                        while (!toSend.isEmpty()){
                            Object o = toSend.get(0);
                            toSend.remove(0);

                            if(o == null) continue;

                            if(ODServer.getProtocol().equals(TCP)) if(o instanceof Packet p){
                                if(!(p instanceof ACK)){
                                    p.uuid = Util.createUUID();
                                    packetCache.put(p.uuid,new PacketCache(System.currentTimeMillis(),p));
                                }
                            }

                            try {
                                out.writeObject(o);
                                out.flush();
                            } catch (SocketException e){
                                disconnect();
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Util.log("Sender closed");
                Thread.currentThread().interrupt();
            }
        });
        t.start();
    }
    public void startPacketListener(final PacketListener listener){
        ClientHandler thisHandler = this;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Object o = null;
                    if(socket.isClosed()){
                        ODServer.getClients().remove(thisHandler);
                        Util.log("Client disconnected");
                        connected = false;
                        break;
                    }
                    try {
                        o = getIn().readObject();
                    } catch (ClassNotFoundException ignored) {
                        Util.log("ClassNotFoundExeption");
                    } catch (EOFException ignored) {
                        Util.log("EOFException");
                        try {
                            getIn().close();
                            setIn(new ObjectInputStream(socket.getInputStream()));
                        } catch (IOException e) {
                            Util.log("Reset unsuccesful");
                            disconnect();
                        }
                        break;
                    } catch (SocketException e) {
                        Util.log(e.getLocalizedMessage());
                        if(e.getLocalizedMessage().equals("Connection reset")){
                            ODServer.getClients().remove(thisHandler);
                            Util.log("Client disconnected"); //8008135
                            connected = false;
                            break;
                        }
                    } catch (StreamCorruptedException e){
                        Util.log("Stream corrupted");
                        disconnect();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    if(ODServer.getProtocol().equals(TCP)) {
                        if (o instanceof ACK p) {
                            packetCache.remove(p.getUuid());
                            System.out.println("ACK");
                        } else if (o instanceof Packet p) {
                            sendObject(new ACK(p.uuid, 0));
                            listener.packetRecievedEvent(o);
                        } else {
                            listener.packetRecievedEvent(o);
                        }
                    } else {
                        listener.packetRecievedEvent(o);
                    }
                }
            }
        });
        t.start();
    }

    public void sendObject(Object packet){
        try {
            toSend.add(packet);
        }catch (Exception e){
            Util.log("Clearing Buffer");
            toSend.clear();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    public int packetsInBuffer(){
        return toSend.size();
    }
    public int packetsInCache(){
        return packetCache.size();
    }
}

class PacketCache{
    private long sendTime;
    private Packet packet;

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public PacketCache(long sendTime, Packet packet) {
        this.sendTime = sendTime;
        this.packet = packet;
    }
}
