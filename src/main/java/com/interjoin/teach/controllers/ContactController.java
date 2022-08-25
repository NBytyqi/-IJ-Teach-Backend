package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.requests.ContactUsRequest;
import com.interjoin.teach.services.ContactUsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactUsService contactUsService;

    @PostMapping
    public ResponseEntity<Void> contactUs(@RequestBody ContactUsRequest request)  {
        contactUsService.contactUs(request);
        return ResponseEntity.ok().build();
    }
}
