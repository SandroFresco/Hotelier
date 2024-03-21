package com.company;

import java.time.LocalDateTime;

public class Review {
    String username;
    String city;
    String nameHotel;
    int rate;
    int[] ratings;
    LocalDateTime date;

    public Review(String username, String city, String nameHotel, int rate, int[] ratings, LocalDateTime date) {
        this.username=username;
        this.city = city;
        this.nameHotel = nameHotel;
        this.rate = rate;
        this.ratings = ratings;
        this.date = date;
    }
}
