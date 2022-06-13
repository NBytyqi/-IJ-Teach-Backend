package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserAsDto());
    }

    @PostMapping(path = "/interjoin-verification")
    public ResponseEntity<?> purschaseVerification(@RequestParam(name = "process") String process) {
        return ResponseEntity.ok(userService.purchaseVerification(process));
    }


}
