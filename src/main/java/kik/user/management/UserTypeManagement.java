package kik.user.management;


import kik.user.data.accessrole.AccessRole;
import kik.user.data.user.User;
import kik.user.data.usertype.UserType;
import kik.user.data.usertype.UserTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Manages requests of {@link kik.user.controller.UserPostController} that create, delete or change {@link UserType}s in any way.
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Service
@Transactional
public class UserTypeManagement {

	private final UserTypeRepository userTypeRepository;

	/**
	 * The default constructor of {@link UserTypeManagement}
	 *
	 * @param userTypeRepository repository that contains all {@link UserType}s
	 */
	public UserTypeManagement(UserTypeRepository userTypeRepository){
		this.userTypeRepository = userTypeRepository;
	}

	/**
	 * method that instantiates and saves an {@link UserType}
	 *
	 * @param name userType-name
	 * @param roleSet set containing all {@link AccessRole}s the {@link UserType} should own
	 * @return {@link UserType} object
	 */
	public UserType createUserType(String name, Set<AccessRole> roleSet){
		return userTypeRepository.save(new UserType(name, roleSet));
	}

	/**
	 * method to delete an {@link UserType}
	 *
	 * @param deleteType {@link UserType} to delete
	 */
	public void deleteUserType(UserType deleteType){
		userTypeRepository.delete(deleteType);
	}


	public UserTypeRepository getUserTypeRepository() {
		return userTypeRepository;
	}
}
