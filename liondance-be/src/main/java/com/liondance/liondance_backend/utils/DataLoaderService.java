package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import com.liondance.liondance_backend.datalayer.User.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Date;

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
                        .joinDate(new Date())
                        .registrationStatus(RegistrationStatus.PENDING)
                        .firstName("John")
                        .middleName("Doe")
                        .lastName("Smith")
                        .gender(Gender.MALE)
                        .dob(new Date("01/01/2000"))
                        .email("john.doe@myfunnywebsite.org")
                        .phone("1234567890")
                        .address(Address.builder()
                                .streetAddress("1234 Main St")
                                .zip("H1H 1H1")
                                .state("QC")
                                .city("Montreal")
                                .build())
                        .build()
        );

        eventRepository.insert(events).subscribe();
        userRepository.insert(students).subscribe();
    }
}
