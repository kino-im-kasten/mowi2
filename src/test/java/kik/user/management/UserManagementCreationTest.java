package kik.user.management;

import kik.config.Configuration;
import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRoleRepository;
import kik.user.data.accessrole.AccessRolesEnum;
import kik.user.data.forms.CreateNewUserForm;
import kik.user.data.user.User;
import kik.user.data.usertype.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class UserManagementCreationTest {


	private final UserManagement userManagement;
	private final UserTypeManagement userTypeManagement;
	private final AccessRoleManagment accessRoleManagment;
	private final AccessRoleRepository accessRoleRepository;
	private final AuthenticationManager authenticationManager;
	private final Configuration configuration;

	final private String USERNAME = "someUser";
	final private String PASSWORD = "somePassword123";
	final private String USERTYPENAME_FIRST = "firstUserType";
	final private String USERTYPENAME_SECOND = "secondUserType";
	final private HashMap<String ,AccessRole> roleMap= new HashMap<>();
	private Set<String> rejectedFields = new HashSet<String>();

	@Autowired
	public UserManagementCreationTest(UserManagement userManagement, UserTypeManagement userTypeManagement, AccessRoleManagment accessRoleManagment, AccessRoleRepository accessRoleRepository, AuthenticationManager authenticationManager, Configuration configuration){
		this.userManagement = userManagement;
		this.userTypeManagement = userTypeManagement;
		this.accessRoleManagment = accessRoleManagment;
		this.accessRoleRepository = accessRoleRepository;
		this.authenticationManager = authenticationManager;
		this.configuration = configuration;
	}

	@BeforeEach
	public void setup() {
		//deleting everything before
		this.rejectedFields.clear();
		//jobRepository.deleteAll();
		userManagement.getUserRepository().deleteAll();

		userTypeManagement.getUserTypeRepository().deleteAll();
		accessRoleRepository.deleteAll();
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
	public void createAccessRoleTest(){
		try {
			AccessRole adminRole = accessRoleManagment.createAccessRole(AccessRolesEnum.ADMIN);
			AccessRole orgaRole = accessRoleManagment.createAccessRole(AccessRolesEnum.ORGA);
			AccessRole userRole = accessRoleManagment.createAccessRole(AccessRolesEnum.USER);

			assertEquals(adminRole.getSalesPointRole(), Role.of(AccessRolesEnum.ADMIN.toString()), "Admin Salespoint role didnt match");
			assertEquals(adminRole.getSalesPointRole(), Role.of(AccessRolesEnum.ADMIN.toString()), "Orga Salespoint role didnt match");
			assertEquals(adminRole.getSalesPointRole(), Role.of(AccessRolesEnum.ADMIN.toString()), "User Salespoint role didnt match");

			this.roleMap.put("adminRole",adminRole);
			this.roleMap.put("orgaRole",orgaRole);
			this.roleMap.put("userRole",userRole);

		}catch (Exception e){
			fail("Exception during AccessRole-creation: " + e.toString());
		}
	}
	@Test
	public void createUserTypeTest(){
		createAccessRoleTest();

		Set<AccessRole> accessRoleSetAdmin = new HashSet<>(roleMap.values());
		Set<AccessRole> accessRoleSetUser = new HashSet<>(accessRoleSetAdmin);
		accessRoleSetUser.remove(roleMap.get("orgaRole"));
		accessRoleSetUser.remove(roleMap.get("userRole"));

		try{
			//first usertype
			UserType firstUserType = userTypeManagement.createUserType(USERTYPENAME_FIRST,accessRoleSetAdmin);
		}catch (Exception e) {
			fail("Exception during UserType-creation: " + e.toString());
		}
		//is present and name check
		Optional<UserType> oFirstUserType = userTypeManagement.getUserTypeRepository().findByName(USERTYPENAME_FIRST);
		assertTrue(oFirstUserType.isPresent());
		assertEquals(oFirstUserType.get().getName(), USERTYPENAME_FIRST);

		//convert accessroles to salespointroles and check if the set contains all roles
		assertTrue(oFirstUserType.get().getRoleSet().stream()
			.map(AccessRole::getSalesPointRole)
			.collect(Collectors.toSet())
			.containsAll(accessRoleSetAdmin.stream()
				.map(AccessRole::getSalesPointRole)
				.collect(Collectors.toSet())));
		//check size so no extra roles are in the set
		assertEquals(3,oFirstUserType.get().getRoleSet().size());


		//second usertype
		UserType secondUserType = null;
		try{
			//first usertype
			secondUserType = userTypeManagement.createUserType(USERTYPENAME_SECOND,accessRoleSetUser);
		}catch (Exception e) {
			fail("Exception during UserType-creation: " + e.toString());
		}

		assertNotNull(secondUserType);

		//is present and name check
		Optional<UserType> oSecondUserType = userTypeManagement.getUserTypeRepository().findByName(USERTYPENAME_SECOND);
		assertTrue(oSecondUserType.isPresent());
		assertEquals(secondUserType.getName(), USERTYPENAME_SECOND);

		//convert accessroles to salespointroles and check if the set contains the one user role
		assertTrue(oSecondUserType.get().getRoleSet().stream()
			.map(AccessRole::getSalesPointRole)
			.collect(Collectors.toSet())
			.contains(accessRoleSetUser.iterator().next().getSalesPointRole()));
		//check size so no extra roles are in the set
		assertEquals(1,oSecondUserType.get().getRoleSet().size());
	}

	@Test
	public void createUserTest(){
		createUserTypeTest();

		configuration.setRegexPassword(PASSWORD);
		userManagement.createUser(new CreateNewUserForm(USERNAME,PASSWORD, USERTYPENAME_FIRST), new ErrorMock());

		assertTrue(this.rejectedFields.isEmpty());
		assertTrue(userManagement.getUserRepository().findByNameIgnoreCase(USERNAME).isPresent());

		User createdUser = userManagement.getUserRepository().findByNameIgnoreCase(USERNAME).get();

		assertFalse(createdUser.isUnlocked(), "Freshly created user was already unlocked! Should be locked !");
		assertEquals(PASSWORD,createdUser.getUnlockPassword());
		assertTrue(createdUser.getUserAccount().getRoles().isEmpty());
		assertTrue(authenticationManager.matches(Password.UnencryptedPassword.of(PASSWORD), createdUser.getUserAccount().getPassword()), "Password didnt match");
		assertEquals(createdUser.getUserType().getName(), USERTYPENAME_FIRST, "Name of userType of created user didnt match");
		assertEquals(createdUser.getUserAccount().getUsername(), USERNAME.toLowerCase());
		assertNotEquals(createdUser.getUserAccount().getUsername(), USERNAME);
	}
}
