package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTeacherId(Long teacherId);
}
