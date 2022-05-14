package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.ReviewSessionException;
import com.interjoin.teach.dtos.requests.ReviewRequest;
import com.interjoin.teach.entities.Review;
import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SessionService sessionService;
    private final UserService userService;

    public void save(ReviewRequest request) throws ReviewSessionException {
        User currentStudent = userService.getCurrentUserDetails();

        Session session = sessionService.findByUuid(request.getSessionUuid());

        if(Optional.ofNullable(session.getStudent()).isPresent()) {
            if(currentStudent.getId() != session.getStudent().getId()) {
                throw new ReviewSessionException();
            }
        }

        Review review = Review.builder()
                .review(request.getReview())
                .session(session)
                .studentId(Optional.ofNullable(session.getStudent()).map(User::getId).orElse(null))
                .teacherId(Optional.ofNullable(session.getStudent()).map(User::getId).orElse(null))
                .build();

        reviewRepository.save(review);
    }
}
