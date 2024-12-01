package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseModel {
    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private LocalDate dob;
    private String email;
    private String phone;
    private Address address;
    private EnumSet<Role> roles;
    private Instant joinDate;
    private RegistrationStatus registrationStatus;
    private String parentFirstName;
    private String parentMiddleName;
    private String parentLastName;
    private String parentEmail;
    private String parentPhone;

    public static UserResponseModel from(User user) {
        UserResponseModel responseModel = new UserResponseModel();
        BeanUtils.copyProperties(user, responseModel);
        return responseModel;
    }
}
