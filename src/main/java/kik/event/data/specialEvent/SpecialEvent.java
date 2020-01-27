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
package kik.event.data.specialEvent;

import kik.event.data.EventPlanningStatus;
import kik.event.data.EventType;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import java.time.OffsetTime;

/**
 * A {@link SpecialEvent} for all events that are not a {@link MovieEvent}
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
@Entity
public class SpecialEvent extends Event {
	
	@Size(max = 3000)
	@Column(length = 3000, columnDefinition = "TEXT")
	private String description;

	private OffsetTime expectedEnd;
	
	private EventType eventType;
	
	/**
	 * The default constructor of an {@link SpecialEvent}
	 *
	 * @param specialEventData a meta-object to store
	 *                         and load data for {@link SpecialEvent}-constructors
	 */
	public SpecialEvent(SpecialEventData specialEventData) {
		super(specialEventData);
		
		this.expectedEnd = specialEventData.getExpectedEnd();
		this.description = specialEventData.getDescription();
		
		this.eventType = specialEventData.getEventType();
		
		setEventPlanningStatus(EventPlanningStatus.SPECIAL);
		setEventPlanningStatusAsString(getEventPlanningStatus().toString());
	}
	
	/**
	 * Needed
	 * Not-used constructor of an {@link SpecialEvent}
	 */
	public SpecialEvent() {
		setEventType(EventType.SPECIAL);
	}
	
	/**
	 * Gets event type.
	 *
	 * @return the event type
	 */
	public EventType getEventType() {
		return this.eventType;
	}
	
	/**
	 * Sets event type.
	 *
	 * @param eventType the event type
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	/**
	 * Gets expected end.
	 *
	 * @return the expected end
	 */
	public OffsetTime getExpectedEnd() {
		return this.expectedEnd;
	}
	
	/**
	 * Sets expected end.
	 *
	 * @param expectedEnd the expected end
	 */
	public void setExpectedEnd(OffsetTime expectedEnd) {
		this.expectedEnd = expectedEnd;
	}
	
	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpecialEvent)) {
			return false;
		}
		
		SpecialEvent specialEvent = (SpecialEvent) obj;
		return specialEvent.getId() == this.getId();
	}
	
	/**
	 * Needed
	 * Creates output to display information nicely
	 *
	 * @return a {@link EventType} nicely formatted
	 */
	@SuppressWarnings("unused")
	public String getTypeAsString() {
		return this.eventType.toString();
	}
}
