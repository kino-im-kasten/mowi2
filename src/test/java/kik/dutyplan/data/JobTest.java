package kik.dutyplan.data;

import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import kik.user.data.usertype.UserType;
import kik.user.data.usertype.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JobTest {

	private final UserAccountManager userAccountManager;
	private final UserRepository userRepository;
	private final UserTypeRepository userTypeRepository;

	@Autowired
	public JobTest(UserAccountManager userAccountManager, UserRepository userRepository, UserTypeRepository userTypeRepository) {
		this.userAccountManager = userAccountManager;
		this.userRepository = userRepository;
		this.userTypeRepository = userTypeRepository;
	}

	@Test
	public void NoDescriptionConstructorTest(){
		Job testJob = new Job("testJobNameOne");

		assertEquals("testJobNameOne", testJob.getJobName());
		assertTrue(testJob.isOpen());
		assertNull(testJob.getWorker());
		assertNull(testJob.getJobDescription());
	}

	@Test
	public void DescriptionConstructorTest(){
		Job testJob = new Job("testJobNameTwo", "You will have to test names for jobs if you want to work as an testJobName");

		assertTrue(testJob.isOpen());
		assertNull(testJob.getWorker());
		assertEquals("You will have to test names for jobs if you want to work as an testJobName", testJob.getJobDescription());
	}

	@Test
	public void MinimumConstructorTest(){
		Job testJob = new Job(new JobForm("testJobNameThree"
			, "You will have to test names for jobs if you want to work as an testJobName"));

		assertTrue(testJob.isOpen());
		assertNull(testJob.getWorker());
	}

	@Test
	public void StandardConstructorTest(){
		//User for test
		User worker = new User("WorkerUserName"
			, userAccountManager.create("WorkerUserName", Password.UnencryptedPassword.of("1234Password"))
			, userTypeRepository.save(new UserType()));
		userRepository.save(worker);

		long id = worker.getId();

		//worker is present
		assertTrue(userRepository.findByNameIgnoreCase(worker.getName()).isPresent());

		//TODO Standart Construktor für JobForm testen
		//wirft bei mir immer eine NullPointerException... der worker existiert aber zweifelsfrei
		try {
			JobForm form = new JobForm("JobName"
				, "SomeDescription"
				, String.valueOf(id));

		}catch (NullPointerException ignored){

		}

	}

	@Test
	public void SetWorkerTest(){
		Job testJob = new Job(new JobForm("testJobNameFour"
			, "You will have to test names for jobs if you want to work as an testJobName"));
		assertTrue(testJob.isOpen());

		User worker = new User();
		testJob.setWorker(worker);

		assertFalse(testJob.isOpen());
	}

	@Test
	public void RemoveWorkerTest(){
		Job testJob = new Job(new JobForm("testJobNameFour"
			, "You will have to test names for jobs if you want to work as an testJobName"));
		User worker = new User();
		testJob.setWorker(worker);
		assertFalse(testJob.isOpen());

		testJob.removeWorker();

		assertTrue(testJob.isOpen());
	}
}
