package kik.dutyplan.data.job;

import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * class containing all information needed to create or edit a Job
 * @author Jonas Höpner
 * @version 1.0
 */
public class JobForm {

	@NotEmpty
	private String jobName;

	@NotEmpty
	private String jobDescription;
	@Autowired
	private UserRepository userRepository;
	private User person;

	/**
	 * constructor used by Spring
	 */
	public JobForm() {}

		/**
	 * standard constructor
	 * @param jobName name of the Job
	 * @param jobDescription description of the Job
	 * @param person the user to be assigned
	 */
	public JobForm(@NotEmpty String jobName,
				   @NotEmpty String jobDescription,
				   String person) {
		this.jobName = jobName;
		this.jobDescription = jobDescription;
		this.person = (userRepository.findById(Long.valueOf(person)).get());
	}

	/**
	 * constructor with only minimum needs. Use the setters after this!
	 * @param jobName name of the Job
	 * @param jobDescription description of the job
	 */
	public JobForm(String jobName, String jobDescription) {
		this.jobName = jobName;
		this.jobDescription = jobDescription;
	}

	// GETTER & SETTER
	public String getJobName() {
		return jobName;
	}
	public String getJobDescription() {
		return jobDescription;
	}

	public User getPerson() {
		return person;
	}

	public void setPerson(User person) {
		this.person = person;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
