package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.AffiliateMarketerDto;
import com.interjoin.teach.entities.AffiliateMarketer;
import com.interjoin.teach.mappers.AffiliateMarketerMapper;
import com.interjoin.teach.repositories.AffiliateMarketerRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Data
@RequiredArgsConstructor
@Service
public class AffiliateMarketingService {

    private final AffiliateMarketerRepository repository;

    @Value("${spring.profiles.active}")
    private String profileActive;

    @Transactional
    public AffiliateMarketerDto save(AffiliateMarketerDto request) {
        AffiliateMarketer affiliateMarketer = repository.findByEmail(request.getEmail())
                .orElse(AffiliateMarketer.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .referalCode(request.getReferalCode())
                        .phoneNumber(request.getPhoneNumber())
                        .build());
        repository.save(affiliateMarketer);
        affiliateMarketer.setReferalCode(String.format("%d_%s", affiliateMarketer.getId(), profileActive));
        return AffiliateMarketerMapper.map(affiliateMarketer);
    }

    public AffiliateMarketer getAffiliateByReferalCode(String affiliateCode) throws InterjoinException {
        return repository.findByReferalCode(affiliateCode).orElseThrow(() -> new InterjoinException("Affiliate marketer not found"));
    }
}
