package me.redstoner2019.serverhandling;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LocalNetworkScanner {
    private static final int PORT = 8008;
    private static final int TIMEOUT = 100;
    private static final Object LOCK = new Object();

    public static List<String> scan() throws IOException {
        List<String> serverList = new ArrayList<>();
        System.out.println(InetAddress.getLocalHost().getHostAddress().replace(".", "-"));
        String[] ipArr = InetAddress.getLocalHost().getHostAddress().replace(".", "-").split("-");

        final int[] ipsScanned = {0};

        for (int i = 1; i <= 255; i++) {
            int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String ip = ipArr[0] + "." + ipArr[1] + "." + ipArr[2] + "." + finalI;
                    System.out.println("Scanning " + ip);
                    if (isReachable(ip) && isOpen(ip, PORT)) {
                        serverList.add(ip);
                    }
                    ipsScanned[0]++;
                    System.out.println(ipsScanned[0]);
                    if (ipsScanned[0] >= 253) {
                        synchronized (LOCK) {
                            LOCK.notify();
                        }
                    }
                }
            });
            t.start();
        }
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return serverList;
    }
    private static boolean isReachable(String ip) {
        try {
            return InetAddress.getByName(ip).isReachable(100);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isOpen(String ip, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port), TIMEOUT);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e){
                // Ignore Exception
            }
        }
    }
}