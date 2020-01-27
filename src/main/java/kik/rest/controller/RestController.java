package kik.rest.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import kik.event.data.event.Event;
import kik.rest.data.RestDutyPlan;
import kik.rest.data.RestEvent;
import kik.rest.data.RestMyEvents;
import kik.rest.management.RestManagement;;

/**
 * {@link RestController} handling HTTP::GET requests
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
@Controller
public class RestController {

    RestManagement management;

    /**
     * Default constructor of an {@link RestController}
     * 
     * @param management The {@link RestManagement}
     */
    RestController(RestManagement management) {
        this.management = management;
    }

    /**
     * Handles the request for HTTP::GET on /rest/event .Returns the event according
     * to the id. Example: /rest/event?id=1
     * 
     * @param id The {@link Event} id
     * @return The event data
     */
    @GetMapping("/rest/event")
    public @ResponseBody RestEvent getEvent(@RequestParam() long id) {
        // Example: /rest/event?id=1
        return management.getEvent(id);
    }

    /**
     * Handles the request for HTTP::GET on /rest/eventList. Returns all
     * {@link Event}'s from the database
     * 
     * @return A list of all {@link Event}'s put into {@link RestEvent}'s
     */
    @GetMapping("/rest/eventList")
    public @ResponseBody List<RestEvent> getEventList() {
        return management.getEventList();
    }

    /**
     * Handles the request for HTTP::GET on /rest/eventsAfter . Returns a specific
     * amount of {@link Event}'s which lay after a given time.
     * 
     * @param amount The amount of {@link Event}'s to get
     * @param start  The time from when the {@link Event}'s start
     * @return A list of the {@link Event}'s put into {@link RestEvent}'s
     */
    @GetMapping("/rest/eventsAfter")
    public @ResponseBody List<RestEvent> getEventsAfterTime(@RequestParam() int amount, @RequestParam() String start) {
        // Example: /rest/eventsAfter?amount=1&start=2019-12-03T20:15
        return this.management.getEventsAfter(amount, start);
    }

    /**
     * Handles the request for HTTP::GET on /rest/eventsBetween . Returns all
     * {@link Event} between a given timespan.
     * 
     * @param start Timespan start
     * @param end   Timespan end
     * @return A list of the {@link Event}'s put into {@link RestEvent}'s
     */
    @GetMapping("/rest/eventsBetween")
    public @ResponseBody List<RestEvent> getEventsBetweenTime(@RequestParam() String start,
            @RequestParam() String end) {
        // Example: /rest/eventsBetween?start=2019-12-03T20:15&end=2020-12-03T20:15
        return this.management.getEventsBetweenTime(start, end);
    }

    /**
     * Handles the request for HTTP::GET on /rest/dutyplan . Returns a dutyplan for
     * a given id.
     * 
     * @param eventId The Event if
     * @return The event
     */
    @GetMapping("/rest/dutyplan")
    public @ResponseBody RestDutyPlan getDutyplanForEvent(@RequestParam() long eventId) {
        return this.management.getDutyplanForEvent(eventId);
    }

    /**
     * Handles the request for HTTP::GET on /rest/eventsForUser . Returns all events
     * for a user between a timespan
     * 
     * @param uuid  The user id
     * @param start Timespan start
     * @param end   Timespan end
     * @return All events
     */
    @GetMapping("/rest/eventsForUser")
    public @ResponseBody List<RestMyEvents> getEventsForUser(@RequestParam() String uuid, @RequestParam() String start,
            @RequestParam() String end) {
        return this.management.getEventsForUser(uuid, start, end);
    }
}