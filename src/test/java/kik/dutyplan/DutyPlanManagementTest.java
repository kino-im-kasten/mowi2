package kik.dutyplan;

import javassist.tools.rmi.ObjectNotFoundException;
import kik.booking.data.Booking;
import kik.booking.data.BookingRepository;
import kik.config.Configuration;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.management.DutyPlanManagement;

import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.management.movieEvent.MovieEventInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class DutyPlanManagementTest {
private DutyPlanManagement management;
private kik.event.management.movieEvent.MovieEventManagement movieEventManagement;
private MovieEventInitializer movieEventInitializer;
private BookingRepository bookingRepository;
private Configuration configuration;

	@Autowired
	public DutyPlanManagementTest(DutyPlanManagement management,
								 kik.event.management.movieEvent.MovieEventManagement movieEventManagement,
								 MovieEventInitializer movieEventInitializer,
								 BookingRepository bookingRepository,
								 Configuration configuration) {
			this.management = management;
			this.movieEventManagement = movieEventManagement;
			this.movieEventInitializer = movieEventInitializer;
			this.bookingRepository = bookingRepository;
			this.configuration = configuration;
	}


	@Test
	public void DutyPlanManagementCreateDutyPlanTestP1() { //create Test
		Long dpId = management.createDutyPlan("TestPlan1");
		DutyPlan dp1 = management.getDutyPlanById(dpId).get();
		assertTrue(dp1.getId() != null);
	}

	@Test
	public void DutyPlanManagementInitTestP2() { //init Test
		Long dpId = management.createDutyPlan("test");
		DutyPlan dp2 = management.getDutyPlanById(dpId).get();
		for (Configuration.ProtoJob j : configuration.getDefaultJobs()) {
			assertTrue(dp2.contains(j.getName()));
		}
	}


	@Test
	public void DutyPlanManagementTestEvent() throws ObjectNotFoundException {
		Booking sampleBooking = bookingRepository.findByTbNumber(String.valueOf(11235813)).get();
		MovieEvent movieEvent = movieEventInitializer.initializeMovieEvent(
		new MovieEventForm("Blade", LocalDate.now(),"20:30",
				String.valueOf(sampleBooking.getId()),"PUBLIC"));

	//create a job for DutyPlan
		management.createJob(movieEvent.getDutyPlan().getId(),
			new JobForm("Kartenverkäufer","verkauft Karten, der Mensch"));
	//update movieEvents DutyPlan
		movieEvent.setDutyPlan(management.getDutyPlanById(movieEvent.getDutyPlan().getId()).get());
		//update our movieEvent
	movieEventManagement.getMovieEventRepository().save(movieEvent);
		movieEvent = movieEventManagement.findMovieEventById(movieEvent.getId()).get(); // is not necessary
		assertTrue(!movieEvent.getDutyPlan().getOpenJobs().isEmpty());
	assertTrue(movieEvent.getDutyPlan().contains("Kartenverkäufer"));

	}


	@Test
	public void DutyPlanManagementTestCreateJobP1() {
		Long dpId = management.createDutyPlan("tests");
		DutyPlan dp1 = management.getDutyPlanById(dpId).get();
		management.createJob(dpId, new JobForm("TestJob","tests"));

		assertTrue(dp1.contains("TestJob"));
	}

	@Test
	public void DutyPlanManagementTestRemoveJobP1() {
		Long dpId = management.createDutyPlan("tests");
		DutyPlan dp1 = management.getDutyPlanById(dpId).get();
		Job j = management.createJob(dpId, new JobForm("TestJob1","tests"));
		assertTrue(dp1.contains("TestJob"));

		management.removeJob(dpId,j);
		dp1 =  management.getDutyPlanById(dpId).get();
		assertTrue(!dp1.contains("TestJob1"));
	}


	@Test
	public void DutyPlanManagementClearDutyPlan() {
		Long dpId = management.createDutyPlan("tests");
		DutyPlan dp1 = management.getDutyPlanById(dpId).get();
		Job j = management.createJob(dpId, new JobForm("TestJob2","tests"));

		management.clearDutyPlan(dp1);
		assertTrue(!management.getJobManagement().getJobRepository().existsById(j.getId()));
		assertTrue(dp1.getAllRoles().isEmpty());
		assertTrue(dp1.getEvent() == null);
	}

	@Test
	public void DutyPlanManagementTestEditJob() {
		Long dpId = management.createDutyPlan("tests");
		DutyPlan dp1 = management.getDutyPlanById(dpId).get();
		Job j = management.createJob(dpId, new JobForm("TestJob2","tests"));
		assertTrue(dp1.contains("TestJob2"));
		management.editJob(dpId,j.getId(),new JobForm("Edited","edited"));

		assertTrue(dp1.contains("edited"));
	}

	@Test
	public void DutyPlanManagementTestGetAllDutyPlans() {
		Long dpId = management.createDutyPlan("tests1");

		assertTrue(management.getAllDutyPlans() instanceof List);
		assertTrue(!management.getAllDutyPlans().isEmpty());
		assertTrue(management.getAllDutyPlans().contains(management.getDutyPlanById(dpId).get()));
	}

	@Test
	public void DutyPlanManagementTestFilteredDutyPlans() {
		Long dpId1 = management.createDutyPlan("filter");
		Long dpId2 = management.createDutyPlan("coffee");

		assertTrue(management.getFilteredDutyPlans("coffee").contains(management.getDutyPlanById(dpId2).get()));
		assertTrue(!management.getFilteredDutyPlans("coffee").contains(management.getDutyPlanById(dpId1).get()));
	}

	@Test
	public void DutyPlanManagementTestSignOutFromDutyPlan() {
		//TODO: write Test
	}


	@Test
	public void DutyPlanManagementTestUpdate() {
		Long dpId1 = management.createDutyPlan("update");
		DutyPlan dp1 = management.getDutyPlanById(dpId1).get();

		dp1.setAnnotation("annotation1");
		management.update(dp1);
		assertTrue(management.getDutyPlanById(dpId1).get().getAnnotation().equals("annotation1"));

	}


	@Test
	public void DutyPlanManagementTestDelete() {
		Long dpId1 = management.createDutyPlan("deleteme");
		management.deleteDutyPlan(dpId1);

		assertTrue(management.getDutyPlanById(dpId1).isEmpty());
	}


}
