package com.liondance.liondance_backend.presentationlayer.ClassFeedback;

import com.liondance.liondance_backend.logiclayer.ClassFeedback.ClassFeedbackService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/classfeedback")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class ClassFeedbackController {
    private final ClassFeedbackService classFeedbackService;

    public ClassFeedbackController(ClassFeedbackService classFeedbackService) {
        this.classFeedbackService = classFeedbackService;
    }

@PostMapping()
public Mono<ClassFeedbackResponseModel> addClassFeedback(@RequestBody Mono<ClassFeedbackRequestModel> classfeedback){
        return classFeedbackService.addClassFeedback(classfeedback);
}

@GetMapping("/reports")
public Flux<ClassFeedbackReportResponseModel> getAllClassFeedbackReports(){
        return classFeedbackService.getAllClassFeedbackReports();
}

    @GetMapping("/reports/{reportId}/download")
    public Mono<ResponseEntity<byte[]>> downloadReport(@PathVariable String reportId) {
        return classFeedbackService.downloadClassFeedbackPdf(reportId)
                .map(pdf -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + reportId + ".pdf")
                        .body(pdf.getPdfData()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
