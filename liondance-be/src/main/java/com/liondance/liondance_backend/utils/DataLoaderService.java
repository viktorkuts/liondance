package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedback;
import com.liondance.liondance_backend.datalayer.ClassFeedback.ClassFeedbackRepository;
import com.liondance.liondance_backend.datalayer.Course.Course;
import com.liondance.liondance_backend.datalayer.Course.CourseRepository;
import com.liondance.liondance_backend.datalayer.Event.*;
import com.liondance.liondance_backend.datalayer.Feedback.Feedback;
import com.liondance.liondance_backend.datalayer.Feedback.FeedbackRepository;
import com.liondance.liondance_backend.datalayer.Promotion.Promotion;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionRepository;
import com.liondance.liondance_backend.datalayer.Promotion.PromotionStatus;
import com.liondance.liondance_backend.datalayer.User.*;
import com.liondance.liondance_backend.datalayer.common.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Profile("!production")
@Service
public class DataLoaderService implements CommandLineRunner {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    ClassFeedbackRepository classFeedbackRepository;

    private void tearDown(){
        eventRepository.deleteAll().subscribe();
        userRepository.deleteAll().subscribe();
        courseRepository.deleteAll().subscribe();
        promotionRepository.deleteAll().subscribe();
        feedbackRepository.deleteAll().subscribe();
    }

    private Map<String, PerformerStatus> createPerformersMap(String... performerIds) {
        Map<String, PerformerStatus> performersMap = new HashMap<>();
        for (String performerId : performerIds) {
            performersMap.put(performerId, PerformerStatus.ACCEPTED);
        }
        return performersMap;
    }

    @Override
    public void run(String... args) throws Exception {
        ArrayList<User> students = new ArrayList<>();
        students.add(
                Student.builder()
                        .userId("eb07c6c6-dc48-489f-aa20-0d7d6fb12448")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Sarah")
                        .middleName("Jane")
                        .lastName("Smith")
                        .dob(LocalDate.parse("2000-01-01"))
//                      .email("Sarah.Smith@myfunnywebsite.org")
                        .email("jhondoesnthack@gmail.com")
                        .phone("123-456-7890")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STAFF))
                        // TODO: Remove this id after presentation
                        .associatedId("google-oauth2|111158373482141727647")
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b79b0c3c-2462-42a1-922d-1a20be857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Nikolaos")
                        .middleName("Georgios")
                        .lastName("Michaloliakos")
                        .dob(LocalDate.parse("2000-01-01"))
                        .email("nikolaos.michaloliakos@goldendawn.org")
                        .phone("123-456-7890")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STAFF))
                        .associatedId("google-oauth2|111871631735892967671")
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b69b0c3c-2462-42a1-832d-1a22ke857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Hawk")
                        .middleName("Doesounow")
                        .lastName("Tuah")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("hawktuah@onthatthang.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .associatedId("google-oauth2|106264117745944422000")
                        .roles(EnumSet.of(Role.STAFF))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b6m3lc3c-2462-42a1-832d-1a22ke857hba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Tony")
                        .middleName("")
                        .lastName("Nearos")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("denbleko@mebatsous.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Sepolia")
                                .build())
                        .associatedId("auth0|67a0600de5c3b505111da424")
                        .roles(EnumSet.of(Role.STAFF))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("c89b0c3c-2462-42a1-933d-1a20be857abc")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Zippora")
                        .middleName("Lemony")
                        .lastName("Snickett")
                        .dob(LocalDate.parse("1998-06-15"))
                        .email("zippora.snickett@stories.org")
                        .phone("438-999-1234")
                        .address(Address.builder()
                                .streetAddress("5678 Mystery Lane")
                                .zip("H2H 2H2")
                                .state("QC")
                                .city("Laval")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .associatedId("auth0|67a060b8afb46e45088b2b63")
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("c98b0c3c-2462-42a1-944d-1a20be857xyz")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Apollo")
                        .middleName("Dart")
                        .lastName("Lightning")
                        .dob(LocalDate.parse("1997-03-21"))
                        .email("apollo.lightning@skyhigh.org")
                        .phone("450-123-4567")
                        .address(Address.builder()
                                .streetAddress("42 Thunder Road")
                                .zip("H3H 3H3")
                                .state("QC")
                                .city("Brossard")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("d78b0c3c-2462-42a1-955d-1a20be857def")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Jazz")
                        .middleName("Beet")
                        .lastName("Nickelback")
                        .dob(LocalDate.parse("2001-11-11"))
                        .email("jazz.nickelback@tuneful.org")
                        .phone("514-777-8899")
                        .address(Address.builder()
                                .streetAddress("321 Harmony Ave")
                                .zip("H4H 4H4")
                                .state("QC")
                                .city("Longueuil")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("d88b0c3c-2462-42a1-966d-1a20be857ghi")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Pixel")
                        .middleName("Bit")
                        .lastName("Byte")
                        .dob(LocalDate.parse("2002-02-02"))
                        .email("pixel.byte@techwave.org")
                        .phone("514-567-8901")
                        .address(Address.builder()
                                .streetAddress("909 Data Drive")
                                .zip("H5H 5H5")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("e98b0c3c-2462-42a1-977d-1a20be857jkl")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Echo")
                        .middleName("Silence")
                        .lastName("Noiseworth")
                        .dob(LocalDate.parse("1996-12-12"))
                        .email("echo.noiseworth@soundscape.org")
                        .phone("438-888-9999")
                        .address(Address.builder()
                                .streetAddress("555 Quiet Street")
                                .zip("H6H 6H6")
                                .state("QC")
                                .city("Saint-Lambert")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("n01a2b3c-4567-89ab-1011-1c23de857zzz")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Kai")
                        .middleName("")
                        .lastName("Smith")
                        .dob(LocalDate.parse("1995-01-01"))
                        .email("kai.smith@ninjago.org")
                        .phone("514-123-1234")
                        .address(Address.builder()
                                .streetAddress("777 Fire Lane")
                                .zip("H7H 7H7")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("n02a2b3c-4567-89ab-1011-1c23de857yyy")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Jay")
                        .middleName("")
                        .lastName("Walker")
                        .dob(LocalDate.parse("1996-02-02"))
                        .email("jay.walker@ninjago.org")
                        .phone("438-234-2345")
                        .address(Address.builder()
                                .streetAddress("888 Lightning Blvd")
                                .zip("H8H 8H8")
                                .state("QC")
                                .city("Laval")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("n03a2b3c-4567-89ab-1011-1c23de857xxx")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Zane")
                        .middleName("")
                        .lastName("Julien")
                        .dob(LocalDate.parse("1997-03-03"))
                        .email("zane.julien@ninjago.org")
                        .phone("450-345-3456")
                        .address(Address.builder()
                                .streetAddress("999 Ice St")
                                .zip("H9H 9H9")
                                .state("QC")
                                .city("Brossard")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("n04a2b3c-4567-89ab-1011-1c23de857www")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Cole")
                        .middleName("")
                        .lastName("Brookstone")
                        .dob(LocalDate.parse("1998-04-04"))
                        .email("cole.brookstone@ninjago.org")
                        .phone("514-456-4567")
                        .address(Address.builder()
                                .streetAddress("1000 Earth Ave")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Longueuil")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("n05a2b3c-4567-89ab-1011-1c23de857vvv")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Lloyd")
                        .middleName("")
                        .lastName("Garmadon")
                        .dob(LocalDate.parse("1999-05-05"))
                        .email("lloyd.garmadon@ninjago.org")
                        .phone("438-567-5678")
                        .address(Address.builder()
                                .streetAddress("2000 Green St")
                                .zip("H2H 2H2")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );

        students.add(
                Student.builder()
                        .userId("b6910jfc-2462-42a1-832d-1a22ke82k5ba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Donald")
                        .middleName("J.")
                        .lastName("Trump")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("donald@trump.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b69b0c3c-2462-42a1-832d-1a29ke857cbf")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Lilliam")
                        .middleName("")
                        .lastName("Pumpernickel")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("guccigang@guccigang.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b6k3mg3c-2462-42a1-832d-1a00ke857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Tim")
                        .middleName("")
                        .lastName("Robinson")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("tim@robinson.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("bl39kc3c-2462-42a1-832d-2039ke857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Dave")
                        .middleName("Fat")
                        .lastName("Blunts")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("icantput@downthecup.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b6992j4c-2462-42a1-832d-1a22ke857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("NBA")
                        .middleName("12 kids")
                        .lastName("Youngboy")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("youngboy@neverbrokeagain.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b69bj25c-2462-42a1-832d-1a22ke857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Michael")
                        .middleName("Gary")
                        .lastName("Scott")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("mike@scott.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b69b0c3c-1937-42a1-832d-1m2k9e857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Knee")
                        .middleName("Doesounow")
                        .lastName("Surgeon")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("hawktuah@onthatthang.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("b01j4c3c-2462-42a1-832d-1a22ke857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("John")
                        .middleName("Doesounow")
                        .lastName("Pork")
                        .dob(LocalDate.parse("1999-01-01"))
                        .email("john@pork.org")
                        .phone("514-637-8400")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );

        students.add(
                Student.builder()
                        .userId("a79b0c3c-2462-42a1-922d-1a20be857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("John")
                        .middleName("Doe")
                        .lastName("Smith")
                        .dob(LocalDate.parse("2000-01-01"))
                        .email("john.doe@myfunnywebsite.org")
                        .phone("123-456-7890")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("c56a8e9d-4362-42c8-965d-2b8b98f9f4d9")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Alice")
                        .middleName("Grace")
                        .lastName("Johnson")
                        .dob(LocalDate.parse("1998-02-15"))
                        .email("alice.johnson@webmail.com")
                        .phone("234-567-8901")
                        .address(Address.builder()
                                .streetAddress("5678 Oak Ave")
                                .zip("H2H 2H2")
                                .state("ON")
                                .city("Toronto")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .associatedId("auth0|67a441ddb02082c6c00f7718")
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("d89f1a3e-01a5-4f97-b2a3-9927555e4951")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Bob")
                        .middleName("Ray")
                        .lastName("Lee")
                        .dob(LocalDate.parse("2001-04-22"))
                        .email("jhondoesnthack@gmail.com")
                        .phone("345-678-9012")
                        .address(Address.builder()
                                .streetAddress("9101 Birch Rd")
                                .zip("J3J 3J3")
                                .state("BC")
                                .city("Vancouver")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(true)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("e72c3e0d-3351-4288-9a52-5b2d4f5f6e73")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Maria")
                        .middleName("Sophia")
                        .lastName("Garcia")
                        .dob(LocalDate.parse("1999-06-10"))
                        .email("maria.garcia@xyz.org")
                        .phone("456-789-0123")
                        .address(Address.builder()
                                .streetAddress("1212 Maple St")
                                .zip("R4R 4R4")
                                .state("AB")
                                .city("Calgary")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("f6ac8fcb-43d1-4958-88ea-040cd379f3a1")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Charlie")
                        .middleName("Alan")
                        .lastName("Brown")
                        .dob(LocalDate.parse("1997-08-25"))
                        .email("charlie.brown@fakeemail.com")
                        .phone("567-890-1234")
                        .address(Address.builder()
                                .streetAddress("3456 Elm St")
                                .zip("K5K 5K5")
                                .state("NS")
                                .city("Halifax")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("g98d9b28-b00e-4d8f-a9f9-7a06368f759f")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Eve")
                        .middleName("Marie")
                        .lastName("Davis")
                        .dob(LocalDate.parse("1996-03-14"))
                        .email("eve.davis@newdomain.org")
                        .phone("678-901-2345")
                        .address(Address.builder()
                                .streetAddress("7876 Pine Ln")
                                .zip("T6T 6T6")
                                .state("MB")
                                .city("Winnipeg")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("h27d6b6c-9069-4a0f-84f3-b259ae05e98d")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("David")
                        .middleName("James")
                        .lastName("Miller")
                        .dob(LocalDate.parse("1995-05-05"))
                        .email("david.miller@workplace.com")
                        .phone("789-012-3456")
                        .address(Address.builder()
                                .streetAddress("1122 Birchwood Dr")
                                .zip("L7L 7L7")
                                .state("SK")
                                .city("Regina")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("i17e1c98-a276-4b2f-a85c-34ed120d1bc4")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Grace")
                        .middleName("Lynn")
                        .lastName("Wilson")
                        .dob(LocalDate.parse("1998-09-30"))
                        .email("grace.wilson@university.com")
                        .phone("890-123-4567")
                        .address(Address.builder()
                                .streetAddress("2205 Redwood Blvd")
                                .zip("A9A 9A9")
                                .state("QC")
                                .city("Quebec City")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("j83e2a9c-332f-471b-a0a7-39b2c8d6b299")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("Jake")
                        .middleName("Kyle")
                        .lastName("Moore")
                        .dob(LocalDate.parse("2000-11-11"))
                        .email("jake.moore@cooldomain.com")
                        .phone("901-234-5678")
                        .address(Address.builder()
                                .streetAddress("4599 Cedar Ct")
                                .zip("V8V 8V8")
                                .state("PE")
                                .city("Charlottetown")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .isSubscribed(false)
                        .build()
        );
        students.add(
                Student.builder()
                        .userId("k14f2e01-d60e-4204-9492-f8391e0d3387")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.ACTIVE)
                        .firstName("Sophia")
                        .middleName("Rachel")
                        .lastName("Taylor")
                        .dob(LocalDate.parse("1999-12-20"))
//                        .email("samprasad7220@gmail.com")
                        .email("jhondoesnthack@gmail.com")
                        .phone("102-345-6789")
                        .address(Address.builder()
                                .streetAddress("6789 Willow Way")
                                .zip("W3W 3W3")
                                .state("NL")
                                .city("St. John's")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
                        .isSubscribed(true)
                        .build()
        );
ArrayList<Promotion> promotions = new ArrayList<>();
        promotions.add(
                Promotion.builder()
                        .promotionId("e876774d-ca7d-40cd-b828-cc007bff3b82")
                        .promotionName("Summer Sale")
                        .startDate(LocalDate.of(2025, 6, 1))
                        .endDate(LocalDate.of(2025, 8, 31))
                        .discountRate(0.20)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("c7ce931c-0455-43e5-8868-706a406cfb57")
                        .promotionName("Back to School Sale")
                        .startDate(LocalDate.of(2025, 9, 1))
                        .endDate(LocalDate.of(2025, 9, 30))
                        .discountRate(0.15)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("df428a05-9174-4189-aff8-d0d60bd5a530")
                        .promotionName("Black Friday Sale")
                        .startDate(LocalDate.of(2025, 11, 1))
                        .endDate(LocalDate.of(2025, 11, 30))
                        .discountRate(0.25)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("9e365149-bf68-497c-babe-e55cc6ac289c")
                        .promotionName("Cyber Monday Sale")
                        .startDate(LocalDate.of(2025, 12, 1))
                        .endDate(LocalDate.of(2025, 12, 31))
                        .discountRate(0.30)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("ea51f582-9508-425f-934d-984fcc3efe3f")
                        .promotionName("Christmas Sale")
                        .startDate(LocalDate.of(2025, 12, 1))
                        .endDate(LocalDate.of(2025, 12, 31))
                        .discountRate(0.35)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("481b4392-2aa9-4cf9-ae60-d538df0222d8")
                        .promotionName("New Year Sale")
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2026, 1, 31))
                        .discountRate(0.20)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("9d96f753-f67d-4998-9dcc-fffb2e1fe64a")
                        .promotionName("Valentine's Day Sale")
                        .startDate(LocalDate.of(2026, 2, 1))
                        .endDate(LocalDate.of(2026, 2, 28))
                        .discountRate(0.15)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("d67ac8de-5465-4782-8c77-e184b2b99658")
                        .promotionName("Spring Sale")
                        .startDate(LocalDate.of(2026, 3, 1))
                        .endDate(LocalDate.of(2026, 5, 31))
                        .discountRate(0.20)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("1bd8d981-5b33-4a37-8cf1-7686687c7c13")
                        .promotionName("Easter Sale")
                        .startDate(LocalDate.of(2026, 4, 1))
                        .endDate(LocalDate.of(2026, 4, 30))
                        .discountRate(0.25)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("1b270296-10ab-41a3-b0b5-5c494507a4ee")
                        .promotionName("Mother's Day Sale")
                        .startDate(LocalDate.of(2026, 5, 1))
                        .endDate(LocalDate.of(2026, 5, 31))
                        .discountRate(0.15)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("6dd48251-fbae-459f-929b-694cf16a393f")
                        .promotionName("Father's Day Sale")
                        .startDate(LocalDate.of(2026, 6, 1))
                        .endDate(LocalDate.of(2026, 6, 30))
                        .discountRate(0.15)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );
        promotions.add(
                Promotion.builder()
                        .promotionId("test-promotion-7days")
                        .promotionName("Test Promotion 7 Days")
                        .startDate(LocalDateTime.now().plusDays(7).plusSeconds(10).toLocalDate())
                        .endDate(LocalDate.now().plusDays(14))
                        .discountRate(0.10)
                        .promotionStatus(PromotionStatus.INACTIVE)
                        .build()
        );

        ArrayList<Feedback> feedbacks = new ArrayList<>();
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("ebd15669-6912-435e-8481-9b9a5fdfdf19")
                        .timestamp(Instant.now())
                        .feedback("Great service!")
                        .rating(5)
                        .eventId("0c384884-8982-45f3-b631-37badc516b7d")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("09078adf-15b7-454d-acce-dc8b9d824430")
                        .timestamp(Instant.now())
                        .feedback("YAAAAAAAAAAAAY!")
                        .rating(5)
                        .eventId("3285f5a9-b643-406e-83c9-890271e78bce")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("27875e99-fa7a-48b6-846a-de697193631b")
                        .timestamp(Instant.now())
                        .feedback("Good performance!")
                        .rating(5)
                        .eventId("3285f5a9-b643-406e-83c9-890271e78bva")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .id("82f6ddc3-a103-42d5-84e4-09af3d48a103")
                        .timestamp(Instant.now())
                        .feedback("Big fan of the team and the performance!")
                        .rating(5)
                        .eventId("3285f5a9-b123-406e-83c9-890271e78bce")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("868dc46e-5f9f-48a9-a5aa-cb295621eaaf")
                        .timestamp(Instant.now())
                        .feedback("Yessir good job!")
                        .rating(5)
                        .eventId("3285f5a9-b643-406e-83c9-732481e78bce")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("b9d30a85-630d-4d00-9953-725656b8c08b")
                        .timestamp(Instant.now())
                        .feedback("Might as well book another event. I liked it!")
                        .rating(5)
                        .eventId("8547f5a9-b643-406e-83c9-890271e78bce")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("a60284e7-ea25-4a52-b62f-f6d57030c9a5")
                        .timestamp(Instant.now())
                        .feedback("Better than eating a burger!")
                        .rating(5)
                        .eventId("75a74804-af57-49ce-a8c7-7211d7d0f9ee")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("13043fa6-ca57-4ce7-b9c7-8b8e807a601b")
                        .timestamp(Instant.now())
                        .feedback("GREAAAAAAAAAAAAT!")
                        .rating(5)
                        .eventId("e3b07c6c6-dc48-489f-aa20-0d7d6fb12450")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("4ff89851-238b-4211-a3f9-0a203dbc76f5")
                        .timestamp(Instant.now())
                        .feedback("Will contact the team again. They are good!")
                        .rating(5)
                        .eventId("e4b07c6c6-dc48-489f-aa20-0d7d6fb12451")
                        .build()
        );
        feedbacks.add(
                Feedback.builder()
                        .feedbackId("e4e02950-20ca-4010-87cb-8c20e3d3354b")
                        .timestamp(Instant.now())
                        .feedback("Bonne performance!")
                        .rating(5)
                        .eventId("e5b07c6c6-dc48-489f-aa20-0d7d6fb12874")
                        .build()
        );

        List<String> userIds = new ArrayList<>();
        userIds.add("a79b0c3c-2462-42a1-922d-1a20be857cba");
        Course course = Course.builder()
                .courseId("40374c92-6c55-417e-b8bc-9dfb38740255")
                .name("Martial Arts")
                .userIds(userIds)
                .startTime(Instant.parse("2025-12-25T10:00:00Z"))
                .endTime(Instant.parse("2025-12-25T12:00:00Z"))
                .dayOfWeek(DayOfWeek.SUNDAY)
                .instructorId("eb07c6c6-dc48-489f-aa20-0d7d6fb12448")
                .cancelledDates(List.of(Instant.parse("2025-12-25T00:00:00Z"), Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-02-14T00:00:00Z")))
                .build();
//        Course coursetest = Course.builder()
//                .courseId("40374c92-6c55-417e-b8bc-9dfb38740255")
//                .name("Martial Arts")
//                .userIds(userIds)
//                .startTime(Instant.now())
//                .endTime(Instant.now().plus(Duration.ofMinutes(5)))
//                .dayOfWeek(LocalDate.now().getDayOfWeek())
//                .instructorId("eb07c6c6-dc48-489f-aa20-0d7d6fb12448")
//                .cancelledDates(new ArrayList<>())
//                .build();

        ArrayList<Event> events = new ArrayList<>();
        events.add(
                Event.builder()
                        .eventId("0c384884-8982-45f3-b631-37badc516b7d")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.OTHER)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("I just got out of jail")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(0).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("3285f5a9-b643-406e-83c9-890271e78bce")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.WEDDING)
                        .paymentMethod(PaymentMethod.CREDIT)
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(2).getUserId(),
                                students.get(3).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(0).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("3285f5a9-b643-406e-83c9-890271e78bva")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.BIRTHDAY)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("Aint no party like a Diddy party")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(2).getUserId(),
                                students.get(3).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(0).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("3285f5a9-b123-406e-83c9-890271e78bce")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Chiraq")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.FESTIVAL)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("bring lean")
                        .eventStatus(EventStatus.CANCELLED)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(1).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("3285f5a9-b643-406e-83c9-732481e78bce")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.WEDDING)
                        .paymentMethod(PaymentMethod.CASH)
                        .eventStatus(EventStatus.CONFIRMED)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(1).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("8547f5a9-b643-406e-83c9-890271e78bce")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.BIRTHDAY)
                        .paymentMethod(PaymentMethod.CASH)
                        .eventStatus(EventStatus.CONFIRMED)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(1).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("75a74804-af57-49ce-a8c7-7211d7d0f9ee")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.OTHER)
                        .paymentMethod(PaymentMethod.CASH)
                        .eventStatus(EventStatus.CANCELLED)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(1).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(2).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("75a74804-af57-49ce-a8c7-7211d7d0f9dd")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.OTHER)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("No special requests")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(2).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("75a74804-af33-12ce-a8c7-7211d7d0f9ee")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.FESTIVAL)
                        .paymentMethod(PaymentMethod.CREDIT)
                        .specialRequest("No special requests")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                        students.get(0).getUserId(),
                        students.get(1).getUserId(),
                        students.get(2).getUserId(),
                        students.get(3).getUserId(),
                        students.get(4).getUserId(),
                        students.get(5).getUserId(),
                        students.get(6).getUserId(),
                        students.get(7).getUserId(),
                        students.get(8).getUserId(),
                        students.get(9).getUserId()
                ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(2).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("75a74804-af57-49ce-a8c7-1265d7d0f9ee")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.OTHER)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("i have chair")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(3).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("e1b07c6c6-dc48-489f-aa20-0d7d6fb12448")
                        .venue(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.BIRTHDAY)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("No special requests")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(3).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("e1b07c6c6-dc48-489f-aa20-0d7d6fb12348")
                        .venue(Address.builder()
                                .streetAddress("42 Thunder Road")
                                .zip("H3H 3H3")
                                .state("QC")
                                .city("Brossard")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.BIRTHDAY)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("No special requests")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(3).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("e2b07c6c6-dc48-489f-aa20-0d7d6fb12449")
                        .venue(Address.builder()
                                .streetAddress("5678 Elm St")
                                .zip("H2H 2H2")
                                .state("QC")
                                .city("Laval")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.WEDDING)
                        .paymentMethod(PaymentMethod.CREDIT)
                        .specialRequest("Vegetarian meal")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(4).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("e3b07c6c6-dc48-489f-aa20-0d7d6fb12450")
                        .venue(Address.builder()
                                .streetAddress("9101 Birch Rd")
                                .zip("H3H 3H3")
                                .state("QC")
                                .city("Brossard")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z"))
                        .eventType(EventType.FESTIVAL)
                        .paymentMethod(PaymentMethod.PAYPAL)
                        .specialRequest("Projector needed")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(4).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("e4b07c6c6-dc48-489f-aa20-0d7d6fb12451")
                        .venue(Address.builder()
                                .streetAddress("1122 Birchwood Dr")
                                .zip("H4H 4H4")
                                .state("QC")
                                .city("Longueuil")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z").plus(3, ChronoUnit.DAYS))
                        .eventType(EventType.FESTIVAL)
                        .paymentMethod(PaymentMethod.CASH)
                        .specialRequest("Whiteboard needed")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PUBLIC)
                        .performers(createPerformersMap(
                                students.get(0).getUserId(),
                                students.get(1).getUserId(),
                                students.get(2).getUserId(),
                                students.get(3).getUserId(),
                                students.get(4).getUserId(),
                                students.get(5).getUserId(),
                                students.get(6).getUserId(),
                                students.get(7).getUserId(),
                                students.get(8).getUserId(),
                                students.get(9).getUserId()
                        ))
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(4).getUserId())
                        .build()
        );
        events.add(
                Event.builder()
                        .eventId("e5b07c6c6-dc48-489f-aa20-0d7d6fb12874")
                        .venue(Address.builder()
                                .streetAddress("2205 Redwood Blvd")
                                .zip("H5H 5H5")
                                .state("QC")
                                .city("Quebec City")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z").plus(4, ChronoUnit.DAYS))
                        .eventType(EventType.WEDDING)
                        .paymentMethod(PaymentMethod.CREDIT)
                        .specialRequest("Handouts needed")
                        .eventStatus(EventStatus.PENDING)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .clientId(students.stream().filter(user -> user.getRoles().contains(Role.CLIENT)).toList().get(4).getUserId())
                        .build()
        );
        
        events.add(
                Event.builder()
                        .eventId("e5b07c6c6-dc48-489f-aa20-0d7d6fb12899")
                        .venue(Address.builder()
                                .streetAddress("2205 Redwood Blvd")
                                .zip("H5H 5H5")
                                .state("QC")
                                .city("Quebec City")
                                .build())
                        .eventDateTime(Instant.parse("2029-01-01T00:00:00Z").plus(4, ChronoUnit.DAYS))
                        .eventType(EventType.WEDDING)
                        .paymentMethod(PaymentMethod.CREDIT)
                        .specialRequest("Handouts needed")
                        .eventStatus(EventStatus.COMPLETED)
                        .eventPrivacy(EventPrivacy.PRIVATE)
                        .clientId("c56a8e9d-4362-42c8-965d-2b8b98f9f4d9")
                        .build()
        );
        
        ArrayList<ClassFeedback> classFeedbacks = new ArrayList<>();
        classFeedbacks.add(
                ClassFeedback.builder()
                        .feedbackId("fe0ec8c1-15eb-486e-bb4d-987a4607e18a")
                        .classDate(LocalDate.now())
                        .score(4.5)
                        .comment("Awesome class, it was so fun")
                        .build()
        );
        classFeedbacks.add(
                ClassFeedback.builder()
                        .feedbackId("9fc7c678-60d0-4519-897f-5b40bc2fa86d")
                        .classDate(LocalDate.now())
                        .score(4.0)
                        .comment("I liked it")
                        .build()
        );

        tearDown();

        eventRepository.insert(events).subscribe();
        userRepository.insert(students).subscribe();
        courseRepository.insert(course).subscribe();
//        courseRepository.insert(coursetest).subscribe();
        promotionRepository.insert(promotions).subscribe();
        feedbackRepository.insert(feedbacks).subscribe();
        classFeedbackRepository.insert(classFeedbacks).subscribe();
    }
}
