package com.liondance.liondance_backend.presentationlayer.Promotion;

import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponseModel {
    private String promotionId;
    private String promotionName;
    private Double discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus promotionStatus;

    public static PromotionResponseModel from(Promotion promotion)
    {
       PromotionResponseModel responseModel = new PromotionResponseModel();
        BeanUtils.copyProperties(promotion, responseModel);
        return responseModel;
    }

}
