package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.datalayer.User.User;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestModel {
    @NotBlank(message = "firstName is required")
    private String firstName;
    private String middleName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @NotNull(message = "dob is required")
    private LocalDate dob;
    @NotBlank(message = "email is required")
    @Email(
            message = "email is invalid",
            regexp = "^(?![_.])[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            flags = Pattern.Flag.CASE_INSENSITIVE
    )
    private String email;
    @NotNull(message = "address is required")
    private Address address;

    private String phone;

    public static User from(UserRequestModel userRequestModel){
     User user = new User();

        BeanUtils.copyProperties(userRequestModel,user);
        user.setUserId(UUID.randomUUID().toString());
        return user;
    }
}
