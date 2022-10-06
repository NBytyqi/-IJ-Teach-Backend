package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.RestrictionDto;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.RestrictionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/restrictions")
@RequiredArgsConstructor
public class RestrictionController {

    private final RestrictionsService service;

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RestrictionDto>> updateRestrictions(@Valid @RequestBody List<RestrictionDto> restrictions) {
        return ResponseEntity.ok(service.saveNewRestrictions(restrictions));
    }

    @GetMapping
    public ResponseEntity<List<RestrictionDto>> getAll() {
        return ResponseEntity.ok(service.getAllRestrictions());
    }
}
