package kik.booking.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import kik.booking.data.*;
import kik.booking.management.BookingManagement;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class BookingGetControllerIntegrationTest {
	private BookingGetController getController;
	private BookingRepository bookingRepository;
	private BookingManagement bookingManagement;

	private MovieRepository movieRepository;
	private DistributorRepository distributorRepository;

	@Autowired
	MockMvc mvc;

	private long bookingId;
	private Booking booking;

	private Model model;
	private RedirectAttributes redirectAttributes;

	private boolean initialized;

	@Autowired
	BookingGetControllerIntegrationTest(BookingGetController getController,
										BookingRepository bookingRepository,
										MovieRepository movieRepository,
										DistributorRepository distributorRepository,
										BookingManagement bookingManagement){
		this.getController = getController;
		this.bookingRepository = bookingRepository;
		this.bookingManagement = bookingManagement;
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;

		this.initialized = false;
	}

	@BeforeEach
	void setup(){
		if(!initialized) {
			initialized = true;

			Movie m1 = new Movie("Blade Runner 2048", "Blade Runner 2048", "", "", "",
				0,"-", 0);
			Distributor d1 = new Distributor("Warner Bros", "Dresden", "12345678", "");
			Conditions c1 = new Conditions(5, 5);

			movieRepository.save(m1);
			distributorRepository.save(d1);

			booking = new Booking("12345678", m1, d1, LocalDate.now().plusDays(7),
				LocalDate.now().plusDays(14), c1);
			this.bookingRepository.save(booking);
			bookingId = booking.getId();

			model = new ExtendedModelMap();
			redirectAttributes = new RedirectAttributesModelMap();
		}
	}


	@Test
	@WithMockUser
	@SuppressWarnings("unchecked")
	void iBookingGetControllerShowBookingsP(){
		String returnedView = getController.
			showBookings(model, "movie.originalName", false, null, null);

		assertThat(returnedView).isEqualTo("booking/bookings");

		Streamable<Booking> bookings = (Streamable<Booking>) model.asMap().get("bookings");

		assertFalse(bookings.isEmpty());
	}


	@Test
	@WithMockUser
	@SuppressWarnings("unchecked")
	void iBookingGetControllerShowCurrentBookingsP(){
		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
		BookingForm bookingForm = bookingManagement.fillBookingForm(booking);
		Booking booking2 = bookingManagement.createBooking(bookingForm,
			new BeanPropertyBindingResult(bookingForm, "bookingForm"));

		bookingRepository.save(booking2);

		booking.setState(BookingState.SETTLEDUP);
		bookingRepository.save(booking);

		String returnedView = getController
			.showCurrentBookings(model, "movie.originalName", false, null, null);

		assertThat(returnedView).isEqualTo("booking/current-bookings");

		Streamable<Booking> bookings = (Streamable<Booking>) model.asMap().get("bookings");

		assertFalse(bookings.isEmpty());

		Streamable<Long> ids = bookings.map(Booking::getId);

		assertFalse(ids.toList().contains(bookingId));
	}

	@Test
	@WithMockUser
	void iBookingGetControllerShowBookingDetailsP() {
		String returnedView = getController.showBookingDetails(model, bookingId);

		assertThat(returnedView).isEqualTo("booking/booking-details");

		Booking booking = (Booking) model.asMap().get("booking");

		assertThat(booking.getId()).isEqualTo(bookingId);
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	@SuppressWarnings("unchecked")
	void iBookingGetControllerCreateBookingP() {
		String returnedView = getController.createBooking(model);

		assertThat(returnedView).isEqualTo("booking/createBooking");

		BookingForm bf = (BookingForm) model.asMap().get("bookingForm");
		Streamable<Movie> movies = (Streamable<Movie>) model.asMap().get("movies");
		Streamable<Distributor> distributors = (Streamable<Distributor>) model.asMap().get("distributors");

		assertThat(bf).isNotNull();
		assertThat(movies).isNotNull();
		assertThat(distributors).isNotNull();
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	@SuppressWarnings("unchecked")
	void iBookingGetControllerEditBookingP() {
		String returnedView = getController.editBooking(bookingId, model);

		assertThat(returnedView).isEqualTo("booking/editBooking");

		BookingForm bf = (BookingForm) model.asMap().get("bookingForm");
		Booking b = (Booking) model.asMap().get("booking");
		Streamable<Movie> movies = (Streamable<Movie>) model.asMap().get("movies");
		Streamable<Distributor> distributors = (Streamable<Distributor>) model.asMap().get("distributors");

		assertThat(bf).isNotNull();
		assertThat(b).isNotNull();
		assertEquals(bookingId, b.getId());
		assertThat(movies).isNotNull();
		assertThat(distributors).isNotNull();
	}

	@Test
	@WithMockUser
	void iBookingGetControllerLinkVisibilityP1() throws Exception{
		MvcResult result = mvc.perform(get("/bookings/booking?id=" + bookingId)).andReturn();

		assertEquals(200, result.getResponse().getStatus());

		String htmlString = result.getResponse().getContentAsString();

		assertTrue(htmlString.contains("id=\"settleUpLink\""));
		assertFalse(htmlString.contains("id=\"generatePDFLink\""));
	}

	@Test
	@WithMockUser
	void iBookingGetControllerLinkVisibilityP2() throws Exception{
		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
		booking.setState(BookingState.OVER);
		bookingRepository.save(booking);

		getController.settleUp(bookingId, redirectAttributes);

		assertTrue(bookingRepository.findById(bookingId).orElseThrow().isSettledUp());

		MvcResult result = mvc.perform(get("/bookings/booking?id=" + bookingId)).andReturn();

		assertEquals(200, result.getResponse().getStatus());

		String htmlString = result.getResponse().getContentAsString();

		assertFalse(htmlString.contains("id=\"settleUpLink\""));
		assertTrue(htmlString.contains("id=\"generatePDFLink\""));
	}

	@Test
	@WithMockUser
	void iBookingGetControllerLSettleUpP() {
		String returnedView = getController.settleUp(bookingId, redirectAttributes);

		assertThat(returnedView).isEqualTo("redirect:/bookings/booking?id=" + bookingId);

		Booking booking = bookingRepository.findById(bookingId).orElseThrow();

		assertFalse(booking.isSettledUp());

		booking.setState(BookingState.OVER);
		bookingRepository.save(booking);

		returnedView = getController.settleUp(bookingId, redirectAttributes);

		assertThat(returnedView).isEqualTo("redirect:/bookings/booking?id=" + bookingId);

		booking = bookingRepository.findById(bookingId).orElseThrow();

		assertTrue(booking.isSettledUp());
		assertTrue(booking.isArchived());
	}
}
