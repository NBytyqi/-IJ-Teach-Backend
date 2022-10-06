package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.ReviewSessionException;
import com.interjoin.teach.dtos.EmailDTO;
import com.interjoin.teach.dtos.ReviewDto;
import com.interjoin.teach.dtos.requests.ReviewRequest;
import com.interjoin.teach.entities.Review;
import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.ReviewMapper;
import com.interjoin.teach.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final SessionService sessionService;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${spring.sendgrid.templates.teacher-new-review}")
    private String newReviewTemplate;
    private String FIRST_NAME = "firstName";

    public void save(ReviewRequest request) throws ReviewSessionException, InterjoinException {
        User currentStudent = userService.getCurrentUserDetails();

        Session session = sessionService.findByUuid(request.getSessionUuid());

        if(Optional.ofNullable(session.getStudent()).isPresent()) {
            if(currentStudent.getId() != session.getStudent().getId()) {
                throw new InterjoinException("You cannot review other people session", HttpStatus.FORBIDDEN);
            }
        }

        Review review = Review.builder()
                .review(request.getReview())
                .session(session)
                .studentId(Optional.ofNullable(session.getStudent()).map(User::getId).orElse(null))
                .teacherId(Optional.ofNullable(session.getTeacher()).map(User::getId).orElse(null))
                .stars(request.getStars())
                .date(LocalDate.now())
                .build();

        reviewRepository.save(review);

        Map<String, String> templateKeys = new HashMap<>();
        templateKeys.put(FIRST_NAME, session.getTeacher().getFirstName());
        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail(session.getTeacher().getEmail())
                .templateId(newReviewTemplate)
                .templateKeys(templateKeys)
                .build();
        emailService.sendEmail(emailDTO);
    }

    public List<ReviewDto> getCurrentTeacherReviews() {
        User teacher = userService.getCurrentUserDetails();
        return Optional.ofNullable(ReviewMapper.map(reviewRepository.findByTeacherId(teacher.getId()))).orElse(new ArrayList<>());
    }
}
