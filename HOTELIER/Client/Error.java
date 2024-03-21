package com.company;

public class Error {

     //true -> errore di trascrizione
    //false -> nessun errore
    public Boolean Check(String command, String[] token) {
        switch (command) {
            //---------------------------------controllo errori REGISTER-------------------------------------------------------------------------
            case "register":
                //register contiene due argomenti, username e password
                if (token.length != 2) {
                    System.out.println("Errore nella trascrizione, il comando register richiede solo due campi, username e password");

                    return true;
                } else {
                    for (String s : token) {
                        if (s == null || s.trim().equals("")) {
                            System.out.println("Non è possibile avere un campo vuoto");
                            return true;
                        }
                    }
                    //System.out.println("Invio al server");
                    return false;
                }

                //---------------------------------controllo errori LOGIN--------------------------------------------------
            case "login":
                //login contiene due argomenti, username e password
                if (token.length != 2) {
                    System.out.println("Errore nella trascrizione, il comando login richiede solo due campi, username e password");

                    return true;
                } else {
                    for (int i = 1; i < token.length; i++) {
                        if (token[i] == null || token[i].trim().equals("")) {
                            System.out.println("Non è possibile avere un campo vuoto");

                            return true;
                        }
                    }
                    //System.out.println("Invio al server");
                    return false;
                }

//---------------------------------controllo errori SEARCHOTEL--------------------------------------------------
            case "searchHotel":
                //searchHotel contiene due campi, nomeHotel e città
                if (token.length != 2) {
                    System.out.println("Errore nella trascrizione, il comando searchHotel richiede solo due campi, nomeHotel e città");

                    return true;
                }
                for (String s : token) {
                    if (s == null || s.trim().equals("")) {
                        System.out.println("Non è possibile avere un campo vuoto");

                        return true;
                    }
                }
                break;

            //---------------------------------controllo errori SEARCHALLHOTEL-------------------------------------------------
            case "searchAllHotels":
                //searchAllHotels richiede solo il campo città
                if (token.length != 1) {
                    System.out.println("Errore nella trascrizione, il comando serchAllHotels richiede solo un campo, città");

                    return true;
                }
                if (token[0] == null || token[0].trim().equals("")) {
                    System.out.println("Non è possibile avere un campo vuoto");

                    return true;
                }
                //System.out.println("Invio al server");
                return false;

//---------------------------------controllo errori INSERTREVIEW--------------------------------------------------
            case "insertReview":
                //insertReview contiene sette campi: nome Hotel, nome città, punteggio globale, punteggi delle singole categorie
                try {
                    if (token.length != 7) {
                        System.out.println("Errore nella trascrizione, il comando insertReview richiede nome hotel, nome città, punteggi globali e punteggi delle singole categorie");


                        return true;
                    }
                    for (int i = 0; i < token.length; i++) {
                        if (token[i] == null)
                            return true;
                        String current = token[i].trim();
                        if (current.equals("")) {
                            System.out.println("Non è possibile avere un campo vuoto");

                        } else if (i >= 2) {
                            current = current.replace("(", "");
                            current = current.replace(")", "");
                            int a = Integer.parseInt(current);
                            if (a < 0 || a > 5) {
                                System.out.println("Il punteggio deve essere compreso tra 0 e 5 estremi inclusi");
                                return true;
                            }
                        }
                    }
                    //System.out.println("invio al server");
                    return false;
                } catch (NumberFormatException e) {
                    //il carattere inserito per indicare il punteggio non era un numero
                    System.out.println("errore, il punteggio inserito deve essere un numero");

                    return true;
                }


            default:
                System.out.println("Errore, ripetere l'operazione");

                return false;
        }
        return false;
    }
}
