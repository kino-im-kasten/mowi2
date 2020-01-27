package kik.user.data;

import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRolesEnum;
import kik.user.data.user.User;
import kik.user.data.usertype.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserTest {

	private final UserAccountManager userAccountManager;
	private final AuthenticationManager authenticationManager;

	private final String userName = "123someusername123456";
	private final String userTypeName = "someuserType123xxx";
	private final Password.UnencryptedPassword password = Password.UnencryptedPassword.of("password123456$$$");
	private final Set<AccessRole> roleSet = Set.of(new AccessRole(AccessRolesEnum.USER)
		, new AccessRole(AccessRolesEnum.ADMIN));
	private User user;

	@Autowired
	public UserTest(UserAccountManager userAccountManager, AuthenticationManager authenticationManager){
		this.userAccountManager = userAccountManager;
		this.authenticationManager = authenticationManager;
	}

	@BeforeEach
	public void setup(){
		UserType userType = new UserType(userTypeName, this.roleSet);
		UserAccount userAccount = userAccountManager.create(userName,password);
		this.user = new User(userName,userAccount,userType);
	}

	@Test
	public void UserDefaultConstructorTest() {
		assertFalse(this.user.isDeleted());
		assertFalse(this.user.isUnlocked());
		assertTrue(authenticationManager.matches(password, this.user.getUserAccount().getPassword()));
		assertEquals(this.user.getUserType().getName(), userTypeName);
		assertEquals(this.user.getUserAccount().getRoles().stream().count(), 0);
	}

	@Test
	public void UserApplyDeapplyUserType(){
		this.user.applyUserType();

		assertTrue(this.user.isUnlocked());
		Iterator<AccessRole> accessRoleIterator = this.roleSet.iterator();
		assertTrue(this.user.getUserAccount().hasRole(accessRoleIterator.next().getSalesPointRole()));
		assertTrue(this.user.getUserAccount().hasRole(accessRoleIterator.next().getSalesPointRole()));

		this.user.deapplyUserType();

		assertFalse(this.user.isUnlocked());
		assertEquals(this.user.getUserAccount().getRoles().stream().count(), 0);
	}
}
