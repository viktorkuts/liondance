package com.liondance.liondance_backend.logiclayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static reactor.core.publisher.Flux.just;

public class ClassFeedbackServiceImpl implements ClassFeedbackService{

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    public ClassFeedbackServiceImpl(CourseRepository courseRepository, UserRepository userRepository, TaskScheduler taskScheduler) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void checkForCoursesToday(){
        LocalDate today = LocalDate.now();
        Flux<Course> courses = courseRepository.findCoursesByDayOfWeek(today.getDayOfWeek());
        Mono<Boolean> isEmpty = courses.hasElements();
        isEmpty.subscribe(empty -> {
            if (empty){
                return;
            }
            courses.subscribe(course -> {
                LocalDateTime taskTime = LocalDateTime.of(LocalDate.now(), course.getEndTime());
                taskScheduler.schedule(() -> sendScheduledFeedbackRequests(course), java.sql.Timestamp.valueOf(taskTime)); //yeah, I'm using the deprecated version whatever
            });
        } ) ;

    }
    public void sendScheduledFeedbackRequests(Course course) {
      // need to figure out how to generate the link
    }

    public List<String> studentEmails(){
     Flux<User>  students = userRepository.findUsersByRolesContaining(Role.STUDENT);
     ArrayList<String> emails = new ArrayList<>();
     students.subscribe(student -> {
         emails.add(student.getEmail());
     });
     return emails;
    }

}
