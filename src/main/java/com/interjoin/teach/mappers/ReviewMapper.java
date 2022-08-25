package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.ReviewDto;
import com.interjoin.teach.entities.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewMapper {

    public static ReviewDto map(Review review) {
        return ReviewDto.builder()
                .review(review.getReview())
                .date(review.getDate())
                .stars(review.getStars())
                .build();
    }

    public static List<ReviewDto> map(List<Review> reviews) {
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for(Review review : reviews) {
            reviewDtos.add(map(review));
        }
        return reviewDtos;
    }
}
