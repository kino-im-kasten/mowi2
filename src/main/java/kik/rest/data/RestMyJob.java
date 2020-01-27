package kik.rest.data;

import kik.dutyplan.data.job.Job;

/**
 * {@link RestMyJob} will be used by spring to create a json string
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestMyJob {
    private String jobName;
    private String jobDescription;

    /**
	 * Default constructor of {@link RestMyJob}
	 * 
	 * @param job The {@link Job} to parse
	 */
    public RestMyJob(Job job) {
        this.jobName = job.getJobName();
        this.jobDescription = job.getJobDescription();
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
}