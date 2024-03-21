package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class HotelServices {
    private JsonServices jsonServices;
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> mapCity;
    private List<Review> accountReview;

    public HotelServices(JsonServices jsonServices) throws IOException {
        this.jsonServices = jsonServices;
        this.mapCity = jsonServices.getMapCity();
        accountReview = jsonServices.jsonReview(accountReview);
    }

    public synchronized boolean containsCity(String name) {
        return mapCity.containsKey(name);
    }

//-----------------------------------------------SEARCHOTEL-----------------------------------------------------------------------

    public synchronized Hotel searchHotel(String nameHotel, String nameCity) {
        CopyOnWriteArrayList<Hotel> hotels;
        hotels = mapCity.get(nameCity);
        for (Hotel hotel : hotels) {
            //si suppone non esistano due hotel con lo stesso nome nella stessa città
            if (nameHotel.equals(hotel.getName())) {
                return hotel;
            }
        }
        return null;
    }

    //--------------------------------------------------SEARCHALLHOTELS--------------------------------------------------------------------
    public synchronized CopyOnWriteArrayList<Hotel> searchAllHotels(String nameCity) {
        if (mapCity.containsKey(nameCity))
            return mapCity.get(nameCity);
        else
            return null;
    }


    //-------------------------------------------------------INSERTREVIEW----------------------------------------------------------------
    public synchronized boolean addReview(String namecity, String nameHotel, int globalScore, int[] singleScore, Account currentUser) throws IOException {
        LocalDateTime date;
        int size = mapCity.get(namecity).size();
        for (int i = 0; i < size; i++) {
            Hotel hotel = mapCity.get(namecity).get(i);

            String currentNameHotel = hotel.getName();

            //voto totale dato dalla somma dei voti
            double actualScore = hotel.getTotalScore();

            //search dell'hotel
            if (currentNameHotel.equals(nameHotel)) {

                //aggiorno la data dell'ultimo voto
                date = LocalDateTime.now();
                hotel.setLastVoteDate(date);

                //aggiorno il numero di recensioni
                currentUser.incrementReview();
                jsonServices.updateUser(currentUser);

                //aggiorno il voto globale
                hotel.incrementVote();
                int n = hotel.getnOfVote();
                double sum = (actualScore + globalScore);
                double mean = sum / n;
                hotel.setTotalScore(sum);
                hotel.setRate(mean);

                //AGGIORNO IL VOTO DELLE SINGOLE CATEGORIE

                //creo una copia della hashmap contenente i voti
                ConcurrentHashMap<String, Double> allSingleScore = hotel.getRatings();

                //creo una copia dell'array contenente la somma dei singoli voti
                double[] totalSingleScore = hotel.getTotalSingleScore();
                for (int j = 0; j < totalSingleScore.length; j++) {
                    sum = totalSingleScore[j] + singleScore[j];
                    mean = sum / n;
                    totalSingleScore[j] = sum;
                    String category = changeIntCategory(j + 1);
                    allSingleScore.put(category, mean);
                }

                //aggiungo la recensione alla Lista
                Review rev = new Review(currentUser.getUsername(), namecity, nameHotel, globalScore, singleScore, date);
                accountReview.add(rev);

                //serializzo le informazioni su Review.json
                jsonServices.jsonAddReview(accountReview);
                return true;
            }
        }
        return false;
    }


    public List<Review> getAccountReview() {
        return accountReview;
    }

    public void setAccountReview(List<Review> accountReview) {
        this.accountReview = accountReview;
    }

    //funzione per trasformare l'indice della HashMap nella categoria corrispondente
    String changeIntCategory(int n) {
        String category = null;
        switch (n) {
            case 1:
                category = "cleaning";
                break;
            case 2:
                category = "position";
                break;
            case 3:
                category = "services";
                break;
            case 4:
                category = "quality";
                break;
        }
        return category;
    }


}
