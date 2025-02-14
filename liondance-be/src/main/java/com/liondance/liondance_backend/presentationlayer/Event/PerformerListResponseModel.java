package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.PerformerStatus;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformerListResponseModel {
    private UserResponseModel performer;
    private PerformerStatus status;
}
