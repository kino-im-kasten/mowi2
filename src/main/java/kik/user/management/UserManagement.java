package kik.user.management;


import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobRepository;
import kik.user.data.accessrole.AccessRole;
import kik.user.data.exceptions.UserException;
import kik.user.data.exceptions.UserNotFoundException;
import kik.user.data.forms.*;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import kik.user.data.usertype.UserType;
import org.apache.commons.text.RandomStringGenerator;

import org.salespointframework.useraccount.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages requests of {@link kik.user.controller.UserPostController} that create, delete or change {@link User}s in any way.
 *
 * @author Richard Müller
 * @version 0.0.3
 */
@Service
@Transactional
public class UserManagement {

	private final UserRepository userRepository;
	private final UserAccountManager userAccountManager;
	private final UserTypeManagement userTypeManagement;
	private final AuthenticationManager authenticationManager;
	private final UserValidation userValidation;
	private final JobRepository jobRepository;

	/**
	 * The default constructor of {@link UserManagement}
	 * @param userRepository repository that contains all {@link User}
	 * @param userAccountManager salespoint manager for {@link UserAccount}s
	 * @param userTypeManagement management for {@link UserType}s
	 * @param authenticationManager salespoint manager for comparing passwords
	 * @param userValidation for forms validation
	 * @param jobRepository holds all {@link Job}s
	 */
	public UserManagement(UserRepository userRepository, UserAccountManager userAccountManager,
						  UserTypeManagement userTypeManagement, AuthenticationManager authenticationManager,
						  UserValidation userValidation, JobRepository jobRepository) {

		this.userRepository = userRepository;
		this.userAccountManager = userAccountManager;
		this.userTypeManagement = userTypeManagement;
		this.authenticationManager = authenticationManager;
		this.userValidation = userValidation;
		this.jobRepository = jobRepository;
	}

	/**
	 * @return {@link User} instance that is in relation to the current authentication
	 */
	public Optional<User> getCurrentUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByNameIgnoreCase(authentication.getName().toLowerCase());
	}

	/**
	 * invalidates the current authentication
	 */
	public void invalidateAuthentication(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		auth.setAuthenticated(false);
	}

	/**
	 * refreshes the authentication roles, with the saved roles in the database
	 */
	public void refreshCurrentAuthentication(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//try to get current user
		if (getCurrentUser().isEmpty()){
			throw new UserNotFoundException("User was not found");
		}
		//return if User is locked
		if (!getCurrentUser().get().isUnlocked()){
			return;
		}
		//extract list of all roles for the user from the database
		List<GrantedAuthority> freshAuthorities = new ArrayList<>();
		for (AccessRole accessRole : getCurrentUser().get().getUserType().getRoleSet()) {
			freshAuthorities.add(new SimpleGrantedAuthority("ROLE_" + accessRole.getSalesPointRole().toString()));
		}
		//apply them to the current authentication
		Authentication newAuth
			= new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), freshAuthorities);

		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}

	/**
	 * method that instantiates and saves an {@link User}
	 *
	 * @param form {@link CreateNewUserForm} holds the information for {@link User} creation
	 * @param errors for rejecting val
	 * @return {@link User} object
	 */
	public User createUser(CreateNewUserForm form, Errors errors) throws UserException {
		//validation
		userValidation.validate(form, errors);
		if (errors.hasErrors()){
			return null;
		}
		//generate password if necessary
		String password = form.getPassword();
		if (form.getPassword().equals("")){
			password = generateRandomPassword();
		}

		//create new UserAccount, UserType
		UserAccount userAccount = userAccountManager.create(form.getName().toLowerCase()
			, Password.UnencryptedPassword.of(password));
		UserType userType = userTypeManagement.getUserTypeRepository().findByName(form.getNewUserTypeName()).get();
		//create User with those two
		User newUser = new User(form.getName(), userAccount, userType);
		newUser.setUnlockPassword(password);

		return userRepository.save(newUser);
	}

	/**
	 * method that deletes an {@link User} keeping it saved in the database as a deleted {@link User}
	 *
	 * @param userName identifier of {@link User} for safeDeletion
	 */
	public void safeDeleteUserByName(String userName){
		User user = userValidation.validateUserNameExists(userName);
		userValidation.validateAdminDanger(user);

		user.setDeleted(true);
		user.getUserAccount().setEnabled(false);

	}

	/**
	 * method that recovers a safe deleted {@link User}
	 *
	 * @param userName identifier of {@link User} for recovery
	 */
	public void recoverDeletedUserByName(String userName){
		User user = userValidation.validateUserNameExists(userName);

		if (user.isDeleted()){
			user.setDeleted(false);
			user.getUserAccount().setEnabled(true);
		}
	}

	/**
	 * method that deletes an already safe deleted {@link User} permanently (recovering is not possible)
	 *
	 * @param userName identifier of {@link User} for permanant deletion
	 */
	public void deleteUserForeverByName(String userName){
		User user = userValidation.validateUserNameExists(userName);
		if (user.isDeleted()){
			jobRepository.findByWorker(user).forEach(Job::removeWorker);
			userRepository.delete(user);
		}
	}

	/**
	 * method to unlock the currently logged {@link User}
	 *
	 * @param form {@link UnlockAccountForm} holds the new password
	 * @param errors to pass down to validation
	 */
	public void unlockUserAccount(UnlockAccountForm form, Errors errors){

		userValidation.validate(form, errors);
		if(errors.hasErrors()){
			return;
		}
		//isPresent() is checked in validation
		User user = userRepository.findByNameIgnoreCase(form.getUserName()).get();
		//apply all AccessRoles of the UserType
		user.applyUserType();
		//change password to new one
		userAccountManager.changePassword(user.getUserAccount(), Password.UnencryptedPassword.of(form.getNewPassword()));

		userRepository.save(user);
	}

	/**
	 * method to change the {@link UserType} of an {@link User} with identifier input parameters
	 *
	 * @param form {@link ChangeUserTypeOfUserForm}
	 * @param errors to pass down to validation
	 */
	public void changeUserTypeOfUser(ChangeUserTypeOfUserForm form, Errors errors){
		//validate and return if there are errors
		userValidation.validate(form, errors);
		if (errors.hasErrors()){
			return;
		}

		//validation checks presence
		User user = userRepository.findByNameIgnoreCase(form.getUserName()).get();
		UserType newUserType = userTypeManagement.getUserTypeRepository().findByName(form.getUserTypeName()).get();

		//change UserType
		user.setUserType(newUserType);

		//only apply UserType if the User has logged in before
		if (user.isUnlocked()){
			user.applyUserType();
		}

		userRepository.save(user);
	}

	/**
	 * method to reset the password of an {@link User} to an autogenerated one
	 * the {@link User} has no {@link Role}s after this and needs to be unlocked again
	 *
	 * @param userName identifiert for the {@link User} that should be reset
	 * @param newPassword new password to be applied
	 * @param errors to pass down to validation
	 */
	public void resetPassword(String userName, String newPassword, Errors errors){
		User user = userValidation.validateUserNameExists(userName);
		userValidation.validatePassword(newPassword, errors);
		if (errors.hasErrors()){
			return;
		}

		//only make password visible if the user is locked
		if (!user.isUnlocked()) {
			user.setUnlockPassword(newPassword);
		}
		//apply changes
		userAccountManager.changePassword(user.getUserAccount(), Password.UnencryptedPassword.of(newPassword));
		userRepository.save(user);
	}

	/**
	 * method to change the password of an {@link User}
	 *
	 * @param form {@link ChangePasswordForm} contains the new password
	 * @param errors to pass down to validation
	 */
	public void changePassword(ChangePasswordForm form, Errors errors){
		userValidation.validate(form, errors);
		refreshCurrentAuthentication();
		User user = getCurrentUser().get();
		if (errors.hasErrors()){
			return;
		}

		userAccountManager.changePassword(user.getUserAccount(), Password.UnencryptedPassword.of(form.getNewPassword()));
		userRepository.save(user);
	}

	/**
	 * method to change the userName of an {@link User}
	 *
	 * @param userName identifiert for the {@link User} to apply a new password to
	 * @param userNameForm contains the new password, old password for verification
	 * @param errors to pass down to validation
	 */
	public void changeUserName(String userName, ChangeUserNameForm userNameForm, Errors errors){
		User user = userValidation.validateUserNameExists(userName);
		userValidation.validateAdminDanger(user);
		userValidation.validate(userNameForm, errors);
		if (errors.hasErrors()) {
			return;
		}

		//get old UserAccount, and delete it
		UserAccount oldUserAccount = user.getUserAccount();
		user.setUserAccount(null);
		userAccountManager.delete(oldUserAccount);

		String randomPassword = generateRandomPassword();
		//set new UserAccount with random password and new Name
		UserAccount newUserAccount = userAccountManager.create(userNameForm.getNewUserName().toLowerCase(),
			Password.UnencryptedPassword.of(randomPassword));
		userAccountManager.save(newUserAccount);
		user.setUserAccount(newUserAccount);
		//reset access
		user.deapplyUserType();
		//set unlockpassword
		user.setUnlockPassword(randomPassword);
		//set name in User instance
		user.setName(userNameForm.getNewUserName());

		//in case the name of an deleted user was changed delete the new one too
		if (user.isDeleted()){
			user.getUserAccount().setEnabled(false);
		}

		userRepository.save(user);
	}

	/**
	 * method to generate a random password
	 *
	 * @return password with ascii characters from 33 to 125 of length 10
	 */
	public String generateRandomPassword() {
		RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(33, 125)
			.build();
		return pwdGenerator.generate(10);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}
}
