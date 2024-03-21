package com.company;

import java.util.Comparator;

//Sorting Hotel
class HotelComparator implements Comparator<Hotel> {
    @Override
    public int compare(Hotel h1, Hotel h2) {

        // Confronto voto globale
        if (h1.getRate() > h2.getRate()) {
            // il voto globale di h1 è maggiore
            return -1;
        } else if (h1.getRate() < h2.getRate()) {
            // il voto globale di h2 è maggiore
            return 1;


        } else {
            // a parità di voti globali confronto i voti delle singole categorie
            if (h1.getAverageRating() > h2.getAverageRating()) {
                // h1>h2
                return -1;
            } else if (h1.getAverageRating() < h2.getAverageRating()) {
                // h2>h1
                return 1;


            } else {
                // A parità di voti delle singole categorie confronto il numero di voti
                if (h1.getnOfVote() > h2.getnOfVote()) {
                    // h1 ha più voti
                    return -1;

                } else if (h1.getnOfVote() < h2.getnOfVote()) {
                    // h2 ha più voti
                    return 1;


                } else {
                    // A parità di numero di voti confronto la data dell'ultimo voto in ordine discendente
                    return h2.getLastVoteDate().compareTo(h1.getLastVoteDate());
                }
            }
        }
    }
}
