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

import kik.event.data.EventPublicity;
import kik.event.management.Check;
import kik.event.data.EventType;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;
import java.util.Objects;

/**
 * An {@link EventForm} to work with generic information every
 * event needs
 *
 * @author Georg Lauterbach
 * @version 0.1.9
 */
public abstract class EventForm {
	
	private EventType eventType;
	private EventPublicity eventPublicity;
	
	private String additionalName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private String start;
	
	/**
	 * Default constructor of an {@link EventForm}
	 *
	 * @param name      Name of the {@link Event}
	 * @param date      Date as {@link LocalDate}
	 * @param start     Time of start
	 * @param eventType type of an {@link Event}
	 */
	public EventForm(String name, LocalDate date, String start, EventType eventType) {
		if (Check.checkOnNull(date, start, eventType)) {
			this.start = "20:30";
			return;
		}
		
		this.eventType = eventType;
		
		this.additionalName = Objects.requireNonNullElse(name, "");
		
		this.date = date;
		this.start = start;
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
	 * Gets additional name.
	 *
	 * @return the additional name
	 */
	public String getAdditionalName() {
		return this.additionalName;
	}
	
	/**
	 * Sets additonal name.
	 *
	 * @param additionalName the additional name
	 */
	public void setAdditonalName(String additionalName) {
		this.additionalName = additionalName;
	}
	
	/**
	 * Gets start.
	 *
	 * @return the start
	 */
	public String getStart() {
		return this.start;
	}
	
	/**
	 * Sets start.
	 *
	 * @param start the start
	 */
	public void setStart(String start) {
		this.start = start;
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
	 * Sets additional name.
	 *
	 * @param additionalName the additional name
	 */
	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}
}
