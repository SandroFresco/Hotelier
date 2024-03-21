package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotifyEventInterface extends Remote {
    public void NotifyEvent(String nameHotel, int oldPosition, int newPosition, String cityName) throws RemoteException;
}
