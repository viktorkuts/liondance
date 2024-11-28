package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.datalayer.User.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestModel extends UserRequestModel {
    private String parentFirstName;
    private String parentMiddleName;
    private String parentLastName;
    private String parentEmail;
    private String parentPhone;

    public static Student toEntity(StudentRequestModel studentRequestModel) {
        Student newStudent = new Student();
        BeanUtils.copyProperties(studentRequestModel, newStudent);
        return newStudent;
    }
}
