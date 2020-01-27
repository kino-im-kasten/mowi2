package kik.dutyplan.data.job;

/**
 * class containing just an ID - needed to be given by a Form
 */
public class JobContainer {
	private Long id;

	/**
	 * @return the encapsulated ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the ID of the Job to be encapsulated
	 */
	public void setId(Long id) {
		this.id = id;
	}
}
