package com.interjoin.teach.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String weekDay;
    private OffsetDateTime dateTime;

//    private Long teacherId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    public User teacher;

    private Long index;
}
