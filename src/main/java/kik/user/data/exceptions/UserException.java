package kik.user.data.exceptions;

import java.util.NoSuchElementException;

/**
 * parent exception of {@link UserNotFoundException} and {@link UserTypeNotFoundException}
 */
public class UserException extends NoSuchElementException {
	public UserException(String errorMessage){
		super(errorMessage);
	}
}
