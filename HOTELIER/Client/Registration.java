package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Registration extends Remote {
    String reg (String action) throws RemoteException;

}
