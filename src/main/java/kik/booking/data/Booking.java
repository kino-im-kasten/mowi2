/*
 * Copyright 2014-2019 the original author or authors.
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

package kik.booking.data;

import kik.booking.management.BookingInitializer;
import kik.distributor.data.Distributor;
import kik.event.data.EventPlanningStatus;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.tickets.Tickets;
import kik.event.management.EventFilter;
import kik.movie.data.Movie;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A {@link Booking} storing all necessary information.
 *
 * @author Erik Schönherr
 */
@Entity
@Table(name = "kikbooking")
public class Booking {
	/**
	 * Every booking has a unique id which is automatically generated.
	 */
	@Id
	@GeneratedValue
	private long id;
	private String tbNumber;
	private BookingState state;
	private boolean archived = false;
	
	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	private Movie movie;
	
	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	private Distributor distributor;

	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER, orphanRemoval = true)
	private Map<Long, MovieEvent> events = new HashMap<>();

	private LocalDate startDate;
	private LocalDate endDate;
	
	@Convert(converter = ConditionsConverter.class)
	private Conditions conditions;

	/**
	 * Default constructor for a booking.
	 */
	public Booking() {
	
	}

	/**
	 * Alternative constructor for a booking. Used by {@link BookingInitializer}.
	 *
	 * @param tbNumber The tb-number
	 * @param movie The booked {@link Movie}
	 * @param distributor The {@link Distributor} the movie got booked from
	 * @param startDate The start date
	 * @param endDate The end date
	 * @param conditions A {@link Conditions} object holding all the information relevant for the billing
	 */
	public Booking(String tbNumber, Movie movie, Distributor distributor, LocalDate startDate, LocalDate endDate,
				   Conditions conditions) {
		this.tbNumber = tbNumber;
		this.movie = movie;
		this.distributor = distributor;
		this.startDate = startDate;
		this.endDate = endDate;
		this.conditions = conditions;
		
		updateState();
	}

	/**
	 * @return List of all presented events
	 */
	public List<MovieEvent> finishedEvents() {
		return Streamable.of(events.values()).filter(
			e -> e.getEventPlanningStatus() == EventPlanningStatus.PRESENTED ||
				 e.getEventPlanningStatus() == EventPlanningStatus.SETTLED ||
				 e.getEventPlanningStatus() == EventPlanningStatus.CANCELED).toList();
	}

	/**
	 * Adds an {@link Event} to the events map
	 *
	 * @param event Event to be added
	 */
	public void addEvent(MovieEvent event) {
		Assert.notNull(event, "Event must not be null!");
		
		if (this.movie != null) {
			this.movie.addEventId(event.getId());
		}
		
		this.events.put(event.getId(),event);
	}

	/**
	 * Removes an {@link Event} from the events map
	 *
	 * @param event Event to be removed
	 * @return true if the Event successfully got removed
	 */
	public boolean removeEvent(MovieEvent event) {
		Assert.notNull(event, "Event must not be null!");
		
		if (this.movie != null) {
			this.movie.removeEventId(event.getId());
		}

		return events.remove(event.getId()) != null;
	}

	/**
	 * Iterates over all related events and sums up their revenue after validating the ticket numbers.
	 *
	 * @return the total revenue of the booking
	 */
	public double getTotalRevenue() {
		double revenue = 0.0;
		for(MovieEvent event : events.values()){
			Tickets tickets = event.getTickets();

			// checks if ticket numbers are valid
			if(tickets.validate()) {
				revenue += event.calculateRawTicketsRevenue();
			}
		}
		return revenue;
	}

	/**
	 * Checks if the Booking is settled up.
	 *
	 * @return true if settled up
	 */
	public boolean isSettledUp() {
		return state == BookingState.SETTLEDUP;
	}


	/**
	 * Checks if the Booking is canceled.
	 *
	 * @return true if canceled
	 */
	public boolean isCanceled() {
		return state == BookingState.CANCELED;
	}
	
	/**
	 * Sets the state of the booking depending on a given time
	 */
	public void updateState() {
		if (archived) {
			return;
		}
		
		LocalDate now = LocalDate.now();
		
		if (now.compareTo(startDate) < 0) {
			state = BookingState.PENDING;
		} else if (now.compareTo(endDate) > 0) {
			state = BookingState.OVER;
		} else {
			state = BookingState.ACTIVE;
		}
	}
	
	
	/**
	 * Returns a String depending on the state of the booking and it's {@link MovieEvent}'s.
	 *
	 * @return String that encodes a colour in the html template
	 */
	public String getColourString() {
		String colourString = "";
		
		if (state == BookingState.PENDING || state == BookingState.ACTIVE) {
			colourString = "green";
		} else if (state == BookingState.OVER) {
			if (allEventsSettledUp()) {
				colourString = "orange";
			} else {
				colourString = "red";
			}
		}
		return colourString;
	}
	
	/**
	 * Iterates over all events and checks if their ticket numbers are entered.
	 *
	 * @return A boolean that is true when all  ticket numbers got entered
	 */
	public boolean allEventsSettledUp() {
		for(MovieEvent ev : events.values()){
			Tickets tickets = ev.getTickets();

			if(tickets == null || !tickets.validate()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a String to identify a booking. The user should be able to know which booking is meant when reading it.
	 *
	 * @return String that contains the movie name and the tbNumber
	 */
	public String getDisplayName() {
		return movie.getGermanName() + ", " + startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
				endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " (" + tbNumber + ")";
	}

	/**
	 * Returns a String to identify a booking. Is used in the {@link EventFilter}.
	 *
	 * @return String that contains start date, end date and tbNumber
	 */
	@SuppressWarnings("unused")
	public String getOverviewDisplayName() {
		return startDate.format(DateTimeFormatter.ofPattern("dd.MM")) + " - " +
				endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ", " + this.tbNumber;
	}

	/**
	 * Sets the state of the booking and automatically updates the 'archived' attribute depending on the given state.
	 *
	 * @param state New {@link BookingState}
	 */
	public void setState(BookingState state) {
		this.state = state;
		
		archived = (state == BookingState.CANCELED || state == BookingState.SETTLEDUP);
	}


	/**
	 * Sets the id.
	 *
	 * @param id the id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	public void setTbNumber(String tbNumber) {
		this.tbNumber = tbNumber;
	}

	/**
	 * Sets the movie of the booking and automatically updates the saved id's of linked Events in the old and new movie.
	 *
	 * @param movie New {@link Movie}
	 */
	public void setMovie(Movie movie) {
		for (Event event : this.events.values()) {
			this.movie.removeEventId(event.getId());
			movie.addEventId(event.getId());
		}
		
		this.movie = movie;
	}

	/**
	 * Sets the distributor.
	 *
	 * @param distributor the distributor
	 */
	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	/**
	 * Sets the events.
	 *
	 * @param events the events
	 */
	public void setEvents(HashMap<Long,MovieEvent> events) {
		this.events = events;
	}

	/**
	 * Sets the start date.
	 *
	 * @param startDate the start date
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Sets the end date
	 *
	 * @param endDate the end date
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Sets the conditions.
	 *
	 * @param conditions the conditions
	 */
	public void setConditions(Conditions conditions) {
		this.conditions = conditions;
	}

	/**
	 * Sets archived.
	 *
	 * @param archived archived
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	/**
	 * Gets archived.
	 *
	 * @return true if the booking is archived.
	 */
	public boolean isArchived() {
		return archived;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets the tb number.
	 *
	 * @return the tb number
	 */
	public String getTbNumber() {
		return tbNumber;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public BookingState getState() {
		return state;
	}

	/**
	 * Gets the movie.
	 *
	 * @return the movie
	 */
	public Movie getMovie() {
		return movie;
	}

	/**
	 * Gets the distributor.
	 *
	 * @return the distributor.
	 */
	public Distributor getDistributor() {
		return distributor;
	}

	/**
	 * Gets the events.
	 *
	 * @return the events
	 */
	public List<MovieEvent> getEvents() {
		return new ArrayList<>(events.values());
	}

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the end date.
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Gets the conditions.
	 *
	 * @return the conditions.
	 */
	public Conditions getConditions() {
		return conditions;
	}
	
	/**
	 * A specific getter for the movieEventForm to
	 * return the movie id as an integer
	 *
	 * @return the {@link Booking}'s id as an int
	 */
	public int getIDAsInt() {
		return (int) this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Booking)) {
			return false;
		}
		
		Booking otherBooking = (Booking) obj;
		
		return otherBooking.getId() == this.id;
	}
}