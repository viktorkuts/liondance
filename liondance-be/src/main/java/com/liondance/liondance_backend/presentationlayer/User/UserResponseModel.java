package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.EnumSet;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseModel {
    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dob;
    private String email;
    private String phone;
    private Address address;
    private EnumSet<Role> roles;

    public static UserResponseModel from(User user) {
        UserResponseModel responseModel = new UserResponseModel();

        if (user instanceof Student) {
            responseModel = new StudentResponseModel();
        }

        BeanUtils.copyProperties(user, responseModel);
        return responseModel;
    }
}
