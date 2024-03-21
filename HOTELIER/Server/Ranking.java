package com.company;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Ranking extends Thread {
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> mapCity;
    private CopyOnWriteArrayList<String> city;
    private int time;
    private RmiServerImpl server;
    private String nameAddress;
    private final int port;

//Classe che si occupa di aggiornare il ranking locale degli hotel
    public Ranking(ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> mapCity, CopyOnWriteArrayList<String> city, int time, RmiServerImpl server, String nameAddress, int port) {
        this.mapCity = mapCity;
        this.city = city;
        this.time = time;
        this.server = server;
        this. nameAddress=nameAddress;
        this.port=port;
    }


    public void run() {
        while (true) {

            Map<Hotel, Integer> currentPositions = new ConcurrentHashMap<>();
            int[] currentRank = new int[mapCity.size()];


            //salvo le posizioni prima del ranking
            for (int i = 0; i < city.size(); i++) {
                List<Hotel> hotels = mapCity.get(city.get(i));
                int id = hotels.get(0).getId();
                currentRank[i] = id;
                for (Hotel hotel : hotels)
                    currentPositions.put(hotel, hotel.getPosition());
            }



            for (List<Hotel> hotels : mapCity.values()) {
                // Sorto le liste di hotel usando la classe HotelComparator
                hotels.sort(new HotelComparator());


                //setto le posizioni
                for (int i = 0; i < hotels.size(); i++) {
                    Hotel hotel = hotels.get(i);
                    hotel.setPosition(i + 1);

                    //se le posizioni sono cambiate notifico l'evento con rmi callback
                    if (hotel.getPosition() != currentPositions.get(hotel)) {
                        server.notifyHotelPositionChanged(hotel.getName(), currentPositions.get(hotel), hotel.getPosition(), hotel.getCity());
                    }
                }
            }

            //se la prima posizione è cambiata mando un messaggio multicast a tutti gli utenti loggati
            for (int i = 0; i < mapCity.size(); i++) {
                List<Hotel> hotel = mapCity.get(city.get(i));
                if (currentRank[i] != hotel.get(0).getId()) {
                    UdpMulticast udpMulticast = new UdpMulticast();
                    try {
                        udpMulticast.sendMulticast(hotel.get(0).getName(), city.get(i), hotel.get(0).getRate(),nameAddress, port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

