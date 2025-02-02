package com.liondance.liondance_backend.presentationlayer.Course;

import com.liondance.liondance_backend.logiclayer.Course.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerIntegrationTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCourses() {
        CourseResponseModel course1 = new CourseResponseModel();
        CourseResponseModel course2 = new CourseResponseModel();
        when(courseService.getAllCourses()).thenReturn(Flux.just(course1, course2));

        Flux<CourseResponseModel> result = courseController.getAllCourses();

        List<CourseResponseModel> courses = result.collectList().block();
        assertNotNull(courses);
        assertEquals(2, courses.size());
        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void testPatchCancelledDates() {
        String courseId = "course123";
        List<Instant> cancelledDates = List.of(Instant.now());
        Map<String, List<Instant>> requestBody = Map.of("cancelledDates", cancelledDates);
        CourseResponseModel responseModel = new CourseResponseModel();
        when(courseService.patchCancelledDates(courseId, cancelledDates)).thenReturn(Mono.just(responseModel));

        Mono<ResponseEntity<CourseResponseModel>> result = courseController.patchCancelledDates(courseId, requestBody);

        ResponseEntity<CourseResponseModel> responseEntity = result.block();
        assertNotNull(responseEntity);
        assertEquals(responseModel, responseEntity.getBody());
        verify(courseService, times(1)).patchCancelledDates(courseId, cancelledDates);
    }

    @Test
    void testGetAllCourses_Empty() {
        when(courseService.getAllCourses()).thenReturn(Flux.empty());

        Flux<CourseResponseModel> result = courseController.getAllCourses();

        List<CourseResponseModel> courses = result.collectList().block();
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void testPatchCancelledDates_InvalidCourseId() {
        String courseId = "invalidCourseId";
        List<Instant> cancelledDates = List.of(Instant.now());
        Map<String, List<Instant>> requestBody = Map.of("cancelledDates", cancelledDates);
        when(courseService.patchCancelledDates(courseId, cancelledDates)).thenReturn(Mono.empty());

        Mono<ResponseEntity<CourseResponseModel>> result = courseController.patchCancelledDates(courseId, requestBody);

        ResponseEntity<CourseResponseModel> responseEntity = result.block();
        assertNull(responseEntity);
        verify(courseService, times(1)).patchCancelledDates(courseId, cancelledDates);
    }

    @Test
    void testPatchCancelledDates_NullRequestBody() {
        String courseId = "course123";
        Map<String, List<Instant>> requestBody = null;

        assertThrows(NullPointerException.class, () -> {
            courseController.patchCancelledDates(courseId, requestBody).block();
        });
    }
}