package com.interjoin.teach.dtos.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ReviewRequest {

    private String review;
    private String sessionUuid;
    private Double stars;
}
