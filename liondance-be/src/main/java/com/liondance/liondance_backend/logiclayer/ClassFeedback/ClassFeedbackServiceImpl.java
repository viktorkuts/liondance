package com.liondance.liondance_backend.logiclayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.ClassFeedback.*;
import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackReportResponseModel;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackRequestModel;
import com.liondance.liondance_backend.presentationlayer.ClassFeedback.ClassFeedbackResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static reactor.core.publisher.Flux.just;
@Slf4j
@Service
public class ClassFeedbackServiceImpl implements ClassFeedbackService{

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;
    private final ClassFeedbackRepository classFeedbackRepository;
    private final ClassFeedbackReportRepository classFeedbackReportRepository;
    private final ClassFeedbackPdfRepository classFeedbackPdfRepository;

    public ClassFeedbackServiceImpl(CourseRepository courseRepository, UserRepository userRepository, TaskScheduler taskScheduler, NotificationService notificationService, ClassFeedbackRepository classFeedbackRepository, ClassFeedbackReportRepository classFeedbackReportRepository, ClassFeedbackPdfRepository classFeedbackPdfRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
        this.notificationService = notificationService;
        this.classFeedbackRepository = classFeedbackRepository;
        this.classFeedbackReportRepository = classFeedbackReportRepository;
        this.classFeedbackPdfRepository = classFeedbackPdfRepository;
    }

    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *" )
    public void checkForCoursesToday() {
        log.debug("Scheduled task started");

        LocalDate today = LocalDate.now();

        courseRepository.findCoursesByDayOfWeek(today.getDayOfWeek())
                .doOnSubscribe(subscription -> log.debug("Fetching courses for {}", today.getDayOfWeek()))
                .doOnNext(course -> log.debug("Processing course: {}", course.getName()))
                .doOnError(error -> log.error("Error fetching courses: {}", error.getMessage(), error))
                .switchIfEmpty(Mono.fromRunnable(() -> log.debug("No courses found for today: {}", today)))
                .filter(course -> {
                    if (course.getCancelledDates().contains(today)) {
                        log.debug("Course {} is canceled for today", course.getName());
                        return false;
                    }
                    return true;
                })
                .doOnNext(course -> {
                    LocalDateTime taskTime = LocalDateTime.ofInstant(course.getEndTime(), ZoneId.systemDefault());
                    log.debug("Scheduling feedback for course {} at {}", course.getName(), taskTime);
                    taskScheduler.schedule(() -> sendScheduledFeedbackRequests(course), java.sql.Timestamp.valueOf(taskTime));

                    //LocalDateTime reportGenerationTime = taskTime.plusDays(1);
                    LocalDateTime reportGenerationTime = taskTime.plusMinutes(10); //this is for testing. Delete after
                    taskScheduler.schedule(() -> generateClassFeedbackReport(course, today), java.sql.Timestamp.valueOf(reportGenerationTime));
                })
                .doOnComplete(() -> log.debug("Scheduled task finished"))
                .subscribe();
    }

public void sendScheduledFeedbackRequests(Course course) {
    log.debug("Starting to send feedback requests for course: {}", course.getName());

    List<String> studentEmails = studentEmails();
    log.debug("Fetched {} student emails for course: {}", studentEmails.size(), course.getName());
    LocalDate date = LocalDate.now();
    String message = new StringBuilder()
            .append("Hello, you can fill out feedback for today's class from the link below. \n")
            .append("https://fe.dev.kleff.io/classfeedback/"+date).toString();

    for (String email : studentEmails) {
        log.debug("Sending feedback email to: {}", email);
        notificationService.sendMail(email, "Class Feedback", message, NotificationType.STUDENT_AFTER_SESSION);
    }

    log.debug("Finished sending feedback requests for course: {}", course.getName());
}

    public List<String> studentEmails() {
        log.debug("Fetching student emails from user repository");

        List<String> emails = userRepository.findUsersByRolesContaining(Role.STUDENT)
                .doOnNext(student -> log.debug("Processing student: {}", student.getEmail()))
                .doOnError(error -> log.error("Error fetching students: {}", error.getMessage(), error))
                .map(User::getEmail)
                .collectList()
                .block();

        if (emails == null || emails.isEmpty()) {
            log.debug("No student emails found");
        } else {
            log.debug("Finished fetching all student emails. Total: {}", emails.size());
        }
        return emails;
    }


    @Override
    public Mono<ClassFeedbackResponseModel> addClassFeedback(Mono<ClassFeedbackRequestModel> classFeedbackRequestModel) {
        return classFeedbackRequestModel
                .map(ClassFeedbackRequestModel::from)
                .flatMap(classFeedbackRepository::save)
                .map(ClassFeedbackResponseModel::from);
    }

    public void generateClassFeedbackReport(Course course, LocalDate classDate) {
        log.info("Generating class feedback report for {} - {}", course.getName(), classDate);

        classFeedbackRepository.findAllByClassDate(classDate)
                .collectList()
                .flatMap(classFeedbacks -> {
                    if (classFeedbacks.isEmpty()) {
                        log.info("No feedback found for {}", classDate);
                        return Mono.empty();
                    }

                    double avgScore = classFeedbacks.stream()
                            .mapToDouble(ClassFeedback::getScore)
                            .average()
                            .orElse(0.0);


                    ClassFeedbackReport report = ClassFeedbackReport.builder()
                            .reportId(UUID.randomUUID().toString())
                            .classDate(classDate)
                            .averageScore(avgScore)
                            .feedbackDetails(
                                    classFeedbacks.stream()
                                            .map(ClassFeedbackResponseModel::from)
                                            .toList()
                            )
                            .build();


                    byte[] pdfBytes = createPdf(report);


                    ClassFeedbackPdf pdfDocument = ClassFeedbackPdf.builder()
                            .reportId(report.getReportId())
                            .classDate(classDate)
                            .pdfData(pdfBytes)
                            .build();

                    return classFeedbackPdfRepository.save(pdfDocument)
                            .then(classFeedbackReportRepository.save(report));
                })
                .subscribe();
    }

    private byte[] createPdf(ClassFeedbackReport report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Class Feedback Report - " + report.getClassDate()));
            document.add(new Paragraph("Average Score: " + report.getAverageScore()));
            document.add(new Paragraph("\nFeedback Comments:\n"));

            for (ClassFeedbackResponseModel feedback : report.getFeedbackDetails()) {
                document.add(new Paragraph("- " + feedback.getComment() + " (Score: " + feedback.getScore() + ")"));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            return new byte[0];
        }
    }

}
