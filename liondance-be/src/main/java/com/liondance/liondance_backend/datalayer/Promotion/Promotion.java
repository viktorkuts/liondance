package com.liondance.liondance_backend.datalayer.Promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "promotions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Promotion {
    @Id
    private String id;
    private String promotionId;
    private String promotionName;
    private Double discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus promotionStatus;
}
