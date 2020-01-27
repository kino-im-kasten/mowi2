package kik.dutyplan.controller;

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobContainer;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.data.job.NewJobForm;
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.user.data.user.User;
import kik.user.management.UserManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.AssertTrue;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class DutyPlanGetControllerTests {
	@Autowired
	MockMvc mvc;
	@Autowired
	DutyPlanManagement dutyPlanManagement;
	@Autowired
	UserManagement userManagement;
	@Autowired
	MovieEventManagement movieEventManagement;

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetControllerAssignTestP() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");

		MvcResult result = this.mvc.perform(get("/dutyplan/assign/")
			.param("dpId", dpId.toString()))
			.andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute("jobs") instanceof Streamable<?>);
	}

	@Test
	@WithMockUser(username = "Robin", roles = {"USER"})
	void iDutyPlanGetControllerAssignTestN() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");

		MvcResult result = this.mvc.perform(get("/dutyplan/assign/")
			.param("dpId", dpId.toString()))
			.andReturn();
		assertEquals(403, result.getResponse().getStatus());

	}

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetControllerUnAssignUserTestP() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");
		Job j = dutyPlanManagement.createJob(dpId,new JobForm("Tset",""));
		assertNull(j.getWorker());
		DutyPlan dp = dutyPlanManagement.getDutyPlanById(dpId).get();
		dp.createJob(j,userManagement.getUserRepository().findAll().toList().get(0));
		assertNotNull(j.getWorker());

		MvcResult result = this.mvc.perform(get("/dutyplan/assignme/")
			.param("dpId", dpId.toString())
			.param("jobId", j.getId().toString())
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());

		assertNull(j.getWorker());
	}


	@Test
	@WithMockUser(username = "Robin", roles = {"USER"})
	void iDutyPlanGetControllerUnAssignUserTestN() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");
		Job j = dutyPlanManagement.createJob(dpId,new JobForm("Tset",""));
		assertNull(j.getWorker());
		DutyPlan dp = dutyPlanManagement.getDutyPlanById(dpId).get();
		dp.createJob(j,userManagement.getUserRepository().findAll().toList().get(0));
		assertNotNull(j.getWorker());

		MvcResult result = this.mvc.perform(get("/dutyplan/assignme/")
			.param("dpId", dpId.toString())
			.param("jobId", j.getId().toString())
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());

		assertNotNull(j.getWorker());
	}


	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetControllerAssignMeTest() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");
		Long jobId = dutyPlanManagement.createJob(dpId, new JobForm("TestJob","")).getId();


		MvcResult result = this.mvc.perform(get("/dutyplan/assignme/")
			.param("dpId", dpId.toString())
			.param("jobId", jobId.toString())
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());

		DutyPlan dp = dutyPlanManagement.getDutyPlanById(dpId).get();
		assertTrue(dp.getOpenJobs().toList().size() == 3); //only the pregenerated jobs left, ours is assigned
		assertTrue(dp.getJobById(jobId).getWorker().getName().equals("Bruce"));
	}

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetUnAssignMeTest() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");
		Long jobId = dutyPlanManagement.createJob(dpId, new JobForm("TestJob","")).getId();


		MvcResult result = this.mvc.perform(get("/dutyplan/assignme/")
			.param("dpId", dpId.toString())
			.param("jobId", jobId.toString())
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());


		result = this.mvc.perform(get("/dutyplan/assignme/")
			.param("dpId", dpId.toString())
			.param("jobId", jobId.toString())
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());



		DutyPlan dp = dutyPlanManagement.getDutyPlanById(dpId).get();
		assertTrue(dp.getOpenJobs().toList().size() == 4); //our job is open again
		assertNull(dp.getJobById(jobId).getWorker());
	}

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetNewJobTest() throws Exception {
		Long dpId = dutyPlanManagement.createDutyPlan("Testplan");
		MvcResult result = this.mvc.perform(get("/dutyplan/newJob/" + dpId + "/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute("form") instanceof NewJobForm);
	}


	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetSignOutTest() throws Exception {
		DutyPlan dp = movieEventManagement.getMovieEventRepository().findAll().toList().get(0).getDutyPlan();
		Long dpId = dp.getId();
		Long jobId1 = dutyPlanManagement.createJob(dpId, new JobForm("Test1","")).getId();
		Long jobId2 = dutyPlanManagement.createJob(dpId, new JobForm("Test2","")).getId();
		dutyPlanManagement.getDutyPlanById(dpId).get().createJob(
			dutyPlanManagement.getDutyPlanById(dpId).get().getJobById(jobId1),userManagement.getCurrentUser().get());
		dutyPlanManagement.getDutyPlanById(dpId).get().createJob(
			dutyPlanManagement.getDutyPlanById(dpId).get().getJobById(jobId2),userManagement.getCurrentUser().get());

		MvcResult result = this.mvc.perform(get("/dutyplan/signOut/")
		.param("dpId", dpId.toString())
		).andReturn();

		assertEquals(302, result.getResponse().getStatus());
		assertEquals(5, dutyPlanManagement.getDutyPlanById(dpId).get().getOpenJobs().toList().size());
	}


	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetEditJobFormTest() throws Exception {
		DutyPlan dp = movieEventManagement.getMovieEventRepository().findAll().toList().get(0).getDutyPlan();
		Long dpId = dp.getId();
		MvcResult result = this.mvc.perform(get("/dutyplan/editJob/")
			.param("dpId", dpId.toString())
		).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute("jobs") instanceof Streamable<?>);
		assertTrue(result.getModelAndView().getModelMap().getAttribute("job") instanceof JobContainer);
	}


	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetEditJobTest() throws Exception {
		DutyPlan dp = movieEventManagement.getMovieEventRepository().findAll().toList().get(0).getDutyPlan();
		Long dpId = dp.getId();
		Long jobId1 = dutyPlanManagement.createJob(dpId, new JobForm("Test1","")).getId();
		MvcResult result = this.mvc.perform(get("/dutyplan/edit/")
			.param("dpId", dpId.toString())
			.param("jobId", jobId1.toString())
		).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertEquals(((JobForm)result.getModelAndView().getModelMap().getAttribute("form")).getJobName(),"Test1");
		assertEquals(((JobForm)result.getModelAndView().getModelMap().getAttribute("form")).getJobDescription(),"");
		assertNull(((JobForm)result.getModelAndView().getModelMap().getAttribute("form")).getPerson());
		assertTrue(!((Streamable<User>)result.getModelAndView().getModelMap().getAttribute("persons")).toList().isEmpty());
	}



	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanGetDeleteJobTest() throws Exception {
		DutyPlan dp = movieEventManagement.getMovieEventRepository().findAll().toList().get(0).getDutyPlan();
		Long dpId = dp.getId();
		Long jobId1 = dutyPlanManagement.createJob(dpId, new JobForm("Test1","")).getId();
		MvcResult result = this.mvc.perform(get("/dutyplan/deleteJob/" + dpId + "/" + jobId1 + "/")
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());

		assertNull(dutyPlanManagement.getDutyPlanById(dpId).get().getJobById(jobId1));
	}


}
