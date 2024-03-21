package com.company;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

//classe usata per deserializzare
public class Hotel {
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private String[] services;
    private double rate;
    private ConcurrentHashMap<String, Double> ratings;
    transient int nOfVote = 0;
    transient LocalDateTime lastVoteDate;
    transient double totalScore = 0;
    transient double[] totalSingleScore = {0, 0, 0, 0};
    transient int position = 0;


    //metodo per stampare le informazioni rilevanti
    public void printAll() {

        DecimalFormat df = new DecimalFormat("0.00");
        double a;
        System.out.println("name: " + name);
        System.out.println("descrizione: " + description);
        System.out.println("citta': " + city);
        System.out.println("telefono: " + phone);

        System.out.println("servizi: ");
        for (String service : services) {
            System.out.println(service);
        }

        System.out.println("voto globale: " + df.format(rate));

        System.out.println("voti alle singole categorie: ");

        a=ratings.get("cleaning");
        System.out.print("cleaning: ");
        System.out.println(df.format(a));

        a=ratings.get("position");
        System.out.print("position: ");
        System.out.println(df.format(a));

        a=ratings.get("services");
        System.out.print("services: ");
        System.out.println(df.format(a));

        a=ratings.get("quality");
        System.out.print("quality:  ");
        System.out.println(df.format(a));
    }
}

