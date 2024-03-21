package com.company;

import java.io.IOException;
import java.net.*;

public class Udp extends Thread {
    private  Login login;
    private final String Addressname;
    private final int port;



    public Udp(Login login, String Addressname, int port) {
        this.login = login;
        this.Addressname = Addressname;
        this.port = port;

    }

    public void run() {
        MulticastSocket socketClient = null;
        try {
            //creo una socket nella porta "port"
            socketClient = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetSocketAddress group = null;
        try {
            group = new InetSocketAddress(InetAddress.getByName(Addressname), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        NetworkInterface netIf = null;
        try {
            netIf = NetworkInterface.getByName("wlan");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            //entro a far parte del gruppo multicast
            socketClient.joinGroup(group, netIf);
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            //attendo i messaggi inviati dal server finché l'utente è loggato
            if (login.getIsLogged()) {
                boolean isReceived = true;
                byte[] a = new byte[150];
                DatagramPacket receivedPacket = new DatagramPacket(a, a.length);
                try {
                    //socket con timeout
                    socketClient.setSoTimeout(10000);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                try {
                    //ricevo i dati dal server
                    socketClient.receive(receivedPacket);
                } catch (IOException e) {
                    //non ho ricevuto i dati, è scaduto il timeout
                    isReceived = false;
                }
                if (isReceived) {
                    //dati ricevuti
                    String s = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                    System.out.println(s);
                }
            } else
                break;
        }
        //utente non loggato quindi chiudo il Thread
        try {
            socketClient.leaveGroup(group, netIf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketClient.close();
    }

}
