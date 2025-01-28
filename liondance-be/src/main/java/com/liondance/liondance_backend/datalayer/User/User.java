package com.liondance.liondance_backend.datalayer.User;

import com.liondance.liondance_backend.datalayer.common.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.EnumSet;

@Document(collection = "users")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dob;
    private String email;
    private String phone;
    private Address address;
    private EnumSet<Role> roles;
    private String associatedId;
}
