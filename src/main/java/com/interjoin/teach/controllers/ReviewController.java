package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.ReviewSessionException;
import com.interjoin.teach.dtos.requests.ReviewRequest;
import com.interjoin.teach.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> addReview(@RequestBody ReviewRequest request) throws ReviewSessionException {
        reviewService.save(request);
        return ResponseEntity.ok().build();
    }

}

