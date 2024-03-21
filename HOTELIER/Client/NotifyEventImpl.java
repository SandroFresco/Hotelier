package com.company;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;
import java.util.List;


public class NotifyEventImpl extends RemoteObject implements NotifyEventInterface {
    //struttura dati per salvare le informazioni relative ai ranking di interesse
    private List<HotelRankUpdate> positionHotel;


    public NotifyEventImpl() throws RemoteException {
        super();
        positionHotel = new ArrayList<>();

    }

    //metodo chiamato dal server tramite rmi
    @Override
    public void NotifyEvent(String nameHotel, int oldPosition, int newPosition, String cityName) throws RemoteException {
        HotelRankUpdate hotelRankUpdate = new HotelRankUpdate(cityName, newPosition, nameHotel, oldPosition);
        System.out.println("l'hotel " + nameHotel + " ha cambiato posizione: da " + oldPosition + " a " + newPosition);
        positionHotel.add(hotelRankUpdate);
    }

}
