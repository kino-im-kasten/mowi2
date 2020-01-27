/*package kik.controller;

import kik.user.RegistrationForm;
import kik.user.User;
import kik.user.UserManagement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DutyPlanTest {
	private DutyPlan dutyPlan;
	private UserManagement userManagement;
	User user;
	DutyPlanTest() {
		user = userManagement.createUser(new RegistrationForm("Batman","123","ORGA"));

	}
	@Test
	void dutyPlanAssign() { //AT0501
		dutyPlan.assign(EVENT1,"KARTENVERKÄUFER",user);
		assertTrue(dutyPlan.getAssignments(user).contains("KARTENVERKÄUFER"));
	}

	@Test
	void dutyPlanUnassign() { //AT0502
		dutyPlan.unassign(EVENT1,"KARTENVERKÄUFER", user);
		assertTrue(!dutyPlan.getAssignments(user).contains("KARTENVERKÄUFER"));
	}

	@Test
	void dutyPlanAddJob() { //AT0503
		dutyPlan.addJobToEvent(EVENT1,"KARTENVERKÄUFER");
		assertTrue(dutyPlan.getJobsForEvent(EVENT1).contains("KARTENVERKÄUFER"));
	}

	@Test
	void dutyPlanRemoveJob() {
		dutyPlan.addJobToEvent(EVENT1,"Eisverkauf");
		dutyPlan.assign(EVENT1,"Eisverkauf",user);
		dutyPlan.removeJob(EVENT1,"Eisverkauf");
		assertTrue(!dutyPlan.getAssignments(user).contains("Eisverkäufer"));
		assertTrue(!dutyPlan.getJobsForEvent(EVENT1).contains("Eisverkäufer"));
	}

}

//TODO: test for redirection*/