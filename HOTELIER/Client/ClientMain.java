package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;


public class ClientMain {
    //definisco i parametri in globali del file di configurazione
    private static String SERVER;
    private static int TCPPORT;
    private static int RMIPORT;
    private static int NOTIFYPORT;
    private static String MULTICAST;
    private static int MCASTPORT;

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        //chiamo la funzione che legge dal file di configurazione i parametri
        readClientConfig();

        Login login = new Login(false);
        //istanza del Thread che si occupa di ricevere i messaggi multicast
        Udp udp = new Udp(login, MULTICAST, MCASTPORT);



        try {
        /* provo a instaurare una connessione tcp al server in ascolto sulla porta TCPPORT
        se la connessione non avviene con successo genera una Exception e il programma termina*/
            Socket s = new Socket();
            SocketAddress address = new InetSocketAddress(SERVER, TCPPORT);
            s.connect(address);

            //-------------------------------------------------RMI CALLBACK-------------------------------------------------------
            rmiCallback rmiCallback = new rmiCallback(NOTIFYPORT, SERVER);

            //associo uno stream di input e di output al socket
            BufferedReader reader;
            DataOutputStream writer;
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new DataOutputStream(s.getOutputStream());


            String reply;
            System.out.println("BENVENUTO SU HOTELIER");

            String username = null;
            while (true) {
                System.out.println("<------------------------------------------->");
                System.out.println("Utilizza il comando help per richiedere assistenza sui possibili comandi");
                System.out.println("Ricorda di inserire comando e argomento nella stessa riga separati da uno spazio, gli argomenti se più di uno dovranno essere separati da una virgola");
                System.out.println("è necessario rispettare maiuscole e minuscole anche dei nomi delle città e degli hotel.");
                System.out.println("<------------------------------------------->");

                String[] arg;
                String[] token;

                //input da tastiera
                Scanner input = new Scanner(System.in);
                String action = input.nextLine();
                Error error = new Error();
                //divido il comando (token[0]) dall'argomento (token[1]) mediante uno spazio
                token = action.split(" ", 2);
                switch (token[0]) {

//----------------------------------------------REGISTRAZIONE-----------------------------------------------------------
                    case "register" -> {
                        //verifico se l'utente è loggato
                        if (login.getIsLogged()) {
                            System.out.println("Non è possibile registrare nessun utente se si è loggati");

                            break;
                        }
                        if (token.length < 2) {
                            System.out.println("Errore nella trascrizione");

                            break;
                        }

                        //divido gli argomenti separati da una virgola
                        arg = token[1].split(",");
                        //controllo errori
                        if (!error.Check(token[0], arg)) {

                            //REGISTER TRAMITE RMI
                            action = rmi(action, RMIPORT);
                            if (action.equals("utente già registrato"))
                                System.out.println(action);
                            if (action.equals("utente registrato")) {
                                action = "login " + arg[0] + "," + arg[1];
                                token = action.split(" ", 2);

//-----------------------------------------LOGIN SUCCESSIVO ALLA REGISTRAZIONE------------------------------------------
                                arg = token[1].split(",");
                                //controllo errori
                                if (!error.Check(token[0], arg)) {
                                    //invio dati al server
                                    writer.writeBytes(action + "\n");
                                    writer.flush();
                                    //lettura dei dati provenienti dal server
                                    reply = reader.readLine();
                                    System.out.println("utente registato con successo");
                                    if (reply.equals("utente loggato")) {

//-------------------------------------------------CITTA' D'INTERESSE---------------------------------------------------
                                        String replycities = reader.readLine();
                                        if (replycities.equals("")) {
                                            System.out.println("indicare le città di interesse");
                                            System.out.println("ATTENZIONE: IN CASO LA CITTA' INSERITA NON SIA PRESENTE NEI NOSTRI ARCHIVI,SI DOVRA' RIPETETE LA PROCEDURA DI LOGIN ");
                                            action = input.nextLine();
                                            writer.writeBytes("favorite " + action + "\n");
                                            reply = reader.readLine();
                                            if (!reply.equals("ok")) {
                                                System.out.println(reply);
                                                break;
                                            }
                                            System.out.println("Preferenze salvate");
                                        }

                                        //RMI CALLBACK
                                        username = arg[0];
                                        rmiCallback.callBackreg(username);

                                        //UDP MULTICAST
                                        login.setIsLogged(true);
                                        udp.start();
                                    }
                                }
                            }
                        }
                    }

//-------------------------------------------------LOGIN----------------------------------------------------------------
                    //uguale a prima ma senza registrazione
                    case "login" -> {
                        if (login.getIsLogged()) {
                            System.out.println("Sei già loggato");


                            break;
                        }
                        if (token.length < 2) {
                            System.out.println("Errore nella trascrizione");

                            break;
                        }
                        arg = token[1].split(",");
                        if (!error.Check(token[0], arg)) {
                            writer.writeBytes(action + "\n");
                            writer.flush();
                            reply = reader.readLine();
                            System.out.println(reply);

//-------------------------------------------------CITTA' D'INTERESSE---------------------------------------------------
                            if (reply.equals("utente loggato")) {
                                String replycities = reader.readLine();
                                if (replycities.equals("")) {
                                    System.out.println("indicare le città di interesse");
                                    System.out.println("ATTENZIONE: IN CASO LA CITTA' INSERITA NON SIA PRESENTE NEI NOSTRI ARCHIVI,SI DOVRA' RIPETETE LA PROCEDURA DI LOGIN");
                                    action = input.nextLine();
                                    writer.writeBytes("favorite " + action + "\n");
                                    reply = reader.readLine();
                                    if (!reply.equals("ok")) {
                                        System.out.println(reply);
                                        break;
                                    }
                                    System.out.println("Preferenze salvate");

                                }

                                //RMI CALLBACK
                                username = arg[0];
                                rmiCallback.callBackreg(username);

                                //UDP MULTICAST
                                login.setIsLogged(true);
                                udp.start();
                            }
                        }
                    }


//--------------------------------------------------LOGOUT--------------------------------------------------------------
                    case "logout" -> {
                        //check del login
                        if (!login.getIsLogged()) {
                            System.out.println("Non sei loggato non puoi effettuare il logout");

                            break;
                        }
                        if (token.length != 1) {
                            System.out.println("Errore nella trascrizione, il comando logout non richiede nessun campo");

                            break;
                        }
                        //invio dati al server
                        writer.writeBytes(action + "\n");
                        writer.flush();
                        //lettura dati dal server
                        reply = reader.readLine();
                        System.out.println(reply);
                        if (reply.equals("logout effettuato")) {

                            //termino il Thread Udp Multicast
                            login.setIsLogged(false);
                            udp.interrupt();
                            udp.join();
                            udp = new Udp(login, MULTICAST, MCASTPORT);

                            //annullo la registrazione a rmi callback
                            rmiCallback.callBackUnreg(username);
                            username = null;
                        }
                    }
//---------------------------------------------SEARCHOTEL---------------------------------------------------------------
                    case "searchHotel" -> {
                        if (token.length < 2) {
                            System.out.println("errore nella trascrizione");

                            break;
                        }
                        arg = token[1].split(",");
                        if (!error.Check(token[0], arg)) {
                            //invio dati al server
                            writer.writeBytes(action + "\n");
                            writer.flush();
                            //leggo i dati dal server
                            reply = reader.readLine();
                            try {
                                //deserializzo la stringa Json in un'istanza della classe Hotel e stampo le informazioni che mi interessano
                                Gson gson = new Gson();
                                Hotel hotel = gson.fromJson(reply, Hotel.class);
                                hotel.printAll();

                            } catch (Exception e) {
                                System.out.println(reply);


                            }
                        }
                    }

//--------------------------------------------SEARCHALLHOTELS-----------------------------------------------------------
                    case "searchAllHotels" -> {
                        if (token.length < 2) {
                            System.out.println("errore nella trascrizione");

                            break;
                        }
                        arg = token[1].split(",");
                        if (!error.Check(token[0], arg)) {
                            //invio dati al server
                            writer.writeBytes(action + "\n");
                            writer.flush();
                            //leggo i dati dal server
                            reply = reader.readLine();
                            try {
                                //deserializzo la stringa json in una lista di Hotel
                                Gson gson = new Gson();
                                List<Hotel> hotel = gson.fromJson(reply, new TypeToken<List<Hotel>>() {
                                }.getType());
                                for (Hotel value : hotel) {
                                    value.printAll();
                                    System.out.println("<----------------------------------------->");
                                }
                            } catch (Exception e) {
                                System.out.println(reply);
                            }
                        }
                    }

//--------------------------------------------INSERTREVIEW--------------------------------------------------------------
                    case "insertReview" -> {
                        if (!login.getIsLogged()) {
                            System.out.println("non puoi aggiungere review senza essere loggato");

                            break;
                        }
                        if (token.length < 2) {
                            System.out.println("errore nella trascrizione");


                            break;
                        }
                        arg = token[1].split(",");
                        if (!error.Check(token[0], arg)) {
                            //invio dati al server
                            writer.writeBytes(action + "\n");
                            writer.flush();
                            //ricevo dati dal server
                            reply = reader.readLine();
                            System.out.println(reply);
                        }
                    }

//---------------------------------------------SHOWMYBADGES-------------------------------------------------------------
                    case "showMyBadges" -> {
                        if (token.length > 1) {
                            System.out.println("Errore nella trascrizione, il comando showMybadges non richiede nessun campo");

                            break;
                        }
                        if (!login.getIsLogged()) {
                            System.out.println("Comando valido solo per utenti loggati");



                            break;
                        }

                        //invio dati al server
                        writer.writeBytes(action + "\n");
                        writer.flush();
                        //ricevo dati dal server
                        reply = reader.readLine();
                        System.out.println(reply);
                    }

                    //comando help utile per avere maggiori informazioni
                    case "help" -> {
                        System.out.println("Questi sono i comandi disponibili:");
                        System.out.println("register;\nlogin;\nsearchHotel;\nserchAllHotels;\ninsertReview;\nshowMyBadges;");
                        System.out.println("Se desideri avere maggiori informazioni digita help-comando di interesse");


                    }
                    case "help-register" -> {
                        System.out.println("register username,password: registrazione a HOTELIER. L’utente deve fornire\n");
                        System.out.println("username e una password.\n");

                    }
                    case "help-login" -> System.out.println("login username,password: login di un utente già registrato per accedere al servizio.");
                    case "help-logout" -> System.out.println("logout: effettua il logout dell’utente dal servizio");
                    case "help-searchHotel" -> System.out.println("searchHotel nomeHotel, città: ricerca i dati di un particolare hotel appartenente a\n" +
                            "una città. Questa operazione può essere effettuata anche dagli utenti non loggati");
                    case "help-searchAllHotels" -> System.out.println("""
                            searchAllHotels città: ricerca i dati di tutti gli hotel di quella città.
                            Questa operazione può essere effettuata anche dagli utenti non loggati.
                            ATTENZIONE: il ranking locale potrebbe non essere aggiornato.""");
                    case "help-insertReview" -> System.out.println("""
                            insertReview nomeHotel, nomeCittà, GlobalScore, (SingleScores): inserisce una
                            review per un hotel di una certa città. Viene indicato sia il punteggio complessivo
                            per quell’hotel che i singoli punteggi per le varie categorie. L’utente deve essere
                            registrato ed aver effettuato il login per effettuare questa operazione""");
                    case "help-showMyBadges" -> System.out.println("""
                            showMyBadges: l’utente richiede di mostrare il proprio distintivo, corrispondente
                            al maggior livello di expertise raggiunto. L’utente deve essere registrato ed aver
                            effettuato il login per effettuare questa operazione.""");
                    default -> System.out.println("Comando non trovato");
                }
            }

        } catch (SocketException IOException) {
            System.out.println("ERRORE, DI CONNESSIONE");
            login.setIsLogged(false);
            try {
                udp.join();
            }catch(InterruptedException ignored){
        }

        }
    }


    //-----------------------------------------REGISTRAZIONE TRAMITE RMI------------------------------------------------
    public static String rmi(String action, int rmiPort) throws RemoteException, NotBoundException {
        Registration serverObject;
        Remote remoteObject;
        Registry r = LocateRegistry.getRegistry(rmiPort);
        remoteObject = r.lookup("register-rmi");
        serverObject = (Registration) remoteObject;
        action = (serverObject.reg(action));
        return action;
    }


    // ----------------------------FILE DI CONFIGURAZIONE------------------------------
    private static void readClientConfig() {

        String config = "ClientInputConfig.txt";
        try (BufferedReader in = new BufferedReader(new FileReader(config))) {

            String line, key, value;

            while ((line = in.readLine()) != null) {
                try {
                    StringTokenizer tkLine = new StringTokenizer(line);
                    key = tkLine.nextToken();
                    // nel caso in cui non sia un commento (#-token)
                    if (!key.equals("#")) {
                        // value prende il secondo token, che non puo' essere un "=-token"
                        // replaceAll serve per accettare i valori con spazi o senza dopo l'= (= value o =value)
                        value = tkLine.nextToken("= ").replaceAll("\\s", "");
                        switch (key) {
                            case "SERVER" -> SERVER = value;
                            case "TCPPORT" -> TCPPORT = Integer.parseInt(value);
                            case "RMIPORT" -> RMIPORT = Integer.parseInt(value);
                            case "NOTIFYPORT" -> NOTIFYPORT = Integer.parseInt(value);
                            case "MULTICAST" -> MULTICAST = value;
                            case "MCASTPORT" -> MCASTPORT = Integer.parseInt(value);
                            default -> throw new RuntimeException("key not recognized: " + key);
                        }
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from client configuration file");
        }
    }

}
