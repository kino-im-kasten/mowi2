package kik.rest.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import kik.event.data.EventPlanningStatus;
import kik.event.data.EventPublicity;
import kik.event.data.event.Event;

/**
 * {@link RestEvent} will be used by spring to create a json string
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestEvent {
	private long id;
	private String additionalName;
	private String fullyQualifiedName;
	private EventPublicity eventPublicity;
	private EventPlanningStatus eventPlanningStatus;
	private LocalDate date;
	private String start;
	private RestDutyPlan dutyplan;
    

	/**
	 * Default constructor of {@link RestEvent}
	 * 
	 * @param event The {@link Event} to parse
	 */
    public RestEvent(Event event) {
        this.id = event.getId();
        this.additionalName = event.getAdditonalName();
        this.fullyQualifiedName = event.getFullyQualifiedName();
        this.eventPublicity = event.getEventPublicity();
        this.eventPlanningStatus = event.getEventPlanningStatus();
		this.date = event.getDate();
		this.start = event.getStart().format(DateTimeFormatter.ISO_OFFSET_TIME);
		this.dutyplan = new RestDutyPlan(event.getDutyPlan());
    }

	/**
     * Getter for the id
     * 
     * @return The id
     */
    public long getId() {
		return this.id;
	}
	
	/**
	 * Getter for the additional name
	 * 
	 * @return The additional name
	 */
	public String getAdditonalName() {
		return this.additionalName;
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
	 * Getter for the event publicity
	 * 
	 * @return The event publicity
	 */
    public EventPublicity getEventPublicity() {
		return this.eventPublicity;
	}
	

	/**
	 * Getter for the event planning status
	 * 
	 * @return the planning status
	 */
	public EventPlanningStatus getEventPlanningStatus() {
		return this.eventPlanningStatus;
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
	 * Getter for the start date
	 * 
	 * @return the start date
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * Getter for the dutyplan
	 * 
	 * @return the dutyplan
	 * 
	 */
	public RestDutyPlan getDutyplan() {
		return this.dutyplan;
	}
}
