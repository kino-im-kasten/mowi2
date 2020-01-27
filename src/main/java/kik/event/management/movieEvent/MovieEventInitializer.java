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

import java.util.Optional;

import com.mysema.commons.lang.Assert;
import javassist.tools.rmi.ObjectNotFoundException;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.management.DutyPlanManagement;
import org.springframework.stereotype.Service;

import kik.booking.data.Booking;
import kik.booking.data.BookingRepository;

import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventData;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.data.movieEvent.MovieEventRepository;
import kik.event.management.event.EventInitializer;
import kik.movie.data.Movie;

/**
 * A {@link MovieEventInitializer} to create {@link MovieEvent}s
 *
 * Called from / after {@link MovieEventManagement} distributed
 * the creation of an {@link MovieEvent}
 *
 * @author Georg Lauterbach
 * @version 0.0.3
 */
@Service
public class MovieEventInitializer extends EventInitializer {
	
	private BookingRepository bookingRepository;
	private MovieEventRepository movieEventRepository;
	private DutyPlanManagement dutyPlanManagement;
	
	/**
	 * Default constructor of an {@link MovieEventInitializer}
	 *
	 * @param bookingRepository    A repository containing all {@link Booking}s
	 * @param movieEventRepository A repository containing all {@link Movie}s
	 * @param dutyPlanManagement   Management layer to distribute functionality for {@link DutyPlan}s
	 */
	public MovieEventInitializer(BookingRepository bookingRepository,
								 MovieEventRepository movieEventRepository,
								 DutyPlanManagement dutyPlanManagement) {
		Assert.notNull(bookingRepository, "bookingRepository must not be null!" +
				" ~ in event.managent.movieEvent.MovieEventInitializer::MovieEventInitializer");
		this.bookingRepository = bookingRepository;
		this.movieEventRepository = movieEventRepository;
		this.dutyPlanManagement = dutyPlanManagement;
	}
	
	/**
	 * Default factory method to create a new {@link MovieEvent}
	 *
	 * @param movieEventForm Holds the neccessary parameters for the
	 *                       creation of an event, validated by {@link MovieEventValidation}
	 * @return A new {@link MovieEvent} instance
	 * @throws ObjectNotFoundException if no booking can be associated to the given id
	 */
	public MovieEvent initializeMovieEvent(MovieEventForm movieEventForm)
			throws ObjectNotFoundException {
		MovieEventData movieEventData = super.initializeEventData(movieEventForm).toMED();
		
		movieEventData.setEventType(EventType.MOVIE);
		movieEventData.setEventPublicity(movieEventForm.getEventPublicity());
		
		var tmp = getBookingById(movieEventForm.getBookingID());
		if (tmp.isEmpty()) {
			throw new ObjectNotFoundException("There was no Booking found which would" +
					" be associated with the given id : " + movieEventForm.getBookingID());
		}
		movieEventData.setBooking(tmp.get());
		
		movieEventData.setAdditionalName(movieEventForm.getAdditionalName());
		String fullyQualifiedName = movieEventData.getBooking().getMovie().getGermanName();
		if (!movieEventForm.getAdditionalName().isEmpty()) {
			fullyQualifiedName += " - " + movieEventForm.getAdditionalName();
		}
		movieEventData.setFullyQualifiedName(fullyQualifiedName);
		
		super.handleTime(movieEventForm, movieEventData);

		MovieEvent movieEvent = new MovieEvent(movieEventData);

		//add a DutyPlan to the Event
		movieEvent = (MovieEvent) super.handleDutyPlan(movieEvent,movieEventRepository,dutyPlanManagement);
		
		movieEvent.setExpectedEnd(movieEvent
				.getStart()
				.plusMinutes(movieEventData
						.getBooking()
						.getMovie()
						.getRunTimeInMin()));
		
		// * link Event.id into the corresponding movie - resolved when deleting the event
		movieEvent.getBooking().getMovie().addEventId(movieEvent.getId());
		// * link Event into the corresponding booking - resolved when deleting the event
		movieEvent.getBooking().addEvent(movieEvent);

		return movieEvent;
	}
	
	/**
	 * Find a certain {@link Booking} by a supplied id
	 *
	 * @param id a unique id every {@link Booking} has
	 * @return a {@link Optional<Booking>} associated with the event, empty
	 * when there was no {@link Booking} found
	 */
	private Optional<Booking> getBookingById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		
		var tmp = this.bookingRepository.findById(id);
		if (tmp.isEmpty()) {
			return Optional.empty();
		}
		
		return tmp;
	}
	
	/**
	 * Handles the editing of an {@link MovieEvent} and updates it
	 *
	 * @param movieEvent     the {@link MovieEvent} that is to be edited
	 * @param movieEventForm the {@link MovieEventForm} containing the changes
	 * @return the updated {@link MovieEvent}
	 */
	public MovieEvent updateMovieEvent(MovieEvent movieEvent, MovieEventForm movieEventForm) {
		MovieEventData med = new MovieEventData();
		
		var tmp = getBookingById(movieEventForm.getBookingID());
		tmp.ifPresent(movieEvent::setBooking);
		
		movieEvent.setEventPublicity(movieEventForm.getEventPublicity());
		
		movieEvent.setAdditonalName(movieEventForm.getAdditionalName());
		String fullQualifiedName = movieEvent.getBooking().getMovie().getOriginalName();
		if (!movieEventForm.getAdditionalName().isEmpty()) {
			fullQualifiedName += " - " + movieEventForm.getAdditionalName();
		}
		movieEvent.setFullyQualifiedName(fullQualifiedName);
		
		super.handleTime(movieEventForm, med);
		movieEvent.setDate(med.getDate());
		movieEvent.setStart(med.getExpectedEnd());
		movieEvent.setOdt(med.getOdt());
		
		movieEvent.setExpectedEnd(movieEvent
				.getStart()
				.plusMinutes(movieEvent
						.getBooking()
						.getMovie()
						.getRunTimeInMin()));
		
		return movieEvent;
	}
}
