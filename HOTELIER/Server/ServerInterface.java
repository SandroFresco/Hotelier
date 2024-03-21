package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {

    void registerForCallback(String username, NotifyEventInterface ClientInterface) throws RemoteException;

    void unregisterForCallback(String username) throws RemoteException;
}
