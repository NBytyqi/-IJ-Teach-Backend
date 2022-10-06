package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.AffiliateMarketerDto;
import com.interjoin.teach.services.AffiliateMarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/affiliate")
@RequiredArgsConstructor
public class AffiliateMarketerController {

    private final AffiliateMarketingService service;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AffiliateMarketerDto> create(@Valid @RequestBody AffiliateMarketerDto request) {
        return ResponseEntity.ok(service.save(request));
    }

}
