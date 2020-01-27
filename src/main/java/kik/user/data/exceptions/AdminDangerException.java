package kik.user.data.exceptions;

/**
 * to be thrown if there is only one {@link kik.user.data.user.User} Entity with {@link kik.user.data.usertype.UserType}=Admin left in the System
 * and there is an attempt to change its {@link kik.user.data.usertype.UserType} or delete it
 */
public class AdminDangerException extends UserException {
	public AdminDangerException(String errorMessage){
		super(errorMessage);
	}
}
