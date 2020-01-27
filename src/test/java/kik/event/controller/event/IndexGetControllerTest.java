package kik.event.controller.event;

import kik.event.MovieEventSampleTest;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.event.management.specialEvent.SpecialEventManagement;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class IndexGetControllerTest extends MovieEventSampleTest {
	@Autowired
	MockMvc mvc;
	
	private final SpecialEventManagement specialEventManagement;
	
	@Autowired
	IndexGetControllerTest(MovieEventManagement movieEventManagement,
						   SpecialEventManagement specialEventManagement) {
		super(movieEventManagement);
		this.specialEventManagement = specialEventManagement;
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"}, username = "Bruce", password = "123")
	void iIndexGetControllerGetOverviewP1() throws Exception {
		MvcResult result = this.mvc.perform(get("/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object movieEvents = result.getModelAndView().getModelMap().get("movieEvents");
		assertTrue("the server's response should contain a List<MovieEvent>", movieEvents instanceof List<?>);
		
		Object specialEvents = result.getModelAndView().getModelMap().get("specialEvents");
		assertTrue("the server's response should contain a List<SpecialEvent>", specialEvents instanceof List<?>);
		
		List<MovieEvent> movieEventList = (List<MovieEvent>) movieEvents;
		assertTrue("wrong PADDING",15 >= movieEventList.size());
		
		List<SpecialEvent> specialEventList = (List<SpecialEvent>) specialEvents;
		assertTrue("wrong PADDING",5 >= specialEventList.size());
		
		var movieEventsInRepository = this.movieEventManagement.getMovieEventRepository().findAll().toList();
		for(MovieEvent movieEvent : movieEventList) {
			if (!movieEventsInRepository.contains(movieEvent)) {
				fail("The movieEvent" + movieEvent.getFullyQualifiedName() + "with id: " + movieEvent.getId() + " should be in the main movieEventsRepository but wasn't");
			}
		}
		
		var specialEventsInRepository = this.specialEventManagement.getEventRepository().findAll().toList();
		for (SpecialEvent specialEvent : specialEventList) {
			if (!specialEventsInRepository.contains(specialEvent)) {
				fail("The specialEvent" + specialEvent.getFullyQualifiedName() + "with id: " + specialEvent.getId() + " should be in the main specialEventsRepository but wasn't");
			}
		}
	}
}
