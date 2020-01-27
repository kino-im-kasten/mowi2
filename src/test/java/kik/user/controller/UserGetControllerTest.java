package kik.user.controller;


import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRoleRepository;
import kik.user.data.accessrole.AccessRolesEnum;
import kik.user.data.forms.CreateNewUserForm;
import kik.user.data.user.User;
import kik.user.data.usertype.UserType;
import kik.user.management.AccessRoleManagment;
import kik.user.management.UserManagement;
import kik.user.management.UserTypeManagement;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.net.http.HttpRequest;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserGetControllerTest {

	@Autowired
	MockMvc mvc;

	private UserManagement userManagement;
	private UserTypeManagement userTypeManagement;
	private AccessRoleManagment accessRoleManagment;
	private AccessRoleRepository accessRoleRepository;

	@Autowired
	public UserGetControllerTest(UserManagement userManagement, UserTypeManagement userTypeManagement, AccessRoleManagment accessRoleManagment, AccessRoleRepository accessRoleRepository){
		this.accessRoleManagment = accessRoleManagment;
		this.userTypeManagement = userTypeManagement;
		this.userManagement = userManagement;
		this.accessRoleRepository = accessRoleRepository;
	}

	@BeforeEach
	public void setup(){
		//deleting everything before
		for (String userTypeName : List.of("TestAdmin","TestOrga","TestUser")){
			userTypeManagement.getUserTypeRepository().findByName(userTypeName)
				.ifPresent(userType -> userTypeManagement.deleteUserType(userType));
		}
		for (String userName : List.of("TestBruce","TestAlfred","TestRobin", "TestDeletedUser")){
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
		User bruce = userManagement.createUser(new CreateNewUserForm("TestBruce","123", adminType.getName()), new BindException(Optional.empty(), ""));
		User alfred = userManagement.createUser(new CreateNewUserForm("TestAlfred","123", orgaType.getName()), new BindException(Optional.empty(), ""));
		User robin = userManagement.createUser(new CreateNewUserForm("TestRobin","123",userType.getName()), new BindException(Optional.empty(), ""));

		//simulating first login
		bruce.applyUserType();
		alfred.applyUserType();
		robin.applyUserType();

		//one deleted User
		userManagement.createUser(new CreateNewUserForm("TestDeletedUser", "123", adminType.getName()), new BindException(Optional.empty(), "")).setDeleted(true);
	}

	@Test
	@WithAnonymousUser
	public void getLoginTest() throws Exception {
		MvcResult result = mvc.perform(get("/login")).andReturn();

		assertEquals(200, result.getResponse().getStatus());
	}

	@Test
	@WithMockUser("someone")
	public void getDocumentationTest() throws Exception {
		MvcResult result = mvc.perform(get("/documentation")).andReturn();

		assertEquals(200, result.getResponse().getStatus());
	}

	@Test
	@WithMockUser(roles = {"USER","ORGA","ADMIN"})
	public void userOverviewTest(){

		long userNumber = userManagement.getUserRepository().findByDeleted(false).stream().count();
		long userTypeNumber = userTypeManagement.getUserTypeRepository().findAll().stream().count();
		try {
			MvcResult response = mvc.perform(get("/user")).andReturn();

			Object userList = Objects.requireNonNull(response.getModelAndView()).getModelMap().get("userList");
			Object userTypeList = response.getModelAndView().getModelMap().get("userTypeList");

			assertNotNull("userList should not be null", userList);
			assertNotNull("userTypeList should not be null", userTypeList);

			assertTrue("userList should be Streamable",userList instanceof Streamable<?>);
			assertTrue("userTypeList should be Streamable", userTypeList instanceof Streamable<?>);

			Streamable<User> userStream = ((Streamable<User>) userList);
			Streamable<UserType> userTypeStream = ((Streamable<UserType>) userTypeList);

			assertEquals(userNumber, userStream.stream().count(), "there should be 3 users more than before");
			assertEquals(userTypeNumber, userTypeStream.stream().count(), "there should be 3 userTyps");

			Set<String> allUsers = new HashSet<String>(Set.of("TestBruce", "TestRobin", "TestAlfred"));

			userStream.stream().forEach(it ->  allUsers.remove(it.getName()));
			assertTrue(allUsers.isEmpty());


		}catch (Exception e){
			fail("Exception was thrown: " + e.toString());
		}
	}

	@Test
	@WithMockUser(roles = {"USER","ORGA","ADMIN"})
	public void userDeletedOverviewTest(){
		long deletedUserNumber = userManagement.getUserRepository().findByDeleted(true).stream().count();

		try {
			HashMap<String, Object> sessionattr = new HashMap<String, Object>();
			sessionattr.put("showDeletedUsers", true);

			MvcResult response = mvc.perform(MockMvcRequestBuilders
				.get("/user")
				.sessionAttrs(sessionattr))
				.andReturn();

			Object userList = Objects.requireNonNull(response.getModelAndView()).getModelMap().get("userList");

			assertNotNull("userList should not be null", userList);
			assertTrue("userList should be Streamable",userList instanceof Streamable<?>);

			Streamable<User> userStream = ((Streamable<User>) userList);
			assertEquals(deletedUserNumber, userStream.stream().count(), "there should be 1 deleted users");

			Set<String> allDeletedUsers = new HashSet<String>(Set.of("TestDeletedUser"));
			userStream.stream().forEach(it ->  allDeletedUsers.remove(it.getName()));
			assertTrue(allDeletedUsers.isEmpty());

		}catch (Exception e){
			fail("Exception was thrown: " + e.toString());
		}
	}

	@Test
	@WithMockUser(roles = {"USER","ORGA","ADMIN"})
	public void getSearchUserOverviewTest(){
		try {
			MvcResult response = mvc.perform(MockMvcRequestBuilders
				.get("/user/TestB"))
				.andReturn();
			assertEquals(200, response.getResponse().getStatus());

			Object userList = Objects.requireNonNull(response.getModelAndView()).getModelMap().get("userList");

			assertNotNull("userList should not be null", userList);
			assertTrue("userList should be Streamable",userList instanceof Streamable<?>);

			Streamable<User> userStream = ((Streamable<User>) userList);
			assertEquals(1, userStream.stream().count(), "there should be 1 users that got searched");

			Set<String> allSearchedUsers = new HashSet<String>(Set.of("TestBruce"));
			userStream.stream().forEach(it ->  allSearchedUsers.remove(it.getName()));
			assertTrue(allSearchedUsers.isEmpty());

		}catch (Exception e){
			fail("Exception was thrown: " + e.toString());
		}
	}


	@Test
	@WithMockUser(username = "TestBruce")
	public void getOwnDetailsTest() throws Exception {
		MvcResult result = mvc.perform(get("/user/details/own")).andReturn();
		User user = ((User)Objects.requireNonNull(result.getModelAndView()).getModelMap().getAttribute("user"));

		assertNotNull(user);
		assertEquals("TestBruce", user.getName());
		assertEquals(200, result.getResponse().getStatus());

		assertNotNull(Objects.requireNonNull(result.getModelAndView()).getModelMap().getAttribute("changePasswordForm"));
	}

	@Test
	@WithMockUser(username = "userthatdoesnotexisz")
	public void getOwnDetailsNegativeTest() throws Exception {
		MvcResult result = mvc.perform(get("/user/details/own")).andReturn();

		assertEquals(302 ,result.getResponse().getStatus());
		assertEquals("/", result.getResponse().getRedirectedUrl());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void getAnyDetailsTest() throws Exception {
		MvcResult result = mvc.perform(get("/user/details/any/TestAlfred")).andReturn();

		assertEquals(200, result.getResponse().getStatus());

		HashMap modelMap = Objects.requireNonNull(result.getModelAndView()).getModelMap();
		assertEquals(true, modelMap.get("anyUser"));
		assertEquals("TestAlfred", ((User)modelMap.get("user")).getName());
	}

	@Test
	@WithMockUser(username = "TestBruce", roles = {"ADMIN"})
	public void getAnyDetailsNegativeTest() throws Exception {
		MvcResult result = mvc.perform(get("/user/details/any/nonexistentuser")).andReturn();

		assertEquals(302, result.getResponse().getStatus());
		assertEquals("/user", result.getResponse().getRedirectedUrl());
	}
}
