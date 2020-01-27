package kik.rest.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.event.EventRepository;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventRepository;
import kik.event.management.movieEvent.MovieEventManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.event.data.event.Event;
import kik.rest.data.RestEvent;
import kik.rest.data.RestMyEvents;
import kik.rest.data.RestMyJob;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RestManagementTest {

    private EventRepository eventRepository;
    private RestManagement restManagement;
    private MovieEventRepository movieEventRepository;
    private UserRepository userRepository;
    private DutyPlanManagement dutyPlanManagement;

    @Autowired
    RestManagementTest(EventRepository eventRepository, RestManagement restManagement,
					   MovieEventManagement movieEventManagement, MovieEventRepository movieEventRepository,
					   UserRepository userRepository,
					   DutyPlanManagement dutyPlanManagement) {
        this.eventRepository = eventRepository;
        this.restManagement = restManagement;
        this.movieEventRepository = movieEventRepository;
        this.userRepository = userRepository;
        this.dutyPlanManagement = dutyPlanManagement;
    }

    @Test
    void uRestManagementGetEventByIdP1() throws Exception {
        Event event = null;
        try {
            event = this.eventRepository.findAll().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            fail("there should have been at least one element in the repository but there was none");
        }

        RestEvent restEvent = this.restManagement.getEvent(event.getId());
        assertEquals(event.getId(), restEvent.getId());
        assertEquals(event.getDate(), restEvent.getDate());
        assertEquals(event.getEventPlanningStatus(), restEvent.getEventPlanningStatus());
        assertEquals(event.getEventPublicity(), restEvent.getEventPublicity());
        assertEquals(event.getStart().format(DateTimeFormatter.ISO_OFFSET_TIME), restEvent.getStart());
    }

    @Test
    void uRestManagementGetSortedEventsP1() {

        Event tmp = null;
        for (Event event : this.restManagement.getSortedEvents()) {
            if (tmp == null) {
                tmp = event;
            } else if (event.getOdt().isAfter(tmp.getOdt()) || event.getOdt().isEqual(tmp.getOdt())) {
                tmp = event;
            } else {
                fail("the event list is not in order");
            }
        }
    }

    @Test
    void uRestManagementAmountAfterTimeP1() throws Exception {

        int amount = 5;
        LocalDateTime input = LocalDateTime.parse("2019-12-03T20:15");
        OffsetDateTime start = OffsetDateTime.of(LocalDateTime.parse("2019-12-03T20:15"), ZoneOffset.ofHours(1));
        List<Long> ids = new ArrayList<>();

        for (Event event : this.eventRepository.findAll()) {
            if (event.getOdt().isAfter(start) || event.getOdt().isEqual(start)) {
                ids.add(event.getId());
            }
        }

        List<RestEvent> events = this.restManagement.getEventsAfter(amount, input.toString());

        assertTrue("there should be at maximum 5 events in the list", events.size() <= 5);

        for (RestEvent event : events) {
            assertTrue("All events which fit the criteria should be in the list", ids.contains(event.getId()));
            assertTrue("The events should happen after or at the given time",
                    this.parse(event).isAfter(start) || this.parse(event).isEqual(start));
        }

    }

    @Test
    void uRestManagementBetweenTimeP1() throws Exception {

        LocalDateTime inputStart = LocalDateTime.parse("2019-12-03T20:15");
        OffsetDateTime start = OffsetDateTime.of(LocalDateTime.parse("2019-12-03T20:15"), ZoneOffset.ofHours(1));

        LocalDateTime inputEnd = LocalDateTime.parse("2020-01-01T20:20");
        OffsetDateTime end = OffsetDateTime.of(LocalDateTime.parse("2020-01-01T20:20"), ZoneOffset.ofHours(1));

        List<Long> ids = new ArrayList<>();

        for (Event event : this.eventRepository.findAll()) {
            OffsetDateTime time = event.getOdt();
            if ((time.isAfter(start) || time.isEqual(start)) && time.isBefore(end)) {
                ids.add(event.getId());
            }
        }

        List<RestEvent> events = this.restManagement.getEventsBetweenTime(inputStart.toString(), inputEnd.toString());

        for (RestEvent event : events) {
            assertTrue("All events which fit the criteria shoul be in the list", ids.contains(event.getId()));
            assertTrue("The events should happen after or at the given time", this.parse(event).isAfter(start)
                    || this.parse(event).isEqual(start) && this.parse(event).isBefore(end));
        }
    }

    @Test
    void uRestControllerMyEventsP1() {
        MovieEvent event = null;
        User user = null;
        try {
            event = this.movieEventRepository.findAll().toList().get(0);
            user = this.userRepository.findAll().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            fail("there should have been at least one element in the repository but there was none");
        }

        DutyPlan dp = event.getDutyPlan();
        Job job = new Job("name");
        job.setJobDescription("jobDescription");
        job.setJobName("jobName");
        job.setWorker(user);

        dutyPlanManagement.clearDutyPlan(dp);
        dp.setEvent(event);
        dp.createJob(job);
        dp.setAnnotation("annotation");

        List<RestMyEvents> events = this.restManagement.getEventsForUser(user.getUuid().toString(), "2000-01-01T00:01",
                "3000-01-01T01:00");

        RestMyEvents myEvents = null;
        for (RestMyEvents e : events) {
            if (Long.compare(e.getId(), event.getId()) == 0) {
                myEvents = e;
                break;
            }
        }

        assertTrue("The event should be the same", Long.compare(myEvents.getId(), event.getId()) == 0);
        assertEquals(1, myEvents.getMyJobs().size());
        RestMyJob myJobs = myEvents.getMyJobs().get(0);

        assertTrue("There should be at least one event where the user is assigned", events.size() >= 1);
        assertEquals("jobDescription", myJobs.getJobDescription());
        assertEquals("jobName", myJobs.getJobName());
        assertEquals(event.getFullyQualifiedName(), myEvents.getFullyQualifiedName());
        assertEquals(event.getEventPublicity(), myEvents.getEventPublicity());
    }

    /**
     * Parses the start string from the rest event to create a
     * {@link OffsetDateTime}
     * 
     * @param event The {@link RestEvent}
     * @return The time and date
     */
    OffsetDateTime parse(RestEvent event) {
        OffsetTime t = OffsetTime.parse(event.getStart(), DateTimeFormatter.ISO_OFFSET_TIME);
        OffsetDateTime odt = OffsetDateTime.of(event.getDate(), t.toLocalTime(), t.getOffset());
        return odt;
    }
}