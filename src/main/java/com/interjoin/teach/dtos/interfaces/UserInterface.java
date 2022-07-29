package com.interjoin.teach.dtos.interfaces;

import java.math.BigDecimal;

public interface UserInterface {

    Long getId();
    String getFirstName();
    String getLastName();
    String getShortBio();
    BigDecimal getListedPrice();
    Double getRating();
    String getAgencyName();
    String getQualifications();
    String getLongBio();
    String getLocation();

}
