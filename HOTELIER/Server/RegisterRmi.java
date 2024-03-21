package com.company;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;

public class RegisterRmi extends RemoteServer implements Registration {
    JsonServices Json;


    public RegisterRmi(JsonServices Json) {
        this.Json=Json;

    }

    //register tramite rmi
    @Override
    public String reg(String action) throws RemoteException {

        String[] token = action.split(" ",2);
        String [] arg=token[1].split(",");
        Account utente = new Account(arg[0].trim(), arg[1].trim(), "", 0, false);
        try {
            action = Json.JsonAddUser(utente);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return action;
    }
}
