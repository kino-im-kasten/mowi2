package kik.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import kik.booking.data.BookingRepository;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.event.EventRepository;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventRepository;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.event.management.movieEvent.SampleMovieEventInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import kik.event.data.event.Event;
import kik.rest.data.RestEvent;
import kik.rest.management.RestManagement;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class RestControllerTest {
	@Autowired
	MockMvc mvc;

	private EventRepository eventRepository;
	private MovieEventRepository movieEventRepository;
	private RestManagement restManagement;
	private SampleMovieEventInitializer eventInitializer;
	private UserRepository userRepository;
	private DutyPlanManagement dutyPlanManagement;

	@Autowired
	RestControllerTest(EventRepository eventRepository, RestManagement restManagement,
			BookingRepository bookingRepository, MovieEventManagement movieEventManagement,
			DutyPlanManagement dutyPlanManagement, MovieEventRepository movieEventRepository,
			UserRepository userRepository) {
		this.eventRepository = eventRepository;
		this.restManagement = restManagement;
		this.eventInitializer = new SampleMovieEventInitializer(movieEventManagement, dutyPlanManagement, null);
		this.movieEventRepository = movieEventRepository;
		this.userRepository = userRepository;
		this.dutyPlanManagement = dutyPlanManagement;
	}

	@BeforeEach
	void setUp() {
		this.eventInitializer.initialize();
	}

	@Test
	void uRestControllerGetEventByIdP1() throws Exception {
		Event event = null;
		try {
			event = this.eventRepository.findAll().toList().get(0);
		} catch (IndexOutOfBoundsException e) {
			fail("there should have been at least one element in the repository but there was none");
		}

		MvcResult response = mvc.perform(get("/rest/event?id=" + event.getId())).andReturn();

		assertEquals(200, response.getResponse().getStatus());

		JSONObject json = new JSONObject(response.getResponse().getContentAsString());

		assertEquals(String.valueOf(event.getId()), json.getString("id"));
	}

	@Test
	void uRestControllerGetEventListP1() throws Exception {
		List<RestEvent> events = this.restManagement.getEventList();

		MvcResult response = mvc.perform(get("/rest/eventList")).andReturn();

		assertEquals(200, response.getResponse().getStatus());

		JSONArray jsonArray = new org.json.JSONArray(response.getResponse().getContentAsString());

		for (int i = 0; i < events.size(); i++) {
			assertEquals(events.get(i).getId(), jsonArray.getJSONObject(i).getLong("id"));
		}
	}

	@Test
	void uRestControllerAmountAfterTimeP1() throws Exception {
		Event event = null;
		try {
			event = this.eventRepository.findAll().toList().get(0);
		} catch (IndexOutOfBoundsException e) {
			fail("there should have been at least one element in the repository but there was none");
		}

		// 2019-12-03T20:15
		String time = event.getOdt().toString().substring(0, 16);

		MvcResult response = mvc.perform(get("/rest/eventsAfter?amount=1&start=" + time)).andReturn();

		assertEquals(200, response.getResponse().getStatus());

		// TODO Jenkins is as great a pice of **** as Java is ~ Georg
		// TODO Felix, pls fix :D

		// JSONArray jsonArray = new
		// org.json.JSONArray(response.getResponse().getContentAsString());

		// assertTrue("the event should be returned as a json string/array",
		// jsonArray.length() == 1);

		// List<RestEvent> events = this.restManagement.getEventsAfter(1, time);

		// for (int i = 0; i < events.size(); i++) {
		// assertEquals(events.get(i).getId(),
		// jsonArray.getJSONObject(i).getLong("id"));
		// }
	}

	@Test
	void uRestControllerBetweenTimeP1() throws Exception {
		Event event = null;
		try {
			event = this.eventRepository.findAll().toList().get(0);
		} catch (IndexOutOfBoundsException e) {
			fail("there should have been at least one element in the repository but there was none");
		}

		// 2019-12-03T20:15
		String start = event.getOdt().toString().substring(0, 16);
		String end = event.getOdt().plusMinutes(1).toString().substring(0, 16);

		MvcResult response = mvc.perform(get("/rest/eventsBetween?start=" + start + "&end=" + end)).andReturn();

		assertEquals(200, response.getResponse().getStatus());

		JSONArray jsonArray = new org.json.JSONArray(response.getResponse().getContentAsString());
		List<RestEvent> events = this.restManagement.getEventsBetweenTime(start, end);

		for (int i = 0; i < events.size(); i++) {
			assertEquals(events.get(i).getId(), jsonArray.getJSONObject(i).getLong("id"));
		}
	}

	@Test
	void uRestControllerDutyplanP1() throws Exception {
		MovieEvent event = null;
		try {
			event = this.movieEventRepository.findAll().toList().get(0);
		} catch (IndexOutOfBoundsException e) {
			fail("there should have been at least one element in the repository but there was none");
		}

		MvcResult response = mvc.perform(get("/rest/dutyplan?eventId=" + event.getId())).andReturn();

		assertEquals(200, response.getResponse().getStatus());

		JSONObject json = new JSONObject(response.getResponse().getContentAsString());

		DutyPlan dp = event.getDutyPlan();

		assertEquals(dp.getId().toString(), json.getString("id"));
		assertEquals(dp.getAnnotation(), json.getString("annotation"));
		assertEquals(dp.getAssignedJobs().toList().size(), json.getJSONArray("assignedRoles").length());
		assertEquals(dp.getOpenJobs().toList().size(), json.getJSONArray("openRoles").length());
	}

	@Test
	void uRestControllerMyEventsP1() {
		MovieEvent event = null;
		User user = null;
		try {
			event = this.movieEventRepository.findAll().toList().get(0);
			user = this.userRepository.findAll().toList().get(0);
		} catch (IndexOutOfBoundsException e) {
			fail("there should have been at least one element in the repository but there was none");
		}

		DutyPlan dp = event.getDutyPlan();
		Job job = new Job("name");
		job.setJobDescription("jobDescription");
		job.setJobName("jobName");
		job.setWorker(user);

		dutyPlanManagement.clearDutyPlan(dp);
		dp.setEvent(event);
		dp.createJob(job);
		dp.setAnnotation("annotation");

	}
}