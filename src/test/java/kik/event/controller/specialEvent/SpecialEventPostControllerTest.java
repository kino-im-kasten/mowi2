package kik.event.controller.specialEvent;

import kik.booking.data.Booking;
import kik.event.SpecialEventSampleTest;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.management.specialEvent.SpecialEventManagement;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SpecialEventPostControllerTest extends SpecialEventSampleTest {
	@Autowired
	MockMvc mvc;
	
	@Autowired
	SpecialEventPostControllerTest(SpecialEventManagement specialEventManagement) {
		super(specialEventManagement);
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iSpecialEventPostControllerFilterMovieEventsP1() throws Exception {
		SpecialEvent specialEvent = this.specialEventManagement.getSpecialEventRepository().findAll().toList().get(0);
		
		MvcResult result = this.mvc.perform(post("/specialEvents/?input=" + specialEvent.getAdditonalName())).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		Object events = result.getModelAndView().getModelMap().get("specialEvents");
		assertTrue("the server's response should be a Streamable<SpecialEvents>", events instanceof Streamable<?>);
		
		List<SpecialEvent> eventList = ((Streamable<SpecialEvent>) events).toList();
		for (SpecialEvent sE : eventList) {
			assertEquals(sE.getAdditonalName(), specialEvent.getAdditonalName());
		}
	}
	
	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iSpecialEventPostControllerCreateSpecialEventP1() throws Exception {
		String additionalName = "Postcontroller TestName";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate correctLocalStartDate = LocalDate.now().plusDays(1);
		
		String description = "A stupid description";
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime start = LocalTime.parse("20:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetTime end = LocalTime.parse("22:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(
				multipart("/specialEvents/createSpecialEvent/")
						.param("additionalName", additionalName)
						.param("date", correctLocalStartDate.format(formatter))
						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm")))
						.param("description", description)
						.param("expectedEnd", end.format(DateTimeFormatter.ofPattern("HH:mm")))
						.param("type", "SPECIAL")
						.param("eventPublicity", "PUBLIC"))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		SpecialEvent specialEvent = null;
		try {
			specialEvent = this.specialEventManagement
					.getSpecialEventRepository()
					.findByDescriptionAndExpectedEnd(description, end);
			if (specialEvent == null) {
				fail("New movieEvent-object could not be located!");
			}
		} catch (Exception e) {
			fail("The newly created movieEvent with additonal name " + additionalName +
					" should have been added to the list of all movieEvents");
		}
		
		assertEquals(specialEvent.getAdditonalName(), additionalName);
		assertEquals(specialEvent.getFullyQualifiedName(), additionalName);
		assertEquals(specialEvent.getDate(), correctLocalStartDate);
		assertEquals(specialEvent.getStart(), start);
		assertEquals(specialEvent.getExpectedEnd(), end);
		assertEquals(specialEvent.getEventType(), EventType.SPECIAL);
		assertEquals(specialEvent.getEventPublicity(), EventPublicity.PUBLIC);
	}
	
	// ! Obsolete with KiK's with to edit SpecialEvents in the past
//	@Test
//	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
//	void iSpecialEventPostControllerCreateSpecialEventN1() throws Exception {
//		String additionalName = "Postcontroller TestName";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		LocalDate correctLocalStartDate = LocalDate.now().minusDays(100);
//
//		String description = "A stupid description";
//
//		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
//		OffsetTime start = LocalTime.parse("20:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
//		OffsetTime end = LocalTime.parse("22:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
//
//		MvcResult result = this.mvc.perform(
//				multipart("/specialEvents/createSpecialEvent/")
//						.param("additionalName", additionalName)
//						.param("date", correctLocalStartDate.format(formatter))
//						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm")))
//						.param("description", description)
//						.param("expectedEnd", end.format(DateTimeFormatter.ofPattern("HH:mm")))
//						.param("type", "SPECIAL")
//						.param("eventPublicity", "PUBLIC"))
//				.andReturn();
//		assertEquals(302, result.getResponse().getStatus());
//
//		SpecialEvent specialEvent = this.specialEventManagement
//				.getSpecialEventRepository()
//				.findByDescriptionAndExpectedEnd(description, end);
//		if (specialEvent != null) {
//			fail("New movieEvent-object should not have been located!");
//		}
//	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iSpecialEventPostControllerEditSpecialEventP1() throws Exception {
		SpecialEvent specialEvent = this.specialEventManagement
				.getSpecialEventRepository()
				.findAll()
				.toList()
				.get(0);
		
		String additionalName = "Postcontroller TestName";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate correctLocalDate = specialEvent.getDate();
		String description = "Useless description";
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime start = LocalTime.parse("20:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetTime expectedEnd = LocalTime.parse("23:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(
				multipart("/specialEvents/edit/" + specialEvent.getId() + "/")
						.param("additionalName", additionalName)
						.param("description", description)
						.param("date", correctLocalDate.format(formatter))
						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm")))
						.param("expectedEnd", expectedEnd.format(DateTimeFormatter.ofPattern("HH:mm")))
						.param("eventType", "SPECIAL")
						.param("eventPublicity", "PRIVATE"))
				.andReturn();
		assertEquals(302, result.getResponse().getStatus());
		
		SpecialEvent newSpecialEvent = null;
		try {
			newSpecialEvent = this.specialEventManagement
					.getSpecialEventRepository()
					.findByDescriptionAndExpectedEnd(description, expectedEnd);
			if (newSpecialEvent == null) {
				fail("New specialEvent-object could not be located!");
			}
		} catch (Exception e) {
			fail("The edited movieEvent with additonal name " + additionalName +
					" should have been updated in the list of all movieEvents");
		}
		
		assertEquals(additionalName, newSpecialEvent.getAdditonalName());
		assertEquals(additionalName, newSpecialEvent.getFullyQualifiedName());
		assertEquals(correctLocalDate, newSpecialEvent.getDate());
		assertEquals(start, newSpecialEvent.getStart());
		assertEquals(expectedEnd, newSpecialEvent.getExpectedEnd());
		assertEquals(description, newSpecialEvent.getDescription());
		assertEquals(EventPublicity.PRIVATE, newSpecialEvent.getEventPublicity());
		assertEquals(EventType.SPECIAL, newSpecialEvent.getEventType());
	}
	
	@Test
	@WithMockUser(roles = {"ORGA", "ADMIN"})
	void iSpecialEventPostControllerEditSpecialEventN1() throws Exception {
		String description = "Useless description";
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime expectedEnd = LocalTime.parse("23:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		MvcResult result = this.mvc.perform(multipart("/specialEvents/edit/" + 745874878 + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		
		SpecialEvent newSpecialEvent = this.specialEventManagement
				.getSpecialEventRepository()
				.findByDescriptionAndExpectedEnd(description, expectedEnd);
		if (newSpecialEvent != null) {
			fail("New specialEvent-object should not have been located!");
		}
	}
	
	// ! Obsolete with KiK's with to edit SpecialEvents in the past
//	@Test
//	@WithMockUser(roles = {"ORGA", "ADMIN"})
//	void iSpecialEventPostControllerEditSpecialEventN2() throws Exception {
//		SpecialEvent specialEvent = this.specialEventManagement
//				.getSpecialEventRepository()
//				.findAll()
//				.toList()
//				.get(0);
//
//		String additionalName = "Postcontroller TestName";
//
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		LocalDate correctLocalDate = specialEvent.getDate().minusDays(100);
//		String description = "Useless description";
//
//		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
//		OffsetTime start = LocalTime.parse("20:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
//		OffsetTime expectedEnd = LocalTime.parse("23:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
//
//		MvcResult result = this.mvc.perform(
//				multipart("/specialEvents/edit/" + specialEvent.getId() + "/")
//						.param("additionalName", additionalName)
//						.param("description", description)
//						.param("date", correctLocalDate.format(formatter))
//						.param("start", start.format(DateTimeFormatter.ofPattern("HH:mm")))
//						.param("expectedEnd", expectedEnd.format(DateTimeFormatter.ofPattern("HH:mm")))
//						.param("eventType", "SPECIAL")
//						.param("eventPublicity", "PRIVATE"))
//				.andReturn();
//		assertEquals(302, result.getResponse().getStatus());
//
//		SpecialEvent newSpecialEvent = this.specialEventManagement
//				.getSpecialEventRepository()
//				.findByDescriptionAndExpectedEnd(description, expectedEnd);
//		if (newSpecialEvent != null) {
//			fail("New specialEvent-object should not have been located!");
//		}
//	}
}
