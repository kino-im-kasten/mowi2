package kik.dutyplan.data.job;

import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * class used to create new Jobs
 */
public class NewJobForm {

	@NotEmpty
	private String jobName;

	@NotEmpty
	private String jobDescription;
	@Autowired
	private UserRepository userRepository;
	private Long person;

	/**
	 * constructor used by spring
	 */
	public NewJobForm() {}

	/**
	 * standard constructor
	 * @param jobName name of the job
	 * @param jobDescription description of the job
	 * @param person ID of user to be assigned
	 */
	public NewJobForm(@NotEmpty String jobName,
					  @NotEmpty String jobDescription,
					  String person) {
		this.jobName = jobName;
		this.jobDescription = jobDescription;

		this.person = Long.parseLong(person);
	}

	/**
	 * constructor with minimal need of information, use the setters after this!
	 * @param jobName name of the Job
	 * @param jobDescription description of the job
	 */
	public NewJobForm(String jobName, String jobDescription) {
		this.jobName = jobName;
		this.jobDescription = jobDescription;
	}


	/**
	 * used to get a "normal" {@link JobForm} with the data
	 * @return {@link JobForm} with same data
	 */
	public JobForm toJobForm() {
		JobForm form = new JobForm(getJobName(),getJobDescription());
		form.setPerson(getPerson());
		return form;
	}


	//GETTER & SETTER

	public String getJobName() {

		return jobName;
	}
	public String getJobDescription() {

		return jobDescription;
	}


	public User getPerson() {
		if (person == null) {
			return null;
		}
		return userRepository.findById(person).get();
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setPerson(Long person) {
		this.person = person;
	}

	public void setPerson(String person) {
		this.person = Long.valueOf(person);
	}

	public void setPerson(User person) {
		this.person = person.getId();
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

}
