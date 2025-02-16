package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequestModel {
    private String promotionName;
    private Double discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus promotionStatus;

    public static Promotion from(PromotionRequestModel promotionRequestModel){
        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(promotionRequestModel,promotion);
        promotion.setPromotionId(UUID.randomUUID().toString());
        return promotion;
    }
}
