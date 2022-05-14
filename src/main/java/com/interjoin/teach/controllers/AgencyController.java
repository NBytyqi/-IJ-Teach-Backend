package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/agency")
@RequiredArgsConstructor
@RolesAllowed(value = {Roles.AGENCY})
public class AgencyController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAgencyUsersPaginated(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(userService.getAgencyUsers(pageable));
    }

    @DeleteMapping("/teacher/{teacherId}")
    public ResponseEntity<Void> removeTeacherFromAgency(@PathVariable Long teacherId) {
        userService.removeAgencyTeacher(teacherId);
        return ResponseEntity.noContent().build();
    }

}
