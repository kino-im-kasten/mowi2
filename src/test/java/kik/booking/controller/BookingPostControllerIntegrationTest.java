package kik.booking.controller;

import kik.booking.data.*;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class BookingPostControllerIntegrationTest {
	private BookingPostController postController;
	private BookingRepository bookingRepository;

	private MovieRepository movieRepository;
	private DistributorRepository distributorRepository;

	private BookingForm bookingForm;

	private Booking booking;
	private Conditions conditions;

	private Model model;
	private RedirectAttributes redirectAttributes;

	@Autowired
	MockMvc mvc;

	private boolean initialized;

	@Autowired
	BookingPostControllerIntegrationTest(BookingPostController postController, BookingRepository bookingRepository,
										 MovieRepository movieRepository, DistributorRepository distributorRepository){
		this.postController = postController;
		this.bookingRepository = bookingRepository;
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;

		this.initialized = false;
	}

	@BeforeEach
	void setup(){
		if(!initialized) {
			initialized = true;

			Movie m = new Movie("a", "b", "c", "d", "e", 1,"-", 1);
			Distributor d = new Distributor();

			movieRepository.save(m);
			distributorRepository.save(d);
			conditions = new Conditions(25.f, 10.f);
			bookingForm = new BookingForm("12345678", m.getId(), d.getId(), LocalDate.now().plusDays(8),
				LocalDate.now().plusDays(15), conditions);

			booking = new Booking("87654321", m, d, LocalDate.now().plusDays(7),
				LocalDate.now().plusDays(14), new Conditions(20.f, 20.f));
			bookingRepository.save(booking);

			model = new ExtendedModelMap();
			redirectAttributes = new RedirectAttributesModelMap();
		}
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	void iBookingPostControllerCreateBookingP1(){
		int sizeBefore = bookingRepository.findAll().toList().size();

		String returnedView = postController.createBooking(bookingForm,
			new BeanPropertyBindingResult(bookingForm, "Booking Form"), redirectAttributes);

		assertThat(returnedView).contains("redirect:/bookings/booking?id=");
		assertEquals(1, bookingRepository.findAll().toList().size() - sizeBefore);
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	void iBookingPostControllerCreateBookingP2(){
		int sizeBefore = bookingRepository.findAll().toList().size();
		bookingForm.setConditions(new Conditions(-3, 200));

		BindingResult binding = new BeanPropertyBindingResult(bookingForm, "Booking Form");
		String returnedView = postController.createBooking(bookingForm, binding, redirectAttributes);

		assertTrue(binding.hasErrors());
		assertThat(returnedView).isEqualTo("redirect:/createBooking");

		Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();

		assertTrue(flashAttributes.containsKey("bookingForm"));
		assertTrue(flashAttributes.containsKey("org.springframework.validation.BindingResult.bookingForm"));

		assertEquals(bookingForm, flashAttributes.get("bookingForm"));
		assertEquals(binding, flashAttributes.get("org.springframework.validation.BindingResult.bookingForm"));

		assertEquals(0, bookingRepository.findAll().toList().size() - sizeBefore);
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	void iBookingPostControllerEditBookingP1(){
		String returnedView = postController.editBooking(booking.getId(), bookingForm,
			new BeanPropertyBindingResult(bookingForm, "Booking Form"), redirectAttributes);

		assertThat(returnedView).isEqualTo("redirect:/bookings/booking?id=" + booking.getId());

		Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();

		assertEquals("12345678", updatedBooking.getTbNumber());
		assertEquals(LocalDate.now().plusDays(8), updatedBooking.getStartDate());
		assertEquals(LocalDate.now().plusDays(15), updatedBooking.getEndDate());
		assertEquals(conditions, updatedBooking.getConditions());
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	void iBookingPostControllerEditBookingP2(){
		long distributorId = bookingForm.getDistributorID();

		bookingForm.setDistributorID(0);

		String returnedView = postController.editBooking(booking.getId(), bookingForm,
			new BeanPropertyBindingResult(bookingForm, "Booking Form"), redirectAttributes);

		assertThat(returnedView).isEqualTo("redirect:/bookings/booking?id=" + booking.getId());

		Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();

		assertEquals(distributorId, updatedBooking.getDistributor().getId());
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	void iBookingPostControllerEditBookingP3(){
		bookingForm.setConditions(new Conditions(-3, 200));
		BindingResult binding = new BeanPropertyBindingResult(bookingForm, "Booking Form");

		String returnedView = postController.editBooking(booking.getId(), bookingForm,
			binding, redirectAttributes);

		assertTrue(binding.hasErrors());
		assertThat(returnedView).isEqualTo("redirect:/bookings/edit?id=" + booking.getId());

		Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();

		assertTrue(flashAttributes.containsKey("bookingForm"));
		assertTrue(flashAttributes.containsKey("org.springframework.validation.BindingResult.bookingForm"));

		assertEquals(bookingForm, flashAttributes.get("bookingForm"));
		assertEquals(binding, flashAttributes.get("org.springframework.validation.BindingResult.bookingForm"));

		Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
		assertEquals(booking, updatedBooking);
	}

	@Test
	@WithMockUser(roles = {"ORGA", "USER"})
	void iBookingPostControllerCancelBookingP() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		long bookingId = booking.getId();

		String returnedView = postController.cancelBooking(bookingId, request, redirectAttributes);

		assertThat(returnedView).isEqualTo("redirect:/bookings/booking?id=" + bookingId);

		Booking booking = bookingRepository.findById(bookingId).orElseThrow();

		assertEquals(BookingState.CANCELED, booking.getState());
		assertTrue(booking.isArchived());
		assertFalse(booking.isSettledUp());
	}


	@Test
	@WithMockUser
	void iBookingPostControllerFilterBookingsP(){
		String returnedView = postController.filterBookings("filter");

		assertThat(returnedView).isEqualTo("redirect:/bookings?input=filter");
	}
}


