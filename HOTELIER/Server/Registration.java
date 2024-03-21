package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface Registration extends Remote {
    String reg (String action) throws RemoteException;


}
