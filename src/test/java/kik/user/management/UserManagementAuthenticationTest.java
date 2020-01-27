package kik.user.management;

import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRoleRepository;
import kik.user.data.accessrole.AccessRolesEnum;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserManagementAuthenticationTest {

	@Autowired
	MockMvc mvc;

	private final UserManagement userManagement;
	private final UserRepository userRepository;
	private final UserTypeRepository userTypeRepository;
	private final AccessRoleRepository accessRoleRepository;
	private final UserAccountManager userAccountManager;

	@Autowired
	UserManagementAuthenticationTest(UserManagement userManagement, UserRepository userRepository, UserTypeRepository userTypeRepository, AccessRoleRepository accessRoleRepository, UserAccountManager userAccountManager){
		this.userManagement = userManagement;
		this.userRepository = userRepository;
		this.userTypeRepository = userTypeRepository;
		this.accessRoleRepository = accessRoleRepository;
		this.userAccountManager = userAccountManager;
	}

	@BeforeEach
	public void setup(){

		AccessRole first_accessRole = new AccessRole(AccessRolesEnum.ORGA);
		AccessRole second_accessRole =new AccessRole(AccessRolesEnum.USER);
		accessRoleRepository.save(first_accessRole);
		accessRoleRepository.save(second_accessRole);
		Set<AccessRole> roleSet = Set.of(first_accessRole, second_accessRole);
		UserType userType = new UserType("usertypeName789", roleSet);
		userTypeRepository.save(userType);
		UserAccount userAccount = userAccountManager.create("firstUserName789".toLowerCase()
			, Password.UnencryptedPassword.of("firstPassword456"));
		userRepository.save(new User("firstUserName789",userAccount,userType));

		userRepository.save(new User("secondUserName789", null, null));

	}

	@Test
	@WithMockUser(username = "firstUserName789")
	public void getCurrentUserTest(){
		Optional<User> oCurrentUser = userManagement.getCurrentUser();
		assertTrue(oCurrentUser.isPresent());
		assertEquals("firstUserName789", oCurrentUser.get().getName());
	}

	@Test
	@WithMockUser(username = "someNameThatDoesNotExist")
	public void getCurrentUserNegativeTest(){
		Optional<User> oCurrentUser = userManagement.getCurrentUser();
		assertTrue(oCurrentUser.isEmpty());
	}

	@Test
	@WithMockUser(username = "firstUserName789")
	public void invalidateAuthenticationTest(){
		//before: authentication is valid
		Authentication authBefore = SecurityContextHolder.getContext().getAuthentication();
		assertTrue(authBefore.isAuthenticated());

		userManagement.invalidateAuthentication();

		//after: authentication is invalid
		Authentication authAfter = SecurityContextHolder.getContext().getAuthentication();
		assertFalse(authAfter.isAuthenticated());
	}

	@Test
	@WithMockUser(username = "firstUserName789",  roles = {"ADMIN"})
	public void refreshCurrentAuthenticationUnlockedUserTest(){
		//before: authentication has only ROLE_ADMIN, no other Roles, User is unlocked
		userManagement.getCurrentUser().get().setUnlocked(true);
		Authentication authBefore = SecurityContextHolder.getContext().getAuthentication();
		assertTrue(authBefore.getAuthorities().stream()
			.map(Object::toString).anyMatch(it -> it.equals("ROLE_ADMIN")));
		assertEquals(authBefore.getAuthorities().size(), 1);

		userManagement.refreshCurrentAuthentication();

		//after: authentication has the ROLE_ORGA and ROLE_USER (saved in the userType of firstUserNama789)
		Authentication authAfter = SecurityContextHolder.getContext().getAuthentication();
		assertTrue(authAfter.getAuthorities().stream()
			.map(Object::toString)
			.collect(Collectors.toSet()).containsAll(Set.of("ROLE_ORGA", "ROLE_USER")));
		assertEquals(2, authAfter.getAuthorities().size());
	}

	@Test
	@WithMockUser(username = "firstUserName789",  roles = {"ADMIN"})
	public void refreshCurrentAuthenticationLockedUserTest(){
		//before: authentication has only ROLE_ADMIN, no other Roles
		userManagement.getCurrentUser().get().setUnlocked(false);
		Authentication authBefore = SecurityContextHolder.getContext().getAuthentication();
		assertTrue(authBefore.getAuthorities().stream()
			.map(Object::toString).anyMatch(it -> it.equals("ROLE_ADMIN")));
		assertEquals(authBefore.getAuthorities().size(), 1);

		userManagement.refreshCurrentAuthentication();

		//after: nothing changed
		Authentication authAfter = SecurityContextHolder.getContext().getAuthentication();
		assertTrue(authAfter.getAuthorities().stream()
			.map(Object::toString).anyMatch(it -> it.equals("ROLE_ADMIN")));
		assertEquals(authAfter.getAuthorities().size(), 1);
	}

	@Test
	@WithAnonymousUser
	public void loginTest() throws Exception{
		//before: authentication is anonymous, only has ROLE_ANONYMOUS
		Authentication authBefore = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("anonymous", authBefore.getName());
		assertEquals(1, authBefore.getAuthorities().size());
		assertTrue(authBefore.getAuthorities().stream().map(Object::toString).collect(Collectors.toSet()).contains("ROLE_ANONYMOUS"));

		MvcResult response = mvc.perform(post("/login")
			.param("username", "firstUserName789".toLowerCase())
			.param("password", "firstPassword456"))
			.andReturn();

		//after: redirect to "/"
		assertEquals(302, response.getResponse().getStatus());
		assertEquals("/", response.getResponse().getRedirectedUrl());
	}

	@Test
	@WithAnonymousUser
	public void loginNegativeTest() throws Exception{
		//before: authentication is anonymous, only has ROLE_ANONYMOUS
		Authentication authBefore = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("anonymous", authBefore.getName());
		assertEquals(1, authBefore.getAuthorities().size());
		assertTrue(authBefore.getAuthorities().stream().map(Object::toString).collect(Collectors.toSet()).contains("ROLE_ANONYMOUS"));

		MvcResult response = mvc.perform(post("/login")
			.param("username", "invalidUsername".toLowerCase())
			.param("password", "invalidPassword"))
			.andReturn();

		//after: redirect to "/login/error"
		assertEquals(302, response.getResponse().getStatus());
		assertEquals("/login/error", response.getResponse().getRedirectedUrl());
	}

	@Test
	@WithMockUser(username = "firstUserName789")
	public void logoutTest() throws Exception {
		//before: authentication is present
		Authentication authBefore = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("firstUserName789", authBefore.getName());

		MvcResult response = mvc.perform((post("/logout"))).andReturn();

		//after: 302 response
		assertEquals(302 ,response.getResponse().getStatus());
	}
}
