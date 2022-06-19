package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimesSlots {

    private List<Long> mon;
    private List<Long> tue;
    private List<Long> wed;
    private List<Long> thu;
    private List<Long> fri;
    private List<Long> sat;
    private List<Long> sun;

}
