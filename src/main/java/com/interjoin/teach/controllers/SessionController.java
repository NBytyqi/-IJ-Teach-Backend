package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.SessionExistsException;
import com.interjoin.teach.dtos.SessionDto;
import com.interjoin.teach.dtos.requests.BookSessionRequest;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/new")
    @RolesAllowed(value = { Roles.STUDENT })
    public void bookNewSession(@Valid @RequestBody BookSessionRequest request) throws SessionExistsException {
        sessionService.bookSession(request);
    }

    @PutMapping("/list")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<List<SessionDto>> getMyBookingSessions() {
        return ResponseEntity.ok(sessionService.getCurrentTeacherSessions());
    }

    @PutMapping("/approve")
    @RolesAllowed(value = { Roles.TEACHER })
    public void approveBookSession() {

    }
}
