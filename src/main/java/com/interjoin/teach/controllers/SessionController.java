package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.SessionExistsException;
import com.interjoin.teach.config.exceptions.SessionNotValidException;
import com.interjoin.teach.dtos.SessionDto;
import com.interjoin.teach.dtos.requests.BookSessionRequest;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/new")
    @RolesAllowed(value = { Roles.STUDENT })
    public ResponseEntity<String> bookNewSession(@Valid @RequestBody BookSessionRequest request) throws InterjoinException {
        return ResponseEntity.ok(sessionService.bookSession(request));
    }

    @GetMapping("/student/history")
    @RolesAllowed(value = { Roles.STUDENT })
    public ResponseEntity<List<SessionDto>> getCurrentStudentHistorySessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.getStudentSessionHistory(pageable));
    }

    @GetMapping("/student/classes")
    @RolesAllowed(value = { Roles.STUDENT })
    public ResponseEntity<List<SessionDto>> getCurrentStudentClassesSessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.getStudentSessionClasses(pageable));
    }

    @GetMapping("/student/requests")
    @RolesAllowed(value = { Roles.STUDENT })
    public ResponseEntity<List<SessionDto>> getStudentRequestedSessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable) {
        return ResponseEntity.ok(sessionService.getStudentSessionRequests(pageable));
    }

    @GetMapping("/teacher/history")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<List<SessionDto>> getCurrentTeacherHistorySessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.getTeacherSessionHistory(pageable));
    }

    @GetMapping("/teacher/classes")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<List<SessionDto>> getOneCurrentTeacherSessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.getTeacherActiveSessions(pageable));
    }

    @GetMapping("/teacher/requests")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<List<SessionDto>> getCurrentTeacherRequestSessions(@PageableDefault(sort = {"dateSlot"}, direction = Sort.Direction.DESC, size = 5) Pageable pageable) throws SessionExistsException {
        return ResponseEntity.ok(sessionService.getTeacherSessionRequests(pageable));
    }

    @PutMapping("/list")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<List<SessionDto>> getMyBookingSessions() {
        return ResponseEntity.ok(sessionService.getCurrentTeacherSessions());
    }

    @PutMapping("/approve/{sessionUuid}")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<List<SessionDto>> approveBookSession(@PathVariable String sessionUuid) throws SessionNotValidException {
        return ResponseEntity.ok(sessionService.approveSession(sessionUuid, true));
    }

    @PutMapping("/decline/{sessionUuid}")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<Void> declineBookSession(@PathVariable String sessionUuid) throws SessionNotValidException {
        sessionService.approveSession(sessionUuid, false);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/finish/{sessionUuid}")
    @RolesAllowed(value = { Roles.TEACHER })
    public ResponseEntity<Void> markSessionAsFinished(@PathVariable String sessionUuid) throws InterjoinException {
        sessionService.markSessionAsFinished(sessionUuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/teacher/absent")
    @RolesAllowed(value = { Roles.STUDENT })
    public ResponseEntity<Void> markTeacherAsAbsent(@PathVariable String sessionUuid) throws InterjoinException {
        sessionService.markTeacherAsAbsent(sessionUuid);
        return ResponseEntity.noContent().build();
    }
}
