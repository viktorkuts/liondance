package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseModel extends UserResponseModel {
    private Instant joinDate;
    private RegistrationStatus registrationStatus;
    private String parentFirstName;
    private String parentMiddleName;
    private String parentLastName;
    private String parentEmail;
    private String parentPhone;
}
