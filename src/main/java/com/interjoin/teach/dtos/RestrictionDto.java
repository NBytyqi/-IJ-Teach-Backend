package com.interjoin.teach.dtos;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class RestrictionDto {

    @NotNull
    private String property;
    @NotNull
    private Double value;
}
