package kik.user.controller;

import kik.config.Configuration;
import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRoleRepository;
import kik.user.data.accessrole.AccessRolesEnum;
import kik.user.data.forms.CreateNewUserForm;
import kik.user.data.forms.UnlockAccountForm;
import kik.user.data.user.User;
import kik.user.data.usertype.UserType;
import kik.user.management.AccessRoleManagment;
import kik.user.management.UserManagement;
import kik.user.management.UserTypeManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserPostControllerTest {

	@Autowired
	MockMvc mvc;

	private final UserManagement userManagement;
	private final UserTypeManagement userTypeManagement;
	private final AccessRoleManagment accessRoleManagment;
	private final AccessRoleRepository accessRoleRepository;
	private final AuthenticationManager authenticationManager;
	private final Configuration configuration;

	@Autowired
	public UserPostControllerTest(UserManagement userManagement, UserTypeManagement userTypeManagement, AccessRoleManagment accessRoleManagment, AccessRoleRepository accessRoleRepository, AuthenticationManager authenticationManager, Configuration configuration){
		this.accessRoleManagment = accessRoleManagment;
		this.userTypeManagement = userTypeManagement;
		this.userManagement = userManagement;
		this.accessRoleRepository = accessRoleRepository;
		this.authenticationManager = authenticationManager;
		this.configuration = configuration;
	}

	public void makeUserAdmin(String userName){
		userManagement.getUserRepository().findByUserType(userTypeManagement.getUserTypeRepository().findByName("Admin").get())
			.stream().forEach(user -> user.setUserType(userTypeManagement.getUserTypeRepository().findByName("TestAdmin").get()));
		userTypeManagement.getUserTypeRepository().deleteByName("Admin");
		userManagement.getUserRepository().findByNameIgnoreCase(userName)
			.get().setUserType(userTypeManagement.createUserType("Admin", Set.of()));
	}

	@BeforeEach
	public void setup(){
		//deleting everything before
		for (String userTypeName : List.of("TestAdmin","TestOrga","TestUser")){
			userTypeManagement.getUserTypeRepository().findByName(userTypeName)
				.ifPresent(userType -> userTypeManagement.deleteUserType(userType));
		}
		for (String userName : List.of("TestBruce","TestAlfred","TestRobin")){
			userManagement.getUserRepository().findByNameIgnoreCase(userName)
				.ifPresent(user -> userManagement.getUserRepository().delete(user));
		}

		//initializing AccessRoles
		AccessRole adminRole = accessRoleManagment.createAccessRole(AccessRolesEnum.ADMIN);
		AccessRole orgaRole = accessRoleManagment.createAccessRole(AccessRolesEnum.ORGA);
		AccessRole userRole = accessRoleManagment.createAccessRole(AccessRolesEnum.USER);

		//initializing UserTyps
		UserType adminType = userTypeManagement.createUserType("TestAdmin", Set.of(adminRole,orgaRole,userRole));
		UserType orgaType = userTypeManagement.createUserType("TestOrga", Set.of(orgaRole,userRole));
		UserType userType = userTypeManagement.createUserType("TestUser", Set.of(userRole));

		//initializing Users
		configuration.setRegexPassword("123");
		User bruce = userManagement.createUser(new CreateNewUserForm("TestBruce","123", adminType.getName()), new BindException(Optional.empty(), ""));
		User alfred = userManagement.createUser(new CreateNewUserForm("TestAlfred","123", orgaType.getName()), new BindException(Optional.empty(), ""));
		User robin = userManagement.createUser(new CreateNewUserForm("TestRobin","123",userType.getName()), new BindException(Optional.empty(), ""));

		//simulating first login
		userManagement.unlockUserAccount(new UnlockAccountForm("123","123", bruce.getName()), new BindException(Optional.empty(), ""));
		userManagement.unlockUserAccount(new UnlockAccountForm("123","123", alfred.getName()), new BindException(Optional.empty(), ""));
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void createUserTest() throws Exception {
		String userName = "Max";
		String password = "12341234";
		String userTypeName = "TestOrga";
		configuration.setRegexPassword(password);

		MvcResult response = mvc.perform(post("/user/createNewUser")
			.param("name", userName)
			.param("password", password)
			.param("newUserTypeName", userTypeName))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());

		Optional<User> oUser = userManagement.getUserRepository().findByNameIgnoreCase(userName);
		assertTrue("User was not created, maybe with wrong name", oUser.isPresent());
		assertEquals("User was found with username but has wrong Username....",userName,oUser.get().getName());
		assertEquals("User should have the right UserType", userTypeName, oUser.get().getUserType().getName());
		assertTrue("Password does not match",
			authenticationManager.matches(Password.UnencryptedPassword.of(password),oUser.get().getUserAccount().getPassword()));
		assertFalse("Users should not be unlocked right after creation!" ,oUser.get().isUnlocked());
		assertTrue("The user account should not have any roles right after creation", oUser.get().getUserAccount().getRoles().isEmpty());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void createUserNegativeTest() throws Exception {
		MvcResult response = mvc.perform(post("/user/createNewUser")
			.param("name", "somename")
			.param("password", "somepassword")
			.param("newUserTypeName", "usertypethatdoesnotexist"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "UserTypeNotFound"))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = "ADMIN")
	public void changeUserTypeTest() throws Exception{

		String userTypeName = "TestAdmin";
		String userName = "TestAlfred";

		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(userName);

		assertTrue(userTypeManagement.getUserTypeRepository().findByName(userTypeName).isPresent());
		assertTrue(oUserBefore.isPresent());

		assertEquals(2, oUserBefore.get().getUserAccount().getRoles().stream().count());

		mvc.perform(post("/user/changeUserType/" + userName)
			.param("userTypeName", userTypeName)
			.param("userName", userName))
			.andReturn();

		assertTrue(userManagement.getUserRepository().findByNameIgnoreCase(userName).isPresent());

		User alfred = userManagement.getUserRepository().findByNameIgnoreCase(userName).get();

		assertEquals(userTypeName,alfred.getUserType().getName());
		assertEquals(3, alfred.getUserAccount().getRoles().stream().count());
		assertTrue(alfred.isUnlocked());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = "ADMIN")
	public void changeUserTypeUserExceptionTest() throws Exception{
		MvcResult response = mvc.perform(post("/user/changeUserType/" + "nonexistentusername")
			.param("userTypeName", "TestAdmin")
			.param("userName", "nonexistentusername"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "UserNotFound"))
			.andReturn();
		assertEquals(302, response.getResponse().getStatus());
		assertEquals("/user/", response.getResponse().getRedirectedUrl());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = "ADMIN")
	public void changeUserTypeUserTypeExceptionTest() throws Exception{
		MvcResult response = mvc.perform(post("/user/changeUserType/" + "TestBruce")
			.param("userTypeName", "nonexistentusertype")
			.param("userName", "TestBruce"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "UserTypeNotFound"))
			.andReturn();
		assertEquals(302, response.getResponse().getStatus());
		assertEquals("/user/details/any/TestBruce", response.getResponse().getRedirectedUrl());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = "ADMIN")
	public void changeUserTypeAdminDangerExceptionTest() throws Exception{
		this.makeUserAdmin("TestBruce");

		MvcResult response = mvc.perform(post("/user/changeUserType/" + "TestBruce")
			.param("userTypeName", "TestAdmin")
			.param("userName", "TestBruce"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "AdminDanger"))
			.andReturn();
		assertEquals(302, response.getResponse().getStatus());
		assertEquals("/user/details/any/TestBruce", response.getResponse().getRedirectedUrl());
	}

	@Test
	@WithMockUser(username = "TestRobin")
	public void unlockAccountOwnTest() throws Exception {
		String userName = "TestRobin";
		String newPassword = "SomePassword123";
		configuration.setRegexPassword(newPassword);

		Optional<User> oUserBefore = userManagement.getUserRepository().findByNameIgnoreCase(userName);
		assertTrue("User should be present before perform", oUserBefore.isPresent());
		long rolesCountBefore = oUserBefore.get().getUserAccount().getRoles().stream().count();
		assertEquals("a locked User should not have any roles", 0, rolesCountBefore);

		MvcResult response = mvc.perform(post("/unlockAccount/own")
			.param("newPassword", newPassword)
			.param("newPasswordAgain", newPassword)
			.param("userName", userName))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());

		Optional<User> oUser = userManagement.getUserRepository().findByNameIgnoreCase(userName);
		assertTrue(oUser.isPresent());
		assertTrue("User should be unlocked now!", oUser.get().isUnlocked());
		assertTrue("There should be roles added to the UserAccount after unlocking it", 0 < oUser.get().getUserAccount().getRoles().stream().count());
	}

	@Test
	@WithMockUser(username = "nonexistentuser")
	public void unlockAccountOwnUserExceptionTest() throws Exception {
		String newPassword = "SomePassword123";
		configuration.setRegexPassword(newPassword);


		MvcResult response = mvc.perform(post("/unlockAccount/own")
			.param("newPassword", newPassword)
			.param("newPasswordAgain", newPassword)
			.param("userName", "nonexistentuser"))
			.andReturn();

		assertEquals(200, response.getResponse().getStatus());
		assertEquals("UserNotFound", Objects.requireNonNull(response.getModelAndView()).getModel().get("exception"));
	}

	@Test
	@WithMockUser(username = "TestBruce")
	public void unlockAccountOwnValidationErrorsTest() throws Exception {
		String newPassword = "SomePassword123";
		configuration.setRegexPassword("willnotmatch");

		MvcResult response = mvc.perform(post("/unlockAccount/own")
			.param("newPassword", newPassword)
			.param("newPasswordAgain", newPassword + "somethingelse")
			.param("userName", "TestBruce"))
			.andReturn();

		assertEquals(200, response.getResponse().getStatus());

		Errors errors = ((Errors) Objects.requireNonNull(response.getModelAndView()).getModel().get("errors"));
		assertTrue(errors.hasFieldErrors("newPassword"));
		assertTrue(errors.hasFieldErrors("newPasswordAgain"));
	}

	@Test
	@WithMockUser(username = "TestBruce", roles={"ADMIN"})
	public void safeDeleteTest() throws Exception {
		String userName = "TestRobin";

		MvcResult response = mvc.perform(post("/user/delete/" + userName + "/confirm"))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());

		assertTrue("User should still be around",userManagement.getUserRepository().findByNameIgnoreCase(userName).isPresent());
		assertTrue("User should have boolean isDeleted==true", userManagement.getUserRepository().findByNameIgnoreCase(userName).get().isDeleted());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles={"ADMIN"})
	public void safeDeleteAdminDangerTest() throws Exception {
		String userName = "TestBruce";
		this.makeUserAdmin(userName);

		MvcResult response = mvc.perform(post("/user/delete/" + userName + "/confirm"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "AdminDanger"))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());

		assertTrue("User should still be around",userManagement.getUserRepository().findByNameIgnoreCase(userName).isPresent());
		assertFalse("User should boolean isDeleted==false", userManagement.getUserRepository().findByNameIgnoreCase(userName).get().isDeleted());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles={"ADMIN"})
	public void deleteForeverTest() throws Exception {
		String userName = "TestRobin";
		userManagement.getUserRepository().findByNameIgnoreCase("TestRobin").get().setDeleted(true);

		MvcResult response = mvc.perform(post("/user/delete/" + userName + "/confirmForever"))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());

		assertTrue("User should be gone", userManagement.getUserRepository().findByNameIgnoreCase(userName).isEmpty());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles={"ADMIN"})
	public void deleteForeverUserExceptionTest() throws Exception {
		String userName = "nonexistinguser";

		MvcResult response = mvc.perform(post("/user/delete/" + userName + "/confirmForever"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "UserNotFound"))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void showDeletedUsersSwitchTest() throws Exception {
		MvcResult responseOne = mvc.perform(post("/user/showDeletedUsers")).andReturn();

		assertEquals(302, responseOne.getResponse().getStatus());
		assertEquals("/user", responseOne.getResponse().getRedirectedUrl());
		assertEquals(true, Objects.requireNonNull(responseOne.getRequest().getSession()).getAttribute("showDeletedUsers"));

		HashMap<String, Object> sessionattr = new HashMap<String, Object>();
		sessionattr.put("showDeletedUsers", true);
		MvcResult responseTwo = mvc.perform(post("/user/showDeletedUsers")
			.sessionAttrs(sessionattr)).andReturn();

		assertEquals(302, responseTwo.getResponse().getStatus());
		assertEquals("/user", responseOne.getResponse().getRedirectedUrl());
		assertNull(responseTwo.getRequest().getSession().getAttribute("showDeletedUsers"));
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void  recoverUserTest() throws Exception {
		userManagement.getUserRepository().findByNameIgnoreCase("TestRobin").get().setDeleted(true);

		MvcResult response = mvc.perform(post("/user/recoverUser/TestRobin")).andReturn();

		assertEquals(302, response.getResponse().getStatus());
		assertFalse(userManagement.getUserRepository().findByNameIgnoreCase("TestRobin").get().isDeleted());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void  recoverUserExceptionTest() throws Exception {
		MvcResult response = mvc.perform(post("/user/recoverUser/nonexistinguser"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "UserNotFound"))
			.andReturn();

		assertEquals(302, response.getResponse().getStatus());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void changeUserNameTest() throws Exception {
		MvcResult response = mvc.perform(post("/user/details/any/" + "TestAlfred" + "/changeUserName")
			.param("newUserName", "newTestAlfred"))
			.andExpect(MockMvcResultMatchers.flash().attribute("error", nullValue()))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void changeUserNameValidationErrorTest() throws Exception {
		MvcResult response = mvc.perform(post("/user/details/any/" + "TestAlfred" + "/changeUserName")
			.param("newUserName", "TestBruce"))
			.andExpect(MockMvcResultMatchers.flash().attributeExists("errors"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void changeUserExceptionTest() throws Exception {
		MvcResult response = mvc.perform(post("/user/details/any/" + "nonexistinguser" + "/changeUserName")
			.param("newUserName", "validnewname"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "UserNotFound"))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void changeAdminDangerTest() throws Exception {
		makeUserAdmin("TestAlfred");
		MvcResult response = mvc.perform(post("/user/details/any/" + "TestAlfred" + "/changeUserName")
			.param("newUserName", "validnewname"))
			.andExpect(MockMvcResultMatchers.flash().attribute("exception", "AdminDanger"))
			.andReturn();
	}

}


