package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.datalayer.User.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

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
    @NotNull(message = "gender is required")
    private Gender gender;
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
}
