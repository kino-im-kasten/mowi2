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

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.event.data.EventPlanningStatus;
import kik.event.data.EventType;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.tickets.Tickets;

import kik.booking.data.Booking;
import kik.movie.data.Movie;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.OffsetTime;

/**
 * A {@link MovieEvent} for all {@link Event}s, which are based on {@link Movie}s.
 *
 * A more concrete implementation of an {@link Event}. Also the most common type of
 * {@link Event}s.
 *
 * @author Georg Lauterbach
 * @version 0.1.5
 */
@Entity
public class MovieEvent extends Event {

	private final static EventType type = EventType.MOVIE;
	
	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	private Booking booking;
	@OneToOne(cascade = CascadeType.ALL)
	private Tickets tickets;

	
	private OffsetTime expectedEnd;
	
	/**
	 * The default constructor of an {@link MovieEvent}
	 *
	 * @param movieEventData A meta-object to store and load data for {@link MovieEvent}-constructors
	 */
	public MovieEvent(MovieEventData movieEventData) {
		super(movieEventData);
		
		this.booking = movieEventData.getBooking();
		this.tickets = new Tickets();
		
		setEventPlanningStatus(EventPlanningStatus.BOOKED);
		setEventPlanningStatusAsString(getEventPlanningStatus().toString());
	}
	
	/**
	 * Needed
	 * Not-used constructor of an {@link MovieEvent}
	 */
	public MovieEvent() {}
	
	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public static EventType getType() {
		return type;
	}
	
	/**
	 * Gets booking.
	 *
	 * @return the booking
	 */
	public Booking getBooking() {
		return booking;
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
	 * Gets tickets.
	 *
	 * @return the tickets
	 */
	public Tickets getTickets() {
		return this.tickets;
	}
	
	/**
	 * Sets tickets.
	 *
	 * @param tickets the tickets
	 */
	public void setTickets(Tickets tickets) {
		this.tickets = tickets;
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
	 * Needed
	 * Creates output to display information nicely
	 *
	 * @return a {@link EventType} nicely formatted
	 */
	@SuppressWarnings("unused")
	public String getTypeAsString() {
		return type.toString();
	}
	
	/**
	 * Creates output to display information nicely
	 *
	 * @return time of a movie nicely formatted
	 */
	@SuppressWarnings("unused")
	public String getDuration() {
		if (this.booking.getMovie() == null) {
			return null;
		}
		
		return String.valueOf(this.booking.getMovie().getRunTimeInMin());
	}
	
	/**
	 * A checker whether tickets have been set or not.
	 *
	 * @return true when tickets have not been validated correctly, i.e. there have no tickets been given
	 */
	public boolean ticketsAreNotSet() {
		return this.tickets.validateForIndex();
	}
	
	/**
	 * A checker whether the {@link EventPlanningStatus} is not
	 * SETTLED
	 *
	 * @return true if status is not SETTLED
	 */
	public boolean isNotSettled() {
		return !getEventPlanningStatus().equals(EventPlanningStatus.SETTLED);
	}
	
	@Override
	public String toString() {
		return super.toString() + "\r\n" +
			"Type = " + getType();
	}
	
	/**
	 * Calculates the revenue earned by an event
	 *
	 * @return the revenue or, if it's not valid, -1
	 */
	public double calculateRawTicketsRevenue() {
		return this.tickets.calculateRawTicketsRevenue();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MovieEvent)) {
			return false;
		}
		
		MovieEvent movieEvent = (MovieEvent) obj;
		return movieEvent.getId() == this.getId();
	}
}
