package kik.dutyplan.management;

import kik.config.Configuration;
import kik.dutyplan.data.dutyplan.DutyPlanRepository;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.data.job.Job;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventRepository;
import kik.user.data.user.User;
import kik.user.management.UserManagement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DutyPlanManagement {
	private DutyPlanRepository dutyPlanRepository;
	private JobManagement jobManagement;
	private MovieEventRepository movieEventRepository; //can't use management, because it would create a bean circle
	private UserManagement userManagement;
	private Configuration configuration;

	/**
	 * standard constructor. Used by Spring. Do not use to instantiate the management!
	 * @param dutyPlanRepository the repository of dutyplans. Filled by Spring.
	 * @param jobManagement the management class for jobs. Filled by Spring.
	 */
	public DutyPlanManagement(DutyPlanRepository dutyPlanRepository, JobManagement jobManagement,
							  MovieEventRepository movieEventRepository,
							  UserManagement userManagement,
							  Configuration configuration) {
		this.dutyPlanRepository = dutyPlanRepository;
		this.jobManagement = jobManagement;
		this.movieEventRepository = movieEventRepository;
		this.userManagement = userManagement;
		this.configuration = configuration;
	}

	/**
	 * Creates a new Dutyplan.
	 * @param annotation the annotation for the dutyplan.
	 * @return the ID of the created dutyplan.
	 */
	public Long createDutyPlan(String annotation) {
		DutyPlan d = new DutyPlan(annotation);
		dutyPlanRepository.save(d);
		init(d);
		return d.getId();
	}

	/**
	 * initializes a DutyPlan with default values proposed by KiK
	 * @param d the {@link DutyPlan} to be initialized
	 * @return the DutyPlan
	 */
	private DutyPlan init(DutyPlan d) {

		for (Configuration.ProtoJob j : configuration.getDefaultJobs()) {
			createJob(d.getId(),new JobForm(j.getName(),j.getDescription()));
		}

		update(d);
		return d;
	}

	/**
	 * Creates a new Dutyplan.
	 * @param annotation the annotation for the dutyplan.
	 * @return the created DutyPlan.
	 */
	public DutyPlan createDutyPlanObject(String annotation, Event event) {
		DutyPlan d = new DutyPlan(annotation);
		dutyPlanRepository.save(d);
		d.setEvent(event);
		init(d);
		dutyPlanRepository.save(d);
		return d;
	}

	/**
	 * adds a Job to a existing Dutyplan.
	 * @param dutyPlanId the ID of the chosen DutyPlan.
	 * @param form the job to be added.
	 * @return the created Job.
	 */
	public Job createJob(Long dutyPlanId, JobForm form) {
		Job j = jobManagement.createJob(form);
		DutyPlan d;
		if (dutyPlanRepository.existsById(dutyPlanId)) {
			d = dutyPlanRepository.findById(dutyPlanId).get();
			d.createJob(j);
			dutyPlanRepository.save(d);
			return j;
		} else {
			throw new NoSuchElementException("This DutyPlan with ID == " + dutyPlanId.toString() + " is not existent!");
		}
	}

	/**
	 * removes a job from a dutyplan
	 * @param dutyPlanId the ID of the chosen dutyplan.
	 * @param role the job to be removed
	 * @return True if successful, false otherwise.
	 */
	public boolean removeJob(Long dutyPlanId, Job role) {
		DutyPlan d;
		if(dutyPlanRepository.existsById(dutyPlanId)) {
			d = dutyPlanRepository.findById(dutyPlanId).get();
			d.deleteJob(role);
			dutyPlanRepository.save(d);
			jobManagement.removeJob(role);
			return true;
		} else {
			// if this DutyPlan is not existent, should we nevertheless delete the given Job?
			// jobManagement.removeJob(role);
			return false;
		}
	}

	/**
	 * Edits a job.
	 * @param dutyPlanId
	 * @param form
	 */
	public void editJob(Long dutyPlanId,Long jobId, JobForm form) {
		DutyPlan d;
		if(dutyPlanRepository.existsById(dutyPlanId)) {
			d = dutyPlanRepository.findById(dutyPlanId).get();
			d.editJob(jobId,form);
			dutyPlanRepository.save(d);
		} else {
			throw new NoSuchElementException("This DutyPlan with ID == " + dutyPlanId.toString() + " is not existent!");
		}
	}

	public List<DutyPlan> getAllDutyPlans() {
		return dutyPlanRepository.findAll().toList();
	}

	/**
	 * filters DutyPlans and returns them
	 * @param attribute String to be searched for
	 * @return list with found {@link DutyPlan}
	 */
	public List<DutyPlan> getFilteredDutyPlans(String attribute) {
		List<DutyPlan> list = new ArrayList<>();
		for (DutyPlan d : getAllDutyPlans()) {
			if (d.contains(attribute)) {
				list.add(d);
			}
		}
		return list;
	}



	/**
	 * used to sign a user out from all jobs of a given Event
	 * @param dpId ID of the DutyPlan
	 * @param user user to be unassigned
	 * @return success
	 */
	public boolean signOutFromDutyPlan(Long dpId, String user) {


		MovieEvent event = movieEventRepository.findById(
			dutyPlanRepository.findById(dpId).get().getEvent().getId()
		).get();
		DutyPlan d = event.getDutyPlan();
		List<Job> jobs = d.getAssignedJobs().toList(); //cannot be a simple getAssignedJobs(), because this would
												       // be call by reference, and deleted Jobs would be removed there
		                                               // and brick the for loop. Why ever.
		for (Job j : jobs) {
			if (j.getWorker().getName().toLowerCase().equals(user.toLowerCase())) {
				j.removeWorker();
				d.deleteJob(j);
				d.createJob(j);
			}
		}
		update(d);
		event.setDutyPlan(d);
		return true;


	}

	/**
	 * clears all jobs and the link to the Event
	 * @param d DutyPlan to be cleared
	 */
	public void clearDutyPlan(DutyPlan d) {
		var jobs = d.getAllRoles().toList();
		d.deleteDutyPlan();
		for (Job j : jobs) {
			jobManagement.removeJob(j);
		}
	}



	/**
	 * getter for DutyPlan by ID
	 * @param dpId ID of the DutyPlan
	 * @return {@link DutyPlan} that was searched for
	 */
	public Optional<DutyPlan> getDutyPlanById(Long dpId) {
		return dutyPlanRepository.findById(dpId);
	}

	/**
	 * getter for the {@link JobManagement}
	 * @return {@link JobManagement}
	 */
	public JobManagement getJobManagement() {
		return jobManagement;
	}

	/**
	 * saves the {@link DutyPlan} in the {@link DutyPlanRepository}
	 * @param d the DutyPlan to be updated
	 * @return success
	 */
	public boolean update(DutyPlan d) {
		if (d != null) {
			return dutyPlanRepository.save(d) != null;
		} else {
			System.err.println("Dutyplan to update must not be null!");
			return false;
		}
	}

	/**
	 * deletes a DutyPlan from the {@link DutyPlanRepository}
	 * @param dpId the ID of the {@link DutyPlan} to be deleted
	 */
	public void deleteDutyPlan(Long dpId) {
		dutyPlanRepository.deleteById(dpId);
	}


	/**
	 * helps to redirect to the page where we were before getting here
	 * @param redirectTo integer which describes the site
	 * @return the redirect string
	 */
	public String getRedirection(Integer redirectTo) {
		if (redirectTo == null) {
			return "redirect:/";
		}

		String ret;
		switch (redirectTo) {
			case 1:
				ret = "redirect:/movieEvents/";
				break;
			case 2:
				ret = "redirect:/specialEvents/";
				break;
			default:
				ret = "redirect:/";
		}

		return ret;
	}


}
