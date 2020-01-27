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

import com.mysema.commons.lang.Assert;
import javassist.tools.rmi.ObjectNotFoundException;
import kik.booking.data.Booking;
import kik.booking.data.BookingRepository;
import kik.event.data.movieEvent.MovieEventForm;

import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.tickets.TicketsForm;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A validator class to validate {@link MovieEvent}s
 * and {@link MovieEventForm}s
 *
 * @author Georg Lauterbach
 * @version 0.0.4
 */
@Service
public class MovieEventValidation implements Validator {
	
	/**
	 * The Booking repository.
	 */
	BookingRepository bookingRepository;
	
	/**
	 * Instantiates a new Movie event validation.
	 *
	 * @param bookingRepository the booking repository
	 */
	MovieEventValidation(BookingRepository bookingRepository) {
		Assert.notNull(bookingRepository, "bookingRepository must not be null!" +
				"~ in event.movieEvent.MovieEventManagement::MovieEventManagement");
		this.bookingRepository = bookingRepository;
	}
	
	@Override
	public boolean supports(@NonNull Class<?> aClass) {
		throw new UnsupportedOperationException("This method is not supported!");
	}
	
	/**
	 * Distributor method for various checks
	 *
	 * @param object to be checked
	 * @param errors to propagate errors throughout methods
	 */
	@Override
	public void validate(@NonNull Object object, @NonNull Errors errors) {
		if (object instanceof MovieEventForm) {
			try {
				validateMovieEventForm((MovieEventForm) object, errors);
			} catch (ObjectNotFoundException e) {
				errors.rejectValue("bookingID", "booking could not be found");
			}
		} else if (object instanceof TicketsForm) {
			validateTicketsForm((TicketsForm) object, errors);
		}
	}
	
	/**
	 * Validates the given {@link MovieEventForm} against check-parameters,
	 * such as a check for the chosen date against the date set in the booking
	 *
	 * @param movieEventForm user-input to be validated
	 * @param errors         to propagate errors throughout methods
	 * @throws ObjectNotFoundException when no booking could be located
	 */
	private void validateMovieEventForm(MovieEventForm movieEventForm, Errors errors)
			throws ObjectNotFoundException {
		if (errors == null) {
			return;
		}
		
		var tmp = this.bookingRepository.findById(movieEventForm.getBookingID());
		if (tmp.isEmpty()) {
			throw new ObjectNotFoundException("During validation of a new movieEvent, no associated booking was found!");
		}
		Booking booking = tmp.get();
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		
		// ? get the chosen odt
		OffsetTime odtStart = LocalTime.parse("12:00", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime chosenDate = odtStart.atDate(movieEventForm.getDate());
		
		// ? create the booking's correct start and end odts
		OffsetTime bookingStartTime = LocalTime.parse("00:01", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime bookingStartOdt = bookingStartTime.atDate(booking.getStartDate());
		OffsetTime bookingEndTime = LocalTime.parse("23:59", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime bookingEndOdt = bookingEndTime.atDate(booking.getEndDate());
		
		if (!(chosenDate.isAfter(bookingStartOdt) && chosenDate.isBefore(bookingEndOdt))) {
			errors.rejectValue("date", "The given date does not match the booking's date");
		}
	}
	
	/**
	 * Validates a given {@link TicketsForm}
	 *
	 * @param ticketsForm the form to be validated
	 * @param errors      to propagate errors throughout methods
	 */
	private void validateTicketsForm(TicketsForm ticketsForm, Errors errors) {
		if (ticketsForm.getCardNormalStartNumber() > ticketsForm.getCardNormalEndNumber()) {
			errors.rejectValue("cardNormalEndNumber", "CardNormalEndNumber too low!");
		}
		
		if (ticketsForm.getCardReducedStartNumber() > ticketsForm.getCardReducedEndNumber()) {
			errors.rejectValue("cardReducedEndNumber", "CardReducedEndNumber too low!");
		}
	}
}
