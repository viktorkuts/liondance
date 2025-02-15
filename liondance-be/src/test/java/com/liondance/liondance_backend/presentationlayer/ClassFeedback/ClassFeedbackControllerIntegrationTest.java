package com.liondance.liondance_backend.presentationlayer.ClassFeedback;


import com.liondance.liondance_backend.datalayer.ClassFeedback.*;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.User;
import com.liondance.liondance_backend.utils.WebTestAuthConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebTestClient
class ClassFeedbackControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ClassFeedbackRepository classFeedbackRepository;

    @Autowired
    private ClassFeedbackReportRepository classFeedbackReportRepository;

    @Autowired
    private ClassFeedbackPdfRepository classFeedbackPdfRepository;


    private User staff;

    @BeforeEach
    void setUp() {
        classFeedbackRepository.deleteAll().block();
        classFeedbackReportRepository.deleteAll().block();
        classFeedbackPdfRepository.deleteAll().block();

        staff = User.builder()
                .userId("staff1")
                .roles(EnumSet.of(Role.STAFF))
                .build();
    }

    @Test
    void addClassFeedback() {
        ClassFeedbackRequestModel requestModel = ClassFeedbackRequestModel.builder()
                .classDate(LocalDate.now())
                .score(4.5)
                .comment("Great class!")
                .build();

        webTestClient.post()
                .uri("/api/v1/classfeedback")
                .body(Mono.just(requestModel), ClassFeedbackRequestModel.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClassFeedbackResponseModel.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(requestModel.getScore(), response.getScore());
                    assertEquals(requestModel.getComment(), response.getComment());
                });
    }

    @Test
    void getAllClassFeedbackReports_returnsReports() {
        ClassFeedbackReport report = ClassFeedbackReport.builder()
                .reportId("report1")
                .classDate(LocalDate.now())
                .averageScore(4.5)
                .feedbackDetails(List.of(ClassFeedbackResponseModel.builder()
                        .score(4.5)
                        .comment("Great class!")
                        .build()))
                .build();

        classFeedbackReportRepository.save(report).block();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/classfeedback/reports")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClassFeedbackReportResponseModel.class)
                .hasSize(1)
                .value(reports -> {
                    ClassFeedbackReportResponseModel response = reports.get(0);
                    assertEquals("report1", response.getReportId());
                    assertEquals(4.5, response.getAverageScore());
                    assertEquals(1, response.getFeedbackDetails().size());
                });
    }

    @Test
    void downloadReport_returnsPdf_whenExists() {
        ClassFeedbackPdf pdf = ClassFeedbackPdf.builder()
                .reportId("report1")
                .classDate(LocalDate.now())
                .pdfData(new byte[]{1, 2, 3})
                .build();

        classFeedbackPdfRepository.save(pdf).block();

        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/classfeedback/reports/report1/download")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-report1.pdf")
                .expectBody(byte[].class)
                .value(body -> assertArrayEquals(new byte[]{1, 2, 3}, body));
    }

    @Test
    void downloadReport_returnsNotFound_whenPdfDoesNotExist() {
        webTestClient
                .mutateWith(WebTestAuthConfig.getAuthFor(staff))
                .mutateWith(WebTestAuthConfig.csrfConfig)
                .get()
                .uri("/api/v1/classfeedback/reports/nonexistent/download")
                .exchange()
                .expectStatus().isNotFound();
    }

}