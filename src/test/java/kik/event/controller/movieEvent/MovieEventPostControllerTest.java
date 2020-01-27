package kik.event.controller.movieEvent;

import kik.booking.data.Booking;
import kik.event.MovieEventSampleTest;

import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.tickets.Tickets;

import kik.event.management.movieEvent.MovieEventManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MovieEventPostControllerTest extends MovieEventSampleTest {
	@Autowired
	MockMvc mvc;
	
	@Autowired
	MovieEventPostControllerTest(MovieEventManagement movieEventManagement) {
		super(movieEventManagement);
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iMovieEventPostControllerFilterMovieEventsP1() throws Exception {
		MovieEvent movieEvent = this.movieEventManagement.getMovieEventRepository().findAll().toList().get(0);
		
		MvcResult result = this.mvc.perform(post("/movieEvents/?input=" + movieEvent.getAdditonalName())).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object events = result.getModelAndView().getModelMap().get("movieEvents");
		assertTrue("the server's response should be a Streamable<MovieEvents>", events instanceof Streamable<?>);
		
		List<MovieEvent> eventList = ((Streamable<MovieEvent>) events).toList();
		for (MovieEvent mE : eventList) {
			assertEquals(mE.getAdditonalName(), movieEvent.getAdditonalName());
		}
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventPostControllerCreateMovieEventP1() throws Exception {
		Booking booking = this.movieEventManagement
				.getBookingManagement()
				.getAllNotArchivedBookings()
				.toList()
				.get(0);
		
		String additionalName = "Postcontroller TestName";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate correctLocaDate = booking.getStartDate().plusDays(1);
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime start = LocalTime.parse("04:33", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(
				multipart("/movieEvents/createMovieEvent/")
						.param("additionalName", additionalName)
						.param("date", correctLocaDate.format(formatter))
						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm")))
						.param("bookingID", String.valueOf(booking.getId())))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		MovieEvent newMovieEvent = null;
		try {
			newMovieEvent = this.movieEventManagement
					.getMovieEventRepository()
					.findByAdditonalNameAndBookingAndDateAndStart(additionalName, booking, correctLocaDate, start);
			if (newMovieEvent == null) {
				fail("New movieEvent-object could not be located!");
			}
		} catch (Exception e) {
			fail("The newly created movieEvent with additonal name " + additionalName +
					" should have been added to the list of all movieEvents");
		}
		
		assertEquals(newMovieEvent.getAdditonalName(), additionalName);
		assertEquals(newMovieEvent.getDate(), correctLocaDate);
		assertEquals(newMovieEvent.getBooking(), booking);
		assertEquals(newMovieEvent.getStart(), start);
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventPostControllerCreateMovieEventN1() throws Exception {
		Booking booking = this.movieEventManagement
				.getBookingManagement()
				.getAllNotArchivedBookings()
				.toList()
				.get(0);
		
		String additionalName = "Postcontroller TestName";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate correctLocaDate = booking.getStartDate().minusDays(100);
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime start = LocalTime.parse("04:33", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(
				multipart("/movieEvents/createMovieEvent/")
						.param("additionalName", additionalName)
						.param("date", correctLocaDate.format(formatter))
						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm")))
						.param("bookingID", String.valueOf(booking.getId())))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		MovieEvent newMovieEvent = null;
		
		newMovieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findByAdditonalNameAndBookingAndDateAndStart(additionalName, booking, correctLocaDate, start);
		if (newMovieEvent != null) {
			fail("New movieEvent-object could not be located!");
		}
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventPostControllerEditMovieEventP1() throws Exception {
		MovieEvent movieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findAll()
				.toList()
				.get(0);
		
		String additionalName = "Postcontroller TestName";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate correctLocalDate = movieEvent.getDate();
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(
				multipart("/movieEvents/edit/" + movieEvent.getId() + "/")
						.param("additionalName", additionalName)
						.param("date", correctLocalDate.format(formatter))
						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm"))))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		MovieEvent newMovieEvent = null;
		try {
			newMovieEvent = this.movieEventManagement
					.getMovieEventRepository()
					.findByAdditonalNameAndBookingAndDateAndStart(additionalName, movieEvent.getBooking(), correctLocalDate, start);
			if (newMovieEvent == null) {
				fail("New movieEvent-object could not be located!");
			}
		} catch (Exception e) {
			fail("The edited movieEvent with additonal name " + additionalName +
					" should have been updated in the list of all movieEvents");
		}
		
		assertEquals(newMovieEvent.getAdditonalName(), additionalName);
		assertEquals(newMovieEvent.getDate(), correctLocalDate);
		assertEquals(newMovieEvent.getBooking(), movieEvent.getBooking());
		assertEquals(newMovieEvent.getStart(), start);
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventPostControllerEditMovieEventN1() throws Exception {
		MvcResult result = this.mvc.perform(multipart("/movieEvents/edit/" + 1985433762 + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventPostControllerEditMovieEventN2() throws Exception {
		MovieEvent movieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findAll()
				.toList()
				.get(0);
		
		String additionalName = "Postcontroller TestName";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate correctLocalDate = movieEvent.getDate().minusDays(100);
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(
				multipart("/movieEvents/edit/" + movieEvent.getId() + "/")
						.param("additionalName", additionalName)
						.param("date", correctLocalDate.format(formatter))
						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm"))))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		MovieEvent newMovieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findByAdditonalNameAndBookingAndDateAndStart(additionalName, movieEvent.getBooking(), correctLocalDate, start);
		if (newMovieEvent != null) {
			fail("New movieEvent-object should nothave been located!");
		}
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventPostControllerDeleteMovieEventP1() throws Exception {
		MovieEvent movieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findAll()
				.toList()
				.get(0);
		MvcResult result = this.mvc.perform(post("/movieEvents/delete/" + movieEvent.getId() + "/")).andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		assertFalse(this.movieEventManagement.getMovieEventRepository().findAll().toList().contains(movieEvent));
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iMovieEventPostControllerEditMovieEventTicketsP1() throws Exception {
		MovieEvent movieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findAll()
				.toList()
				.get(0);
		String cardFreeAmount = "5";
		String cardNormalStartNumber = "250";
		String cardNormalEndNumber = "300";
		String cardReducedStartNumber = "22";
		String cardReducedEndNumber = "34";
		
		MvcResult result = this.mvc.perform(
				multipart("/movieEvents/tickets/" + movieEvent.getId() + "/")
						.param("cardFreeAmount", cardFreeAmount)
						.param("cardNormalStartNumber", cardNormalStartNumber)
						.param("cardNormalEndNumber", cardNormalEndNumber)
						.param("cardReducedStartNumber", cardReducedStartNumber)
						.param("cardReducedEndNumber", cardReducedEndNumber))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		Tickets tickets;
		tickets = this.movieEventManagement
				.getMovieEventRepository()
				.findById(movieEvent.getId())
				.get()
				.getTickets();
		
		assertEquals(tickets.getCardFreeAmount(), Integer.parseInt(cardFreeAmount));
		assertEquals(tickets.getCardNormalStartNumber(), Integer.parseInt(cardNormalStartNumber));
		assertEquals(tickets.getCardNormalEndNumber(), Integer.parseInt(cardNormalEndNumber));
		assertEquals(tickets.getCardReducedStartNumber(), Integer.parseInt(cardReducedStartNumber));
		assertEquals(tickets.getCardReducedEndNumber(), Integer.parseInt(cardReducedEndNumber));
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iMovieEventPostControllerEditMovieEventTicketsN1() throws Exception {
		MvcResult result = this.mvc.perform(multipart("/movieEvents/tickets/" + 1985433762 + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
	}
	
	@Test
	@WithMockUser(roles = {"USER","ORGA", "ADMIN"})
	void iMovieEventPostControllerEditMovieEventTicketsN2() throws Exception {
		MovieEvent movieEvent = this.movieEventManagement
				.getMovieEventRepository()
				.findAll()
				.toList()
				.get(0);
		String cardFreeAmount = "5";
		String cardNormalStartNumber = "300";
		String cardNormalEndNumber = "250";
		String cardReducedStartNumber = "50";
		String cardReducedEndNumber = "34";
		
		Tickets ticketsBefore = movieEvent.getTickets();
		
		MvcResult result = this.mvc.perform(
				multipart("/movieEvents/tickets/" + movieEvent.getId() + "/")
						.param("cardFreeAmount", cardFreeAmount)
						.param("cardNormalStartNumber", cardNormalStartNumber)
						.param("cardNormalEndNumber", cardNormalEndNumber)
						.param("cardReducedStartNumber", cardReducedStartNumber)
						.param("cardReducedEndNumber", cardReducedEndNumber))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		Tickets tickets = this.movieEventManagement
				.getMovieEventRepository()
				.findById(movieEvent.getId())
				.get()
				.getTickets();
		
		if (!tickets.equals(ticketsBefore)) {
			fail("There should have been not tickets found!");
		}
	}
}
