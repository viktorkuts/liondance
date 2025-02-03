package com.liondance.liondance_backend.presentationlayer.ClassFeedback;

import com.liondance.liondance_backend.logiclayer.ClassFeedback.ClassFeedbackService;
import org.springframework.web.bind.annotation.*;
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
}
