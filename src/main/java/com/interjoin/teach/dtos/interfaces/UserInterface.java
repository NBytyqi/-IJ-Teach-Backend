package com.interjoin.teach.dtos.interfaces;

import java.math.BigDecimal;

public interface UserInterface {

    Long getId();
    String getFirstName();
    String getLastName();
    String getShortBio();
    BigDecimal getListedPrice();
    Double getRating();

}
