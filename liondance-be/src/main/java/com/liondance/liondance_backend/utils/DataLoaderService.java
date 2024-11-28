package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.datalayer.Event.Event;
import com.liondance.liondance_backend.datalayer.Event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DataLoaderService implements CommandLineRunner {

    @Autowired
    EventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception {
        ArrayList<Event> events = new ArrayList<>();
        events.add(Event.builder().name("Event 1").build());
        events.add(Event.builder().name("Event 2").build());
        events.add(Event.builder().name("Event 3").build());
        events.add(Event.builder().name("Event 4").build());
        events.add(Event.builder().name("Event 5").build());

        eventRepository.insert(events).subscribe();
    }
}
