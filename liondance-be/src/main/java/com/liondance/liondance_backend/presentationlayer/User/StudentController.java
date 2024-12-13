package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.Course.CourseService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.presentationlayer.Course.CourseResponseModel;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class StudentController {
    private final UserService userService;
    private final CourseService courseService;

    public StudentController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
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

    @PatchMapping("/{userId}/registration-status")
    public Mono<ResponseEntity<UserResponseModel>> updateStudentRegistrationStatus(@PathVariable String userId, @RequestBody RegistrationStatusPatchRequestModel registrationStatusPatchRequestModel) {
        return userService.updateStudentRegistrationStatus(userId, registrationStatusPatchRequestModel)
                .map(userResponseModel -> ResponseEntity.ok().body(userResponseModel));
    }

    @GetMapping("/status")
    public Flux<UserResponseModel> getStudentsByStatuses(@RequestParam List<RegistrationStatus> statuses) {
        return userService.getStudentsByRegistrationStatuses(statuses);
    }

    @GetMapping(value = "/pending/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseModel> getPendingStudentById(@PathVariable String userId) {
        return userService.getPendingStudentById(userId);
    }

    @GetMapping("/{studentId}/courses")
    public Flux<CourseResponseModel> getCoursesByStudentId(@PathVariable String studentId) {
        return courseService.getAllCoursesByStudentId(studentId);
    }

    @GetMapping("/{studentId}")
    public Mono<UserResponseModel> getStudentById(@PathVariable String studentId) {
        return userService.getStudentById(studentId);
    }


}
