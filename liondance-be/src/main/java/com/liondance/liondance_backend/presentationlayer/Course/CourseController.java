package com.liondance.liondance_backend.presentationlayer.Course;

import com.liondance.liondance_backend.datalayer.Course.CourseStatus;
import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.logiclayer.Course.CourseService;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @PatchMapping("/{courseId}/status")
    public Mono<ResponseEntity<CourseResponseModel>> cancelCourse(@PathVariable String courseId, @RequestBody Mono<Map<String, String>> requestBody) {
        return requestBody
                .flatMap(body -> {
                    String status = body.get("courseStatus");
                    if (status == null || status.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Status cannot be null or empty"));
                    }
                    CourseStatus courseStatus;
                    try {
                        courseStatus = CourseStatus.valueOf(status.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new IllegalArgumentException("Invalid status value: " + status));
                    }
                    return courseService.cancelCourse(courseId, Mono.just(courseStatus));
                })
                .map(courseResponseModel -> ResponseEntity.ok().body(courseResponseModel));
    }

}
