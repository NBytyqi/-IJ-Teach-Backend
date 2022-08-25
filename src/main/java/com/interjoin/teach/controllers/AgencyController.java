package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.AgencyDashboardDataDto;
import com.interjoin.teach.dtos.AgencyTeacher;
import com.interjoin.teach.dtos.AgencyTeachersList;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/agency")
@RequiredArgsConstructor
@RolesAllowed(value = {Roles.AGENCY})
public class AgencyController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<AgencyTeacher>> getActiveAgencyUsers(@RequestParam String status) {
        return ResponseEntity.ok(userService.getAgencyUsers(status));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AgencyTeachersList>> getAllAgenciesData() {
        return ResponseEntity.ok(userService.getAgenciesTeachersList());
    }

    @PutMapping("/teacher/{teacherId}")
    public ResponseEntity<Void> approveOrDeclineAgencyTeacher(@PathVariable Long teacherId, @RequestParam boolean approve) {
        userService.approveOrDeclineAgencyTeacher(teacherId, approve);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AgencyDashboardDataDto> getAgencyDashboardData() {
        return ResponseEntity.ok(userService.getAgencyDashboardData());
    }

}
