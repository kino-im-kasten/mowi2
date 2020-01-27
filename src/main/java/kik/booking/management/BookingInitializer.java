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

import kik.booking.data.Booking;
import kik.booking.data.BookingForm;
import kik.booking.data.BookingRepository;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A {@link BookingInitializer} to create {@link Booking}s
 *
 * Called from / after {@link BookingManagement} distributed the creation of a {@link Booking}
 *
 * @author Erik Schönherr
 */
@Service
public class BookingInitializer {

	private final MovieRepository movieRepository;
	private final DistributorRepository distributorRepository;
	private final BookingRepository bookingRepository;

	/**
	 * Default constructor of the BookingInitializer.
	 *
	 * @param bookingRepository Repository storing all {@link Booking}s
	 * @param movieRepository Repository storing all {@link Movie}s
	 * @param distributorRepository Repository storing all {@link Distributor}s
	 */
	public BookingInitializer(BookingRepository bookingRepository, MovieRepository movieRepository,
							  DistributorRepository distributorRepository){
		this.bookingRepository = bookingRepository;
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;
	}

	/**
	 * Creates a new {@link Booking} with a given {@link BookingForm} and saves it in the {@link BookingRepository}.
	 *
	 * @param bookingForm form containing all the data relevant for initializing a booking
	 * @return the created booking
	 */
	public Booking initializeBooking(BookingForm bookingForm){
		Booking booking = new Booking();
		fillBooking(booking, bookingForm);

		this.bookingRepository.save(booking);

		return booking;
	}

	/**
	 * Sets every attribute of a {@link Booking} by a given {@link BookingForm}.
	 *
	 * @param booking Booking which will get changed
	 * @param bookingForm BookingForm containing all the data relevant for initializing a booking
	 * @return the created booking
	 */
	public Booking fillBooking(Booking booking, BookingForm bookingForm){
		booking.setTbNumber(bookingForm.getTbNumber());

		Optional<Movie> movie = movieRepository.findById(bookingForm.getMovieID());
		movie.ifPresent(booking::setMovie);

		Optional<Distributor> distributor = distributorRepository.findById(bookingForm.getDistributorID());
		distributor.ifPresent(booking::setDistributor);

		booking.setStartDate(bookingForm.getStartDate());
		booking.setEndDate(bookingForm.getEndDate());

		booking.setConditions(bookingForm.getConditions());

		booking.updateState();

		return booking;
	}


}
