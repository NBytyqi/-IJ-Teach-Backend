package com.interjoin.teach.entities;

import com.interjoin.teach.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @OneToOne(fetch = FetchType.LAZY)
    private User student;

    @OneToOne(fetch = FetchType.LAZY)
    private User teacher;

    private OffsetDateTime dateSlot;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;
}
