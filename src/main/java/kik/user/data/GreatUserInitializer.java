package kik.user.data;

import kik.config.Configuration;
import kik.dutyplan.data.job.Job;
import kik.dutyplan.data.job.JobRepository;
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
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.Optional;
import java.util.Set;

/**
 * Initializer for {@link AccessRole}s, {@link UserType}s, and {@link User}s
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Component
@Order(10)
public class GreatUserInitializer implements DataInitializer {

	private final AccessRoleManagment accessRoleManagment;
	private final UserTypeManagement userTypeManagement;
	private final UserManagement userManagement;
	private final AccessRoleRepository accessRoleRepository;
	private final JobRepository jobRepository;
	private final Configuration configuration;

    public GreatUserInitializer(AccessRoleManagment accessRoleManagment,
								UserTypeManagement userTypeManagement,
								UserManagement userManagement,
								AccessRoleRepository accessRoleRepository,
								JobRepository jobRepository, Configuration configuration) {

		this.accessRoleManagment = accessRoleManagment;
		this.userTypeManagement = userTypeManagement;
		this.userManagement = userManagement;
		this.accessRoleRepository = accessRoleRepository;
		this.jobRepository = jobRepository;
		this.configuration = configuration;
	}

    @Override
    public void initialize() {
		String defaultPassword = "123";

		//save original passwordRegex, and set it to make sure creating/unlocking works
		String originalRegex = configuration.getRegexPassword();

		configuration.setRegexPassword(defaultPassword);

		//will be deleted soon, is for overriding bruce, even if he is present
		if (userManagement.getUserRepository().findByNameIgnoreCase("Bruce").isPresent()
			&& userTypeManagement.getUserTypeRepository().findByName("Admin").isPresent()){
			User bruceOverride = userManagement.getUserRepository().findByNameIgnoreCase("Bruce").get();
			jobRepository.findByWorker(bruceOverride).forEach(Job::removeWorker);
			userManagement.getUserRepository().delete(bruceOverride);

			userManagement.createUser(new CreateNewUserForm(
					"Bruce",defaultPassword, "Admin")
				, new BindException(Optional.empty(), ""));
			userManagement.unlockUserAccount(new UnlockAccountForm(defaultPassword, defaultPassword, "Bruce")
				, new BindException(Optional.empty(), ""));
		}
		//reset to original
		configuration.setRegexPassword(originalRegex);
		
		if (accessRoleRepository.findAll().iterator().hasNext()){
			return;
		}
		//initializing AccessRoles
		AccessRole adminRole = accessRoleManagment.createAccessRole(AccessRolesEnum.ADMIN);
		AccessRole orgaRole = accessRoleManagment.createAccessRole(AccessRolesEnum.ORGA);
		AccessRole userRole = accessRoleManagment.createAccessRole(AccessRolesEnum.USER);

		if (!userTypeManagement.getUserTypeRepository().findAll().isEmpty()){
			return;
		}
		//initializing UserTyps
		UserType adminType = userTypeManagement.createUserType("Admin", Set.of(adminRole,orgaRole,userRole));
		UserType orgaType = userTypeManagement.createUserType("Orga", Set.of(orgaRole,userRole));
		UserType userType = userTypeManagement.createUserType("User", Set.of(userRole));

		//only initialize if there are no users
		if (userManagement.getUserRepository().findAll().stream().count() > 0){
			return;
		}

		configuration.setRegexPassword(defaultPassword);

		//initializing Users
		User bruce = userManagement.createUser(new CreateNewUserForm(

			"Bruce",defaultPassword, adminType.getName()), new BindException(Optional.empty(), ""));
		User alfred = userManagement.createUser(new CreateNewUserForm(
			"Alfred",defaultPassword, orgaType.getName()), new BindException(Optional.empty(), ""));
		User robin = userManagement.createUser(new CreateNewUserForm(
			"Robin",defaultPassword,userType.getName()), new BindException(Optional.empty(), ""));

		//simulating first login
		Set.of(bruce,alfred,robin).forEach(user ->
			userManagement.unlockUserAccount(new UnlockAccountForm(defaultPassword,
				defaultPassword, user.getName()), new BindException(Optional.empty(), "")));

		//set original regex
		configuration.setRegexPassword(originalRegex);

	}

}

