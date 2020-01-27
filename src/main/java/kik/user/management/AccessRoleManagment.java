package kik.user.management;


import kik.user.data.accessrole.AccessRoleRepository;
import kik.user.data.accessrole.AccessRole;
import kik.user.data.accessrole.AccessRolesEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Does not manage any user requests. Is only for creating {@link AccessRole}s during initialization.
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Service
@Transactional
public class AccessRoleManagment {

	private final AccessRoleRepository accessRoleRepository;

	/**
	 * The default constructor of {@link AccessRoleManagment}
	 *
	 * @param accessRoleRepository repository that contains all {@link AccessRole}s
	 */
	public AccessRoleManagment(AccessRoleRepository accessRoleRepository) {
		this.accessRoleRepository = accessRoleRepository;
	}

	/**
	 * method to instantiate and save {@link AccessRole}s
	 *
	 * @param accessRolesEnum Access-Role (is an Enum so all possible Roles are fixed)
	 * @return {@link AccessRole} object
	 */
	public AccessRole createAccessRole(AccessRolesEnum accessRolesEnum){
		return accessRoleRepository.save(new AccessRole(accessRolesEnum));
	}
}

