package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseModel extends UserResponseModel {

    private List<EventResponseModel> activeEvents;
    private List<EventResponseModel> pastEvents;

}
