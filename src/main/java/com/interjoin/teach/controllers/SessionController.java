package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.SessionExistsException;
import com.interjoin.teach.dtos.SessionDto;
import com.interjoin.teach.dtos.requests.BookSessionRequest;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<String> bookNewSession(@Valid @RequestBody BookSessionRequest request) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.bookSession(request));
    }

    @GetMapping("/history")
    @RolesAllowed(value = { Roles.STUDENT })
    public ResponseEntity<List<SessionDto>> getCurrentStudentHistorySessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.getStudentSessionHistory(pageable));
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
