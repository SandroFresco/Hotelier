package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NotifyEventInterface extends Remote {
    public void NotifyEvent(String nameHotel, int oldPosition, int newPosition, String cityName) throws RemoteException;
}
