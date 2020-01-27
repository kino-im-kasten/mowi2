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
package kik.event.data.movieEvent;

import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.event.Event;
import kik.event.data.event.EventForm;

import kik.event.management.movieEvent.MovieEventValidation;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * A {@link MovieEventForm} to work with user input
 * when creating a new {@link MovieEvent}
 *
 * There is no business logic or validation to
 * be implemented here.
 *
 * @author Georg Lauterbach
 * @version 0.2.0
 */
public class MovieEventForm extends EventForm {
	
	private Long bookingID;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SuppressWarnings("unused")
	private LocalDate date;
	
	/**
	 * Default constructor of an {@link MovieEventForm}
	 * <p>
	 * Validated by {@link MovieEventValidation}
	 *
	 * @param additionalName Name of the {@link MovieEvent}
	 * @param date           Date as {@link LocalDate}
	 * @param start          Time of start
	 * @param bookingID      The unique id every movie carries
	 * @param eventPublicity the event publicity
	 */
	public MovieEventForm(String additionalName,
						  LocalDate date,
						  String start,
						  String bookingID,
						  String eventPublicity) {
		super(additionalName, date, start, EventType.MOVIE);
		
		if (bookingID == null) {
			return;
		}
		
		this.bookingID = Long.valueOf(bookingID);
		setEventPublicity(EventPublicity.toPublicity(eventPublicity));
	}
	
	/**
	 * Gets booking id.
	 *
	 * @return the booking id
	 */
	public Long getBookingID() { return this.bookingID; }
	
	/**
	 * Sets booking id.
	 *
	 * @param bookingID the booking id
	 */
	public void setBookingID(Long bookingID) {
		this.bookingID = bookingID;
	}
}
