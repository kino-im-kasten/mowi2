package kik.rest.management;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import kik.event.data.event.EventRepository;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.user.data.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import kik.rest.controller.RestController;
import kik.rest.data.RestDutyPlan;
import kik.rest.data.RestEvent;
import kik.rest.data.RestMovieEvent;
import kik.rest.data.RestMyEvents;
import kik.rest.data.RestSpecialEvent;

/**
 * {@link RestManagement} Manages the incomming data from the
 * {@link RestController}
 *
 * @author Felix Rülke
 * @version 0.0.3
 */
@Service
public class RestManagement {

    EventRepository eventRepository;
    MovieEventManagement movieEventManagement;
    DutyPlanManagement dutyPlanManagement;
    UserRepository userRepository;

    /**
     * Default constructor of an {@link RestManagement}
     * 
     * @param eventRepository      A repository containing all {@link Event}'s
     * @param dutyPlanManagement   The {@link DutyPlanManagement}
     * @param movieEventManagement The {@link MovieEventManagement}
     * @param userRepository       The {@link UserRepository}
     */
    RestManagement(EventRepository eventRepository, DutyPlanManagement dutyPlanManagement,
            MovieEventManagement movieEventManagement, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.dutyPlanManagement = dutyPlanManagement;
        this.movieEventManagement = movieEventManagement;
        this.userRepository = userRepository;

    }

    /**
     * Returns an event for a given id
     * 
     * @param id The {@link Event} id
     * @return The {@link RestEvent}
     */
    public RestEvent getEvent(long id) {
        Optional<Event> o = this.eventRepository.findById(id);
        if (!o.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }

        Event event = o.get();

        if (event instanceof MovieEvent) {
            return new RestMovieEvent(event);
        }

        return new RestEvent(event);
    }

    /**
     * Returns all event's put into {@link RestEvent}'s
     * 
     * @return The list of all {@link RestEvent}'s
     */
    public List<RestEvent> getEventList() {
        List<RestEvent> events = new ArrayList<>();

        for (Event event : this.getSortedEvents()) {
            if (event instanceof MovieEvent) {
                events.add(new RestMovieEvent(event));
            } else if (event instanceof SpecialEvent){
                events.add(new RestSpecialEvent(event));
            } else {
                events.add(new RestEvent(event));
            }
        }
        return events;
    }

    /**
     * Returns a specific amount of event's which lay after a given time.
     * 
     * @param amount    The amount of Event's to get
     * @param afterTime The time from when the event's start
     * @return A list of the {@link RestEvent}'s
     */
    public List<RestEvent> getEventsAfter(int amount, String afterTime) {
        OffsetDateTime start = OffsetDateTime.of(LocalDateTime.parse(afterTime), ZoneOffset.ofHours(1));

        List<RestEvent> events = new ArrayList<>();

        for (Event event : this.getSortedEvents()) {
            if (amount > 0 && (event.getOdt().isAfter(start) || event.getOdt().isEqual(start))) {
                if (event instanceof MovieEvent) {
                    events.add(new RestMovieEvent(event));
                } else if (event instanceof SpecialEvent){
                    events.add(new RestSpecialEvent(event));
                } else {
                    events.add(new RestEvent(event));
                }
                amount--;
            }
        }

        return events;
    }

    /**
     * Returns all event's between a given timespan.
     * 
     * @param start Timespan start
     * @param end   Timespan end
     * @return A list of {@link RestEvent}'s
     */
    public List<RestEvent> getEventsBetweenTime(String start, String end) {
        List<RestEvent> events = new ArrayList<>();
        SortedSet<Event> sorted = this.getSortedEvents();

        OffsetDateTime startTime = OffsetDateTime.of(LocalDateTime.parse(start), ZoneOffset.ofHours(1));

        OffsetDateTime endTime = OffsetDateTime.of(LocalDateTime.parse(end), ZoneOffset.ofHours(1));

        for (Event event : sorted) {
            if ((event.getOdt().isAfter(startTime) || event.getOdt().isEqual(startTime))
                    && event.getOdt().isBefore(endTime)) {
                if (event instanceof MovieEvent) {
                    events.add(new RestMovieEvent(event));
                } else if (event instanceof SpecialEvent){
                    events.add(new RestSpecialEvent(event));
                } else {
                    events.add(new RestEvent(event));
                }
            }
        }

        return events;
    }

    /**
     * Returns an {@link TreeSet} of all {@link Event}'s sorted by time (sooner date
     * equals lesser index)
     * 
     * @return The Set of {@link Event}'s
     */
    public SortedSet<Event> getSortedEvents() {
        SortedSet<Event> sortedEvents = new TreeSet<>();

        // make sure the events are in the right order
        // uses the compareTo method in Events
        for (Event event : this.eventRepository.findAll()) {
            sortedEvents.add(event);
        }

        return sortedEvents;
    }

    /**
     * Returns a duyplan for a given event-id
     * 
     * @param eventId The {@link Event} id
     * @return A {@link RestDutyPlan} ready to be parsed to json
     */
    public RestDutyPlan getDutyplanForEvent(long eventId) {
        Event event = this.eventRepository.findById(eventId).get();
        return new RestDutyPlan(event.getDutyPlan());
    }

    /**
     * Returns all events for a specific user between a timespan
     * 
     * @param uuid  The user id
     * @param start Timespan start
     * @param end   Timespan end
     * @return All events
     */
    public List<RestMyEvents> getEventsForUser(String uuid, String start, String end) {
        List<RestMyEvents> events = new ArrayList<>();
        UUID uid = UUID.fromString(uuid);

        OffsetDateTime startTime = OffsetDateTime.of(LocalDateTime.parse(start), ZoneOffset.ofHours(1));
        OffsetDateTime endTime = OffsetDateTime.of(LocalDateTime.parse(end), ZoneOffset.ofHours(1));

        for (Event event : this.eventRepository.findAll()) {
            if ((event.getOdt().isAfter(startTime) || event.getOdt().isEqual(startTime))
                    && event.getOdt().isBefore(endTime) && event.getDutyPlan().containsUser(uid)) {
                events.add(new RestMyEvents(event, uid));
            }
        }

        return events;
    }
}