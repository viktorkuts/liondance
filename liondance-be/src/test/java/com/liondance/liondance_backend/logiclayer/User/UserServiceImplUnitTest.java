package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.Event.EventStatus;
import com.liondance.liondance_backend.datalayer.Notification.NotificationType;
import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import com.liondance.liondance_backend.logiclayer.Event.EventService;
import com.liondance.liondance_backend.logiclayer.Event.EventServiceImpl;
import com.liondance.liondance_backend.logiclayer.Notification.NotificationService;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.presentationlayer.User.*;
import com.liondance.liondance_backend.presentationlayer.User.StudentResponseModel;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.MailSendException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private EventService eventService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserServiceImpl userService;
//    private EventServiceImpl eventService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = Student.builder()
                .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@null.local")
                .dob(LocalDate.parse("1995-01-01"))
                .roles(EnumSet.of(Role.STUDENT))
                .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                .registrationStatus(RegistrationStatus.ACTIVE)
                .build();

        student2 = Student.builder()
                .userId("7876ea26-3f76-4e50-870f-5e5dad6d63d1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@null.local")
                .dob(LocalDate.parse("2000-01-01"))
                .roles(EnumSet.of(Role.STUDENT))
                .joinDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                .registrationStatus(RegistrationStatus.PENDING)
                .build();
    }

    @Test
    void whenGetStudentsByRegistrationStatuses_thenReturnMatchingStudents() {
        List<RegistrationStatus> statuses = List.of(RegistrationStatus.PENDING);
        when(userRepository.findStudentsByRegistrationStatuses(statuses))
                .thenReturn(Flux.just(student2));
        Flux<UserResponseModel> result = userService.getStudentsByRegistrationStatuses(statuses);
        StepVerifier.create(result)
                .expectNext(UserResponseModel.from(student2))
                .verifyComplete();
        verify(userRepository, times(1))
                .findStudentsByRegistrationStatuses(statuses);
    }

    @Test
    void whenGetUserByUserId_thenReturnUser() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        when(userRepository.findUserByUserId(userId))
                .thenReturn(Mono.just(student1));
        Mono<UserResponseModel> result = userService.getUserByUserId(userId);
        StepVerifier.create(result)
                .expectNext(UserResponseModel.from(student1))
                .verifyComplete();
        verify(userRepository, times(1))
                .findUserByUserId(userId);
    }
    @Test
    void whenGetUserByUserId_thenThrowNotFoundException() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1a";
        when(userRepository.findUserByUserId(userId))
                .thenReturn(Mono.empty());
        Mono<UserResponseModel> result = userService.getUserByUserId(userId);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("User with userId: " + userId + " not found"))
                .verify();
        verify(userRepository, times(1))
                .findUserByUserId(userId);
    }

    @Test
    void whenGetPendingStudentById_thenReturnStudent() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        when(userRepository.findByUserId(userId))
                .thenReturn(Mono.just(student2));
        Mono<UserResponseModel> result = userService.getPendingStudentById(userId);
        StepVerifier.create(result)
                .expectNext(UserResponseModel.from(student2))
                .verifyComplete();
        verify(userRepository, times(1))
                .findByUserId(userId);
    }

    @Test
    void whenGetPendingStudentById_thenThrowNotFoundException() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1a";
        when(userRepository.findByUserId(userId))
                .thenReturn(Mono.empty());
        Mono<UserResponseModel> result = userService.getPendingStudentById(userId);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Pending student not found with userId: " + userId))
                .verify();
        verify(userRepository, times(1))
                .findByUserId(userId);
    }

      @Test
      void whenUpdateUser_thenReturnUpdatedUser() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        UserRequestModel updatedUser = new UserRequestModel();
        BeanUtils.copyProperties(student1, updatedUser);

        updatedUser.setFirstName("UpdatedName");

        when(userRepository.findUserByUserId(userId))
                .thenReturn(Mono.just(student1));
        when(userRepository.save(student1))
                .thenReturn(Mono.just(student1));

        Mono<UserResponseModel> result = userService.updateUser(userId, updatedUser);
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getFirstName().equals("UpdatedName"))
                .verifyComplete();
        verify(userRepository, times(1)).findUserByUserId(userId);
        verify(userRepository, times(1)).save(student1);
    }

    @Test
    void whenUpdateStudent_thenReturnUpdatedStudent() {
        String studentId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        StudentRequestModel updatedStudent = new StudentRequestModel();
        BeanUtils.copyProperties(student1, updatedStudent);

        updatedStudent.setFirstName("UpdatedName");
        updatedStudent.setLastName("UpdatedLastName");
        updatedStudent.setEmail("updated.email@null.local");
        updatedStudent.setPhone("123-456-7890");
        updatedStudent.setAddress(new Address("123 Updated St", "Updated City", "UC", "12345"));
        updatedStudent.setParentFirstName("UpdatedParentFirstName");
        updatedStudent.setParentLastName("UpdatedParentLastName");
        updatedStudent.setParentEmail("updated.parent@null.local");
        updatedStudent.setParentPhone("098-765-4321");

        when(userRepository.findByUserId(studentId))
                .thenReturn(Mono.just(student1));
        when(userRepository.save(Mockito.any(Student.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<StudentResponseModel> result = userService.updateStudent(studentId, updatedStudent).cast(StudentResponseModel.class);
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getFirstName().equals("UpdatedName") &&
                        user.getLastName().equals("UpdatedLastName") &&
                        user.getEmail().equals("updated.email@null.local") &&
                        user.getPhone().equals("123-456-7890") &&
                        user.getAddress().getStreetAddress().equals("123 Updated St") &&
                        user.getAddress().getCity().equals("Updated City") &&
                        user.getAddress().getState().equals("UC") &&
                        user.getAddress().getZip().equals("12345") &&
                        user.getParentFirstName().equals("UpdatedParentFirstName") &&
                        user.getParentLastName().equals("UpdatedParentLastName") &&
                        user.getParentEmail().equals("updated.parent@null.local") &&
                        user.getParentPhone().equals("098-765-4321"))
                .verifyComplete();
        verify(userRepository, times(1)).findByUserId(studentId);
        verify(userRepository, times(1)).save(Mockito.any(Student.class));
    }

    @Test
    void whenUpdateStudentWithNonExistantID_thenThrowNotFoundException() {
        String studentId = "non-existent-id";
        StudentRequestModel updatedStudent = new StudentRequestModel();
        BeanUtils.copyProperties(student1, updatedStudent);

        when(userRepository.findByUserId(studentId))
                .thenReturn(Mono.empty());

        Mono<StudentResponseModel> result = userService.updateStudent(studentId, updatedStudent).cast(StudentResponseModel.class);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Student not found with userId: " + studentId))
                .verify();
        verify(userRepository, times(1)).findByUserId(studentId);
        verify(userRepository, times(0)).save(Mockito.any(Student.class));
    }

    @Test
    void whenGetStudentById_thenReturnStudent() {
        String studentId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        when(userRepository.findByUserId(studentId))
                .thenReturn(Mono.just(student1));
        Mono<UserResponseModel> result = userService.getStudentById(studentId);
        StepVerifier.create(result)
                .expectNext(StudentResponseModel.from(student1))
                .verifyComplete();
        verify(userRepository, times(1))
                .findByUserId(studentId);
    }

    @Test
    void whenGetStudentById_thenThrowNotFoundException() {
        String studentId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1a";
        when(userRepository.findByUserId(studentId))
                .thenReturn(Mono.empty());
        Mono<UserResponseModel> result = userService.getStudentById(studentId);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Student not found with studentId: " + studentId))
                .verify();
        verify(userRepository, times(1))
                .findByUserId(studentId);
    }

    @Test
    void getClientDetails_ShouldReturnClientWithEvents() {
        String clientId = "c56a8e9d-4362-42c8-965d-2b8b98f9f4d9";
        User mockUser = new User();
        mockUser.setUserId(clientId);
        mockUser.setFirstName("Alice");
        mockUser.setMiddleName("Grace");
        mockUser.setLastName("Johnson");
        mockUser.setEmail("alice.johnson@webmail.com");
        mockUser.setPhone("234-567-8901");
        mockUser.setRoles(EnumSet.of(Role.CLIENT));

        EventResponseModel activeEvent = EventResponseModel.builder()
                .eventId("active-event-1")
                .eventStatus(EventStatus.CONFIRMED)
                .build();
        EventResponseModel pastEvent = EventResponseModel.builder()
                .eventId("past-event-1")
                .eventStatus(EventStatus.COMPLETED)
                .build();

        when(userRepository.findByUserId(clientId)).thenReturn(Mono.just(mockUser));

        when(eventService.getEventsByClientId(clientId))
                .thenReturn(Flux.just(activeEvent, pastEvent));

        StepVerifier.create(userService.getClientDetails(clientId))
                .assertNext(clientResponse -> {
                    assertEquals(clientId, clientResponse.getUserId());
                    assertEquals("Alice", clientResponse.getFirstName());
                    assertEquals("Grace", clientResponse.getMiddleName());
                    assertEquals("Johnson", clientResponse.getLastName());
                    assertEquals("alice.johnson@webmail.com", clientResponse.getEmail());
                    assertEquals("234-567-8901", clientResponse.getPhone());

                    ClientResponseModel clientResponseModel = (ClientResponseModel) clientResponse;
                    assertEquals(1, clientResponseModel.getActiveEvents().size());
                    assertEquals("active-event-1", clientResponseModel.getActiveEvents().get(0).getEventId());
                    assertEquals(1, clientResponseModel.getPastEvents().size());
                    assertEquals("past-event-1", clientResponseModel.getPastEvents().get(0).getEventId());
                })
                .verifyComplete();
        
        verify(userRepository, times(1)).findByUserId(clientId);
        verify(eventService, times(1)).getEventsByClientId(clientId); // Expect 1 call
    }

    @Test
    void whenSubscribeToPromotions_thenReturnUpdatedUser() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        Boolean isSubscribed = true;
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Jane");
        user.setEmail("jane.doe@null.local");
        user.setIsSubscribed(false);

        when(userRepository.findUserByUserId(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(notificationService.sendMail(
                eq("jane.doe@null.local"),
                eq("Lion Dance - Subscription Update"),
                anyString(),
                eq(NotificationType.SUBSCRIPTION)
        )).thenReturn(true);

        Mono<UserResponseModel> result = userService.subscribeToPromotions(userId, isSubscribed);
        StepVerifier.create(result)
                .expectNextMatches(updatedUser -> updatedUser.getIsSubscribed().equals(isSubscribed))
                .verifyComplete();
        verify(userRepository, times(1)).findUserByUserId(userId);
        verify(userRepository, times(1)).save(user);
        verify(notificationService, times(1)).sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Subscription Update"),
                anyString(),
                eq(NotificationType.SUBSCRIPTION)
        );
    }

    @Test
    void whenUnsubscribeFromPromotions_thenReturnUpdatedUser() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        Boolean isSubscribed = false;
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Jane");
        user.setEmail("jane.doe@null.local");
        user.setIsSubscribed(true);

        when(userRepository.findUserByUserId(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(notificationService.sendMail(
                eq("jane.doe@null.local"),
                eq("Lion Dance - Subscription Update"),
                anyString(),
                eq(NotificationType.SUBSCRIPTION)
        )).thenReturn(true);

        Mono<UserResponseModel> result = userService.subscribeToPromotions(userId, isSubscribed);
        StepVerifier.create(result)
                .expectNextMatches(updatedUser -> updatedUser.getIsSubscribed().equals(isSubscribed))
                .verifyComplete();
        verify(userRepository, times(1)).findUserByUserId(userId);
        verify(userRepository, times(1)).save(user);
        verify(notificationService, times(1)).sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Subscription Update"),
                anyString(),
                eq(NotificationType.SUBSCRIPTION)
        );
    }

    @Test
    void whenSubscribeToPromotionsWithNonExistentUser_thenThrowNotFoundException() {
        String userId = "non-existent-id";
        Boolean isSubscribed = true;

        when(userRepository.findUserByUserId(userId)).thenReturn(Mono.empty());

        Mono<UserResponseModel> result = userService.subscribeToPromotions(userId, isSubscribed);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("User with userId: " + userId + " not found"))
                .verify();
        verify(userRepository, times(1)).findUserByUserId(userId);
        verify(userRepository, times(0)).save(Mockito.any(User.class));
    }

    @Test
    void whenCheckForUpcomingPromotions_thenSendEmails() {
        LocalDate today = LocalDate.now();
        Promotion promotion = new Promotion();
        promotion.setStartDate(today.plusDays(7));
        promotion.setPromotionName("New Year Promotion");

        User user = new User();
        user.setUserId("7876ea26-3f76-4e50-870f-5e5dad6d63d1");
        user.setFirstName("Jane");
        user.setEmail("jane.doe@null.local");
        user.setIsSubscribed(true);

        when(promotionRepository.findAll()).thenReturn(Flux.just(promotion));
        when(userRepository.findUsersByIsSubscribed(true)).thenReturn(Flux.just(user));
        when(notificationService.sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Upcoming Promotion"),
                anyString(),
                eq(NotificationType.PROMOTION)
        )).thenReturn(true);

        userService.checkForUpcomingPromotions();

        verify(promotionRepository, times(1)).findAll();
        verify(userRepository, times(1)).findUsersByIsSubscribed(true);
        verify(notificationService, times(1)).sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Upcoming Promotion"),
                anyString(),
                eq(NotificationType.PROMOTION)
        );
    }

    @Test
    void whenSubscribeToPromotions_thenThrowMailSendException() {
        String userId = "7876ea26-3f76-4e50-870f-5e5dad6d63d1";
        Boolean isSubscribed = true;
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Jane");
        user.setEmail("jane.doe@null.local");
        user.setIsSubscribed(false);

        when(userRepository.findUserByUserId(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(notificationService.sendMail(
                eq("jane.doe@null.local"),
                eq("Lion Dance - Subscription Update"),
                anyString(),
                eq(NotificationType.SUBSCRIPTION)
        )).thenReturn(false);

        Mono<UserResponseModel> result = userService.subscribeToPromotions(userId, isSubscribed);
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof MailSendException &&
                        throwable.getMessage().equals("Failed to send subscription update email to " + user.getEmail()))
                .verify();
        verify(userRepository, times(1)).findUserByUserId(userId);
        verify(userRepository, times(1)).save(user);
        verify(notificationService, times(1)).sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Subscription Update"),
                anyString(),
                eq(NotificationType.SUBSCRIPTION)
        );
    }

    @Test
    void whenCheckForUpcomingPromotions_thenFilterPromotionsCorrectly() {
        LocalDate today = LocalDate.now();
        Promotion promotion1 = new Promotion();
        promotion1.setStartDate(today.plusDays(7));
        promotion1.setPromotionName("Promotion 1");

        Promotion promotion2 = new Promotion();
        promotion2.setStartDate(today.plusDays(3));
        promotion2.setPromotionName("Promotion 2");

        Promotion promotion3 = new Promotion();
        promotion3.setStartDate(today);
        promotion3.setPromotionName("Promotion 3");

        Promotion promotion4 = new Promotion();
        promotion4.setStartDate(today.plusDays(10));
        promotion4.setPromotionName("Promotion 4");

        User user = new User();
        user.setUserId("7876ea26-3f76-4e50-870f-5e5dad6d63d1");
        user.setFirstName("Jane");
        user.setEmail("jane.doe@null.local");
        user.setIsSubscribed(true);

        when(promotionRepository.findAll()).thenReturn(Flux.just(promotion1, promotion2, promotion3, promotion4));
        when(userRepository.findUsersByIsSubscribed(true)).thenReturn(Flux.just(user));
        when(notificationService.sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Upcoming Promotion"),
                anyString(),
                eq(NotificationType.PROMOTION)
        )).thenReturn(true);

        userService.checkForUpcomingPromotions();

        verify(promotionRepository, times(1)).findAll();
        verify(userRepository, times(3)).findUsersByIsSubscribed(true);
        verify(notificationService, times(3)).sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Upcoming Promotion"),
                anyString(),
                eq(NotificationType.PROMOTION)
        );
    }

    @Test
    void whenCheckForUpcomingPromotions_thenSendEmailsToSubscribedUsers() {
        LocalDate today = LocalDate.now();
        Promotion promotion = new Promotion();
        promotion.setStartDate(today.plusDays(7));
        promotion.setPromotionName("New Year Promotion");

        User user = new User();
        user.setUserId("7876ea26-3f76-4e50-870f-5e5dad6d63d1");
        user.setFirstName("Jane");
        user.setEmail("jane.doe@null.local");
        user.setIsSubscribed(true);

        when(promotionRepository.findAll()).thenReturn(Flux.just(promotion));
        when(userRepository.findUsersByIsSubscribed(true)).thenReturn(Flux.just(user));
        when(notificationService.sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Upcoming Promotion"),
                anyString(),
                eq(NotificationType.PROMOTION)
        )).thenReturn(true);

        userService.checkForUpcomingPromotions();

        verify(promotionRepository, times(1)).findAll();
        verify(userRepository, times(1)).findUsersByIsSubscribed(true);
        verify(notificationService, times(1)).sendMail(
                eq(user.getEmail()),
                eq("Lion Dance - Upcoming Promotion"),
                anyString(),
                eq(NotificationType.PROMOTION)
        );
    }
}
