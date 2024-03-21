package com.company;

//struttura dati per memorizzare le informazioni dei cambiamenti notificati dall'rmi callback
public class HotelRankUpdate {
    private String cityName;
    private int newPosition;
    private String hotelName;
    private int oldPosition;

    public HotelRankUpdate(String cityName, int newPosition, String hotelName, int oldPosition) {
        this.cityName = cityName;
        this.newPosition = newPosition;
        this.hotelName = hotelName;
        this.oldPosition = oldPosition;
    }

}
