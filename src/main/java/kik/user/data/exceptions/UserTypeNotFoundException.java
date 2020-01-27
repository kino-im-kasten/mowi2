package kik.user.data.exceptions;

/**
 * to be thrown if a {@link kik.user.data.usertype.UserType} is not found in the {@link kik.user.data.usertype.UserTypeRepository}
 * but should be
 */
public class UserTypeNotFoundException extends UserException {
	public UserTypeNotFoundException(String errorMessage){
		super(errorMessage);
	}
}
