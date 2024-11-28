package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Flux<UserResponseModel> getAllUsers() {
        return userRepository.findAll().map(UserResponseModel::from);
    }

    @Override
    public Flux<UserResponseModel> getAllUsers(Role role){
        return userRepository.findUsersByRolesContaining(role).map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> registerStudent(Mono<StudentRequestModel> studentRequestModel) {
        return studentRequestModel.map(StudentRequestModel::toEntity)
                .map(student -> {
                    student.setUserId(UUID.randomUUID().toString());
                    student.setJoinDate(Instant.now());
                    student.setRoles(EnumSet.of(Role.STUDENT));
                    student.setRegistrationStatus(RegistrationStatus.PENDING);
                    return student;
                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }
}
