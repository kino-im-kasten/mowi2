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

import kik.booking.data.Booking;

import kik.event.data.EventType;
import kik.event.data.event.EventData;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;

/**
 * A {@link MovieEventData} model, containing every information
 * needed to create a {@link MovieEvent}
 *
 * Used to transport information across the initializers and management
 * and in the end to create a real {@link MovieEvent}
 *
 * @author Georg Lauterbach
 * @version 0.1.5
 */
public class MovieEventData extends EventData {
	
	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	private Booking booking;
	
	/**
	 * Default constructor of an {@link MovieEventData}
	 *
	 * There are no default parameters, as everything is
	 * managed with getters and setters.
	 */
	public MovieEventData() {
		setEventType(EventType.MOVIE);
	}
	
	/**
	 * Gets booking.
	 *
	 * @return the booking
	 */
	public Booking getBooking() {
		return this.booking;
	}
	
	/**
	 * Sets booking.
	 *
	 * @param booking the booking
	 */
	public void setBooking(Booking booking) {
		this.booking = booking;
	}
	
	/**
	 * Override to not accidentaly overwrite the actual
	 * {@link EventType}
	 *
	 * @param type type of an event
	 */
	@Override
	public void setEventType(EventType type) {
		super.setEventType(EventType.MOVIE);
	}
}
