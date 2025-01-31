package com.liondance.liondance_backend.presentationlayer.Course;

import com.liondance.liondance_backend.logiclayer.Course.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STAFF')")
    public Flux<CourseResponseModel> getAllCoursesByCourseId(@RequestParam String courseId) {
        return courseService.getAllCoursesByCourseId(courseId);
    }

    @PatchMapping("/{courseId}/date")
    @PreAuthorize("hasAuthority('STAFF')")
    public Mono<ResponseEntity<CourseResponseModel>> cancelCourse(@PathVariable String courseId, @RequestBody Mono<Map<String, String>> requestBody) {
        return requestBody
                .flatMap(body -> {
                    String date = body.get("date");
                    if (date == null || date.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Date cannot be null or empty"));
                    }
                    LocalDate cancelledDate = LocalDate.parse(date);
                    return courseService.cancelCourse(courseId, cancelledDate);
                })
                .map(courseResponseModel -> ResponseEntity.ok().body(courseResponseModel));
    }

}
