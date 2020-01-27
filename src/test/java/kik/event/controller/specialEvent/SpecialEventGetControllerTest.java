package kik.event.controller.specialEvent;

import kik.event.SpecialEventSampleTest;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;
import kik.event.management.specialEvent.SpecialEventManagement;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SpecialEventGetControllerTest extends SpecialEventSampleTest {
	@Autowired
	MockMvc mvc;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Autowired
	SpecialEventGetControllerTest(SpecialEventManagement specialEventManagement) {
		super(specialEventManagement);
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iSpecialEventGetControllerGetOverviewP1() throws Exception {
		MvcResult result = this.mvc.perform(get("/specialEvents/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object events = result.getModelAndView().getModelMap().get("specialEvents");
		assertTrue("the server's response should be a List<SpecialEvents>", events instanceof List<?>);
		
		List<SpecialEvent> eventList = (List<SpecialEvent>) events;
		int PADDING = 7;
		assertTrue(PADDING >= eventList.size());
		
		var moviesInRepository = this.specialEventManagement.getSpecialEventRepository().findAll().toList();
		for(SpecialEvent specialEvent : eventList) {
			if (!moviesInRepository.contains(specialEvent)) {
				fail("The movieEvent" + specialEvent.getFullyQualifiedName() +
						"with id: " + specialEvent.getId() +
						" should be in the main movieEventsRepository but wasn't");
			}
		}
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iSpecialEventGetControllerCreateSpecialEventP1() throws Exception {
		MvcResult result = this.mvc.perform(get("/specialEvents/createSpecialEvent/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object specialEventForm = result.getModelAndView().getModelMap().get("specialEventForm");
		assertTrue("the server's response should contain a MovieEventForm", specialEventForm instanceof SpecialEventForm);
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iSpecialEventGetControllerDetailsP1() throws Exception {
		var sE = this.specialEventManagement.getSpecialEventRepository().findAll().toList().get(0);
		MvcResult result = this.mvc.perform(get("/specialEvents/details/" + sE.getId() + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object specialEvent = result.getModelAndView().getModelMap().get("specialEvent");
		assertTrue("The server's response should contain a MovieEvent", specialEvent instanceof SpecialEvent);
		
		assertEquals(sE, specialEvent);
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iSpecialEventGetControllerEditSpecialEventP1() throws Exception {
		var sE = this.specialEventManagement.getSpecialEventRepository().findAll().toList().get(0);
		MvcResult result = this.mvc.perform(get("/specialEvents/edit/" + sE.getId() + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object specialEvent = result.getModelAndView().getModelMap().get("specialEvent");
		assertTrue("The server's response should contain a MovieEvent", specialEvent instanceof SpecialEvent);
		assertEquals(sE, specialEvent);
		
		Object specialEventForm = result.getModelAndView().getModelMap().get("specialEventForm");
		assertTrue("the server's response should contain a MovieEventForm", specialEventForm instanceof SpecialEventForm);
	}
}
