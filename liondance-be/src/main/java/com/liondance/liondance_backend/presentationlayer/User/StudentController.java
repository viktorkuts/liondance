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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping
    public Flux<UserResponseModel> getAllStudents(@AuthenticationPrincipal JwtAuthenticationToken jwt) {
        return userService.getAllUsers(Role.STUDENT);
    }

    @PostMapping(consumes = "application/json")
    public Mono<ResponseEntity<UserResponseModel>> registerStudent(@Valid @RequestBody Mono<StudentRequestModel> studentRequestModel) {
        return userService.registerStudent(studentRequestModel)
                .map(userResponseModel -> ResponseEntity.status(HttpStatus.CREATED).body(userResponseModel));
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PatchMapping("/{userId}/registration-status")
    public Mono<ResponseEntity<UserResponseModel>> updateStudentRegistrationStatus(@PathVariable String userId, @RequestBody RegistrationStatusPatchRequestModel registrationStatusPatchRequestModel) {
        return userService.updateStudentRegistrationStatus(userId, registrationStatusPatchRequestModel)
                .map(userResponseModel -> ResponseEntity.ok().body(userResponseModel));
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping("/status")
    public Flux<UserResponseModel> getStudentsByStatuses(@RequestParam List<RegistrationStatus> statuses) {
        return userService.getStudentsByRegistrationStatuses(statuses);
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping(value = "/pending/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseModel> getPendingStudentById(@PathVariable String userId) {
        return userService.getPendingStudentById(userId);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT','STAFF')")
    @GetMapping("/{studentId}/courses")
    public Flux<CourseResponseModel> getCoursesByStudentId(@PathVariable String studentId) {
        return courseService.getAllCoursesByStudentId(studentId);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'STAFF')")
    @GetMapping("/{studentId}")
    public Mono<UserResponseModel> getStudentById(@PathVariable String studentId) {
        return userService.getStudentById(studentId);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'STAFF')")
    @PutMapping("/{studentId}")
    public Mono<UserResponseModel> updateStudent(@PathVariable String studentId, @RequestBody StudentRequestModel studentRequestModel) {
        return userService.updateStudent(studentId, studentRequestModel);
    }


}
