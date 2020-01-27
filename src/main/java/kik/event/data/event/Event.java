/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kik.event.data.event;

import com.mysema.commons.lang.Assert;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.event.data.EventPlanningStatus;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.*;

/**
 * This {@link Event} serves an abstraction and stores all neccessary information
 * regarding the name, date and time, etc.
 *
 * @author Georg Lauterbach
 * @version 0.1.6
 */
@Entity
@Table(name = "kikevent")
public abstract class Event implements Comparable<Event> {
	@Id
	@GeneratedValue
	private long id;
	
	private String additonalName;
	private String fullyQualifiedName;
	
	private EventPublicity eventPublicity;
	private EventPlanningStatus eventPlanningStatus;
	private String eventPlanningStatusAsString;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate date;
	private OffsetTime start;
	@DateTimeFormat(pattern = "dd.MM.yyyy, HH:mm")
	private OffsetDateTime odt;

	@OneToOne
	private DutyPlan dutyPlan;
	
	private boolean isOld;
	
	/**
	 * Default constructor of an {@link Event}
	 *
	 * @param eventData A meta-object to store and load data for event constructors
	 */
	protected Event(EventData eventData) {
		Assert.notNull(eventData, "eventData must not be null! ~ in event.standard.Event::Event");
		
		this.additonalName = eventData.getAdditionalName();
		this.fullyQualifiedName = eventData.getFullyQualifiedName();
		
		this.date = eventData.getDate();
		this.start = eventData.getStart();
		this.odt = eventData.getOdt();
		
		this.eventPublicity = eventData.getEventPublicity();
		
		this.isOld = false;
	}
	
	/**
	 * Needed
	 * Not-used constructor of an {@link Event}
	 */
	@SuppressWarnings("unused")
	public Event() {}
	
	/**
	 * Gets id.
	 *
	 * @return Value of id.
	 */
	public long getId() { return this.id; }
	
	/**
	 * Gets additonalName.
	 *
	 * @return Value of additonalName.
	 */
	public String getAdditonalName() { return this.additonalName; }
	
	/**
	 * Sets new additonalName.
	 *
	 * @param additonalName New value of additonalName.
	 */
	public void setAdditonalName(String additonalName) { this.additonalName = additonalName; }
	
	/**
	 * Sets new fullyQualifiedName.
	 *
	 * @param fullyQualifiedName New value of fullyQualifiedName.
	 */
	public void setFullyQualifiedName(String fullyQualifiedName) { this.fullyQualifiedName = fullyQualifiedName; }
	
	/**
	 * Gets fullyQualifiedName.
	 *
	 * @return Value of fullyQualifiedName.
	 */
	public String getFullyQualifiedName() { return this.fullyQualifiedName; }
	
	/**
	 * Gets date.
	 *
	 * @return Value of date.
	 */
	public LocalDate getDate() { return this.date; }
	
	/**
	 * Sets new date.
	 *
	 * @param date New value of date.
	 */
	public void setDate(LocalDate date) { this.date = date; }
	
	/**
	 * Sets new odt.
	 *
	 * @param odt New value of odt.
	 */
	public void setOdt(OffsetDateTime odt) { this.odt = odt; }
	
	/**
	 * Sets new start.
	 *
	 * @param start New value of start.
	 */
	public void setStart(OffsetTime start) { this.start = start; }
	
	/**
	 * Gets odt.
	 *
	 * @return Value of odt.
	 */
	public OffsetDateTime getOdt() { return this.odt; }
	
	/**
	 * Gets start.
	 *
	 * @return Value of start.
	 */
	public OffsetTime getStart() { return this.start; }
	
	/**
	 * Sets new eventPublicity.
	 *
	 * @param eventPublicity New value of eventPublicity.
	 */
	public void setEventPublicity(EventPublicity eventPublicity) { this.eventPublicity = eventPublicity; }
	
	/**
	 * Gets eventPublicity.
	 *
	 * @return Value of eventPublicity.
	 */
	public EventPublicity getEventPublicity() { return this.eventPublicity; }
	
	/**
	 * Sets new eventPlanningStatus.
	 *
	 * @param eventPlanningStatus New value of eventPlanningStatus.
	 */
	public void setEventPlanningStatus(EventPlanningStatus eventPlanningStatus) {
		this.eventPlanningStatus = eventPlanningStatus;
	}
	
	/**
	 * Gets eventPlanningStatus.
	 *
	 * @return Value of eventPlanningStatus.
	 */
	public EventPlanningStatus getEventPlanningStatus() { return this.eventPlanningStatus; }
	
	/**
	 * Special setter to set the {@link EventPlanningStatus} with a {@link String}
	 *
	 * @param eventPlanningStatusAsString an {@link EventPlanningStatus} formatted as a string
	 */
	public void setEventPlanningStatusAsString(String eventPlanningStatusAsString) {
		this.eventPlanningStatusAsString = eventPlanningStatusAsString;
	}
	
	/**
	 * Is old boolean.
	 *
	 * @return the boolean
	 */
	public boolean getIsOld() {
		return this.isOld;
	}
	
	/**
	 * Sets old.
	 *
	 * @param isOld the old
	 */
	public void setIsOld(boolean isOld) {
		this.isOld = isOld;
	}
	
	/**
	 * Needed
	 * Creates output to display information nicely
	 *
	 * @return a {@link EventPublicity} nicely formatted
	 */
	@SuppressWarnings("unused")
	public String getEventPublicityAsString() { return this.eventPublicity.toString(); }
	
	/**
	 * Needed
	 * Creates output to display information nicely
	 *
	 * @return a {@link EventPlanningStatus} nicely formatted
	 */
	public String getEventPlanningStatusAsString() {
		return this.eventPlanningStatus.toString();
	}
	
	/**
	 * Needed
	 * Creates output to display information nicely
	 *
	 * @return a {@link EventPlanningStatus} nicely formatted
	 */
	@SuppressWarnings("unused")
	public String getPlanningStatusAsString() {
		return getEventPlanningStatusAsString();
	}
	
	/**
	 * A checker whether the {@link Event} is marked as not old
	 *
	 * @return negated value of isOld
	 */
	public boolean isNotOld() {
		return !this.isOld;
	}
	
	/**
	 * A checker whether the {@link Event} is marked as old
	 *
	 * @return value of isOld
	 */
	public boolean isOld() {
		return this.isOld;
	}
	
	/**
	 * For debugginf purposes
	 *
	 * @return this object's important data field
	 */
	@Override
	public String toString() {
		return "[videoshop.event.Event] " +
				"ID = " + getId() + ", " +
				"Name = " + getFullyQualifiedName();
	}
	
	/**
	 * Compares the time between two {@link Event}'s
	 *
	 * @param event The {@link Event} to compare to
	 * @return Returns a negative value if the compared to {@link Event} happens after the other.
	 * Positive if the compared to {@link Event} happens before or at the same time
	 */
	@Override
	public int compareTo(Event event) {
		if (this.odt.isBefore(event.getOdt())) {
			return -1;
		} else if (this.odt.isAfter(event.getOdt())) {
			return 1;
		}
		
		// Trick to be able to put events with the same time in a tree set
		return 1;
	}

	/**
	 * @return the associated dutyplan
	 */
	public DutyPlan getDutyPlan() {
		return dutyPlan;
	}

	/**
	 * Returns the color-values for the bootstrap design
	 *
	 * @return a color value as a {@link String}
	 */
	@SuppressWarnings("unused")
	public String getColour() {
		if (dutyPlan.hasOpenJobs()) {
			return "red";
		} else {
			return "green";
		}
	}

	/**
	 * sets the {@link DutyPlan} for the event
	 * @param dutyPlan the {@link DutyPlan} to be set
	 */
	public void setDutyPlan(DutyPlan dutyPlan) {
		this.dutyPlan = dutyPlan;
	}


	/**
	 * Needed for tests, and for tests only
	 *
	 * @param id this {@link Event}s id
	 */
	public void setId(long id) {
		this.id = id;
	}
}
