package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;

@Service
public class DataLoaderService implements CommandLineRunner {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        ArrayList<Event> events = new ArrayList<>();
        events.add(Event.builder().name("Event 1").build());
        events.add(Event.builder().name("Event 2").build());
        events.add(Event.builder().name("Event 3").build());
        events.add(Event.builder().name("Event 4").build());
        events.add(Event.builder().name("Event 5").build());

        ArrayList<User> students = new ArrayList<>();
        students.add(
                Student.builder()
                        .userId("b79b0c3c-2462-42a1-922d-1a20be857cba")
                        .joinDate(Instant.now())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("John")
                        .middleName("Doe")
                        .lastName("Smith")
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("2000-01-01"))
                        .email("john.doe@myfunnywebsite.org")
                        .phone("1234567890")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
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
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("2000-01-01"))
                        .email("john.doe@myfunnywebsite.org")
                        .phone("1234567890")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .roles(EnumSet.of(Role.STUDENT))
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
                        .gender(Gender.FEMALE)
                        .dob(LocalDate.parse("1998-02-15"))
                        .email("alice.johnson@webmail.com")
                        .phone("2345678901")
                        .address(Address.builder()
                                .streetAddress("5678 Oak Ave")
                                .zip("H2H 2H2")
                                .state("ON")
                                .city("Toronto")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("2001-04-22"))
                        .email("bob.lee@someplace.com")
                        .phone("3456789012")
                        .address(Address.builder()
                                .streetAddress("9101 Birch Rd")
                                .zip("J3J 3J3")
                                .state("BC")
                                .city("Vancouver")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.FEMALE)
                        .dob(LocalDate.parse("1999-06-10"))
                        .email("maria.garcia@xyz.org")
                        .phone("4567890123")
                        .address(Address.builder()
                                .streetAddress("1212 Maple St")
                                .zip("R4R 4R4")
                                .state("AB")
                                .city("Calgary")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("1997-08-25"))
                        .email("charlie.brown@fakeemail.com")
                        .phone("5678901234")
                        .address(Address.builder()
                                .streetAddress("3456 Elm St")
                                .zip("K5K 5K5")
                                .state("NS")
                                .city("Halifax")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.FEMALE)
                        .dob(LocalDate.parse("1996-03-14"))
                        .email("eve.davis@newdomain.org")
                        .phone("6789012345")
                        .address(Address.builder()
                                .streetAddress("7876 Pine Ln")
                                .zip("T6T 6T6")
                                .state("MB")
                                .city("Winnipeg")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("1995-05-05"))
                        .email("david.miller@workplace.com")
                        .phone("7890123456")
                        .address(Address.builder()
                                .streetAddress("1122 Birchwood Dr")
                                .zip("L7L 7L7")
                                .state("SK")
                                .city("Regina")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.FEMALE)
                        .dob(LocalDate.parse("1998-09-30"))
                        .email("grace.wilson@university.com")
                        .phone("8901234567")
                        .address(Address.builder()
                                .streetAddress("2205 Redwood Blvd")
                                .zip("A9A 9A9")
                                .state("QC")
                                .city("Quebec City")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.MALE)
                        .dob(LocalDate.parse("2000-11-11"))
                        .email("jake.moore@cooldomain.com")
                        .phone("9012345678")
                        .address(Address.builder()
                                .streetAddress("4599 Cedar Ct")
                                .zip("V8V 8V8")
                                .state("PE")
                                .city("Charlottetown")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
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
                        .gender(Gender.FEMALE)
                        .dob(LocalDate.parse("1999-12-20"))
                        .email("sophia.taylor@somemail.com")
                        .phone("0123456789")
                        .address(Address.builder()
                                .streetAddress("6789 Willow Way")
                                .zip("W3W 3W3")
                                .state("NL")
                                .city("St. John's")
                                .build())
                        .roles(EnumSet.of(Role.CLIENT))
                        .build()
        );

        eventRepository.insert(events).subscribe();
        userRepository.insert(students).subscribe();
    }
}
