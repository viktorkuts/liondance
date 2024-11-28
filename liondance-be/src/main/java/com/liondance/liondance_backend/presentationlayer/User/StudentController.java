package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final UserService userService;

    public StudentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Flux<UserResponseModel> getAllStudents() {
        return userService.getAllUsers(Role.STUDENT);
    }

    @PostMapping(consumes = "application/json")
    public Mono<UserResponseModel> registerStudent(@Valid @RequestBody Mono<StudentRequestModel> studentRequestModel) {
        return userService.registerStudent(studentRequestModel);
    }

}
