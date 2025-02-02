package com.liondance.liondance_backend.logiclayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.logiclayer.Feedback.FeedbackService;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import reactor.core.publisher.Flux;

import java.time.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ClassFeedbackServiceImplUnitTest {
        @Mock
        private CourseRepository courseRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private TaskScheduler taskScheduler;

        @Mock
        private NotificationService notificationService;

        @InjectMocks
        private ClassFeedbackServiceImpl feedbackService;

        private Course course1;

        @BeforeEach
        void setUp() {
            course1 = Course.builder()
                    .courseId("1")
                    .name("Sample Course")
                    .dayOfWeek(DayOfWeek.SUNDAY)
                    .startTime(Instant.parse("2025-12-25T10:00:00Z"))
                    .endTime(Instant.parse("2025-12-25T12:00:00Z"))
                    .cancelledDates(new ArrayList<>())
                    .userIds(List.of("student-id-1"))
                    .instructorId("instructor-id-1")
                    .build();
        }

        @Test
        void whenNoCoursesToday_thenNoTasksScheduled() {

            Mockito.when(courseRepository.findCoursesByDayOfWeek(Mockito.any())).thenReturn(Flux.empty());

            feedbackService.checkForCoursesToday();

            Mockito.verify(taskScheduler, Mockito.never()).schedule(Mockito.any(), (Trigger) Mockito.any());
        }

        @Test
        void whenCourseCancelled_thenNoTasksScheduled() {

            course1.getCancelledDates().add(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

            Mockito.when(courseRepository.findCoursesByDayOfWeek(LocalDate.now().getDayOfWeek()))
                    .thenReturn(Flux.just(course1));

            feedbackService.checkForCoursesToday();

            Mockito.verify(taskScheduler, Mockito.never()).schedule(Mockito.any(), (Trigger) Mockito.any());
        }

        @Test
        void whenTaskRuns_thenFeedbackRequestsAreSent() {

            User student = User.builder()
                    .userId("student-id-1")
                    .email("student1@example.com")
                    .roles(EnumSet.of(Role.STUDENT))
                    .build();

            Mockito.when(userRepository.findUsersByRolesContaining(Role.STUDENT)).thenReturn(Flux.just(student));


            feedbackService.sendScheduledFeedbackRequests(course1);
            LocalDate date = LocalDate.now();

            Mockito.verify(notificationService, Mockito.times(1)).sendMail(
                    Mockito.eq("student1@example.com"),
                    Mockito.eq("Class Feedback"),
                    Mockito.contains("https://fe.dev.kleff.io/classfeedback/"+date),
                    Mockito.eq(NotificationType.STUDENT_AFTER_SESSION)
            );
        }

        @Test
        void whenNoStudents_thenNoEmailsSent() {

            Mockito.when(userRepository.findUsersByRolesContaining(Role.STUDENT)).thenReturn(Flux.empty());

            feedbackService.sendScheduledFeedbackRequests(course1);

            Mockito.verify(notificationService, Mockito.never()).sendMail(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        }
    }
