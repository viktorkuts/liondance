package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.StudentResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.*;
import com.liondance.liondance_backend.utils.exceptions.EmailInUse;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import com.liondance.liondance_backend.utils.exceptions.StudentNotPending;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Slf4j
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
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId: " + userId + " not found")))
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> updateUser(String userId, UserRequestModel userRequestModel) {
        return userRepository.findUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId: " + userId + " not found")))
                .map(user -> {
                    user.setFirstName(userRequestModel.getFirstName());
                    user.setMiddleName(userRequestModel.getMiddleName());
                    user.setLastName(userRequestModel.getLastName());
                    user.setGender(userRequestModel.getGender());
                    user.setDob(userRequestModel.getDob());
                    user.setEmail(userRequestModel.getEmail());
                    user.setPhone(userRequestModel.getPhone());
                    user.setAddress(userRequestModel.getAddress());
                    return user;
                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> registerStudent(Mono<StudentRequestModel> studentRequestModel) {
        return studentRequestModel
                .flatMap(request -> Mono.just(userRepository.findUserByEmail(request.getEmail()))
                        .flatMap(user -> user.hasElement().flatMap(exists -> {
                            if(exists){
                                return Mono.error(new EmailInUse("Email: " + request.getEmail() + " is already in use"));
                            }else{
                                return Mono.just(request);
                            }
                        }))
                )
                .map(StudentRequestModel::toEntity)
                .map(student -> {
                    student.setUserId(UUID.randomUUID().toString());
                    student.setJoinDate(Instant.now());
                    student.setRoles(EnumSet.of(Role.STUDENT));
                    student.setRegistrationStatus(RegistrationStatus.PENDING);
                    return student;
                })
                .doOnNext(user -> {
                    String  message = new StringBuilder()
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

    @Override
    public Mono<UserResponseModel> updateStudentRegistrationStatus(String userId, RegistrationStatusPatchRequestModel registrationStatus) {
        return userRepository.findUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId: " + userId + " not found")))
                .filter(user -> user instanceof Student)
                .switchIfEmpty(Mono.error(new NotFoundException("Student with userId: " + userId + " not found")))
                .cast(Student.class)
                .filter(student -> student.getRegistrationStatus() == RegistrationStatus.PENDING)
                .switchIfEmpty(Mono.error(new StudentNotPending("Student with userId: " + userId + " is not pending")))
                .map(student -> {
                    String message = "";

                    student.setRegistrationStatus(registrationStatus.getRegistrationStatus());

                    if(registrationStatus.getRegistrationStatus() == RegistrationStatus.ACTIVE) {
                        message = new StringBuilder()
                                .append("Congratulations, ")
                                .append(student.getFirstName())
                                .append("!")
                                .append("\nYour registration has been approved.")
                                .append("\nYou can now access all features of Lion Dance.")
                                .append("\n\nThank you for joining Lion Dance!")
                                .toString();

                        userRepository.save(student).subscribe();
                    }else{
                        message = new StringBuilder()
                                .append("We're sorry, ")
                                .append(student.getFirstName())
                                .append("!")
                                .append("\nYour registration could not be approved at the moment.")
                                .append("\nPlease contact us directly to get approved")
                                .append("\n\nThank you for joining Lion Dance!")
                                .toString();

                        userRepository.delete(student).subscribe();
                    }

                    Boolean status = notificationService.sendMail(
                            student.getEmail(),
                            "Lion Dance - Registration Update",
                            message,
                            NotificationType.STUDENT_CONFIRM_REGISTRATION
                    );

                    return student;
                })
                .map(UserResponseModel::from);
    }

    @Override
    public Flux<UserResponseModel> getStudentsByRegistrationStatuses(List<RegistrationStatus> statuses) {
        return userRepository.findStudentsByRegistrationStatuses(statuses)
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> getPendingStudentById(String userId) {
        return userRepository.findByUserId(userId)
                .filter(user -> user instanceof Student)
                .cast(Student.class)
                .filter(student -> student.getRegistrationStatus() == RegistrationStatus.PENDING)
                .map(StudentResponseModel::from)
                .switchIfEmpty(Mono.error(new NotFoundException("Pending student not found with userId: " + userId)));
    }

    @Override
    public Mono<UserResponseModel> AddNewUser(String role, Mono<UserRequestModel> userRequestModel) {
        return userRequestModel
                .map(UserRequestModel::from)
                .map(user -> {
                    user.setUserId(UUID.randomUUID().toString());
                    user.setRoles(EnumSet.of(Role.valueOf(role)));
                    return user;
                })
                .doOnNext(user -> {
                    String message = new StringBuilder()
                            .append("Welcome to Lion Dance, ")
                            .append(user.getFirstName())
                            .append("!")
                            .append("\nYou have been registered")
                            .append("\n\nThank you for joining Lion Dance!")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            user.getEmail(),
                            "Welcome to Lion Dance - Account Registration Confirmation",
                            message,
                            NotificationType.USER_REGISTRATION
                    );

                    if(!success){
                        throw new MailSendException("Failed to send email to " + user.getEmail());
                    }
                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }
    @Override
    public Mono<UserResponseModel> getStudentById(String studentId) {
        return userRepository.findByUserId(studentId)
                .filter(user -> user instanceof Student)
                .cast(Student.class)
                .map(StudentResponseModel::from)
                .switchIfEmpty(Mono.error(new NotFoundException("Student not found with userId: " + studentId)));
    }


}
