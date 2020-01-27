package kik.dutyplan.data;

import kik.dutyplan.data.job.NewJobForm;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class NewJobFormTest {

	private final UserRepository userRepository;

	@Autowired
	public NewJobFormTest(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Test
	public void NewJobFormConstructorTest(){
		User worker = userRepository.save(new User("userone", null, null));

		NewJobForm form = new NewJobForm("jobname", "some description", String.valueOf(worker.getId()));

		assertEquals("jobname", form.getJobName());
		assertEquals("some description", form.getJobDescription());

		//TODO, nullpointer exception for userRepository in NewJobForm
		//assertEquals(worker.getName(), form.getPerson().getName());
	}
}
