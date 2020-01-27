package kik.user.data.forms;

import javax.validation.constraints.NotEmpty;

/**
 * Form that holds the data for creating a new {@link kik.user.data.user.User}
 *
 * @author Richard Müller
 * @version 0.0.1
 */

public class CreateNewUserForm {

	@NotEmpty
	private final String name;
	//password can be empty, then there will be a generated one
	private final String password;
	@NotEmpty
	private final String newUserTypeName;

	/**
	 * default constructor
	 *
	 * @param name that the {@link kik.user.data.user.User} is going to have
	 * @param password of the new {@link kik.user.data.user.User}
	 * @param newUserTypeName {@link kik.user.data.usertype.UserType} of the new {@link kik.user.data.user.User}
	 */
	public CreateNewUserForm(String name, String password,String newUserTypeName) {
		this.name = name;
		this.password = password;
		this.newUserTypeName = newUserTypeName;
	}

	//getter
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public String getNewUserTypeName() {
		return newUserTypeName;
	}
}
