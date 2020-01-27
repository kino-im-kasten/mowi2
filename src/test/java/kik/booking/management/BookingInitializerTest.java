package kik.booking.management;

import kik.booking.data.*;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class BookingInitializerTest {
	private BookingInitializer initializer;
	private BookingRepository bookingRepository;
	private MovieRepository movieRepository;
	private DistributorRepository distributorRepository;

	private long movieId1;
	private long movieId2;

	private long distributorId1;
	private long distributorId2;

	private Conditions conditions1;
	private Conditions conditions2;

	private boolean initialized;

	@Autowired
	BookingInitializerTest(BookingInitializer bookingInitializer, BookingRepository bookingRepository,
						   MovieRepository movieRepository, DistributorRepository distributorRepository){
		this.initializer = bookingInitializer;
		this.bookingRepository = bookingRepository;
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;

		this.initialized = false;
	}

	@BeforeEach
	void setup(){
		if(!initialized) {
			initialized = true;

			Movie movie1 = new Movie("a", "b", "c", "d", "e", 1,"-", 1);
			Movie movie2 = new Movie("a", "b", "c", "d", "e", 1,"-", 1);


			this.movieRepository.save(movie1);
			this.movieRepository.save(movie2);

			this.movieId1 = movie1.getId();
			this.movieId2 = movie2.getId();

			Distributor distributor1 = new Distributor();
			Distributor distributor2 = new Distributor();

			this.distributorRepository.save(distributor1);
			this.distributorRepository.save(distributor2);

			this.distributorId1 = distributor1.getId();
			this.distributorId2 = distributor2.getId();

			conditions1 = new Conditions(20, 40);
			conditions2 = new Conditions(5, 10);
		}
	}

	@Test
	void uBookingInitializerFillBookingP1(){
		Booking booking = new Booking();
		LocalDate startDate = LocalDate.now().plusDays(7);
		LocalDate endDate = LocalDate.now().plusDays(14);
		BookingForm bf = new BookingForm("12345678", movieId1, distributorId1, startDate, endDate, conditions1);

		initializer.fillBooking(booking, bf);

		assertTrue(bookingRepository.findById(booking.getId()).isEmpty());
		assertEquals("12345678", booking.getTbNumber());
		assertEquals(movieId1, booking.getMovie().getId());
		assertEquals(distributorId1,(long) booking.getDistributor().getId());
		assertEquals(startDate, booking.getStartDate());
		assertEquals(endDate, booking.getEndDate());
		assertEquals(conditions1, booking.getConditions());

		assertEquals(BookingState.PENDING, booking.getState());
	}

	@Test
	void uBookingInitializerFillBookingP2(){
		LocalDate startDate2 = LocalDate.now().minusDays(3);
		LocalDate endDate2 = LocalDate.now().plusDays(4);

		Booking booking = new Booking("87654321", movieRepository.findById(movieId2).orElseThrow(),
			distributorRepository.findById(distributorId2).orElseThrow(), startDate2, endDate2, conditions2);

		LocalDate startDate = LocalDate.now().plusDays(7);
		LocalDate endDate = LocalDate.now().plusDays(14);
		BookingForm bf = new BookingForm("12345678", movieId1, distributorId1, startDate, endDate, conditions1);

		initializer.fillBooking(booking, bf);

		assertTrue(bookingRepository.findById(booking.getId()).isEmpty());
		assertEquals("12345678", booking.getTbNumber());
		assertEquals(movieId1, booking.getMovie().getId());
		assertEquals(distributorId1,(long) booking.getDistributor().getId());
		assertEquals(startDate, booking.getStartDate());
		assertEquals(endDate, booking.getEndDate());
		assertEquals(conditions1, booking.getConditions());

		assertEquals(BookingState.PENDING, booking.getState());
	}

	@Test
	void uBookingInitializerInitializeBookingP(){
		LocalDate startDate = LocalDate.now().minusDays(3);
		LocalDate endDate = LocalDate.now().plusDays(4);
		BookingForm bf = new BookingForm("12345678", movieId1, distributorId1, startDate, endDate, conditions1);

		Booking booking = initializer.initializeBooking(bf);

		assertFalse(bookingRepository.findById(booking.getId()).isEmpty());
		assertEquals("12345678", booking.getTbNumber());
		assertEquals(movieId1, booking.getMovie().getId());
		assertEquals(distributorId1,(long) booking.getDistributor().getId());
		assertEquals(startDate, booking.getStartDate());
		assertEquals(endDate, booking.getEndDate());
		assertEquals(conditions1, booking.getConditions());

		assertEquals(BookingState.ACTIVE, booking.getState());
	}
}
