package com.liondance.liondance_backend.presentationlayer.ClassFeedback;

import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassFeedbackRequestModel {
    private LocalDate ClassDate;
    private Double score;
    private String comment;

public static ClassFeedback from(ClassFeedbackRequestModel classFeedbackRequestModel){
    ClassFeedback classFeedback = new ClassFeedback();
    BeanUtils.copyProperties(classFeedbackRequestModel,classFeedback);
    classFeedback.setFeedbackId(UUID.randomUUID().toString());
    return classFeedback;
}

}
