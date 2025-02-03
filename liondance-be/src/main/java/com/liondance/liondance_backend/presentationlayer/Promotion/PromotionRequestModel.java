package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequestModel {
    private String promotionId;
    private String promotionName;
    private Double discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus promotionStatus;
}
