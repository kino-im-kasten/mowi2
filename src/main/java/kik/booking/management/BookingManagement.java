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

package kik.booking.management;

import kik.booking.data.*;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.event.data.EventPlanningStatus;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * An implementation for managing all the business logic regarding {@link Booking}'s.
 *
 * @author Erik Schönherr
 */
@Service
public class BookingManagement {
	private final BookingRepository bookingRepository;
	private final BookingValidation bookingValidation;
	private final BookingFilter bookingFilter;
	private final MovieRepository movieRepository;
	private final DistributorRepository distributorRepository;
	private final BookingInitializer bookingInitializer;

	/**
	 * Default constructor of the BookingManagement
	 *
	 * @param bookingRepository Repository storing all {@link Booking}s
	 * @param movieRepository Repository storing all {@link Movie}s
	 * @param distributorRepository Repository storing all {@link Distributor}s
	 * @param bookingInitializer {@link BookingInitializer} to create and edit bookings
	 */
	public BookingManagement(BookingRepository bookingRepository, MovieRepository movieRepository,
							 DistributorRepository distributorRepository, BookingInitializer bookingInitializer) {
		Assert.notNull(bookingRepository, "bookingRepository must not be null! " +
			"[CLASS::METHOD kik.booking.management.bookingManagement::bookingManagement]");

		this.bookingRepository = bookingRepository;
		this.bookingValidation = new BookingValidation();
		this.bookingFilter = new BookingFilter();
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;
		this.bookingInitializer = bookingInitializer;
	}

	/**
	 * Creates a new {@link Booking} using the information given in the {@link BookingForm} after validating it.
	 *
	 * @param bookingForm object that holds the neccessary parameters for the creation of an booking as a form
	 * @param errors Errors
	 * @return created {@link Booking}
	 */
	public Booking createBooking(BookingForm bookingForm, Errors errors) {
		Assert.notNull(bookingForm, "bookingForm must not be null! " +
			"[CLASS.METHOD kik.booking.management.bookingManagement::createbooking]");

		this.bookingValidation.validate(bookingForm, movieRepository, distributorRepository, errors);

		if(errors.hasErrors()) {
			return null;
		}

		return this.bookingInitializer.initializeBooking(bookingForm);
	}

	/**
	 * Edits the {@link Booking} given by its Id after validating the given {@link BookingForm}.
	 *
	 * @param id Id of the {@link Booking}
	 * @param bookingForm A filled {@link BookingForm} containing all data to update the {@link Booking}
	 * @param errors Errors
	 */
	public void editBooking(long id, BookingForm bookingForm, Errors errors) {
		Booking booking = bookingRepository.findById(id).orElseThrow();

		//if a normal User calls this function the distributorId in the form is set to default (0), which would invoke
		//an error in the validation
		if(bookingForm.getDistributorID() == 0) {
			bookingForm.setDistributorID(booking.getDistributor().getId());
		}

		this.bookingValidation.validate(bookingForm, movieRepository, distributorRepository, errors);

		if(errors.hasErrors()) {
			return;
		}

		bookingRepository.save(bookingInitializer.fillBooking(booking, bookingForm));
	}

	/**
	 * Settles a {@link Booking} up.
	 *
	 * @param id of the booking
	 * @return String that contains the error message when the booking can't get settled up
	 */
	public String settleUp(long id) {
		Booking booking = bookingRepository.findById(id).orElseThrow();

		if(booking.getState() != BookingState.OVER) {
			return "booking.details.error.bookingNotOver";
		}
		if(!booking.allEventsSettledUp()) {
			return "booking.details.error.eventNotFilled";
		}

		booking.setState(BookingState.SETTLEDUP);

		booking.getEvents().forEach(e -> e.setEventPlanningStatus(EventPlanningStatus.SETTLED));

		bookingRepository.save(booking);

		return "";
	}

	/**
	 * Undoes the settling up of a {@link Booking}.
	 *
	 * @param id of the booking
	 */
	public void undoSettleUp(long id) {
		Booking booking = bookingRepository.findById(id).orElseThrow();

		if(booking.getState() != BookingState.SETTLEDUP) {
			return;
		}
		booking.setState(BookingState.OVER);

		booking.getEvents().forEach(e -> e.setEventPlanningStatus(EventPlanningStatus.PRESENTED));

		bookingRepository.save(booking);
	}

	/**
	 * Cancels the {@link Booking} given by its Id.
	 *
	 * @param id Id of the {@link Booking} that will be canceled
	 * @param request {@link HttpServletRequest} to check for the user role
	 * @return String that contains the error message when the booking can't get canceled
	 */
	public String cancelBooking(long id, HttpServletRequest request) {
		Booking booking = bookingRepository.findById(id).orElseThrow();

		if(booking.isArchived()) {
			return "booking.details.error.bookingArchived";
		}

		// When the booking has events and gets canceled, all related events get canceled as well, so only users with
		// the role 'Orga' should be allowed to cancel this booking.
		if(!booking.getEvents().isEmpty() && !request.isUserInRole("ORGA")) {
			return "booking.details.error.notOrga";
		}

		booking.setState(BookingState.CANCELED);

		booking.getEvents().forEach(e -> e.setEventPlanningStatus(EventPlanningStatus.CANCELED));

		bookingRepository.save(booking);

		return "";
	}

	/**
	 * Undos the cancellation of a {@link Booking} given by its Id.
	 *
	 * @param id Id of the {@link Booking} that will get activated back
	 */
	public void undoCancelBooking(long id) {
		Booking booking = bookingRepository.findById(id).orElseThrow();

		if(!booking.isCanceled()) {
			return;
		}

		booking.setState(BookingState.PENDING);
		booking.updateState();

		booking.getEvents().forEach(e -> {
			if(e.getOdt().compareTo(OffsetDateTime.now()) > 0) {
				e.setEventPlanningStatus(EventPlanningStatus.BOOKED);
			} else {
				e.setEventPlanningStatus(EventPlanningStatus.PRESENTED);
			}
		});

		bookingRepository.save(booking);
	}

	/**
	 * Creates a new {@link BookingForm} based of a {@link Booking}.
	 *
	 * @param booking a {@link Booking} holding the information to be filled in the form
	 * @return new {@link BookingForm}
	 */
	public BookingForm fillBookingForm(Booking booking) {
		Assert.notNull(booking, "booking must not be null!");

		return new BookingForm(
			booking.getTbNumber(),
			booking.getMovie().getId(),
			booking.getDistributor().getId(),
			booking.getStartDate(),
			booking.getEndDate(),
			booking.getConditions());
	}

	/**
	 * Updates the state of all given {@link Booking}s every day at midnight.
	 */
	@Scheduled(cron="0 0 0 ? * *")
	public void updateBookingStates() {
		for(Booking booking : bookingRepository.findAll()){
			booking.updateState();
			bookingRepository.save(booking);
		}
	}

	/**
	 * Returns all {@link Booking}s currently available in the system.
	 *
	 * @return all {@link Booking} entities
	 */
	public Streamable<Booking> getAllBookings() {
		return bookingRepository.findAll();
	}

	/**
	 * Returns all {@link Booking}s currently available in the system sorted by the given parameters.
	 *
	 * @param sort attribute to be sorted by
	 * @param asc ascending or descending
	 * @return all {@link Booking} entities in a sorted {@link Streamable}
	 */
	public Streamable<Booking> getAllBookings(String sort, Boolean asc) {
		return bookingRepository.findAll(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
	}

	/**
	 * Returns all {@link Booking}s that aren't archived.
	 *
	 * @return all {@link Booking} entities where archived=false
	 */
	public Streamable<Booking> getAllNotArchivedBookings() {
		return bookingRepository.findBookingsByArchivedFalse();
	}

	/**
	 * Returns all {@link Booking}s that aren't archived sorted by the given parameters.
	 *
	 * @param sort attribute to be sorted by
	 * @param asc ascending or descending
	 * @return all {@link Booking} entities in a sorted {@link Streamable} where archived=false
	 */
	public Streamable<Booking> getAllNotArchivedBookings(String sort, Boolean asc) {
		return bookingRepository.findBookingsByArchivedFalse(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
	}

	/**
	 * Returns all {@link Booking}s currently available in the system sorted by the given parameters.
	 *
	 * @param input String to filter by
	 * @return all filtered {@link Booking} entities in a {@link Streamable}
	 */
	public Streamable<Booking> getFilteredBookings(String input) {
		return bookingFilter.filter(bookingRepository.findAll(), input);
	}

	/**
	 * Returns all {@link Booking}s that aren't archived filtered by a given string.
	 *
	 * @param input String to filter by
	 * @return all filtered {@link Booking} entities in a {@link Streamable}
	 */
	public Streamable<Booking> getAllNotArchivedFilteredBookings(String input) {
		return bookingFilter.filter(bookingRepository.findBookingsByArchivedFalse(), input);
	}

	/**
	 * Gets a Booking by an id.
	 *
	 * @param id the id
	 * @return an {@link Optional}  containing the Booking with the given id in case there is one
	 */
	public Optional<Booking> getById(long id) {
		return bookingRepository.findById(id);
	}

	/**
	 * Gets all movies.
	 *
	 * @return a {@link Streamable} with all saved movies
	 */
	public Streamable<Movie> getAllMovies(){
		return movieRepository.findAll();
	}

	/**
	 * Gets all movies.
	 *
	 * @return a {@link Streamable} with all saved distributors
	 */
	public Streamable<Distributor> getAllDistributors(){
		return Streamable.of(distributorRepository.findAll());
	}

	/**
	 * Gets a Booking by a tb number.
	 *
	 * @param tbNumber the tb number
	 * @return an {@link Optional}  containing the Booking with the given tb number in case there is one
	 */
	public Optional<Booking> findByTbNumber(String tbNumber) {
		return bookingRepository.findByTbNumber(tbNumber);
	}
}