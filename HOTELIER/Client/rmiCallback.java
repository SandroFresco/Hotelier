package com.company;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;


public class rmiCallback {
    NotifyEventInterface callbackObj;
    NotifyEventInterface stub;
    ServerInterface server;


    public rmiCallback(int port, String nameServer) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(nameServer, port);
        String name = "Server";
        server = (ServerInterface) registry.lookup(name);
    }

//registrazione al servizio di notifica
    public void callBackreg(String username) throws RemoteException {
        callbackObj = new NotifyEventImpl();
        stub = (NotifyEventInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
        server.registerForCallback(username, stub);
    }

    //unregister dal servizio di notifica
    public void callBackUnreg(String username) throws RemoteException {
        server.unregisterForCallback(username);
    }
}
