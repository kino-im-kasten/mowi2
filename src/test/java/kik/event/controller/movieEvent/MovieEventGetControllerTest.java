package kik.event.controller.movieEvent;

import kik.booking.data.Booking;

import kik.event.MovieEventSampleTest;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.data.movieEvent.tickets.TicketsForm;

import kik.event.management.movieEvent.MovieEventManagement;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MovieEventGetControllerTest extends MovieEventSampleTest {
	@Autowired
	MockMvc mvc;
	
	@Autowired
	MovieEventGetControllerTest(MovieEventManagement movieEventManagement) {
		super(movieEventManagement);
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iMovieEventGetControllerGetOverviewP1() throws Exception {
		MvcResult result = this.mvc.perform(get("/movieEvents/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		Object events = result.getModelAndView().getModelMap().get("movieEvents");
		assertTrue("the server's response should be a List<MovieEvents>", events instanceof List<?>);

		List<MovieEvent> eventList = (List<MovieEvent>) events;
		int PADDING = 10;
		assertTrue("wrong PADDING",PADDING >= eventList.size());

		var moviesInRepository = this.movieEventManagement.getMovieEventRepository().findAll().toList();
		for(MovieEvent movieEvent : eventList) {
			if (!moviesInRepository.contains(movieEvent)) {
				fail("The movieEvent" + movieEvent.getFullyQualifiedName() + "with id: " + movieEvent.getId() + " should be in the main movieEventsRepository but wasn't");
			}
		}
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventGetControllerCreateMovieEventP1() throws Exception {
		MvcResult result = this.mvc.perform(get("/movieEvents/createMovieEvent/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object movieEventForm = result.getModelAndView().getModelMap().get("movieEventForm");
		assertTrue("the server's response should contain a MovieEventForm", movieEventForm instanceof MovieEventForm);
		
		Object bookings = result.getModelAndView().getModelMap().get("bookings");
		assertTrue("The server's response should be a Streamable<MovieEvents>", bookings instanceof Streamable<?>);
		
		List<Booking> bookingsList = ((Streamable<Booking>) bookings).toList();
		assertEquals(this.movieEventManagement.getBookingManagement().getAllNotArchivedBookings().toList().size(), bookingsList.size());
		
		for (Booking booking : this.movieEventManagement.getBookingManagement().getAllNotArchivedBookings().toList()) {
			if (!(bookingsList.contains(booking))) {
				fail("The booking " + booking.getDisplayName() + " should be in the response but wasn't!");
			}
		}
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iMovieEventGetControllerDetailsP1() throws Exception {
		var mE = this.movieEventManagement.getMovieEventRepository().findAll().toList().get(0);
		MvcResult result = this.mvc.perform(get("/movieEvents/details/" + mE.getId() + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object movieEvent = result.getModelAndView().getModelMap().get("movieEvent");
		assertTrue("The server's response should contain a MovieEvent", movieEvent instanceof MovieEvent);
		
		assertEquals(mE, movieEvent);
	}
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iMovieEventGetControllerEditMovieEventP1() throws Exception {
		var mE = this.movieEventManagement.getMovieEventRepository().findAll().toList().get(0);
		MvcResult result = this.mvc.perform(get("/movieEvents/edit/" + mE.getId() + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object movieEvent = result.getModelAndView().getModelMap().get("movieEvent");
		assertTrue("The server's response should contain a MovieEvent", movieEvent instanceof MovieEvent);
		assertEquals(mE, movieEvent);
		
		Object movieEventForm = result.getModelAndView().getModelMap().get("movieEventForm");
		assertTrue("the server's response should contain a MovieEventForm", movieEventForm instanceof MovieEventForm);
		
		exceptionRule.expect(NullPointerException.class);
		result.getModelAndView().getModelMap().get("bookings");
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iMovieEventGetControllerEditTicketsP1() throws Exception {
		var mE = this.movieEventManagement.getMovieEventRepository().findAll().toList().get(0);
		MvcResult result = this.mvc.perform(get("/movieEvents/tickets/" + mE.getId() + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object movieEvent = result.getModelAndView().getModelMap().get("movieEvent");
		assertTrue("The server's response should contain a MovieEvent", movieEvent instanceof MovieEvent);
		assertEquals(mE, movieEvent);
		
		Object ticketsForm = result.getModelAndView().getModelMap().get("ticketsForm");
		assertTrue("The server's response should contain a TicketsForm", ticketsForm instanceof TicketsForm);
		assertEquals(ticketsForm, mE.getTickets().intoTicketsForm());
	}
}
