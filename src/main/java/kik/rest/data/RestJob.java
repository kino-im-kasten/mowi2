package kik.rest.data;

import kik.dutyplan.data.job.Job;

/**
 * {@link RestJob} will be used by spring to create a json string
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestJob {
    //private Long id;
    private boolean open;
    private String jobName;
    private String jobDescription;
    private RestUser worker;

    /**
	 * Default constructor of {@link RestJob}
	 * 
	 * @param job The {@link Job} to parse
	 */
    public RestJob(Job job) {
        //this.id = job.getId();
        this.open = job.isOpen();
        this.jobName = job.getJobName();
        this.jobDescription = job.getJobDescription();
        if (!job.isOpen()) {
            this.worker = new RestUser(job.getWorker());
        }
    }

    // public Long getId() {
    //     return id;
    // }

    /**
     * Getter for the person who is assigned to a job
     *  
     * @return The person
     */
    public RestUser getWorker() {
        return worker;
    }

    /**
     * Getter for the job description
     * 
     * @return The job description
     */
    public String getJobDescription() {
        return jobDescription;
    }


    /**
     * Getter for the job name
     * 
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }


    /**
     * Getter for the state if there are open jobs
     * 
     * @return Boolean if there are open jobs
     */
    public boolean isOpen() {
        return open;
    }
}