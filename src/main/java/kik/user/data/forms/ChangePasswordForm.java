package kik.user.data.forms;

import org.salespointframework.useraccount.Password;

import javax.validation.constraints.NotEmpty;

/**
 * Form that holds data for changing the password of an {@link kik.user.data.user.User}
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public class ChangePasswordForm {
	@NotEmpty
	private final String oldPassword;
	@NotEmpty
	private final String newPassword;
	@NotEmpty
	private final String currentUserName;

	/**
	 * default constructor
	 *
	 * @param oldPassword password still present currently for authentication
	 * @param newPassword password to change to
	 * @param currentUserName identifier of the {@link kik.user.data.user.User} changing his password
	 */
	public ChangePasswordForm(String oldPassword, String newPassword, @NotEmpty String currentUserName){
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.currentUserName = currentUserName;
	}

	//getter
	public String getOldPassword() {
		return oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public String getCurrentUserName() {
		return currentUserName;
	}
}
