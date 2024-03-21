package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerMain {

    // variabili per il file di configurazione
    private static int TCPPORT;
    private static int RMIPORT;
    private static int NOTIFYPORT;
    private static String MULTICAST;
    private static int MCASTPORT;
    private static int TIME;
    private static int TIMEUPDATE;


    public static void main(String[] args) throws IOException {

        //leggo il file di configurazione
        readServerConfig();

        //creo l'istanza delle liste di Account e Hotel
        CopyOnWriteArrayList<Account> allAccount=new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Hotel> allHotel= new CopyOnWriteArrayList<>();

        //rmi callback
        RmiServerImpl server= new RmiServerImpl(allAccount);
        ServerInterface Stub= (ServerInterface) UnicastRemoteObject.exportObject(server,39000) ;
        String name = "Server";
        LocateRegistry.createRegistry(NOTIFYPORT);
        Registry registry=LocateRegistry.getRegistry(NOTIFYPORT);
        try {
            registry.bind (name,Stub);
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }


        //chiamo i metodi per inizializzare i file Json
        JsonServices jsonServices = new JsonServices(allAccount, allHotel);
        jsonServices.createNewFile();
        jsonServices.JsonHotel(server, TIME, MULTICAST, MCASTPORT);
        jsonServices.JsonAccount();
        jsonServices.JsonHotelWriter(TIMEUPDATE);

        //register rmi
        RegisterRmi rmi = new RegisterRmi(jsonServices);
        Registration stub = (Registration) UnicastRemoteObject.exportObject(rmi, 0);
        LocateRegistry.createRegistry(RMIPORT);
        Registry r= LocateRegistry.getRegistry(RMIPORT);
        r.rebind("register-rmi", stub);

        //------------------------------------------THREADPOOL TCP SERVER-----------------------------------------------
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int i=1;
        try (ServerSocket serverSocket = new ServerSocket(TCPPORT)) {
                while(true) {

                // Attendo una connessione dal Client
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket,allAccount, allHotel, jsonServices, server, i);
                i++;

                // Invio il task di gestione del client al ThreadPool
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }


    // ---------------------------- FILE DI CONFIGURAZIONE ------------------------------
    private static void readServerConfig(){

        String config = "serverInputConfig.txt";
        try(BufferedReader in = new BufferedReader(new FileReader(config))){

            String line, key, value;

            while ((line = in.readLine()) != null){
                try{
                    StringTokenizer tkLine = new StringTokenizer(line);
                    key = tkLine.nextToken();
                    // nel caso in cui non sia un commento (#-token)
                    if(!key.equals("#")){
                        value = tkLine.nextToken("= ").replaceAll("\\s", "");
                        // value prende il secondo token, che non puo' essere un "=-token"
                        // replaceAll serve per accettare i valori con spazi o senza dopo l'= (= value o =value)
                        switch (key) {
                            case "TCPPORT" -> TCPPORT = Integer.parseInt(value);
                            case "RMIPORT" -> RMIPORT = Integer.parseInt(value);
                            case "NOTIFYPORT" -> NOTIFYPORT = Integer.parseInt(value);
                            case "MULTICAST" -> MULTICAST = value;
                            case "MCASTPORT" -> MCASTPORT = Integer.parseInt(value);
                            case "TIME" -> TIME = Integer.parseInt(value);
                            case "TIMEUPDATE" -> TIMEUPDATE= Integer.parseInt(value);

                            default -> throw new RuntimeException("key not recognized "+key);
                        }
                    }
                }
                catch (NoSuchElementException e){
                    continue;
                }
            }
        }
        catch (IOException e){
            System.out.println("Errore nella lettura del file di configurazione");
            System.exit(1);
        }
    }
}