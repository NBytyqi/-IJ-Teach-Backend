package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.AffiliateMarketerDto;
import com.interjoin.teach.entities.AffiliateMarketer;

import java.util.ArrayList;
import java.util.List;

public class AffiliateMarketerMapper {

    public static AffiliateMarketerDto map(AffiliateMarketer affiliateMarketer) {
        return AffiliateMarketerDto.builder()
                .email(affiliateMarketer.getEmail())
                .firstName(affiliateMarketer.getFirstName())
                .lastName(affiliateMarketer.getLastName())
                .phoneNumber(affiliateMarketer.getPhoneNumber())
                .referalCode(affiliateMarketer.getReferalCode())
                .build();
    }

    public static List<AffiliateMarketerDto> map(List<AffiliateMarketer> marketers) {
        List<AffiliateMarketerDto> affiliates = new ArrayList<>();

        for(AffiliateMarketer aff : marketers) {
            affiliates.add(map(aff));
        }
        return affiliates;
    }
}
