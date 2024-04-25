package me.redstoner2019.server.odclient;

import me.redstoner2019.server.defaultpackets.ACK;
import me.redstoner2019.server.defaultpackets.ConnectRequestPacket;
import me.redstoner2019.server.defaultpackets.Packet;
import me.redstoner2019.server.events.ConnectionFailedEvent;
import me.redstoner2019.server.events.ConnectionLostEvent;
import me.redstoner2019.server.events.ConnectionSuccessEvent;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.util.ConnectionProtocol;
import me.redstoner2019.server.util.Util;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import static me.redstoner2019.server.util.ConnectionProtocol.TCP;


public class ODClient {
    public Socket socket = null;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    public List<PacketListener> listener = new ArrayList<>();
    public boolean isConnected = false;
    public ConnectionFailedEvent connectionFailEvent = reason -> {};
    public ConnectionSuccessEvent connectionSuccessEvent = () -> {};
    public ConnectionLostEvent connectionLostEvent = reason -> {};
    private final List<Object> toSend = new ArrayList<>();
    private ConnectionProtocol protocol;
    public HashMap<String, PacketCache> packetCache = new HashMap<>();
    public List<String> deactivatedUUIDs = new ArrayList<>();
    private static final Object REFERENCE = new Object();

    public void setConnectionLostEvent(ConnectionLostEvent connectionLostEvent) {
        this.connectionLostEvent = connectionLostEvent;
    }

    public boolean isConnected(){
        return isConnected;
    }
    public void connect(String address, int port, ConnectionProtocol connProtocol){
        protocol = connProtocol;
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
                        e.printStackTrace();
                    }
                    while (!socket.isClosed()){
                        if(in == null) continue;
                        if(listener == null){
                            continue;
                        }
                        try {
                            Object o = in.readObject();
                            if(protocol.equals(TCP)) {
                                if (o instanceof ACK p) {
                                    if (packetCache.containsKey(p.getUuid())) {
                                        packetCache.remove(p.getUuid());
                                    }
                                } else if (o instanceof Packet p) {
                                    if(!deactivatedUUIDs.contains(p.uuid)){
                                        sendObject(new ACK(p.uuid, 0));
                                        callListener(o);
                                        deactivatedUUIDs.add(p.uuid);
                                    } else {

                                    }
                                } else {
                                    callListener(o);
                                }
                            } else {
                                callListener(o);
                            }
                        } catch (ClassNotFoundException ignored){
                            System.err.println("Class not found");
                            ignored.printStackTrace();
                        } catch (ClassCastException ignored){
                            System.err.println("Couldnt cast class");
                            ignored.printStackTrace();
                        } catch (StreamCorruptedException ignored){
                            System.err.println("Stream corrupted");
                            ignored.printStackTrace();
                            try {
                                in.reset();
                            } catch (IOException e) {
                                if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent(ignored.getLocalizedMessage());
                                break;
                            }
                        } catch (SocketException ignored){
                            if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent(ignored.getLocalizedMessage());
                            break;
                        } catch (EOFException ignored){
                            try {
                                in.reset();
                            } catch (IOException e) {
                                connectionLostEvent.onConnectionLostEvent(e.getLocalizedMessage());
                                disconnect();
                            }
                        }catch (Exception e) {
                            System.err.println("Lukas du hurensohn was hast du getan dass dies ausgegeben wird");
                            System.err.println("Localized message: " + e.getLocalizedMessage());
                            e.printStackTrace();
                            if(connectionLostEvent != null) connectionLostEvent.onConnectionLostEvent(e.getClass() + " " + e.getLocalizedMessage());
                            try {
                                out.flush();
                            } catch (IOException ex) {

                            }
                            break;
                        }
                    }
                    disconnect();
                }
            });
            t.start();
            sendObject(new ConnectRequestPacket(protocol));
        } catch (SocketException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            System.err.println("Couldnt connect, socket exception!");
            Util.log(e.getLocalizedMessage());
        } catch (UnknownHostException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            System.err.println("Unknown Host");
            Util.log(e.getLocalizedMessage());
        } catch (IOException e) {
            if(connectionFailEvent != null) connectionFailEvent.onConnectionFailedEvent(e);
            e.printStackTrace();
        }
    }
    public String lastObjectSendName = "";
    public void sendObject(Object o){
        try {
            toSend.add(o);
        }catch (Exception e){
            Util.log("Clearing Buffer");
            toSend.clear();
        }
    }
    public void startSender() {
        final long TIMEOUT = 2000;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Util.log("Starting sender");
                while (!isConnected()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                while (true){
                    synchronized (REFERENCE){
                        if(!isConnected) break;
                        Iterator<String> uuids = packetCache.keySet().iterator();
                        if(protocol.equals(TCP)) while (uuids.hasNext()){
                            try{
                                String uuid = uuids.next();
                                PacketCache pc = packetCache.get(uuid);
                                if(pc == null) continue;
                                if(System.currentTimeMillis() - pc.getSendTime() > TIMEOUT){
                                    sendObject(pc.getPacket());
                                    uuids.remove();
                                }
                            }catch (Exception e){}
                        }
                        //Util.log("[SENDER] running");
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        synchronized (REFERENCE){
                            if(!toSend.isEmpty()){
                                Object o = null;
                                try {
                                    if(!toSend.isEmpty()) o = toSend.get(0);
                                    if(!toSend.isEmpty()) toSend.remove(0);
                                    if(o == null) continue;
                                    if(protocol.equals(TCP)) if(o instanceof Packet p){
                                        if(!(p instanceof ACK) && p.uuid == null){
                                            p.uuid = Util.createUUID();
                                            packetCache.put(p.uuid,new PacketCache(System.currentTimeMillis(),p));
                                        }
                                    }
                                    lastObjectSendName = o.getClass().toString();
                                    out.writeObject(o);
                                    out.flush();
                                } catch (SocketException e){
                                    connectionLostEvent.onConnectionLostEvent(e.getLocalizedMessage());
                                    Util.log("Connection lost");
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                Util.log("Sender stopped");
            }
        });
        t.start();
    }

    public void callListener(Object o){
        for(PacketListener listener1 : listener) listener1.packetRecievedEvent(o);
    }
    public void setPacketListener(PacketListener packetListener){
        listener.add(packetListener);
    }
    public void setConnectionFailedEvent(ConnectionFailedEvent listener){
        this.connectionFailEvent = listener;
    }
    public void setOnConnectionSuccessEvent(ConnectionSuccessEvent listener){
        this.connectionSuccessEvent = listener;
    }
    public void disconnect(){
        try {
            socket.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int packetsInBuffer(){
        return toSend.size();
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