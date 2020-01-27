package kik.user.data.usertype;


import kik.user.data.accessrole.AccessRole;

import javax.persistence.*;
import java.util.Set;

/**
 * {@link UserType} holds a set of {@link AccessRole}s
 * that represent the accessible areas of {@link kik.user.data.user.User}s with such Type.
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Entity
public class UserType {
	private @Id @GeneratedValue long id;

	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<AccessRole> roleSet;

	/**
	 * Needed, not-used, non-default constructor of {@link UserType}
	 */
	public UserType(){ }

	/**
	 * default constructor of {@link UserType}
	 *
	 * @param name ,name of UserType e.g. "Admin", "Orga"
	 * @param roleSet ,roles of UserType
	 */
	public UserType(String name, Set<AccessRole> roleSet){
		this.name = name;
		this.roleSet = roleSet;
	}

	//getter
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Set<AccessRole> getRoleSet() {
		return roleSet;
	}

	//setter
	public void setName(String name) {
		this.name = name;
	}
}
