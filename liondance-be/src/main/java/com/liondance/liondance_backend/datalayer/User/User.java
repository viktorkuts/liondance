package com.liondance.liondance_backend.datalayer.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    private Gender gender;
    private Date dob;
    private String email;
    private String phone;
    private Address address;
}
