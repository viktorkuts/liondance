package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import org.springframework.beans.BeanUtils;

public class EntityModelUtil {
    public static EventResponseModel toEventResponseModel(Event event) {
        EventResponseModel eventResponseModel = new EventResponseModel();
        BeanUtils.copyProperties(event, eventResponseModel);
        return eventResponseModel;
    }
}
