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

import kik.event.data.EventPlanningStatus;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEventData;

import kik.event.data.specialEvent.SpecialEventData;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

/**
 * A {@link EventData} model, containing information all events
 * have in common.
 * <p>
 * Used to transport information across the initializers and managements
 * and in the end to create a real {@link Event}.
 *
 * @author Georg Lauterbach
 * @version 0.1.5
 */
public class EventData {
	
	private EventType type;
	
	private String additionalName;
	private String fullyQualifiedName;
	
	private EventPublicity eventPublicity;
	private EventPlanningStatus eventPlanningStatus;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate date;
	private OffsetTime start;
	@DateTimeFormat(pattern = "dd.MM.yyyy, HH:mm")
	private OffsetDateTime odt;
	
	/**
	 * Creates a new instance of a {@link MovieEventData} object
	 * to control the conversion between different {@link EventData}
	 * types and to avoid {@link ClassCastException}s.
	 *
	 * @return A new {@link MovieEventData} instance
	 */
	public MovieEventData toMED() {
		MovieEventData med = new MovieEventData();
		
		med.setEventType(getEventType());
		
		med.setAdditionalName(getAdditionalName());
		
		med.setDate(getDate());
		med.setExpectedEnd(getExpectedEnd());
		med.setOdt(getOdt());
		
		return med;
	}
	
	/**
	 * Creates an associated {@link SpecialEventData}
	 * instance from this object
	 *
	 * @return a {@link SpecialEventData} instance
	 */
	public SpecialEventData toSED() {
		SpecialEventData sed = new SpecialEventData();
		
		sed.setEventType(getEventType());
		sed.setAdditionalName(getAdditionalName());
		
		sed.setDate(getDate());
		sed.setStart(getStart());
		sed.setExpectedEnd(getExpectedEnd());
		sed.setOdt(getOdt());
		
		return sed;
	}
	
	/**
	 * Gets event type.
	 *
	 * @return the event type
	 */
	public EventType getEventType() {
		return this.type;
	}
	
	/**
	 * Sets event type.
	 *
	 * @param type the type
	 */
	public void setEventType(EventType type) {
		this.type = type;
	}
	
	/**
	 * Gets additional name.
	 *
	 * @return the additional name
	 */
	public String getAdditionalName() {
		return this.additionalName;
	}
	
	/**
	 * Sets additional name.
	 *
	 * @param additionalName the additional name
	 */
	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}
	
	/**
	 * Gets fully qualified name.
	 *
	 * @return the fully qualified name
	 */
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}
	
	/**
	 * Sets fully qualified name.
	 *
	 * @param fullyQualifiedName the fully qualified name
	 */
	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}
	
	/**
	 * Gets date.
	 *
	 * @return the date
	 */
	public LocalDate getDate() {
		return this.date;
	}
	
	/**
	 * Sets date.
	 *
	 * @param date the date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	/**
	 * Gets expected end.
	 *
	 * @return the expected end
	 */
	public OffsetTime getExpectedEnd() {
		return this.start;
	}
	
	/**
	 * Sets expected end.
	 *
	 * @param expectedEnd the expected end
	 */
	public void setExpectedEnd(OffsetTime expectedEnd) {
		this.start = expectedEnd;
	}
	
	/**
	 * Gets odt.
	 *
	 * @return the odt
	 */
	public OffsetDateTime getOdt() {
		return this.odt;
	}
	
	/**
	 * Sets odt.
	 *
	 * @param odt the odt
	 */
	public void setOdt(OffsetDateTime odt) {
		this.odt = odt;
	}
	
	/**
	 * Gets event publicity.
	 *
	 * @return the event publicity
	 */
	public EventPublicity getEventPublicity() {
		return this.eventPublicity;
	}
	
	/**
	 * Sets event publicity.
	 *
	 * @param eventPublicity the event publicity
	 */
	public void setEventPublicity(EventPublicity eventPublicity) {
		this.eventPublicity = eventPublicity;
	}
	
	/**
	 * Gets event planning status.
	 *
	 * @return the event planning status
	 */
	public EventPlanningStatus getEventPlanningStatus() {
		return this.eventPlanningStatus;
	}
	
	/**
	 * Sets event planning status.
	 *
	 * @param eventPlanningStatus the event planning status
	 */
	public void setEventPlanningStatus(EventPlanningStatus eventPlanningStatus) {
		this.eventPlanningStatus = eventPlanningStatus;
	}
	
	/**
	 * Gets start.
	 *
	 * @return the start
	 */
	public OffsetTime getStart() {
		return this.start;
	}
	
	/**
	 * Sets start.
	 *
	 * @param start the start
	 */
	public void setStart(OffsetTime start) {
		this.start = start;
	}
}
