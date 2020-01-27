package kik.dutyplan;

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.management.DutyPlanManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
public class DutyPlanTest {
	DutyPlanManagement management;
	@Autowired
	public DutyPlanTest(DutyPlanManagement dutyPlanManagement) {
		this.management = dutyPlanManagement;
	}

	@Test
	public void DutyPlanTestCreateJob() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();

		dp.createJob(new Job("testJob","test"));
		assertTrue(dp.contains("testJob"));
	}

	@Test
	public void DutyPlanTestCreateJobWithNoUser() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();

		dp.createJob(new Job("testJob","test"),null);
		assertTrue(dp.contains("testJob"));
	}

	@Test
	public void DutyPlanTestDeleteJob() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		Job j = new Job("testJobdeleteme","test");
		dp.createJob(j);
		dp.deleteJob(j);
		assertTrue(!dp.contains(j.getJobName()));
	}

	@Test
	public void DutyPlanTestDeleteJobWithID() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		Job j = new Job("testJobdeleteme","test");
		dp.createJob(j);
		dp.deleteJob(j.getId());
		assertTrue(!dp.contains(j.getJobName()));
	}

	@Test
	public void DutyPlanTestHasOpenRoles() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		Job j = new Job("testJobdeleteme","test");
		dp.createJob(j);
		assertTrue(dp.hasOpenJobs());
	}

	@Test
	public void DutyPlanTestGetJobById() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		Job j = management.createJob(dpId,new JobForm("tset","test"));
		assertTrue(dp.getJobById(j.getId()).equals(j));
	}


	@Test
	public void DutyPlanTestGetAllRoles() {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		management.createJob(dpId,new JobForm("tset","test"));
		assertTrue(!dp.getAllRoles().isEmpty());
	}

	@Test
	public void DutyPlanTestCreateN1() { //not filled form simulation
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		try {
			dp.createJob(null);
		} catch (Exception e) {
			return;
		}
		fail("No Exception thrown!");
	}

	@Test
	public void DutyPlanTestDeleteJobN1() { //not filled form simulation
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		assertTrue(!dp.deleteJob((Job)null));
		assertTrue(!dp.deleteJob((Long)null));
	}

	@Test
	public void DutyPlanTestEditJobN1()  {
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp = management.getDutyPlanById(dpId).get();
		try {
			dp.editJob(null,null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
