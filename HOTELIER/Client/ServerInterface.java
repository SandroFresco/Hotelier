package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    public void registerForCallback(String username, NotifyEventInterface ClientInterface) throws RemoteException;

    public void unregisterForCallback (String username) throws  RemoteException;
}
