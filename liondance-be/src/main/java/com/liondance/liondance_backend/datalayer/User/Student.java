package com.liondance.liondance_backend.datalayer.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Student extends User {
    private Date joinDate;
    private RegistrationStatus registrationStatus;
    private String parentFirstName;
    private String parentMiddleName;
    private String parentLastName;
    private String parentEmail;
    private String parentPhone;
}
