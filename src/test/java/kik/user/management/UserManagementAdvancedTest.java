package kik.user.management;

import kik.user.data.forms.*;
import kik.user.data.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserManagementAdvancedTest {

	private UserManagement userManagement;
	private UserTypeManagement userTypeManagement;
	private AuthenticationManager authenticationManager;

	private final String firstUserName = "first_someUser123";
	private final String secondUserName = "second_someUser123";
	private final String thirdUserName = "third_someUser123";
	private final String firstUserTypeName = "first_someUserType123";
	private final String secondUserTypeName = "second_someUserType123";
	private final String firstPassword = "first_password123";
	private final String secondPassword = "second_password123";

	@Autowired
	public UserManagementAdvancedTest(UserManagement userManagement, UserTypeManagement userTypeManagement, AuthenticationManager authenticationManager) {
		this.userManagement = userManagement;
		this.userTypeManagement = userTypeManagement;
		this.authenticationManager = authenticationManager;
	}

	@BeforeEach
	public void setup() {
		//delete
		userTypeManagement.getUserTypeRepository().deleteByName(firstUserTypeName);
		userTypeManagement.getUserTypeRepository().deleteByName(secondUserTypeName);
		userManagement.getUserRepository().deleteByName(firstUserName);
		userManagement.getUserRepository().deleteByName(secondUserName);

		//recreate
		userTypeManagement.createUserType(firstUserTypeName, Set.of());
		userTypeManagement.createUserType(secondUserTypeName, Set.of());
		userManagement.createUser(new CreateNewUserForm(firstUserName,firstPassword, firstUserTypeName)
			, new BindException(Optional.empty(), ""));
		User secondUser = userManagement.createUser(new CreateNewUserForm(secondUserName,firstPassword, firstUserTypeName)
			, new BindException(Optional.empty(), ""));
		//secondUser is safe-deleted already
		secondUser.setDeleted(true);
		secondUser.getUserAccount().setEnabled(false);
	}


	@Test
	public void safeDeleteByNameTest(){
		//before: User is present and has status deleted==false, UserAccount is enabled
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserBefore.isPresent());
		assertFalse(oUserBefore.get().isDeleted());
		assertTrue(oUserBefore.get().getUserAccount().isEnabled());

		userManagement.safeDeleteUserByName(firstUserName);

		//after: User is present and has status deleted==true, UserAccount of User is disabled
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserAfter.isPresent());
		assertTrue(oUserAfter.get().isDeleted());
		assertFalse(oUserAfter.get().getUserAccount().isEnabled());
	}

	@Test
	public void deleteUserForeverByNameTest(){
		//before: User is present and has status deleted==true, UserAccount is disabled
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(secondUserName);
		assertTrue(oUserBefore.isPresent());
		assertTrue(oUserBefore.get().isDeleted());
		assertFalse(oUserBefore.get().getUserAccount().isEnabled());

		userManagement.deleteUserForeverByName(secondUserName);

		//after: User is not present
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(secondUserName);
		assertTrue(oUserAfter.isEmpty());
	}

	@Test void recoverDeletedUserByNameTest(){
		//before: User is present and has status deleted==true, UserAccount is disabled
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(secondUserName);
		assertTrue(oUserBefore.isPresent());
		assertTrue(oUserBefore.get().isDeleted());
		assertFalse(oUserBefore.get().getUserAccount().isEnabled());

		userManagement.recoverDeletedUserByName(secondUserName);

		//after: User is present and has status deleted==false, UserAccount is enabled
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(secondUserName);
		assertTrue(oUserAfter.isPresent());
		assertFalse(oUserAfter.get().isDeleted());
		assertTrue(oUserAfter.get().getUserAccount().isEnabled());
	}

	@Test
	public void unlockUserAccountTest(){
		//before: User is present and has status deleted==false, UserAccount is enabled, User is locked
		//password of User matches first password
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserBefore.isPresent());
		assertFalse(oUserBefore.get().isDeleted());
		assertTrue(oUserBefore.get().getUserAccount().isEnabled());
		assertFalse(oUserBefore.get().isUnlocked());
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(firstPassword)
			, oUserBefore.get().getUserAccount().getPassword()));

		userManagement.unlockUserAccount(new UnlockAccountForm(secondPassword,secondPassword,firstUserName)
			, new BindException(Optional.empty(), ""));

		//after: User is present and has status deleted==false, UserAccount is enabled, User is unlocked
		//password of User matches second password
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserAfter.isPresent());
		assertFalse(oUserAfter.get().isDeleted());
		assertTrue(oUserBefore.get().getUserAccount().isEnabled());
		assertTrue(oUserBefore.get().isUnlocked());
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(secondPassword)
			, oUserBefore.get().getUserAccount().getPassword()));
	}

	@Test
	public void changeUserTypeTest(){
		//before: User has first UserType, second UserType is present in repository
		User userBefore = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName).get();
		assertEquals(firstUserTypeName,userBefore.getUserType().getName());
		assertTrue(userTypeManagement.getUserTypeRepository().findByName(secondUserTypeName).isPresent());

		userManagement.changeUserTypeOfUser(new ChangeUserTypeOfUserForm(secondUserTypeName, firstUserName)
			, new BindException(Optional.empty(), ""));

		//after: User is still present in repository, and has second UserType
		assertTrue(userManagement.getUserRepository().findByNameIgnoreCase(firstUserName).isPresent());
		User userAfter = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName).get();
		assertEquals(secondUserTypeName, userAfter.getUserType().getName());
	}

	@Test
	public void resetPasswordLockedUserTest(){
		//before: User is present and locked, unlock password of User/UserAccount matches first password
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserBefore.isPresent());
		assertFalse(oUserBefore.get().isUnlocked());
		assertEquals(oUserBefore.get().getUnlockPassword(), firstPassword);
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(firstPassword)
			, oUserBefore.get().getUserAccount().getPassword()));

		userManagement.resetPassword(firstUserName, secondPassword, new BindException(Optional.empty(), ""));

		//after: User is present and locked, unlock password of User/UserAccount matches second password
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserAfter.isPresent());
		assertFalse(oUserAfter.get().isUnlocked());
		assertEquals(oUserAfter.get().getUnlockPassword(), secondPassword);
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(secondPassword)
			, oUserBefore.get().getUserAccount().getPassword()));
	}

	@Test
	public void resetPasswordUnlockedUserTest(){
		//before: User is present and unlocked, password of UserAccount matches second password
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		userManagement.unlockUserAccount(new UnlockAccountForm(secondPassword,secondPassword,firstUserName)
			, new BindException(Optional.empty(), ""));
		assertTrue(oUserBefore.isPresent());
		assertTrue(oUserBefore.get().isUnlocked());
		assertEquals(oUserBefore.get().getUnlockPassword(), firstPassword);
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(secondPassword)
			, oUserBefore.get().getUserAccount().getPassword()));

		userManagement.resetPassword(firstUserName, firstPassword, new BindException(Optional.empty(), ""));

		//after: User is present and unlocked, unlock password of UserAccount matches first password
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserAfter.isPresent());
		assertTrue(oUserAfter.get().isUnlocked());
		assertEquals(oUserBefore.get().getUnlockPassword(), firstPassword);
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(firstPassword)
			, oUserBefore.get().getUserAccount().getPassword()));
	}

	@Test
	public void changeUserNameTest(){
		//before: User with firstUserName is present
		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(oUserBefore.isPresent());

		userManagement.changeUserName(firstUserName, new ChangeUserNameForm(thirdUserName)
			, new BindException(Optional.empty(), ""));

		//after: user with thirdUserName is present, User is the same before and after
		Optional<User> formerUser = userManagement.getUserRepository().findByNameIgnoreCase(firstUserName);
		assertTrue(formerUser.isEmpty());
		Optional<User> oUserAfter = userManagement.getUserRepository().findByNameIgnoreCase(thirdUserName);
		assertTrue(oUserAfter.isPresent());
		assertSame(oUserBefore.get(), oUserAfter.get());
	}

}