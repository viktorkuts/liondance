package com.liondance.liondance_backend.presentationlayer.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseModel extends UserResponseModel {

    private Instant joinDate;
    private List<String> activeEvents;
    private List<String> pastEvents;

}
