package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.RestrictionDto;
import com.interjoin.teach.entities.Restriction;

import java.util.ArrayList;
import java.util.List;

public class RestrictionMapper {

    public static List<RestrictionDto> map(List<Restriction> restrictions) {
        List<RestrictionDto> restrictionDtos = new ArrayList<>();
        for(Restriction restriction : restrictions) {
            restrictionDtos.add(map(restriction));
        }
        return restrictionDtos;
    }

    public static RestrictionDto map(Restriction restriction) {
        return RestrictionDto.builder()
                .property(restriction.getProperty())
                .value(restriction.getValue())
                .build();
    }
}
