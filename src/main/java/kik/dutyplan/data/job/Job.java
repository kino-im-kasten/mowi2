package kik.dutyplan.data.job;

import kik.user.data.user.User;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;

/**
 * @author Jonas Höpner
 * @version 0.2.0
 */
@Entity
public class Job {


	@Column( length = Long.SIZE )
	@Id
	@GeneratedValue private Long id;

	private boolean open = true;
	private String jobName;
	private String jobDescription;
	@ManyToOne
	private User worker;

	public Job(){ }

	/**
	 * standard constructor for Job
	 * @param jobName the name of the job. Will initialize worker to null.
	 */
	public Job(String jobName){
		this.jobName = jobName;

		open = true;
		worker = null;
	}

	/**
	 * standard constructor. Adds the possibility to add a description.
	 * @param jobName short job name
	 * @param jobDescription longer description of the job
	 */
	public Job(String jobName, String jobDescription){
		this.jobName = jobName;
		this.jobDescription = jobDescription;

		open = true;
		worker = null;
	}

	/**
	 * Recommended Constructor. Takes needed information out of a  {@link JobForm}.
	 * @param form holds necessary data for proper {@link Job} creation
	 */
	public Job(JobForm form) {
		this.jobName = form.getJobName();
		this.jobDescription = form.getJobDescription();
		this.worker = form.getPerson();
		open = (worker == null || worker.getName() == null || worker.getName().equals(""));
	}

	/**
	 * removes the worker from the Job and sets bool open true
	 */
	public void removeWorker() {
		this.open = true;
		this.worker = null;
	}

	// GETTER & SETTER
	/**
	 * @param jobName the Name of the Job
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @param jobDescription the Description to be set
	 */
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	/**
	 * sets the Worker for the Job
	 * @param worker the User to be assigned
	 */
	public void setWorker(User worker) {
		this.worker = worker;
		this.open = worker == null;
	}

	/**
	 * @return the Job Name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @return the Job Description
	 */
	public String getJobDescription() {
		return jobDescription;
	}

	/**
	 * @return true if the Job is Open, false otherwise
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @return the Worker of the Job
	 */
	public User getWorker() {
		return worker;
	}

	/**
	 * @return returns the id of the Job
	 */
	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return getJobName().toLowerCase() + "[" + (isOpen() ? "open" : "closed") + "]";
	}
}
