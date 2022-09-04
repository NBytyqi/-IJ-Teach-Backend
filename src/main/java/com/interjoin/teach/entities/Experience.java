package com.interjoin.teach.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
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

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "uuid", updatable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String companyName;

//    @Lob
//    @Type(type="org.hibernate.type.BinaryType")
//    @Column(name = "logo")
//    private byte[] logo;

    private String awsLogoRef;
    @Column(length = 5000)
    private String awsLogoUrl;

    @Column(name = "from_date")
    private String from;
    @Column(name = "to_date")
    private String to;

    @Column(name = "description")
    private String description;
}