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
package kik.event.management.movieEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

import com.mysema.commons.lang.Assert;
import javassist.tools.rmi.ObjectNotFoundException;

import kik.booking.data.Booking;

import kik.booking.management.BookingManagement;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.management.DutyPlanManagement;

import kik.event.controller.movieEvent.MovieEventGetController;
import kik.event.controller.movieEvent.MovieEventPostController;

import kik.event.data.EventPlanningStatus;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEventRepository;
import kik.event.data.event.EventRepository;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.data.movieEvent.tickets.Tickets;
import kik.event.data.movieEvent.tickets.TicketsForm;

import kik.event.management.EventFilter;

import kik.event.management.event.EventManagement;
import kik.user.data.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Management layer for all logic involving {@link MovieEvent}s
 * <p>
 * Manages requests from {@link MovieEventGetController}
 * or {@link MovieEventPostController} and distributes this
 * information to the correct initializers. Calls validation and
 * serves as a getter-object to get all repositories.
 *
 * @author Georg Lauterbach
 * @version 0.3.5
 */
@Service
@Qualifier("movieEventManagement")
public class MovieEventManagement extends EventManagement {
	
	private static final Logger LOG = LoggerFactory.getLogger(MovieEventManagement.class);
	private EventFilter eventFilter = new EventFilter();
	
	private final BookingManagement bookingManagement;
	private final MovieEventRepository movieEventRepository;
	private final MovieEventInitializer movieEventInitializer;
	private final MovieEventValidation movieEventValidation;
	private final DutyPlanManagement dutyPlanManagement;
	
	/**
	 * The default constructor of an {@link MovieEventManagement}
	 *
	 * @param movieEventRepository  A repository containing all {@link MovieEvent}s
	 * @param movieEventInitializer Factory class of a {@link MovieEvent}
	 * @param movieEventValidation  A {@link Validator} to validate {@link MovieEvent}s
	 * @param eventManagement       Management layer for all {@link Event}s
	 * @param bookingManagement     Management layer for all {@link Booking}s
	 * @param dutyPlanManagement    Management layer for all {@link DutyPlan}s
	 */
	MovieEventManagement(MovieEventRepository movieEventRepository,
						 MovieEventInitializer movieEventInitializer,
						 MovieEventValidation movieEventValidation,
						 EventManagement eventManagement,
						 BookingManagement bookingManagement,
						 DutyPlanManagement dutyPlanManagement) {
		super(eventManagement.getEventRepository());
		
		Assert.notNull(movieEventRepository, "movieEventRepository must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::MovieEventManagement");
		this.movieEventRepository = movieEventRepository;
		
		Assert.notNull(movieEventInitializer, "movieEventInitializer must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::MovieEventManagement");
		this.movieEventInitializer = movieEventInitializer;
		
		Assert.notNull(bookingManagement, "bookingRepository must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::MovieEventManagement");
		this.bookingManagement = bookingManagement;
		
		Assert.notNull(movieEventValidation, "movieEventValidation must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::MovieEventManagement");
		this.movieEventValidation = movieEventValidation;
		
		Assert.notNull(dutyPlanManagement, "dutyPlanManagement must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::MovieEventManagement");
		this.dutyPlanManagement = dutyPlanManagement;
	}
	
	/**
	 * Creates a new {@link MovieEvent} using the information given
	 * in the {@link MovieEventForm} and saves it
	 *
	 * @param movieEventForm Holds the neccessary parameters for the creation of an {@link MovieEvent}
	 * @param errors         to propagate errors throughout the methods
	 * @return the newly created {@link MovieEvent}
	 */
	public MovieEvent createAndSaveMovieEvent(MovieEventForm movieEventForm, Errors errors) {
		Assert.notNull(movieEventForm, "movieEventForm must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::createMovieEvent");
		
		this.movieEventValidation.validate(movieEventForm, errors);
		if (errors != null && errors.hasErrors()) {
			return null;
		}
		
		MovieEvent movieEvent;
		try {
			movieEvent = this.movieEventInitializer.initializeMovieEvent(movieEventForm);
		} catch (ObjectNotFoundException e) {
			errors.rejectValue("bookingID", "The given booking IDis nowhere to be found!");
			return null;
		}
		
		this.movieEventRepository.save(movieEvent);
		super.saveMovieEvent(movieEvent);
		
		return movieEvent;
	}
	
	/**
	 * Evaluates changes to an {@link MovieEvent}, (validates) and saves them
	 *
	 * @param movieEventForm the input from the user-iput, validated
	 * @param id             the id of the event that belongs to the form
	 * @param errors         to propagate errors throughout the methods
	 */
	public void editMovieEvent(MovieEventForm movieEventForm, long id, Errors errors) {
		Optional<MovieEvent> tmp = findMovieEventById(id);
		if (tmp.isEmpty()) {
			return;
		}
		var movieEvent = tmp.get();
		
		this.movieEventValidation.validate(movieEventForm, errors);
		if (errors.hasErrors()) {
			return;
		}
		
		this.movieEventRepository
				.save(this.movieEventInitializer
						.updateMovieEvent(movieEvent, movieEventForm));
	}
	
	
	/**
	 * Update duty plan.
	 *
	 * @param dutyPlan the duty plan
	 * @param eventId  the event id
	 */
	@SuppressWarnings("unused")
	public void updateDutyPlan(DutyPlan dutyPlan, long eventId) {
		if (movieEventRepository.findById(eventId).isEmpty()) {
			throw new IllegalArgumentException("eventId could not be located!");
		}
		
		movieEventRepository.findById(eventId).get().setDutyPlan(dutyPlan);
	}
	
	/**
	 * Returns all {@link MovieEvent}s currently available in the system.
	 *
	 * @return all {@link MovieEvent} entities.
	 */
	public Streamable<MovieEvent> findAll() {
		return this.movieEventRepository.findAll();
	}
	
	/**
	 * Creates a {@link MovieEventForm} out of a {@link MovieEvent}
	 * <p>
	 * This is necessary when a user wants to edit a {@link MovieEvent}
	 * as the form has to be injected into the model.
	 *
	 * @param movieEvent the {@link MovieEvent} which is to be edited
	 * @return a new {@link MovieEventForm} ready for injection
	 */
	public MovieEventForm fillMovieEventForm(MovieEvent movieEvent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		
		return new MovieEventForm(
				movieEvent.getAdditonalName(),
				movieEvent.getDate(),
				movieEvent.getStart().format(formatter),
				String.valueOf(movieEvent.getBooking().getId()),
				movieEvent.getEventPublicity().name()
		);
	}
	
	/**
	 * Deletes the {@link MovieEvent} associated by the id from the
	 * {@link MovieEventRepository} and from the {@link EventRepository}
	 *
	 * @param id id associated with the event that is to be deleted
	 * @return true when no errors occured, false otherwise
	 */
	public boolean deleteMovieEvent(long id) {
		Optional<MovieEvent> tmp = findMovieEventById(id);
		if (tmp.isEmpty()) {
			LOG.warn("The movieEvents with id : " + id + " that was supposed to be" +
					" deleted was not found.");
			return false;
		}
		MovieEvent movieEvent = tmp.get();
		
		// ? referential integrity violations need to be resolved
		Long dpId = movieEvent.getDutyPlan().getId();
		dutyPlanManagement.clearDutyPlan(movieEvent.getDutyPlan());
		movieEvent.setDutyPlan(null);
		this.dutyPlanManagement.deleteDutyPlan(dpId);

		movieEvent.getBooking().getMovie().removeEventId(id);
		movieEvent.getBooking().removeEvent(movieEvent);
		getMovieEventRepository().save(movieEvent);
		getMovieEventRepository().deleteById(id);
		super.deleteMovieEvent(id);
		return true;
	}
	
	/**
	 * Tries to find a {@link MovieEvent} for the given id
	 * If none was found, an empty {@link Optional} is retuned
	 *
	 * @param id id for which we try to lookup an {@link MovieEvent}
	 * @return an Optional with an {@link MovieEvent} if present, otherwise as empty {@link Optional}
	 */
	public Optional<MovieEvent> findMovieEventById(long id) {
		return this.movieEventRepository
				.findById(id);
	}
	
	/**
	 * Tries to generate {@link Tickets} for the {@link MovieEvent}
	 * which belongs to the supplied id
	 *
	 * @param movieEvent  {@link MovieEvent} whose tickets are supposed to be edited
	 * @param ticketsForm user-input for the generation of {@link Tickets}
	 * @param errors      to distribute errors between methods
	 */
	public void editTicketNumbers(MovieEvent movieEvent, TicketsForm ticketsForm, Errors errors) {
		this.movieEventValidation.validate(ticketsForm, errors);
		
		if (errors.hasErrors()) {
			return;
		}
		
		movieEvent.getTickets().update(ticketsForm);
		this.movieEventRepository.save(movieEvent);
	}
	
	/**
	 * Used to create a filled TicketsForm to be edited later
	 *
	 * @param movieEvent the associated {@link MovieEvent} whose {@link Tickets} shall be edited
	 * @return a filled {@link TicketsForm}
	 */
	public TicketsForm fillTicketsForm(MovieEvent movieEvent) {
		return movieEvent.getTickets().intoTicketsForm();
	}
	
	/**
	 * Calls filter logic to filter all {@link MovieEvent}s by a
	 * given {@link String} in "asc" order
	 *
	 * @param input search {@link String}
	 * @return all {@link MovieEvent}s that match the filter {@link String}
	 */
	public Streamable<MovieEvent> getFilteredMovieEvents(String input) {
		return this.eventFilter
				.filterMovieEvents(this.movieEventRepository.findAll(), input);
	}
	
	/**
	 * For HTTP::GET in / to display only important {@link MovieEvent}s,
	 * i.e. those, which are not already settled up
	 *
	 * @param sort the sort
	 * @param asc  the asc
	 * @return only those {@link MovieEvent}s, whcih are not settled up
	 */
	public List<MovieEvent> getMovieEventsForIndex(String sort, Boolean asc) {
		int listLen = 15;
		List<MovieEvent> suitedEvents = new ArrayList<>(listLen);
		int i = 0;
		
		for (MovieEvent movieEvent : this.movieEventRepository
				.findAll(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort))) {
			if (movieEvent.isNotOld() || (movieEvent.isNotSettled() && movieEvent.ticketsAreNotSet())) {
				suitedEvents.add(movieEvent);
				++i;
				
				if (i == listLen) {
					break;
				}
			}
		}
		return suitedEvents;
	}
	
	/**
	 * Sorts all {@link MovieEvent}s according to the given "sort"
	 *
	 * @param sort attribute to be sorted by
	 * @param asc  ordering
	 * @return all {@link MovieEvent}s, but sorted
	 */
	public Streamable<MovieEvent> getMovieEventsSorted(String sort, Boolean asc) {
		return this.movieEventRepository.findAll(Sort
				.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
	}
	
	
	/**
	 * Gets booking management.
	 *
	 * @return the booking management
	 */
	public BookingManagement getBookingManagement() {
		return this.bookingManagement;
	}
	
	/**
	 * Gets movie event repository.
	 *
	 * @return the movie event repository
	 */
	public MovieEventRepository getMovieEventRepository() {
		return this.movieEventRepository;
	}
	
	/**
	 * Shall update all {@link MovieEvent}s, check for status-updates, etc.
	 */
	@Scheduled(cron = "0 0 1 * * ?")
//	@Scheduled(cron = "0 */1 * * * ?")
	public void updateMovieEvents() {
		checkForDueDate();
	}
	
	/**
	 * A scheduled method to check whether a {@link MovieEvent}'s
	 * date has already gone by ( + 1 day).
	 */
	private void checkForDueDate() {
		LocalDate localDatePlusOffset = LocalDate.now().minusDays(2);
		
		for (MovieEvent movieEvent: findAll()) {
			if (movieEvent.getDate().isBefore(localDatePlusOffset)) {
				movieEvent.setIsOld(true);
				this.movieEventRepository.save(movieEvent);
			}
		}
	}
}
