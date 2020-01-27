package kik.booking.management;

import kik.booking.data.*;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.event.data.EventPlanningStatus;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.data.movieEvent.MovieEventRepository;
import kik.event.data.movieEvent.tickets.Tickets;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingManagementTest {
	private BookingManagement bookingManagement;
	private BookingRepository bookingRepository;
	private MovieRepository movieRepository;
	private DistributorRepository distributorRepository;
	private MovieEventManagement movieEventManagement;
	private MovieEventRepository movieEventRepository;

	private long movieId1;
	private long movieId2;
	private long distributorId1;
	private long distributorId2;

	private Conditions conditions1;
	private Conditions conditions2;

	private Booking booking;
	private long bookingId;
	private BookingForm bookingForm;

	private boolean initialized;

	@Autowired
	BookingManagementTest(BookingManagement bookingManagement, BookingRepository bookingRepository, MovieRepository movieRepository,
						  DistributorRepository distributorRepository, MovieEventManagement movieEventManagement,
						  MovieEventRepository movieEventRepository){
		this.bookingManagement = bookingManagement;
		this.bookingRepository = bookingRepository;
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;
		this.movieEventManagement = movieEventManagement;
		this.movieEventRepository = movieEventRepository;

		initialized = false;
	}

	@BeforeEach
	void setUp(){
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

			this.conditions1 = new Conditions(20, 40);

			this.conditions2 = new Conditions(5, 10);

			this.booking = new Booking(
				"11235813",
				movieRepository.findById(movieId1).orElseThrow(),
				distributorRepository.findById(distributorId1).orElseThrow(),
				LocalDate.now().minusDays(14),
				LocalDate.now().minusDays(7),
				conditions1);

			this.bookingRepository.save(booking);
			this.bookingId = booking.getId();

			Booking booking2 = new Booking(
				"32235813",
				movieRepository.findById(movieId2).orElseThrow(),
				distributorRepository.findById(distributorId2).orElseThrow(),
				LocalDate.now().minusDays(14),
				LocalDate.now().minusDays(7),
				conditions1);

			booking2.setState(BookingState.SETTLEDUP);
			this.bookingRepository.save(booking2);
		}
	}

	@Test
	void uBookingManagementCreateBookingP(){
		bookingForm = new BookingForm("12345678", movieId2, distributorId2,
			LocalDate.now().plusDays(7), LocalDate.now().plusDays(14), conditions2);

		Errors errors = new BeanPropertyBindingResult(bookingForm, "Booking Form");

		Booking booking = bookingManagement.createBooking(bookingForm, errors);
		long bookingID = booking.getId();

		assertTrue(bookingRepository.findById(bookingID).isPresent());

		errors.rejectValue("tbNumber", "example");
		assertNull( bookingManagement.createBooking(bookingForm, errors));
	}

	@Test
	void uBookingManagementEditBookingP(){
		bookingForm = new BookingForm("12345678", movieId2, distributorId2,
			LocalDate.now().plusDays(7), LocalDate.now().plusDays(14), conditions2);

		Errors errors = new BeanPropertyBindingResult(bookingForm, "Booking Form");
		bookingManagement.editBooking(bookingId, bookingForm, errors);

		assertTrue(bookingRepository.findById(bookingId).isPresent());

		booking = bookingRepository.findById(bookingId).get();

		assertEquals("12345678", booking.getTbNumber());
		assertEquals(movieRepository.findById(movieId2).orElseThrow().getId(), booking.getMovie().getId());
		assertEquals(distributorRepository.findById(distributorId2).orElseThrow().getId(), booking.getDistributor().getId());
		assertEquals(LocalDate.now().plusDays(7), booking.getStartDate());
		assertEquals(LocalDate.now().plusDays(14), booking.getEndDate());

		bookingForm.setTbNumber("1234");
		bookingForm.setDistributorID(0);
		bookingForm.setMovieID(-24);
		bookingForm.setStartDate(LocalDate.now());
		bookingForm.setEndDate(LocalDate.now().minusDays(7));
		bookingForm.setConditions(new Conditions(-3, -3, -3, -3, -3, -3));

		bookingManagement.editBooking(bookingId, bookingForm, errors);

		assertEquals("12345678", booking.getTbNumber());
		assertEquals(movieRepository.findById(movieId2).orElseThrow().getId(), booking.getMovie().getId());
		assertEquals(distributorRepository.findById(distributorId2).orElseThrow().getId(), booking.getDistributor().getId());
		assertEquals(LocalDate.now().plusDays(7), booking.getStartDate());
		assertEquals(LocalDate.now().plusDays(14), booking.getEndDate());
	}

	@Test
	void uBookingManagementSettleUpP(){
		MovieEvent event = this.movieEventManagement
			.createAndSaveMovieEvent(
				new MovieEventForm(
					"Batman trilogy pt. 2",
					this.booking.getStartDate().plusDays((long) (Math.random() * 10000) % 3 + 1),
					"20:30",
					String.valueOf(this.booking.getId()),
					"PRIVATE"),
				null);
		Tickets tickets = new Tickets();
		tickets.setCardNormalStartNumber(125);
		tickets.setCardNormalEndNumber(128);
		tickets.setCardReducedStartNumber(125);
		tickets.setCardReducedEndNumber(125);
		tickets.setCardFreeAmount(5);

		event.setTickets(tickets);

		assertNotEquals(EventPlanningStatus.SETTLED, movieEventRepository.findById(event.getId()).orElseThrow().getEventPlanningStatus());

		bookingManagement.settleUp(bookingId);

		assertEquals(EventPlanningStatus.SETTLED, movieEventRepository.findById(event.getId()).orElseThrow().getEventPlanningStatus());

		assertEquals(BookingState.SETTLEDUP, bookingRepository.findById(bookingId).orElseThrow().getState());
	}


	@Test
	void uBookingManagementCancelBookingP(){
		MockHttpServletRequest request = new MockHttpServletRequest();

		bookingManagement.cancelBooking(bookingId, request);

		assertTrue(bookingRepository.findById(bookingId).orElseThrow().isCanceled());
	}

	@Test
	void uBookingManagementUndoCancelBookingP(){
		booking.setState(BookingState.CANCELED);
		bookingRepository.save(booking);

		bookingManagement.undoCancelBooking(bookingId);

		assertFalse(bookingRepository.findById(bookingId).orElseThrow().isCanceled());
		assertEquals(BookingState.OVER, bookingRepository.findById(bookingId).orElseThrow().getState());
	}


	@Test
	void uBookingManagementFillBookingFormN(){
		assertThrows(IllegalArgumentException.class, () -> bookingManagement.fillBookingForm(null));
	}

	@Test
	void uBookingManagementUpdateBookingStatesP(){
		booking.setState(BookingState.ACTIVE);
		bookingRepository.save(booking);

		assertEquals(BookingState.ACTIVE, bookingRepository.findById(bookingId).orElseThrow().getState());

		bookingManagement.updateBookingStates();

		assertEquals(BookingState.OVER, bookingRepository.findById(bookingId).orElseThrow().getState());
	}

	@Test
	void uBookingManagementGetAllBookingsP(){
		assertEquals(bookingRepository.findAll().toList(), bookingManagement.getAllBookings().toList());
	}

	@Test
	void uBookingManagementGetAllBookingsSortedP(){
		String sort = "tbNumber";

		assertEquals(bookingRepository.findAll(Sort.by(Sort.Direction.ASC, sort)).toList(),
			bookingManagement.getAllBookings(sort, true).toList());
	}

	@Test
	void uBookingManagementGetByIdP(){
		assertEquals(bookingRepository.findById(booking.getId()), bookingManagement.getById(booking.getId()));
	}

	@Test
	void uBookingManagementGetAllNotArchivedBookingsP(){
		Streamable<Booking> notArchivedBookings = bookingManagement.getAllNotArchivedBookings();

		assertEquals(bookingRepository.findAll().filter(b -> !b.isArchived()).toList(), notArchivedBookings.toList());

		notArchivedBookings.forEach(b -> assertFalse(b.isArchived()));
	}

	@Test
	void uBookingManagementGetAllNotArchivedBookingsSortedP(){
		String sort = "tbNumber";

		Streamable<Booking> notArchivedBookings = bookingManagement.getAllNotArchivedBookings(sort, true);


		assertEquals(bookingRepository.findAll(Sort.by(Sort.Direction.ASC, sort)).filter(b -> !b.isArchived()).toList(),
			notArchivedBookings.toList());

		notArchivedBookings.forEach(b -> assertFalse(b.isArchived()));
	}
}
