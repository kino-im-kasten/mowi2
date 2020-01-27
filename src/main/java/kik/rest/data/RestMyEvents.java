package kik.rest.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kik.dutyplan.data.job.Job;
import kik.event.data.EventPublicity;
import kik.event.data.event.Event;

/**
 * {@link RestMyEvents} will be used by spring to create a json string
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestMyEvents {
    private long id;
    private String fullyQualifiedName;
    private EventPublicity eventPublicity;
    private LocalDate date;
    private String start;
    private List<RestMyJob> myJobs = new ArrayList<>();

    /**
     * Default constructor of {@link RestMyEvents}
     * 
     * @param event The {@link Event} to parse
     * @param id    The user id
     */
    public RestMyEvents(Event event, UUID id) {
        this.id = event.getId();
        this.fullyQualifiedName = event.getFullyQualifiedName();
        this.eventPublicity = event.getEventPublicity();
        this.date = event.getDate();
        this.start = event.getStart().format(DateTimeFormatter.ISO_OFFSET_TIME);
        for (Job job : event.getDutyPlan().getJobsForWorker(id)) {
            myJobs.add(new RestMyJob(job));
        }
    }

    /**
     * Getter for the start time
     * 
     * @return the start time
     */
    public String getStart() {
        return this.start;
    }

    /**
     * Getter for the event publicity
     * 
     * @return The event publicity
     */
    public EventPublicity getEventPublicity() {
        return this.eventPublicity;
    }

    /**
     * Getter for the start date
     * 
     * @return tthe start date
     */
    public LocalDate getDate() {
        return this.date;
    }

    public List<RestMyJob> getMyJobs() {
        return this.myJobs;
    }

    /**
     * Getter for the full name
     * 
     * @return The full name
     */
    public String getFullyQualifiedName() {
        return this.fullyQualifiedName;
    }

    /**
     * Getter for the id
     * 
     * @return The id
     */
    public long getId() {
        return this.id;
    }
}