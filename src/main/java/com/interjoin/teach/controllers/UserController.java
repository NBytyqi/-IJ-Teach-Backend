package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.TeacherInfo;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.requests.TeacherFilterRequest;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.UserService;
import com.sendgrid.Response;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.symmetric.TEA;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/token/{token}")
    public ResponseEntity<Void> checkToken(@PathVariable String token) {
        this.userService.getUserDetails(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserAsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getTeacherById(@PathVariable Long id) throws InterjoinException {
        return ResponseEntity.ok(userService.getTeacherById(id));
    }

    @PostMapping(path = "/interjoin-verification")
    public ResponseEntity<?> purschaseVerification(@RequestParam(name = "process") String process) {
        return ResponseEntity.ok(userService.purchaseVerification(process));
    }

    @RolesAllowed(value = {Roles.ADMIN})
    @PostMapping(path = "/interjoin-verification/approve/{teacherId}")
    public ResponseEntity<?> verifyTeacherProfessionalism(@PathVariable Long teacherId) throws InterjoinException {
        userService.verifyTeacherProfessionalism(teacherId, true);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed(value = {Roles.ADMIN})
    @PostMapping(path = "/interjoin-verification/decline/{teacherId}")
    public ResponseEntity<?> declineTeacherProfessionalism(@PathVariable Long teacherId) throws InterjoinException {
        userService.verifyTeacherProfessionalism(teacherId, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/teachers")
    public ResponseEntity<List<TeacherInfo>> getFilteredTeachers(@RequestBody TeacherFilterRequest filterRequest) {
        return ResponseEntity.ok(userService.getFilteredTeachers(filterRequest));
    }

    @PutMapping(path = "/favorite/{teacherId}")
    public ResponseEntity<Void> favoriteTeacherById(@PathVariable("teacherId") Long teacherId, @RequestParam boolean favorite) {
        userService.favoriteTeacher(teacherId, favorite);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<String>> getCurriculumsOfSubjectAndTeacher(@RequestParam Long teacherId, @RequestParam String subject) throws InterjoinException {
        return ResponseEntity.ok(userService.getCurriculumsOfSubjectAndTeacher(teacherId, subject));
    }

    @DeleteMapping("/agency")
    public ResponseEntity<Void> removeCurrentTeacherFromAgency() {
        userService.removeMyselfFromAgency();
        return ResponseEntity.ok().build();
    }


}
