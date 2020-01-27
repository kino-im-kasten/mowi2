package kik.user.management;

import kik.config.Configuration;
import kik.user.data.exceptions.AdminDangerException;
import kik.user.data.exceptions.UserException;
import kik.user.data.exceptions.UserNotFoundException;
import kik.user.data.exceptions.UserTypeNotFoundException;
import kik.user.data.forms.*;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import kik.user.data.usertype.UserType;
import kik.user.data.usertype.UserTypeRepository;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

/**
 * Object for validating forms used in {@link kik.user.management.UserManagement} and names of {@link User}, {@link UserType}
 *
 * @author Richard Müller
 * @version 0.0.1
 */

@Service
public class UserValidation implements Validator {

	private UserRepository userRepository;
	private UserTypeRepository userTypeRepository;
	private AuthenticationManager authenticationManager;
	private Configuration configuration;

	/**
	 * default constructor for {@link UserValidation}
	 *
	 * @param userRepository holds all {@link User}
	 * @param userTypeRepository hold all {@link UserType}s
	 * @param authenticationManager for checking if {@link Password}s match
	 * @param configuration holds data of the configuration file
	 */
	public UserValidation(UserRepository userRepository,
						  UserTypeRepository userTypeRepository,
						  AuthenticationManager authenticationManager,
						  Configuration configuration){
		this.userRepository = userRepository;
		this.userTypeRepository = userTypeRepository;
		this.authenticationManager = authenticationManager;
		this.configuration = configuration;
	}

	/**
	 * validates {@link CreateNewUserForm}
	 *
	 * @param form holds data for {@link User} creation
	 * @param errors object to reject values on
	 * @throws UserException if the {@link UserType} specified in the form is not found
	 */
	private void validateCreateNewUserForm(CreateNewUserForm form, Errors errors) throws UserException {
		validateUserTypeNameExists(form.getNewUserTypeName());
		if (userRepository.findByNameIgnoreCase(form.getName()).isPresent()){
			errors.rejectValue("name", "User already exists");
		}
		if (!form.getPassword().isEmpty() && !form.getPassword().matches(configuration.getRegexPassword())){
			errors.rejectValue("password", "the new password is not secure enough");
		}
	}

	/**
	 * validates {@link ChangeUserTypeOfUserForm}
	 *
	 * @param form holds data for change of {@link UserType} of an {@link User}
	 * @throws UserException if the {@link User} or {@link UserType} is not found
	 */
	private void validateChangeUserTypeOfUserForm(ChangeUserTypeOfUserForm form) throws UserException{
		User user = validateUserNameExists(form.getUserName());
		validateUserTypeNameExists(form.getUserTypeName());
		validateAdminDanger(user);
	}

	/**
	 * validates {@link UnlockAccountForm}
	 *
	 * @param form holds data necessary for unlocking the {@link org.salespointframework.useraccount.UserAccount} of an {@link User}
	 * @param errors object to reject values on
	 * @throws UserException if the {@link User} specified in the {@link UnlockAccountForm} is not found
	 */
	private void validateUnlockAccountForm(UnlockAccountForm form, Errors errors) throws UserException{
		validateUserNameExists(form.getUserName());
		if (!form.getNewPassword().equals(form.getNewPasswordAgain())){
			errors.rejectValue("newPassword", "Passwords do not match");
		}
		if (!form.getNewPassword().matches(configuration.getRegexPassword())){
			errors.rejectValue("newPasswordAgain", "the new password is not secure enough");
		}
	}

	/**
	 * validates {@link ChangePasswordForm}
	 *
	 * @param form holds data necessary for the change of the password of an {@link User}
	 * @param errors object to reject values on
	 * @throws UserException if the {@link User} specified in the {@link ChangePasswordForm} is not found
	 */
	private void validateChangePasswordForm(ChangePasswordForm form, Errors errors) throws UserException{
		User user = validateUserNameExists(form.getCurrentUserName());
		if (!authenticationManager.matches(	Password.UnencryptedPassword.of(form.getOldPassword()),
											user.getUserAccount().getPassword())){
			errors.rejectValue("oldPassword", "old Password did not match");
		}
		if (!form.getNewPassword().matches(configuration.getRegexPassword())){
			errors.rejectValue("newPassword", "the new password is not secure enough");
		}
	}

	/**
	 * validates {@link ChangeUserNameForm}
	 *
	 * @param form holds necessary data for an {@link User}name change
	 * @param errors object to reject values on
	 */
	private void validateChangeUserNameForm(ChangeUserNameForm form, Errors errors){
		if (userRepository.findByNameIgnoreCase(form.getNewUserName()).isPresent()){
			errors.rejectValue("newUserName", "UserAlreadyExistsError");
		}
	}

	/**
	 * method to determine if a class can be validated with this {@link UserValidation}
	 *
	 * @param clazz class that needs to be checked
	 * @return is the class supported / can it be validated
	 */
	@Override
	public boolean supports(Class<?> clazz) {

		Optional<?> supportedClazz = List.of(	CreateNewUserForm.class, ChangeUserTypeOfUserForm.class,
												UnlockAccountForm.class, ChangePasswordForm.class,
												ChangeUserNameForm.class)
			.stream()
			.filter(suppClazz -> suppClazz.isInstance(clazz))
			.findFirst();

		return supportedClazz.isPresent();
	}

	/**
	 * method that calls the right private validation method for each type of form
	 *
	 * @param target form that needs to be validated
	 * @param errors {@link Errors} to be passed down to the private validation methods
	 * @throws UserException thrown by any of the private validation methods
	 */
	@Override
	public void validate(@NonNull Object target,@NonNull Errors errors) throws UserException{

		if (target instanceof CreateNewUserForm){
			validateCreateNewUserForm((CreateNewUserForm) target, errors);
		} else if (target instanceof ChangeUserTypeOfUserForm) {
			validateChangeUserTypeOfUserForm((ChangeUserTypeOfUserForm) target);
		} else if (target instanceof UnlockAccountForm){
			validateUnlockAccountForm((UnlockAccountForm) target, errors);
		} else if (target instanceof ChangePasswordForm){
			validateChangePasswordForm((ChangePasswordForm)target, errors);
		} else if (target instanceof ChangeUserNameForm){
			validateChangeUserNameForm((ChangeUserNameForm)target, errors);
		}
	}

	/**
	 * method to validate a possible name of an {@link User}
	 *
	 * @param userName identifier for the {@link User}
	 * @return {@link User} that has the userName
	 * @throws UserNotFoundException if the {@link User} with the input name is not found
	 */
	public User validateUserNameExists(String userName) throws UserNotFoundException{
		Optional<User> oUser = userRepository.findByNameIgnoreCase(userName);
		if (oUser.isEmpty()){
			throw new UserNotFoundException("User was not found");
		}
		return oUser.get();
	}

	/**
	 * method to validate a possible name of an {@link UserType}
	 *
	 * @param userTypeName identifier for the {@link UserType}
	 * @return {@link UserType} that has the userTypeName
	 * @throws UserTypeNotFoundException if the {@link UserType} with the input name is not found
	 */
	public UserType validateUserTypeNameExists(String userTypeName) throws UserTypeNotFoundException{
		Optional<UserType> oUserType = userTypeRepository.findByName(userTypeName);
		if (oUserType.isEmpty()){
			throw new UserTypeNotFoundException("UserType was not found");
		}
		return oUserType.get();
	}

	/**
	 * method to validate if there is only one {@link User} with {@link UserType}=Admin left, that is not deleted or locked
	 *
	 * @param user possible admin that is endangered
	 * @throws AdminDangerException if there is only one admin left
	 */
	public void validateAdminDanger(User user) throws AdminDangerException {
		if (user.isDeleted()){
			return;
		}
		if (!user.getUserType().getName().equals("Admin")){
			return;
		}
		if (userRepository.findByUserType(userTypeRepository.findByName("Admin").get()).stream()
			.filter(admin -> !admin.isDeleted())
			.filter(User::isUnlocked).count() == 1){
			throw new AdminDangerException("There is only one Admin left, it should not be modified");
		}
	}

	/**
	 * method to validate a password
	 *
	 * @param password input string to be checked
	 * @param errors object to reject values on
	 */
	public void validatePassword(String password, Errors errors){
		if (!password.matches(configuration.getRegexPassword())){
			errors.rejectValue("newPassword", "the new password is not secure enough");
		}
	}
}
