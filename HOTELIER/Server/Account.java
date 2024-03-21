package com.company;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

//Classe per serializzare e deserializzare le informazioni relative agli account
public class Account {
    private String username;
    private String password;
    private String badge;
    private int nReview =0;
    private transient boolean loggedIn=false;
    private List<String> cities = new CopyOnWriteArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account(String username) {
        this.username = username;
    }

    public Account(String username, String password, String badge, int nRecensioni, boolean loggedIn) {
        this.password = password;
        this.username = username;
        this.badge = badge;
        this.nReview = nRecensioni;
        this.loggedIn = loggedIn;
    }

//incremento il numero di recensioni inseriti dall'utente
    public  void incrementReview(){
        nReview++;
    }

    public List<String> getCities() {
        return cities;
    }

    public  void setCities(List<String> cities) {
        this.cities = cities;
    }

    public String setBadge() {
        if (nReview < 0)
            return "non è possibile avere meno di 0 recensioni";
        else {
            switch (nReview) {
                case 0 -> badge = "nessun badge";
                case 1 -> badge = "Recensore";
                case 2 -> badge = "Recensore esperto";
                case 3 -> badge = "Contributore";
                case 4 -> badge = "Contributore esperto";
                default -> badge = "Contributore Super";
            }
        }
        return badge;
    }


    public synchronized void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public synchronized boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return username.equals(account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}


