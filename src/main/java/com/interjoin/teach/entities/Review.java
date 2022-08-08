package com.interjoin.teach.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long studentId;
    private Long teacherId;

    @OneToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private Session session;

    @Lob
    @Column(name = "review", length = 1000)
    private String review;

    private Double stars;

    private LocalDate date;
}
