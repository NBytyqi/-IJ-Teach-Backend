package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.ReviewSessionException;
import com.interjoin.teach.dtos.ReviewDto;
import com.interjoin.teach.dtos.requests.ReviewRequest;
import com.interjoin.teach.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> addReview(@RequestBody ReviewRequest request) throws ReviewSessionException, InterjoinException {
        reviewService.save(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getCurrentTeacherReviews() {
        return ResponseEntity.ok(reviewService.getCurrentTeacherReviews());
    }


}

