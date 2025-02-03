package com.liondance.liondance_backend.presentationlayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassFeedbackResponseModel {
    private Double score;
    private String comment;

    public static ClassFeedbackResponseModel from(ClassFeedback classFeedback){
        ClassFeedbackResponseModel responseModel = new ClassFeedbackResponseModel();
        BeanUtils.copyProperties(classFeedback,responseModel);
        return responseModel;
    }
}
