package com.liondance.liondance_backend.presentationlayer.Feedback;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Feedback.Feedback;
import com.liondance.liondance_backend.datalayer.common.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseModel {
    private String feedbackId;
    private Instant timestamp;
    private String feedback;
    private int rating;
    private Event event;
    private Visibility visibility;

    public static FeedbackResponseModel from(Feedback feedback) {
        FeedbackResponseModel responseModel = new FeedbackResponseModel();
        BeanUtils.copyProperties(feedback, responseModel);
        return responseModel;
    }
}