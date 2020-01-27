package kik.user.management;

import kik.config.Configuration;
import kik.dutyplan.data.job.JobRepository;
import kik.user.data.accessrole.AccessRoleRepository;
import kik.user.data.exceptions.AdminDangerException;
import kik.user.data.exceptions.UserException;
import kik.user.data.exceptions.UserNotFoundException;
import kik.user.data.exceptions.UserTypeNotFoundException;
import kik.user.data.forms.*;
import kik.user.data.user.User;
import kik.user.data.user.UserRepository;
import kik.user.data.usertype.UserType;
import kik.user.data.usertype.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserValidationTest {

	private final UserValidation userValidation;
	private final UserRepository userRepository;
	private final UserTypeRepository userTypeRepository;
	private final AccessRoleRepository accessRoleRepository;
	private final UserManagement userManagement;
	private final JobRepository jobRepository;
	private final Configuration configuration;
	private final UserAccountManager userAccountManager;

	private Set<String> rejectedFields = new HashSet<String>();
	private final String password = "123456passwordLikeRegex";
	private User validUser;
	private UserType userType;

	@Autowired
	public UserValidationTest(UserValidation userValidation, UserRepository userRepository,
							  UserTypeRepository userTypeRepository, AccessRoleRepository accessRoleRepository,
							  UserManagement userManagement, JobRepository jobRepository, Configuration configuration,
							  UserAccountManager userAccountManager) {
		this.userValidation = userValidation;
		this.userRepository = userRepository;
		this.userTypeRepository = userTypeRepository;
		this.accessRoleRepository = accessRoleRepository;
		this.userManagement = userManagement;
		this.jobRepository = jobRepository;
		this.configuration = configuration;
		this.userAccountManager = userAccountManager;
	}

	@BeforeEach
	public void setup(){
		rejectedFields.clear();
		//delete all, User, UserTypes, AccessRoles
		accessRoleRepository.deleteAll();
		userTypeRepository.deleteAll();
		for (User u : userManagement.getUserRepository().findAll()){
			jobRepository.findByWorker(u).forEach(kik.dutyplan.data.job.Job::removeWorker);
			userManagement.getUserRepository().delete(u);
		}
		//one User is present
		UserAccount userAccount = userAccountManager.create("validUser", Password.UnencryptedPassword.of(this.password));
		this.userType = userTypeRepository.save(new UserType("usertype", Set.of()));
		this.validUser = userRepository.save(new User("validUser", userAccount, this.userType));
	}

	@Test
	public void validateUserNameExistsTest(){
		userRepository.save(new User("1234SomeUsernAme",null,null));
		try {
			User lowerCaseUser = userValidation.validateUserNameExists("1234someusername");
			User mixedCaseUser = userValidation.validateUserNameExists("1234somEUserNAME");
			User UpperCaseUser = userValidation.validateUserNameExists("1234SOMEUSERNAME");

			assertSame(lowerCaseUser, mixedCaseUser);
			assertSame(mixedCaseUser, UpperCaseUser);
		}catch (UserNotFoundException ue){
			fail("there should be no exception thrown, the user with said name exists, exception; " + ue.toString());
		}
	}

	@Test
	public void validateUserNameExistsNegativeTest(){
		assertThrows(UserNotFoundException.class
			, () -> userValidation.validateUserNameExists("someusernamethatdoesnotexist123$"));
	}

	@Test
	public void validateUserTypeNameExistsTest(){
		try{
			userValidation.validateUserTypeNameExists(this.userType.getName());
		}catch (UserTypeNotFoundException ue){
			fail("there should be no exception thrown, the userType with said name exists, exception; " + ue.toString());
		}
	}

	@Test
	public void validateUserTypeNameExistsNegativeTest(){
		assertThrows(UserTypeNotFoundException.class
			, () -> userValidation.validateUserTypeNameExists("someusertypenamethatdoesnotexist123$"));
	}

	@Test
	public void validateAdminDangerTest(){
		//one unlocked, one locked, one deleted User with UserType==Admin, and the this.validUser
		UserType adminType = userTypeRepository.save(new UserType("Admin", Set.of()));
		User adminUser = userRepository.save(new User("1234SomeUsernAme", null, adminType));
		adminUser.setUnlocked(true);
		User lockedAdminUser = userRepository.save(new User("1234lockedSomeUsernAme", null, adminType));
		lockedAdminUser.setUnlocked(false);
		User deletedAdminUser = userRepository.save(new User("1234deletedSomeUsernAme", null, adminType));
		deletedAdminUser.setDeleted(true);

		assertEquals(userRepository.findAll().stream().count(), 4);
		assertEquals(userRepository.findAll().stream()
			.filter(User::isUnlocked)
			.filter(user -> !user.isDeleted())
			.count(), 1);
		assertThrows(AdminDangerException.class
			, () -> userValidation.validateAdminDanger(adminUser));
	}

	@Test
	public void validateAdminDangerNegativeTest(){
		UserType adminType = userTypeRepository.save(new UserType("Admin", Set.of()));
		User adminUser = userRepository.save(new User("1234SomeUsernAme", null, adminType));
		adminUser.setUnlocked(true);
		User anotherAdminUser = userRepository.save(new User("1234SomeUsernAme2", null, adminType));
		anotherAdminUser.setUnlocked(true);

		try{
			userValidation.validateAdminDanger(adminUser);
			userValidation.validateAdminDanger(anotherAdminUser);
		}catch (AdminDangerException ue){
			fail("there are enough Admins, exception should not be thrown, exception: " + ue.toString());
		}
	}

	//Class for collecting all rejected fields in the following tests
	class ErrorMock extends BindException {
		ErrorMock(){
			super(Optional.empty(), "");
		}
		public void rejectValue(String field, String errorCode){
			rejectedFields.add(field);
		}
	}

	@Test
	public void validatePasswordTest(){
		configuration.setRegexPassword(this.password);
		userValidation.validatePassword(this.password, new ErrorMock());

		assertTrue(rejectedFields.isEmpty());
	}

	@Test
	public void validatePasswordNegativeTest(){
		configuration.setRegexPassword(this.password);
		userValidation.validatePassword("incorrectExpression", new ErrorMock());

		assertTrue(rejectedFields.contains("newPassword"));
	}

	@Test
	public void validateTest(){
		configuration.setRegexPassword(password);
		UserType userType = userTypeRepository.save(new UserType("Orga",Set.of()));
		//valid forms
		Set<Object> forms = Set.of(new CreateNewUserForm("newUser", password, userType.getName())
			, new ChangeUserTypeOfUserForm(userType.getName(), this.validUser.getName())
			, new UnlockAccountForm(password, password, this.validUser.getName())
			, new ChangePasswordForm(password, password, this.validUser.getName())
			, new ChangeUserNameForm("validNewUserName"));

		for (Object form : forms){
			try{
				userValidation.validate(form, new ErrorMock());
			}catch (UserException ue){
				fail("Form validation should not throw an exception for this form: " + form.getClass() + "\n"
				+ " Exception: " + ue.toString());
			}
		}
		assertTrue(rejectedFields.isEmpty());
	}

	@Test
	public void validateFieldErrorTest(){
		Set<List<Object>> testInputs = Set.of(
			List.of(new CreateNewUserForm(this.validUser.getName(), this.password, userType.getName())
				, "name"),
			List.of(new CreateNewUserForm("notYetExistingUser", "unsafePassword", userType.getName())
				, "password"),
			List.of(new UnlockAccountForm(this.password, "diffPassword", this.validUser.getName())
				, "newPassword"),
			List.of(new UnlockAccountForm("unsecurePassword", "unsecurePassword", this.validUser.getName())
				, "newPasswordAgain"),
			List.of(new ChangePasswordForm("passwordNoMatch", this.password, this.validUser.getName())
				, "oldPassword"),
			List.of(new ChangePasswordForm(this.password, "someNewPassword", this.validUser.getName())
				, "newPassword"),
			List.of(new ChangeUserNameForm(this.validUser.getName())
				, "newUserName")
		);

		configuration.setRegexPassword(this.password);
		for (List<Object> testInput : testInputs){
			Object form = testInput.get(0);
			String fieldError = (String)testInput.get(1);

			userValidation.validate(form, new ErrorMock());
			assertTrue(this.rejectedFields.contains(fieldError));
			assertEquals(1, this.rejectedFields.size(), "fieldErrors: " + this.rejectedFields.toString() + "\n"
			+ "Tested FieldError: " + fieldError);

			this.rejectedFields.clear();
		}
	}
}

