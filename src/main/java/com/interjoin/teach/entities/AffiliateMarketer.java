package com.interjoin.teach.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateMarketer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String referalCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "affiliateMarketer")
    private List<User> users;

}
