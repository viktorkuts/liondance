package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.InvalidInputException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "http://localhost:5173")
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
    public Mono<ResponseEntity<UserResponseModel>> registerStudent(@Valid @RequestBody Mono<StudentRequestModel> studentRequestModel) {
        return userService.registerStudent(studentRequestModel)
                .map(userResponseModel -> ResponseEntity.status(HttpStatus.CREATED).body(userResponseModel));
    }
    @GetMapping("/status")
    public Flux<UserResponseModel> getStudentsByStatuses(@RequestParam List<RegistrationStatus> statuses) {
        return userService.getStudentsByRegistrationStatuses(statuses);
    }

    @GetMapping(value = "/pending/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseModel> getPendingStudentById(@PathVariable String userId) {
        return userService.getPendingStudentById(userId);
    }


}
