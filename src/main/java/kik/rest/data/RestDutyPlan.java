package kik.rest.data;

import java.util.ArrayList;
import java.util.List;

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.data.job.Job;

/**
 * {@link RestDutyPlan} will be used by spring to create a json string
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestDutyPlan {
    private long id;
	private String annotation;
	//private Event event;
	List<RestJob> assignedRoles = new ArrayList<>();
	List<RestJob> openRoles = new ArrayList<>();

    

	/**
	 * Default constructor of {@link RestDutyPlan}
	 * 
	 * @param dutyplan The {@link DutyPlan} to parse
	 */
    public RestDutyPlan(DutyPlan dutyplan) {
        this.id = dutyplan.getId();
        this.annotation = dutyplan.getAnnotation();
        for (Job job : dutyplan.getAssignedJobs()) {
            this.assignedRoles.add(new RestJob(job));
        }

        for (Job job : dutyplan.getOpenJobs()) {
            this.openRoles.add(new RestJob(job));
        }
        
        //this.event = dutyplan.getEvent();
    }

    /**
     * Getter for the id
     * 
     * @return The id
     */
    public long getId() {
		return this.id;
	}

    /**
     * Getter for the assigned roles
     * 
     * @return The assigned roles
     */
    public List<RestJob> getAssignedRoles() {
        return this.assignedRoles;
    }

    /**
     * Getter for the open roles
     * 
     * @return The open roles
     */
    public List<RestJob> getOpenRoles() {
        return this.openRoles;
    }

    /**
     * Getter for the annotation
     * 
     * @return The annotation
     */
    public String getAnnotation() {
        return this.annotation;
    }

    // public Event getEvent() {
    //     return this.event;
    // }
}
