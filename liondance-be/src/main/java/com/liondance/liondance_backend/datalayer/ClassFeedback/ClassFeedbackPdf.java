package com.liondance.liondance_backend.datalayer.ClassFeedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "classfeedbackpdfs")
public class ClassFeedbackPdf {
    @Id
    private String id;
    private String reportId;
    private LocalDate classDate;
    private byte[] pdfData;
}
