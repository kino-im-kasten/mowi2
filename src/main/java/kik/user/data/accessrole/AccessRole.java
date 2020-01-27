package kik.user.data.accessrole;

import org.salespointframework.useraccount.Role;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * An {@link AccessRole} instance encapsulates a {@link Role} of salespoint.
 * Entities of {@link AccessRole} are only created and saved during initialization.
 *
 * @author Richard Müller
 * @version 0.0.1
 */

@Entity
public class AccessRole {

	private @Id @GeneratedValue long id;
	private Role salesPointRole;

	/**
	 * Needed, not-used, non-default constructor of {@link AccessRole}
	 */
	public AccessRole(){
	}

	/**
	 * Default constructor of {@link AccessRole}
	 * {@link AccessRolesEnum} instances are converted to {@link Role}s during construction
	 *
	 * @param accessRole an instance of the {@link AccessRolesEnum}
	 */
	public AccessRole(AccessRolesEnum accessRole){
		this.salesPointRole = Role.of(accessRole.toString());
	}

	//getter
	public long getId() {
		return id;
	}
	public Role getSalesPointRole() {
		return salesPointRole;
	}
}