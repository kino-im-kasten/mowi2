package kik.dutyplan.data.dutyplan;

/**
 * class used to capsulate Data used to assign a {@link kik.user.data.user.User} to a {@link kik.dutyplan.data.job.Job}
 * @author Jonas Höpner
 * @version 1.0.0
 */
public class AssignForm {
	private Long user;
	private Long job;

	/**
	 * creates an empty AssignForm
	 */
	public AssignForm() {}

	/**
	 * standardConstructor
	 * @param user the {@link kik.user.data.user.User} to be assigned
	 * @param job the {@link kik.dutyplan.data.job.Job}
	 */
	public AssignForm(Long user, Long job) {
		this.user = user;
		this.job = job;
	}

	// GETTER & SETTER
	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Long getJob() {
		return job;
	}

	public void setJob(Long job) {
		this.job = job;
	}
}
