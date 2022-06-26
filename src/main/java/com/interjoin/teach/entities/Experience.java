package com.interjoin.teach.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "experience")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String companyName;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "from_date")
    private LocalDate from;
    @Column(name = "to_date")
    private LocalDate to;

    @Column(name = "description")
    private String description;
}