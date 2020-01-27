package kik.dutyplan.controller;

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.management.DutyPlanManagement;
import kik.user.management.UserManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class DutyPlanPostControllerTest {
	@Autowired
	MockMvc mvc;
	@Autowired
	DutyPlanManagement dutyPlanManagement;
	@Autowired
	UserManagement userManagement;


	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanPostTestAssignUser() throws Exception {
		DutyPlan d = dutyPlanManagement.getAllDutyPlans().get(0);
		Long jobId = dutyPlanManagement.createJob(d.getId(),new JobForm("Test","")).getId();

		MvcResult result = this.mvc.perform(
			multipart("/dutyplan/assign/")
				.param("id",d.getId().toString())
				.param("user", String.valueOf(userManagement.getCurrentUser().get().getId()))
				.param("job", jobId.toString())
		).andReturn();
		assertEquals(302, result.getResponse().getStatus());

		assertTrue(d.getJobById(jobId).getWorker().getName().equals("Bruce"));
	}

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void uDutyPlanPostTestRedirectEdit() throws Exception {
		DutyPlan d = dutyPlanManagement.getAllDutyPlans().get(0);
		MvcResult result = mvc.perform(post("/dutyplan/editJob/")
			.param("dpId", d.getId().toString())
		).andReturn();
		assertEquals(302,result.getResponse().getStatus());
	}

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	void iDutyPlanPostTestNewJob() throws Exception {
		DutyPlan d = dutyPlanManagement.getAllDutyPlans().get(0);
		MvcResult result = this.mvc.perform(
			multipart("/dutyplan/newJob/" + d.getId().toString() + "/")
				.param("jobName","postConTest")
				.param("jobDescription","desc")
		).andReturn();
		assertEquals(302,result.getResponse().getStatus());
		assertTrue(d.contains("postConTest"));
	}

	@Test
	@WithMockUser(username = "Bruce", roles = {"USER", "ORGA", "ADMIN"})
	public void iDutyPlanPostTestEditJob() throws Exception {
		DutyPlan dp1 = dutyPlanManagement.getAllDutyPlans().get(0);
		Long dpId = dp1.getId();
		Job j = dutyPlanManagement.createJob(dpId, new JobForm("TestJob2","tests"));

		MvcResult result = mvc.perform(
			multipart("/dutyplan/edit/?jobId=" + j.getId() + "&dpId=" + dpId)
			.param("jobName", "EDIT")
			.param("jobDescription","edited")
		).andReturn();
		assertEquals(302,result.getResponse().getStatus());
		assertEquals("EDIT",j.getJobName());
		assertEquals("edited",j.getJobDescription());


	}

}
