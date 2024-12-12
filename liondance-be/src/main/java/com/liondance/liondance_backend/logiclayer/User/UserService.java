package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.presentationlayer.User.RegistrationStatusPatchRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    Flux<UserResponseModel> getAllUsers();
    Flux<UserResponseModel> getAllUsers(Role role);
    Mono<UserResponseModel> getUserByUserId(String userId);
    Mono<UserResponseModel> updateUser(String userId, UserRequestModel userRequestModel);
    Mono<UserResponseModel> updateStudentRegistrationStatus(String userId, RegistrationStatusPatchRequestModel registrationStatus);
    Mono<UserResponseModel> registerStudent(Mono<StudentRequestModel> studentRequestModel);
    Flux<UserResponseModel> getStudentsByRegistrationStatuses(List<RegistrationStatus> statuses);
    Mono<UserResponseModel> getPendingStudentById(String userId);
    Mono<UserResponseModel> getStudentById(String studentId);
}
