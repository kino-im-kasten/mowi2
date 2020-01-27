package kik.user.data.exceptions;

/**
 * to be thrown if a {@link kik.user.data.user.User} is not found in the {@link kik.user.data.user.UserRepository}
 * but should be
 */
public class UserNotFoundException extends UserException {
	public UserNotFoundException(String errorMessage){
		super(errorMessage);
	}
}
