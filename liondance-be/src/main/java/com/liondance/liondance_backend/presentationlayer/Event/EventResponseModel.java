package com.liondance.liondance_backend.presentationlayer.Event;

import com.liondance.liondance_backend.datalayer.Event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseModel {
    private String id;
    private String name;

    public static EventResponseModel from(Event event) {
        EventResponseModel responseModel = new EventResponseModel();
        BeanUtils.copyProperties(event, responseModel);
        return responseModel;
    }
}
