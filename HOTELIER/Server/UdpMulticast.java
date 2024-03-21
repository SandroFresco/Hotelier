package com.company;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;


public class UdpMulticast {
    //invio il messaggio tramite un pacchetto udp
    public void sendMulticast(String name, String city, Double vote,String nameAddress, int port) throws IOException {
        DatagramSocket serverSock = new DatagramSocket();
        String message = "Cambio di classifica, l'attuale hotel primo classificato e' " + name + " nella citta' di " + city+" con un voto globale di "+vote;
        byte[] send;

        //SEND
        send = message.getBytes(StandardCharsets.US_ASCII);
        DatagramPacket dp = new DatagramPacket(send, send.length, InetAddress.getByName(nameAddress), port);
        serverSock.send(dp);
        serverSock.close();
        serverSock.disconnect();
    }
    }
