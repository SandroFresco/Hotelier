package com.company;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// Implementa l'interfaccia ServerInterface ed estende RemoteServer per supportare RMI
public class RmiServerImpl extends RemoteServer implements ServerInterface {
    // Map <città, lista di account interessati a quella città>
    private Map<String, List<Account>> userPreferences;

    // Map <account, interfaccia di notifica per callback>
    private Map<Account, NotifyEventInterface> clients;

    // Lista di tutti gli account
    private List<Account> accounts;


    public RmiServerImpl(List<Account> accounts) throws RemoteException {
        super();
        clients = new ConcurrentHashMap<>();
        this.accounts = accounts;
        userPreferences = new ConcurrentHashMap<>();

        // inserisce i rispettivi account in userPreferences basandosi sulle città d'interesse di ciascun account
        for (Account account : accounts) {
            for (String city : account.getCities()) {
                List<Account> listSameCity = userPreferences.get(city);
                if (listSameCity == null)
                    listSameCity = new CopyOnWriteArrayList<>();
                listSameCity.add(account);
                userPreferences.put(city, listSameCity);
            }
        }
    }

    // Metodo per registrare un client per le callback
    @Override
    public synchronized void registerForCallback(String username, NotifyEventInterface ClientInterface) throws RemoteException {
        Account account = new Account(username);
        if (!clients.containsKey(account)) {
            Account accountFound = null;

            // Cerca l'account corrispondente
            for (Account current : accounts) {
                if (current.getUsername().equals(username)) {
                    accountFound = current;
                }
            }
            if (accountFound != null) {

                // Aggiunge il client e l'interfaccia di notifica
                clients.put(accountFound, ClientInterface);

                // Aggiorna userPreference per ogni città d'interesse dell'account
                for (String city : accountFound.getCities()) {
                    List<Account> listSameCity = userPreferences.get(city);
                    if (listSameCity == null)
                        listSameCity = new CopyOnWriteArrayList<>();
                    listSameCity.add(account);
                    userPreferences.put(city, listSameCity);
                }
            }
        }
    }

    // Metodo unregister
    @Override
    public synchronized void unregisterForCallback(String username) throws RemoteException {
        Account account = new Account(username);
        if (clients.containsKey(account)) {
            Account accountFound = null;

            // Cerca l'account corrispondente
            for (Account current : accounts) {
                if (current.getUsername().equals(username)) {
                    accountFound = current;
                }
            }
            if (accountFound != null) {
                // Rimuove il client
                clients.remove(accountFound);

                // Rimuove l'account da userPreference per ogni città d'interesse
                for (String city : accountFound.getCities()) {
                    List<Account> listSameCity = userPreferences.get(city);
                    if (listSameCity != null)
                        listSameCity.remove(account);
                }
            }
        }
    }

    // Notifica i client di un cambio di posizione di un hotel
    public void notifyHotelPositionChanged(String nameHotel, int oldPosition, int newPosition, String cityName) {
        List<Account> accounts = userPreferences.get(cityName);
        if (accounts == null)
            return;

        for (Account account : accounts) {
            NotifyEventInterface notifyEventInterface = clients.get(account);
            try {

                // Invia la notifica
                notifyEventInterface.NotifyEvent(nameHotel, oldPosition, newPosition, cityName);
            } catch (RemoteException e) {
                System.out.println("errore notifica rmi callback " + e.getMessage());
            }
        }
    }

    // Metodo per ottenere userPreference
    public Map<String, List<Account>> getUserPreferences() {
        return userPreferences;
    }
}
