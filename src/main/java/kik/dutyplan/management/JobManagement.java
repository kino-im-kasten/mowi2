package kik.dutyplan.management;


import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobRepository;
import kik.user.data.user.User;
import kik.user.management.UserManagement;
import org.springframework.stereotype.Service;

/**
 * Managerclass for {@link Job}s
 */
@Service
public class JobManagement {

	private final UserManagement userManagement;
	private final JobRepository jobRepository;

	/**
	 * constructor for Spring
	 * @param userManagement the {@link UserManagement}
	 * @param jobRepository the {@link JobRepository}
	 */
	public JobManagement(UserManagement userManagement, JobRepository jobRepository) {
		this.userManagement = userManagement;
		this.jobRepository = jobRepository;
	}

	/**
	 * creates and Saves a Job
	 * @param form the {@link JobForm} containing needed Data
	 * @return the created {@link Job}
	 */
	public Job createJob(JobForm form){
		return jobRepository.save(new Job(form));
	}

	/**
	 * deletes a Job from the {@link JobRepository}
	 * @param job Job to be deleted
	 * @return success
	 */
	public boolean removeJob(Job job) {
		if (jobRepository.existsById(job.getId())) {
			jobRepository.delete(job);
			return true;
		}
		return false;
	}
	/**
	 * saves/updates a job in the {@link JobRepository}
	 * @param j the job to be saved
	 * @return the saved {@link Job}
	 */
	public Job save(Job j) {
		return jobRepository.save(j);
	}

	/**
	 * sets and saves the Worker in the {@link Job}
	 * @param user the {@link User} to be assigned
	 * @param job the {@link Job}
	 * @return success
	 */
	private boolean signInForRole(User user, Job job){
		job.setWorker(user);
		return jobRepository.save(job) != null;
	}


	public JobRepository getJobRepository() {
		return jobRepository;
	}

	public boolean signSelfInForJob(Job job){
		job.setWorker(userManagement.getCurrentUser().get());
		return jobRepository.save(job) != null;
	}

	public boolean signUserInForRole(Job job, User user){
		job.setWorker(user);
		return jobRepository.save(job) != null;
	}
}
