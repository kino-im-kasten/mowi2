package kik.dutyplan.data.dutyplan;

import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.event.data.event.Event;
import kik.user.data.user.User;
import org.springframework.data.util.Streamable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class containing the dutyplan for an {@link Event}
 * @author Jonas Höpner
 * @version 1.0
 */
@Entity
public class DutyPlan {
	@GeneratedValue @Id private Long id;
	private String annotation = "";
	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH,CascadeType.MERGE})
	private Event event;
	@ManyToMany(fetch = FetchType.EAGER)//, cascade = CascadeType.REFRESH)
	private Map<Long,Job> assignedJobs = new HashMap<>();
	@ManyToMany(fetch = FetchType.EAGER)//, cascade = CascadeType.ALL)
	private Map<Long,Job> openJobs = new HashMap<>();

	/**
	 * Constructor used by Spring
	 */
	public DutyPlan(){}

	/**
	 * standart constructor
	 * @param annotation the "name" of the DutyPlan
	 */
	public DutyPlan(String annotation){
		this.annotation = annotation;
	}

	/**
	 * adds a new Job to the DutyPlan. If the Job is open, it will be added to OpenJobs, else to AssignedJobs
	 * @param job the Job to be added
	 * @return success
	 */
	public boolean createJob(@NotNull Job job) {
		if (job.isOpen()) {
			openJobs.put(job.getId(), job);
			return true;
		} else {
			assignedJobs.put(job.getId(), job);
			return true;
		}
	}

	/**
	 * used to add a new Job to DutyPlan. The given user will be saved in the Job. If the user is null,
	 *  the job will be saved in openJobs, else in assignedJobs
	 * @param job The job to be added
	 * @param user the user to be assigned
	 * @return success
	 */
	public boolean createJob(@NotNull Job job, User user) {
		job.setWorker(user);
		deleteByName(job);
		if(user != null) {
			if (assignedJobs.put(job.getId(), job) != null) {
				return true;
			}
		} else {
			if (openJobs.put(job.getId(), job) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * removes a Job from the DutyPlan
	 * @param job the job to be removed
	 * @return success
	 */
	public boolean deleteJob(Job job) {

		if (assignedJobs.containsValue(job)) {
			assignedJobs.remove(job.getId(),job);
			return true;
		} else if (openJobs.containsValue(job)) {
			openJobs.remove(job.getId(),job);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * removes a Job from the DutyPlan
	 * @param jobId the ID of the job to be removed
	 * @return success
	 */
	public boolean deleteJob(Long jobId) {
		if (assignedJobs.containsKey(jobId)) {
			assignedJobs.remove(jobId);
			return true;
		} else if (openJobs.containsKey(jobId)) {
			openJobs.remove(jobId);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * edits a job with information of a {@link JobForm}
	 * @param jobId the ID of the Job
	 * @param form the form containing new Data
	 */
	public void editJob(Long jobId, JobForm form) {
		if (assignedJobs.containsKey(jobId)) {
			saveJob(jobId, form, assignedJobs, openJobs);
			return;

		} else if (openJobs.containsKey(jobId)){
			saveJob(jobId, form, openJobs, assignedJobs);
		}
	}

	/**
	 * helper function to save a job to one of the HashMaps
	 * @param jobId the id of the job to be saved
	 * @param form the form containing needed data
	 * @param jobs1 HashMap 1 (either assignedJobs or openJobs)
	 * @param jobs2 HashMap 2 (either assignedJobs or openJobs)
	 */
	private void saveJob(Long jobId, JobForm form, Map<Long, Job> jobs1, Map<Long, Job> jobs2) {
		Job j = jobs1.get(jobId);
		if (form.getPerson() != null) {
			j.setWorker(form.getPerson());
			//j.setEndTime(form.getEndTime());
			j.setJobDescription(form.getJobDescription());
			//j.setStartTime(form.getStartTime());
			j.setJobName(form.getJobName());
			jobs1.put(jobId, j);
		} else {
			jobs1.remove(jobId);
			j.setWorker(null);
			//j.setEndTime(form.getEndTime());
			j.setJobDescription(form.getJobDescription());
			//j.setStartTime(form.getStartTime());
			j.setJobName(form.getJobName());

			jobs2.put(jobId, j);
		}
	}

	/**
	 * removes a job from the DutyPlan, but by searching the HashMaps for the Jobs Name
	 * @param j the job to be removed
	 * @return success
	 */
	private boolean deleteByName(Job j) {
		if (j != null || j.getJobName() != null) {
			//try to delete form openRoles, should come first
			for (Job d : openJobs.values()) {
				if (d.getJobName().equals(j.getJobName())) {
					openJobs.remove(d.getId(), d);
					return true;
				}
			}
			//try to delete from assigned roles
			for (Job d : assignedJobs.values()) {
				if (d.getJobName().equals(j.getJobName())) {
					assignedJobs.remove(d.getId(), d);
					return true;
				}
			}
		} else {
			System.err.println("In DutyPlan.deleteByName was either Job or its name null!");
		}
		return false;
	}

	/**
	 * checks if there is any Object related to attribute
	 * @param attribute the string to be checked
	 * @return true if exists, false otherwise
	 */
	public boolean contains(String attribute) {
		return annotation.contains(attribute) || checkContains(attribute, assignedJobs, openJobs);
	}

	/**
	 * Helper function, that checks the HashMaps, if a Job is saved
	 * @param attribute String to be searched for
	 * @param jobs1 HashMap 1 (either assignedJobs or openJobs)
	 * @param jobs2 HashMap 1 (either assignedJobs or openJobs)
	 * @return success
	 */
	private static boolean checkContains(String attribute, Map<Long, Job> jobs1, Map<Long, Job> jobs2) {
		for (Job j : jobs1.values()) {
			if (j.getJobDescription().contains(attribute)
				|| j.getJobName().contains(attribute)) {
				return true;
			}
		}
		for (Job j : jobs2.values()) {
			if (j.getJobDescription().contains(attribute)
				|| j.getJobName().contains(attribute)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * @return true, if there are unassigned Jobs
	 */
	public boolean hasOpenJobs() {
		return !openJobs.isEmpty();
	}

	/**
	 * unassigned a user from a job, and moves the job to openJobs
	 * @param uId the user to be unassigned
	 * @param jobId the job to be opened
	 * @return success
	 */
	public boolean signUserOut(Long uId, Long jobId) {
		Job j = assignedJobs.get(jobId);
		if (j.getWorker().getId() == uId) {
			j.removeWorker();
			assignedJobs.remove(jobId, j);
			openJobs.put(jobId,j);
			return true;
		}
		return false;
	}

	/**
	 * clears all jobs and the link to the Event
	 */
	public void deleteDutyPlan() {
		openJobs.clear();
		assignedJobs.clear();
		event = null;
	}

	/**
	 * gets a specific job
	 * @param jobId the Job to be returned
	 * @return the searched Job
	 */
	public Job getJobById(Long jobId) {
		if (assignedJobs.containsKey(jobId)) {
			return assignedJobs.get(jobId);
		} else if (openJobs.containsKey(jobId)) {
			return openJobs.get(jobId);
		} else {
			return null;
		}
	}

	//GETTER & SETTER

	public Long getId() {
		return id;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Streamable<Job> getOpenJobs() {
		return Streamable.of(openJobs.values());
	}

	public Streamable<Job> getAssignedJobs() {
		return Streamable.of(assignedJobs.values());
	}


	/**
	 * Gets All Jobs sorted of this DutyPlan
	 * @return a List of Sorted Jobs
	 */
	public Streamable<Job> getAllRoles() {
		var ret = getAssignedJobs().and(getOpenJobs());
		var y = ret.stream().sorted(Comparator.comparing(Job::toString));
		return Streamable.of(y.collect(Collectors.toList()));
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return this.event;
	}


	/**
	 * Returns true if a user with an id is connected to this job
	 * 
	 * @param uuid The user id
	 * @return Returns true if connected
	 */
	public boolean containsUser(UUID uuid) {
		for (Job job : this.getAssignedJobs()) {
			if (job.getWorker().getUuid().compareTo(uuid) == 0) {
				 return true;
			 }
		}
		return false;
	}

	/**
	 * Returns all Jobs if a user has multiple in one dutyplan
	 * 
	 * @param uuid The user id
	 * @return All jobs connected to that user for that dutyplan
	 */
	public List<Job> getJobsForWorker(UUID uuid) {
		List<Job> jobs = new ArrayList<>();
		for (Job job : this.getAssignedJobs()) {
			if (job.getWorker().getUuid().compareTo(uuid) == 0) {
				jobs.add(job);
			 }
		}
		return jobs;
	}

}
