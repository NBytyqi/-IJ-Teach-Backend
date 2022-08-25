package com.interjoin.teach.services;

import com.interjoin.teach.dtos.EmailDTO;
import com.interjoin.teach.dtos.requests.ContactUsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContactUsService {

    private EmailService emailService;
    private final String CONTACT_US_EMAIL = "nderim@interjoinlimited.com";

    public void contactUs(ContactUsRequest request) {
        // send an email to us as team
        Map<String, String> templateKeys = new HashMap<>();
        templateKeys.put("name", request.getName());
        templateKeys.put("email", request.getEmail());
        templateKeys.put("message", request.getMessage());
        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail("CONTACT_US_EMAIL")
                .templateKeys(templateKeys)
//                .templateId()
                .build();
//        emailService.sendEmail(emailDTO);
    }
}
