package com.liondance.liondance_backend.presentationlayer.Course;

import com.liondance.liondance_backend.logiclayer.Course.CourseService;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
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
    public Flux<CourseResponseModel> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PatchMapping("/{courseId}/dates")
    @PreAuthorize("hasAuthority('STAFF')")
    public Mono<ResponseEntity<CourseResponseModel>> patchCancelledDates(
            @PathVariable String courseId,
            @RequestBody Map<String, List<Instant>> requestBody) {

        List<Instant> cancelledDates = requestBody.get("cancelledDates");
        return courseService.patchCancelledDates(courseId, cancelledDates)
                .map(ResponseEntity::ok);
    }
}
