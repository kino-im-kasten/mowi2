package kik.user.data.forms;

import kik.user.data.user.User;
import kik.user.data.usertype.UserType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Form that holds the data for changing the {@link kik.user.data.usertype.UserType} of an {@link kik.user.data.user.User}
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public class ChangeUserTypeOfUserForm {

	@NotEmpty
	private String userTypeName;
	@NotEmpty
	private String userName;

	/**
	 * default constructor
	 *
	 * @param userTypeName identifier of the {@link UserType} to be applied
	 * @param userName identifier of the {@link User}
	 */
	public ChangeUserTypeOfUserForm(String userTypeName, String userName){
		this.userTypeName = userTypeName;
		this.userName = userName;
	}

	public String  getUserTypeName() {
		return userTypeName;
	}
	public String getUserName() {
		return userName;
	}
}
