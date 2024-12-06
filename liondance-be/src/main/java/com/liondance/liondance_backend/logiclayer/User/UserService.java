package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<UserResponseModel> getAllUsers();
    Flux<UserResponseModel> getAllUsers(Role role);
    Mono<UserResponseModel> getUserByUserId(String userId);
    Mono<UserResponseModel> registerStudent(Mono<StudentRequestModel> studentRequestModel);
}
