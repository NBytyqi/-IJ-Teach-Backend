package com.interjoin.teach.entities;

import com.interjoin.teach.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "uuid", updatable = false)
    private String uuid;

    @OneToOne(fetch = FetchType.LAZY)
    private User student;

    @OneToOne(fetch = FetchType.LAZY)
    private User teacher;

    private BigDecimal price;

    private OffsetDateTime dateSlot;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;

    @Column(name = "review", nullable = true)
    private String review;
    @Column(name = "review_score", nullable = true)
    private Double reviewScore;

    private String subject;
    private String curriculum;

}
