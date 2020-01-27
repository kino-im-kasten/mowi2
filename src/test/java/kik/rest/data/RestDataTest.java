package kik.rest.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.format.DateTimeFormatter;

import kik.event.data.event.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RestDataTest {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    @Autowired
    RestDataTest(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Test
    void uRestEventCreateP1() {
        Event event = null;
        try {
            event = this.eventRepository.findAll().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            fail("there should have been at least one element in the repository but there was none");
        }

        RestEvent restEvent = null;
        if (event instanceof MovieEvent) {
            restEvent = new RestMovieEvent(event);
            assertEquals(((MovieEvent) event).getBooking().getMovie(), ((RestMovieEvent) restEvent).getMovie());
        } else {
            restEvent = new RestEvent(event);
        }

        assertEquals(event.getId(), restEvent.getId());
        assertEquals(event.getDate(), restEvent.getDate());
        assertEquals(event.getEventPlanningStatus(), restEvent.getEventPlanningStatus());
        assertEquals(event.getEventPublicity(), restEvent.getEventPublicity());
        assertEquals(event.getStart().format(DateTimeFormatter.ISO_OFFSET_TIME), restEvent.getStart());
    }

    @Test
    void uRestUserCreateP1() {
        User user = null;
        try {
            user = this.userRepository.findAll().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            fail("there should have been at least one element in the repository but there was none");
        }

        RestUser restUser = new RestUser(user);

        assertEquals(user.getName(), restUser.getName());
        assertEquals(user.getUserType().getName(), restUser.getType());
        assertEquals(user.getUuid(), restUser.getUuid());
    }

    @Test
    void uRestSpecialEventCreateP1() {
        SpecialEvent specialEvent = null;

        for (Event event : this.eventRepository.findAll().toList()) {
            if (event instanceof SpecialEvent) {
                specialEvent = (SpecialEvent) event;
            }
        }

        assertTrue(specialEvent != null);
        RestSpecialEvent restSpecialEvent = new RestSpecialEvent(specialEvent);
        assertEquals(specialEvent.getDescription(), restSpecialEvent.getDescription());
    }

}