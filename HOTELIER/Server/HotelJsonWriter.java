package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

import java.util.concurrent.CopyOnWriteArrayList;

//Thread che periodicamente (ogni "time" millisecondi) serializza i dati degli hotel nel corrispettivo file json
public class HotelJsonWriter extends Thread {
    private CopyOnWriteArrayList<Hotel> hotels;
    private int time;

    public HotelJsonWriter(CopyOnWriteArrayList<Hotel> hotels, int time) {
        this.hotels = hotels;
        this.time = time;
    }


    public void run() {
        while(true) {
                if (!hotels.isEmpty()) {
                    try {
                        FileWriter fileWriter = new FileWriter("Hotels.json");
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String Json = gson.toJson(hotels);
                        fileWriter.write(Json);
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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

