package kik.dutyplan.controller;

import kik.dutyplan.data.job.JobContainer;
import kik.dutyplan.data.dutyplan.AssignForm;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobForm;
import kik.dutyplan.data.job.NewJobForm;
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.event.Event;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.user.data.user.User;
import kik.user.management.UserManagement;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * controller class for get methods of DutyPlan
 * @author Jonas Höpner
 * @version 1.0.0
 */
@Controller
public class DutyPlanGetController {
	private DutyPlanManagement dutyPlanManagement;
	private UserManagement userManagement;
	private MovieEventManagement movieEventManagement;

	/**
	 * standard constructor, used by Spring
	 * @param dutyPlanManagement The {@link DutyPlanManagement}
	 * @param userManagement The {@link UserManagement}
	 * @param movieEventManagement The {@link MovieEventManagement}
	 */
	public DutyPlanGetController(DutyPlanManagement dutyPlanManagement, UserManagement userManagement,
								 MovieEventManagement movieEventManagement) {
		this.dutyPlanManagement = dutyPlanManagement;
		this.userManagement = userManagement;
		this.movieEventManagement = movieEventManagement;
	}


	/**
	 * Provides the possibility to assign a {@link User} to a Job
	 * @param model The Model of the HTML page
	 * @param dpId the ID of the {@link DutyPlan}, where the User wants to Sign in
	 * @return the Template with the needed Form
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/dutyplan/assign/")
	public String assignUser(Model model, @RequestParam Long dpId,
							 @RequestParam(required = false) Integer redirectTo) {
		AssignForm form = new AssignForm();
		model.addAttribute("id", dpId);
		model.addAttribute("form",form);
		model.addAttribute("selection", Long.valueOf(0));
		model.addAttribute("redirectTo", redirectTo);

		//getting users, ORGA and USER should only be able to assign themselves
		var roles = userManagement.getCurrentUser().get().getUserAccount().getRoles();
		List<String> roleStrings = new ArrayList<>();
		roles.forEach(it ->
			roleStrings.add(it.getName()) );
		if (roleStrings.contains("ADMIN")) {
			model.addAttribute("users", userManagement.getUserRepository().findByDeleted(false));
		} else {
			model.addAttribute("users", userManagement.getCurrentUser().get());
		}

		model.addAttribute("jobs",
			dutyPlanManagement.getDutyPlanById(dpId).get().getOpenJobs());
		/*if (dutyPlanManagement.getDutyPlanById(dpId).get().getOpenJobs().isEmpty()) {
			//when there is no Job left to be assigned, create a new one!
			NewJobForm newJobForm = new NewJobForm();
			model.addAttribute("form",newJobForm);
			return "dutyplan/newJob";
		}*/
		return "dutyplan/assignUser";


	}

	/**
	 * Assigns the current user to Given Job.
	 * @param jobId the {@link Job} to be assigned
	 * @param dpId the {@link DutyPlan} in which the Job is
	 * @return the Template with the needed Form
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/dutyplan/assignme/")
	public String assignMe(@RequestParam Long jobId, @RequestParam Long dpId,
						   @RequestParam(required = false) Integer redirectTo) {
		if (dutyPlanManagement.getDutyPlanById(dpId).isPresent()) {
			DutyPlan dp = dutyPlanManagement.getDutyPlanById(dpId).get();
			User me = userManagement.getCurrentUser().get();

			//is a worker already assigned to this job?
			if (!dp.getJobById(jobId).isOpen()) {

				//if we are admin/ORGA, we unassign the given user on click, but without signing us in!
				var roles = userManagement.getCurrentUser().get().getUserAccount().getRoles();
				List<String> roleStrings = new ArrayList<>();
				roles.forEach(it ->
					roleStrings.add(it.getName()) );
				if (roleStrings.contains("ADMIN") || roleStrings.contains("ORGA")) {
					dp.signUserOut(dp.getJobById(jobId).getWorker().getId(), jobId);
					dutyPlanManagement.update(dp);
				} else if (dp.getJobById(jobId).getWorker().equals(me)) { // if we are assigned, unassign us!
					dp.signUserOut(me.getId(), jobId);
					dutyPlanManagement.update(dp);
				}

				//return "redirect:/movieEvents/";
				return dutyPlanManagement.getRedirection(redirectTo);
			}

			//else, if noone is assigned, assign us!
			Job j = dp.getJobById(jobId);
			if (j == null) {
				//return "redirect:/movieEvents/";
				return dutyPlanManagement.getRedirection(redirectTo);
			}

			dp.createJob(j, me); //updates the job
			dutyPlanManagement.update(dp);
		}
		//return "redirect:/movieEvents/";
		return dutyPlanManagement.getRedirection(redirectTo);
	}


	/**
	 * Provides the functionality for creating a new Job
	 * @param model The Model of the HTML page
	 * @param id the ID of the {@link DutyPlan} for the new Job
	 * @return the Template with needed Form
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/dutyplan/newJob/{id}/")
	public String newJob(Model model, @PathVariable Long id,
						 @RequestParam(required = false) Integer redirectTo) {
		NewJobForm form = new NewJobForm();
		model.addAttribute("form",form);
		model.addAttribute("id",id);
		model.addAttribute("redirectTo", redirectTo);
		return "dutyplan/newJob";
	}

	/**
	 * Signs a user out of a Job
	 * @param dpId The event containing the DutyPlan, where the user wants to sign out
	 * @param authentication used to get the current User
	 * @return redirect back to movieEvents.html
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/dutyplan/signOut/")
	public String signUserOut(@RequestParam Long dpId, Authentication authentication,
							  @RequestParam(required = false) Integer redirectTo) {
		String user = ((UserDetails)authentication.getPrincipal()).getUsername();
		dutyPlanManagement.signOutFromDutyPlan(dpId,user);

		//return "redirect:/movieEvents/";
		return dutyPlanManagement.getRedirection(redirectTo);
	}

	@PreAuthorize("hasRole('ORGA')")
	@GetMapping("/dutyplan/editJob/")
	public String editJobForm(@RequestParam Long dpId, Model model,
							  @RequestParam(required = false) Integer redirectTo) {
		model.addAttribute("jobs", dutyPlanManagement.getDutyPlanById(dpId).get().getAllRoles());
		model.addAttribute("job", new JobContainer());
		model.addAttribute("dpId",dpId);
		model.addAttribute("redirectTo", redirectTo);
		return "dutyplan/editJobForm";
	}

	/**
	 * used to edit a {@link Job}
	 * @param jobId the {@link Job} to be edited
	 * @param dpId the ID of the {@link DutyPlan} containing the job
	 * @param model The Model of the HTML page
	 * @return the Template containing the Form
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping("/dutyplan/edit/")
	public String editJob(@RequestParam Long jobId, @RequestParam Long dpId, Model model,
						  @RequestParam(required = false) Integer redirectTo) {
		Job j = dutyPlanManagement.getJobManagement().getJobRepository().findById(jobId).get();
		JobForm form = new JobForm(j.getJobName(),j.getJobDescription());

		if (j.getWorker() != null) {
			form.setPerson(j.getWorker());
		}
		model.addAttribute("form",form);
		model.addAttribute("id", j.getId());
		model.addAttribute("dpId", dpId);
		model.addAttribute("job", dutyPlanManagement.getJobManagement().getJobRepository().findById(jobId).get());
		model.addAttribute("persons", userManagement.getUserRepository().findByDeleted(false));
		model.addAttribute("redirectTo", redirectTo);
		return "dutyplan/editDutyPlan";
	}

	/**
	 * Used to delete a {@link Job} in a {@link DutyPlan}
	 * @param dpId the ID of the {@link DutyPlan}
	 * @param jobId the ID of the {@link Job}
	 * @return redirect back to movieEvents.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping("/dutyplan/deleteJob/{dpId}/{jobId}")
	public String deleteJob(@PathVariable Long dpId, @PathVariable Long jobId,
							@RequestParam(required = false) Integer redirectTo) {
		DutyPlan d = dutyPlanManagement.getDutyPlanById(dpId).get();
		d.deleteJob(jobId);
		dutyPlanManagement.update(d);
		//MovieEvent movieEvent = (MovieEvent) d.getEvent();
		//movieEvent.setDutyPlan(d); // seems not necessary

		dutyPlanManagement.getJobManagement().getJobRepository().delete(
			dutyPlanManagement.getJobManagement().getJobRepository().findById(jobId).get()
		);
		//return "redirect:/movieEvents/";
		return dutyPlanManagement.getRedirection(redirectTo);
	}

	/**
	 * An overview of assigned jobs
	 * @param model The Model of the HTML page
	 * @return the template containing the list of own Jobs
	 */
	@GetMapping("/myJobs/own")
	@PreAuthorize("hasRole('USER')")
	public String myJobs(Model model){

		User user = userManagement.getCurrentUser().get();
		model.addAttribute("eventList", movieEventManagement.findUserRelatedEvents(user).stream()
			.filter(movieEvent -> !movieEvent.getDate().isBefore(LocalDate.now()))
			.sorted(Comparator.comparing(Event::getDate))
			.collect(Collectors.toList()));
		model.addAttribute("user", userManagement.getCurrentUser().get());


		return "dutyplan/myJobs";
	}


}
