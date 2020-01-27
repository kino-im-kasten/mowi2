package kik.dutyplan.controller;

import kik.dutyplan.data.job.JobContainer;
import kik.dutyplan.data.dutyplan.AssignForm;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.data.job.JobRepository;
import kik.dutyplan.data.job.NewJobForm;
import kik.dutyplan.management.DutyPlanManagement;
import kik.user.data.user.User;
import kik.user.management.UserManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * class controlling the Post-Requests
 * @author Jonas Höpner
 * @version 1.0.0
 */
@Controller
public class DutyPlanPostController {
private DutyPlanManagement dutyPlanManagement;
private UserManagement userManagement;
private JobRepository jobRepository;

	/**
	 * standard constructor, used by Spring
	 * @param dutyPlanManagement the {@link DutyPlanManagement}
	 * @param userManagement the {@link UserManagement}
	 * @param jobRepository the {@link JobRepository}
	 */
	public DutyPlanPostController(DutyPlanManagement dutyPlanManagement,
							  UserManagement userManagement,
							  JobRepository jobRepository) {
		this.dutyPlanManagement = dutyPlanManagement;
		this.jobRepository = jobRepository;
		this.userManagement = userManagement;
	}

	/**
	 * assignes the {@link User} of the {@link AssignForm} to the {@link Job}
	 * @param form the {@link AssignForm} containing the {@link User}
	 * @param id the ID of the {@link DutyPlan}
	 * @return redirect back to movieEvents.html
	 */
	@PreAuthorize("hasRole('USER')")
	@PostMapping("/dutyplan/assign/")
	public String assignUser(@ModelAttribute AssignForm form, @RequestParam Long id,
							 @RequestParam(required = false) Integer redirectTo) {
		DutyPlan d = dutyPlanManagement.getDutyPlanById(id).get();

		Job j = jobRepository.findById(form.getJob()).get();
		User u = userManagement.getUserRepository().findById(form.getUser()).get();

		d.createJob(j,u);
		dutyPlanManagement.update(d);
		//return "redirect:/movieEvents/";
		return dutyPlanManagement.getRedirection(redirectTo);
	}

	/**
	 * a redirection from editJob to edit, gets the ID from the Form
	 * @param job the {@link JobContainer} with the JobID
	 * @param dpId the {@link DutyPlan} in which the job is
	 * @return redirect back to movieEvents.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@PostMapping("/dutyplan/editJob/")
	public String editJob(@ModelAttribute JobContainer job, @RequestParam Long dpId,
						  @RequestParam(required = false) Integer redirectTo) {
		return "redirect:/dutyplan/edit/?dpId=" +dpId + "&jobId=" + job.getId();
	}

	/**
	 * saves a new Job in the {@link DutyPlan}
	 * @param id the ID of the {@link DutyPlan}
	 * @param form the {@link NewJobForm} containing needed Data
	 * @return redirect back to movieEvents.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@PostMapping("/dutyplan/newJob/{id}/")
	public String newJob(@PathVariable Long id, @ModelAttribute NewJobForm form,
						 @RequestParam(required = false) Integer redirectTo) {
		DutyPlan d = dutyPlanManagement.getDutyPlanById(id).get();
		Job j = new Job(form.toJobForm());
		jobRepository.save(j);
		d.createJob(j);
		dutyPlanManagement.update(d);
		//return "redirect:/movieEvents/";
		return dutyPlanManagement.getRedirection(redirectTo);
	}

	/**
	 * edits the Job with given Data
	 * @param jobId the ID of the {@link Job}
	 * @param form the {@link JobForm} containing all the data
	 * @param dpId the ID of the {@link DutyPlan}
	 * @return redirect back to movieEvents.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@PostMapping("/dutyplan/edit/")
	public String editJob(@RequestParam Long jobId, @ModelAttribute JobForm form, @RequestParam Long dpId,
						  @RequestParam(required = false) Integer redirectTo) {
		Job j = jobRepository.findById(jobId).get();
		j.setJobName(form.getJobName());
		j.setJobDescription(form.getJobDescription());
		if (form.getPerson() != null) {
			j.setWorker(form.getPerson()); // is not in Frontend, but needs to be set
		}

		jobRepository.save(j);
		DutyPlan d = dutyPlanManagement.getDutyPlanById(dpId).get();
		d.createJob(j,j.getWorker());
		dutyPlanManagement.update(d);
		//return "redirect:/movieEvents/";
		return dutyPlanManagement.getRedirection(redirectTo);
	}

}
