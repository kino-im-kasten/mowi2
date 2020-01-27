package kik.user.data.forms;

import org.salespointframework.useraccount.Password;

import javax.validation.constraints.NotEmpty;

/**
 * Form that holds the data for unlocking an {@link kik.user.data.user.User},
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public class UnlockAccountForm {

	@NotEmpty
	private final String newPassword;
	@NotEmpty
	private final String newPasswordAgain;
	@NotEmpty
	private final String userName;

	/**
	 * default constructor
	 *
	 * @param newPassword chosen new password
	 * @param newPasswordAgain confirmation of the chosen password
	 * @param userName identifier for the {@link kik.user.data.user.User} to be unlocked
	 */
	public UnlockAccountForm(String newPassword,
							 String newPasswordAgain,
							 String userName) {
		this.newPassword = newPassword;
		this.newPasswordAgain = newPasswordAgain;
		this.userName = userName;
	}

	public String getNewPassword() {
		return newPassword;
	}
	public String getNewPasswordAgain() {
		return newPasswordAgain;
	}
	public String getUserName() {
		return userName;
	}
}
