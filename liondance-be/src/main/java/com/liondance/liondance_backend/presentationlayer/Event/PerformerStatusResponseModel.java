package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.PerformerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformerStatusResponseModel {
    private PerformerResponseModel performerInfo;
    private EventResponseModel eventInfo;
}
