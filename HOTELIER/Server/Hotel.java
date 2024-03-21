package com.company;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//classe per serializzare e deserializzare le informazioni degli Hotel
public class Hotel {
    private  int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private String[] services;
    private double rate;
    private ConcurrentHashMap<String, Double> ratings;
    private transient int nOfVote = 0;
    private transient LocalDateTime lastVoteDate;
    private transient double totalScore = 0;
    private transient double[] totalSingleScore = {0, 0, 0, 0};
    private transient int position = 0;

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPosition() {
        return position;
    }

    public synchronized LocalDateTime getLastVoteDate() {
        return lastVoteDate;
    }


    public synchronized double getRate() {
        return rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalSingleScore(double[] totalSingleScore) {
        this.totalSingleScore = totalSingleScore;
    }

    public synchronized double getAverageRating() {
        if (ratings.isEmpty()) {
            // Ritorna 0 se non ci sono voti
            return 0.0;
        }
        double sum = 0.0;
        for (double rating : ratings.values()) {
            sum += rating;
        }
        //calcola e ritorna la media dei voti
        return sum / ratings.size();
    }

    //non synchronized perché uso questo metodo una sola volta all'interno di un unico Thread
    public void setPosition(int position) {
        this.position = position;
    }

    public synchronized void setLastVoteDate(LocalDateTime lastVoteDate) {
        this.lastVoteDate = lastVoteDate;
    }

    public synchronized void incrementVote() {
        nOfVote++;
    }

    public synchronized int getnOfVote() {
        return nOfVote;
    }


    public synchronized void setRate(double rate) {
        this.rate = rate;
    }


    public synchronized void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public synchronized ConcurrentHashMap<String, Double> getRatings() {
        return ratings;
    }

    public synchronized double[] getTotalSingleScore() {
        return totalSingleScore;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return id == hotel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

