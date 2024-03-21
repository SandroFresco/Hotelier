package com.company;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccountServices {
    private JsonServices jsonServices;
    private CopyOnWriteArrayList<Account> AllAccount;

    public AccountServices(JsonServices jsonServices) {
        this.jsonServices = jsonServices;
        AllAccount = jsonServices.getAllAccount();
    }

    //------------------------------------------------LOGIN--------------------------------------------------------------
    public synchronized String login(String username, String password) throws IOException {
        Account account;
        if (AllAccount.isEmpty())
            return "nessun utente registrato";

        for (Account value : AllAccount) {
            account = value;
            //Check dello username
            if (account.getUsername().equals(username)) {
                //check della password
                if (account.getPassword().equals(password)) {
                    if (value.isLoggedIn()) {
                        return "utente gia' loggato in un'altra sessione, impossibile connettersi con piu' dispositivi contemporaneamente";
                    }
                    value.setLoggedIn(true);
                    return "utente loggato";
                } else return "password errata";
            }
        }
        return "utente non trovato";
    }

    //-----------------------------------------------------LOGOUT------------------------------------------------------------
    public synchronized String logout(Account utente) throws IOException {
        Account account;
        if (AllAccount.isEmpty()) {
            return "nessun utente registrato";
        }
        for (Account value : AllAccount) {
            account = value;
            //check username per trovare l'utente in questione
            if (account.getUsername().equals(utente.getUsername())) {
                value.setLoggedIn(false);
                return "logout effettuato";
            }
        }
        //condizione a cui non arriverà mai
        return "account non trovato";
    }

}
