package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class JsonServices {
    private CopyOnWriteArrayList<Account> AllAccount;
    private CopyOnWriteArrayList<Hotel> AllHotels;
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> mapCity;
    private CopyOnWriteArrayList<String> city;
    private Ranking ranking;

    public JsonServices(CopyOnWriteArrayList<Account> AllAccount, CopyOnWriteArrayList<Hotel> AllHotels) {
        this.AllAccount = AllAccount;
        this.AllHotels = AllHotels;
    }

    //crea il file Json per serializzare e deserializzare le informazioni degli Account, si suppone che esista già il Json degli hotel
    public void createNewFile() throws IOException {
        System.out.println("tentativo di creazione di account.json...");
        File file = new File("account.json");
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        } else {
            System.out.println("File già presente.");
        }
    }

    //avvio il thread che periodicamente serializza sul file Json le informazioni aggiornate degli hotel
    public void JsonHotelWriter(int time) {
        HotelJsonWriter HotelJsonWriter = new HotelJsonWriter(AllHotels, time);
        HotelJsonWriter.start();
    }

    //se al'avvio del server ho delle informazioni sul file "account.json" allora
    //serializzo la stringa json in istanze di Account e le aggiungo alla lista
    public void JsonAccount() throws IOException {
        File file = new File("account.json");
        if (file.length() != 0) {
            FileInputStream inputStream = new FileInputStream("account.json");
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.beginArray();
            while (reader.hasNext()) {
                Account account = new Gson().fromJson(reader, Account.class);
                account.setLoggedIn(false);
                AllAccount.add(account);
            }
            reader.endArray();
        }

    }


    ///all'avvio del server ho delle informazioni sul file "Hotels.json" allora
    //serializzo la stringa json in istanze di Hotel e le aggiungo alla lista
    public void JsonHotel(RmiServerImpl server, int time, String nameAddress, int port) throws IOException {
        FileInputStream inputStream = new FileInputStream("Hotels.json");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.beginArray();
        while (reader.hasNext()) {
            //aggiungo gli hotel alla lista
            Hotel hotel = new Gson().fromJson(reader, Hotel.class);
            AllHotels.add(hotel);
        }
        reader.endArray();
        //creo una ConcurrentHashMap con key=città, value=lista degli hotel della città
        city = new CopyOnWriteArrayList<>();
        addCity(city);
        mapCity = new ConcurrentHashMap<>();
        for (String s : city) {
            CopyOnWriteArrayList<Hotel> list = new CopyOnWriteArrayList<>();
            for (Hotel allHotel : AllHotels) {
                if (s.equals(allHotel.getCity())) {
                    //setto la data iniziale al momento della creazione della lista
                    allHotel.setLastVoteDate(LocalDateTime.now());
                    list.add(allHotel);
                }
            }
            mapCity.put(s, list);
        }

        //Thread che calcola il ranking ogni "time" tempo,
        ranking = new Ranking(mapCity, city, time, server, nameAddress, port);
        ranking.start();
    }


    //------------------------------------------------ADDUSER (REGISTER)------------------------------------------------------------------
    //metodo synchronized per aggiungere un nuovo utente (nuova registrazione)
    public synchronized String JsonAddUser(Account utente) throws IOException {
        String reply;
        //caso base in cui la lista è vuota
        if (AllAccount.isEmpty()) {
            FileWriter fileWriter = new FileWriter("account.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            AllAccount.add(utente);
            String Json = gson.toJson(AllAccount);
            fileWriter.write(Json);
            fileWriter.flush();
            fileWriter.close();
            reply = "utente registrato";
            System.out.println(reply);
            return reply;
        } else {
            //controllo se l'account è già presente
            for (Account account : AllAccount) {
                if (account.getUsername().equals(utente.getUsername())) {
                    reply = "utente già registrato";
                    System.out.println(reply);
                    return reply;
                }
            }
            FileWriter fileWriter = new FileWriter("account.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//se l'account non è presente allora lo aggiungo alla lista
            AllAccount.add(utente);
            String Json = gson.toJson(AllAccount);
            fileWriter.write(Json);
            reply = "utente registrato";
            fileWriter.flush();
            fileWriter.close();
        }
        System.out.println(reply);
        return reply;
    }

    public synchronized boolean updateUser(Account account) {
        if (AllAccount.isEmpty())
            return false;
        for (int i = 0; i < AllAccount.size(); i++) {
            if (AllAccount.get(i).getUsername().equals(account.getUsername())) {
                try {
                    FileWriter fileWriter = new FileWriter("account.json");
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String Json = gson.toJson(AllAccount);
                    fileWriter.write(Json);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("scrittura fallita");
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public List<Review> jsonReview(List<Review> reviews) throws FileNotFoundException {
        File file = new File("Review.json");
        //se la lista non è vuota deserializzo
        if (file.length() != 0) {
            FileReader fileReader = new FileReader("Review.json");
            Type type = new TypeToken<List<Review>>() {
            }.getType();

            //utilizzo l'override del metodo deserialize per LocalDateTime
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();

            reviews = gson.fromJson(fileReader, type);
        } else
            //la lista è vuota
             reviews= new ArrayList<>();
        return reviews;
    }

    public void jsonAddReview(List<Review> reviews) throws IOException {
        //serializzo la lista
        FileWriter fileWriter = new FileWriter("Review.json");
        //utilizzo l'override del metodo serialize per LocalDateTime
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(reviews);
        fileWriter.write(json);
        fileWriter.flush();
        fileWriter.close();
    }




    public synchronized CopyOnWriteArrayList<Account> getAllAccount() {
        return AllAccount;
    }

    public synchronized Account getAccount(String username) {
        for (Account account : AllAccount) {
            if (account.getUsername().equals(username))
                return account;
        }
        return null;
    }


    public synchronized ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> getMapCity() {
        return mapCity;
    }




//funzione di jsonservice
    //crea un Arraylist con elementi le città presenti nel file
    void addCity(CopyOnWriteArrayList<String> capoluoghi) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("listOfCity.txt"));
        String s;
        while ((s = reader.readLine()) != null)
            capoluoghi.add(s);

    }


}








