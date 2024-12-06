package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.EnumSet;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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
    public Mono<UserResponseModel> getUserByUserId(String userId) {
        return userRepository.findUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId " + userId + " not found")))
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> registerStudent(Mono<StudentRequestModel> studentRequestModel) {
        return studentRequestModel
                .map(StudentRequestModel::toEntity)
                .map(student -> {
                    student.setUserId(UUID.randomUUID().toString());
                    student.setJoinDate(Instant.now());
                    student.setRoles(EnumSet.of(Role.STUDENT));
                    student.setRegistrationStatus(RegistrationStatus.PENDING);
                    return student;
                })
                .doOnNext(user -> {
                    String message = new StringBuilder()
                            .append("Welcome to Lion Dance, ")
                            .append(user.getFirstName())
                            .append("!")
                            .append("\nYour registration is pending approval.")
                            .append("\nA staff member will validate details and approve shortly")
                            .append("\n\nThank you for joining Lion Dance!")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            user.getEmail(),
                            "Welcome to Lion Dance - Pending Registration",
                            message,
                            NotificationType.STUDENT_REGISTRATION
                    );

                    if(!success){
                        throw new MailSendException("Failed to send email to " + user.getEmail());
                    }

                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }
}
