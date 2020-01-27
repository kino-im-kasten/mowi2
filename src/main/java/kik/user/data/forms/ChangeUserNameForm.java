package kik.user.data.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Form that holds data for a username change, the {@link kik.user.data.user.User} is not saved here,
 * but given as an additional parameter when the form is used in the {@link kik.user.management.UserManagement}
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public class ChangeUserNameForm {
	@NotEmpty
	private final String  newUserName;

	/**
	 * default constructor
	 *
	 * @param newUserName new name to be applied
	 */
	public ChangeUserNameForm(String newUserName) {
		this.newUserName = newUserName;
	}

	public String getNewUserName() {
		return newUserName;
	}
}
