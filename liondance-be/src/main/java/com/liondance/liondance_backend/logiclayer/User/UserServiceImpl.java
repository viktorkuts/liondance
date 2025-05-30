package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.logiclayer.Event.EventService;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.StudentResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.*;
import com.liondance.liondance_backend.utils.exceptions.EmailInUse;
import com.liondance.liondance_backend.presentationlayer.User.StudentRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.StudentResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.UserRequestModel;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.*;
import com.liondance.liondance_backend.utils.exceptions.EmailInUse;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import com.liondance.liondance_backend.utils.exceptions.StudentNotPending;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EventService eventService;
    private final PromotionRepository promotionRepository;

    public UserServiceImpl(UserRepository userRepository, NotificationService notificationService, @Lazy EventService eventService, PromotionRepository promotionRepository) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.eventService = eventService;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Flux<UserResponseModel> getAllUsers() {
        return userRepository.findAll().map(UserResponseModel::from);
    }

    @Override
    public Flux<UserResponseModel> getAllUsers(Role role) {
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
                            if (exists) {
                                return Mono.error(new EmailInUse("Email: " + request.getEmail() + " is already in use"));
                            } else {
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

                    if (!success) {
                        throw new MailSendException("Failed to send email to " + user.getEmail());
                    }

                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> updateStudentRegistrationStatus(String userId, RegistrationStatusPatchRequestModel registrationStatus) {
        String frontendUrl = System.getenv("FRONTEND_URL");
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            return Mono.error(new IllegalStateException("FRONTEND_URL environment variable is not set"));
        }
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
                        String accountLinkUrl = frontendUrl + "/link-account?userId=" + student.getUserId();

                        message = new StringBuilder()
                                .append("Congratulations, ")
                                .append(student.getFirstName())
                                .append("!")
                                .append("\nYour registration has been approved.")
                                .append("\n\nTo complete your registration and access Lion Dance, please link your Google account:")
                                .append("\n" + accountLinkUrl)
                                .append("\n\nOnce linked, you can sign in using your Google account.")
                                .append("\n\nThank you for joining Lion Dance!")
                                .toString();

                        userRepository.save(student).subscribe();
                    } else {
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

                    notificationService.sendMail(
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
    public Mono<UserResponseModel> addNewUser(String role, Mono<UserRequestModel> userRequestModel, JwtAuthenticationToken jwt) {
        return userRequestModel
                .map(UserRequestModel::from)
                .map(user -> {
                    user.setUserId(UUID.randomUUID().toString());
                    user.setRoles(EnumSet.of(Role.valueOf(role)));
                    if (jwt != null) {
                        user.setAssociatedId(jwt.getName());
                    }
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

                    if (!success) {
                        throw new MailSendException("Failed to send email to " + user.getEmail());
                    }
                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> updateUserRole(String userId, UserRolePatchRequestModel role) {
        if (role.getRoles() == null || role.getRoles().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Roles cannot be null or empty"));
        }
        return userRepository.findUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId: " + userId + " not found")))
                .map(user -> {
                    user.setRoles(EnumSet.copyOf(role.getRoles()));
                    return user;
                })
                .doOnNext(user -> {
                    String rolesFormatted = role.getRoles().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));

                    String message = new StringBuilder()
                            .append(user.getFirstName())
                            .append(", Your role has changed.")
                            .append("\nYour account now has the following roles: ")
                            .append(rolesFormatted)
                            .append("\n\nContact the Admin if this doesn't seem right.")
                            .toString();

                    Boolean success = notificationService.sendMail(
                            user.getEmail(),
                            "Lion Dance Account Role Changes",
                            message,
                            NotificationType.AUTHORIZATION
                    );

                    if (!success) {
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
                .switchIfEmpty(Mono.error(new NotFoundException("Student not found with studentId: " + studentId)));
    }

    @Override
    public Mono<UserResponseModel> updateStudent(String studentId, StudentRequestModel studentRequestModel) {
        return userRepository.findByUserId(studentId)
                .filter(user -> user instanceof Student)
                .switchIfEmpty(Mono.error(new NotFoundException("Student not found with userId: " + studentId)))
                .cast(Student.class)
                .map(student -> {
                    student.setFirstName(studentRequestModel.getFirstName());
                    student.setMiddleName(studentRequestModel.getMiddleName());
                    student.setLastName(studentRequestModel.getLastName());
                    student.setDob(studentRequestModel.getDob());
                    student.setEmail(studentRequestModel.getEmail());
                    student.setPhone(studentRequestModel.getPhone());
                    student.setAddress(studentRequestModel.getAddress());
                    student.setParentFirstName(studentRequestModel.getParentFirstName());
                    student.setParentMiddleName(studentRequestModel.getParentMiddleName());
                    student.setParentLastName(studentRequestModel.getParentLastName());
                    student.setParentEmail(studentRequestModel.getParentEmail());
                    student.setParentPhone(studentRequestModel.getParentPhone());
                    return student;
                })
                .flatMap(userRepository::save)
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> getClientDetails(String clientId) {
        return userRepository.findByUserId(clientId)
                .filter(user -> user.getRoles().contains(Role.CLIENT))
                .flatMap(user -> {
                    Flux<EventResponseModel> allEvents = eventService.getEventsByClientId(user.getUserId()).cache();

                    Mono<List<EventResponseModel>> activeEvents = allEvents
                            .filter(event -> event.getEventStatus() == EventStatus.CONFIRMED)
                            .collectList();

                    Mono<List<EventResponseModel>> pastEvents = allEvents
                            .filter(event -> event.getEventStatus() == EventStatus.COMPLETED)
                            .collectList();

                    return Mono.zip(Mono.just(user), activeEvents, pastEvents)
                            .map(tuple -> ClientResponseModel.builder()
                                    .userId(user.getUserId())
                                    .firstName(user.getFirstName())
                                    .middleName(user.getMiddleName())
                                    .lastName(user.getLastName())
                                    .email(user.getEmail())
                                    .phone(user.getPhone())
                                    .roles(user.getRoles())
                                    .activeEvents(tuple.getT2())
                                    .pastEvents(tuple.getT3())
                                    .build());
                });
    }

    @Override
    public Mono<User> validate(String subId) {
        return userRepository.findUserByAssociatedId(subId)
                .switchIfEmpty(Mono.error(new NotFoundException("Session user is not associated")));
    }

    @Override
    public Mono<UserResponseModel> linkUserAccount(String userId, JwtAuthenticationToken jwt) {
        if (jwt == null || jwt.getName() == null) {
            return Mono.error(new IllegalArgumentException("Authentication token is required"));
        }

        return userRepository.findUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId: " + userId + " not found")))
                .flatMap(user -> {
                    if (user.getAssociatedId() != null) {
                        return Mono.error(new IllegalStateException("Account is already linked"));
                    }
                    
                    return userRepository.findUserByAssociatedId(jwt.getName())
                            .hasElement()
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalStateException("Google account is already linked to another user"));
                                }
                                
                                user.setAssociatedId(jwt.getName());
                                return userRepository.save(user);
                            });
                     })
                .map(UserResponseModel::from);
    }

    @Override
    public Mono<UserResponseModel> subscribeToPromotions(String userId, Boolean isSubscribed) {
        return userRepository.findUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with userId: " + userId + " not found")))
                .map(user -> {
                    user.setIsSubscribed(isSubscribed);
                    return user;
                })
                .flatMap(userRepository::save)
                .doOnNext(user -> {
                    String message;
                    if (isSubscribed) {
                        message = new StringBuilder()
                                .append("Hello, ")
                                .append(user.getFirstName())
                                .append("!\n\n")
                                .append("You have successfully subscribed to promotion notifications.")
                                .append("\n\nThank you for choosing Lion Dance!")
                                .toString();
                    } else {
                        message = new StringBuilder()
                                .append("Hello, ")
                                .append(user.getFirstName())
                                .append("!\n\n")
                                .append("You have successfully unsubscribed from promotion notifications.")
                                .append("\n\nThank you for choosing Lion Dance!")
                                .toString();
                    }

                    Boolean success = notificationService.sendMail(
                            user.getEmail(),
                            "Lion Dance - Subscription Update",
                            message,
                            NotificationType.SUBSCRIPTION
                    );

                    if (!success) {
                        throw new MailSendException("Failed to send subscription update email to " + user.getEmail());
                    }
                })
                .map(UserResponseModel::from);
    }


    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
    public void checkForUpcomingPromotions() {
    LocalDate today = LocalDate.now();

    promotionRepository.findAll()
            .filter(promotion -> {
                LocalDate startDate = promotion.getStartDate();
                return startDate.minusDays(7).isEqual(today) || startDate.minusDays(3).isEqual(today) || startDate.isEqual(today);
            })
            .flatMap(promotion -> {
                LocalDate startDate = promotion.getStartDate();
                String message = new StringBuilder()
                        .append("Hello! ")
                        .append("!\n\n")
                        .append("We are excited to announce our upcoming promotion: ")
                        .append(promotion.getPromotionName())
                        .append(".\n")
                        .append("Don't miss out! It starts on ")
                        .append(startDate)
                        .append(".\n\nThank you for choosing Lion Dance!")
                        .toString();
                return userRepository.findUsersByIsSubscribed(true)
                        .flatMap(user -> notificationService.sendMail(
                                user.getEmail(),
                                "Lion Dance - Upcoming Promotion",
                                message,
                                NotificationType.PROMOTION
                        ) ? Mono.just(user) : Mono.error(new MailSendException("Failed to send email to " + user.getEmail())));
            })
            .subscribe();
    }
}
