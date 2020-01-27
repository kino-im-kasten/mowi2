package kik.user.data.user;

import kik.user.data.usertype.UserType;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * {@link User} encapsulates {@link UserAccount} of the Salespoint Framework
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Entity
@Table(name = "kikuser")
public class User {

	private @Id @GeneratedValue long id;
	private UUID uuid= UUID.randomUUID();

	private String name;

	@ManyToOne
	private UserType userType;

	@OneToOne(cascade = CascadeType.ALL)
	private UserAccount userAccount;

	private boolean unlocked;

	private boolean deleted;

	private String unlockPassword;

	/**
	 * Needed, not-used, non-default constructor of {@link UserType}
	 */
	public User(){ }

	/**
	 * default constructor of {@link User}
	 *
	 * @param userAccount ,Salespoint UserAccount Object
	 * @param userType ,{@link UserType} Object that holds Roles witch are added later to the userAccount.
	 */
	public User(String userName, UserAccount userAccount, UserType userType) {
		this.userAccount = userAccount;
		this.userType = userType;
		this.unlocked = false;
		this.deleted = false;
		this.name = userName;
	}

	/**
	 * method to apply the AccessRights of the {@link UserType} owned by the {@link User}
	 * Sideeffect: sets unlocked to true for the {@link User}
	 */
	public void applyUserType(){
		//remove all previous roles
		Set<Role> oldRoles = new HashSet<Role>(this.userAccount.getRoles().toSet());
		oldRoles.forEach(this.userAccount::remove);

		//add new ones
		this.userType.getRoleSet().forEach(it -> this.userAccount.add(it.getSalesPointRole()));

		//set status to unlocked
		this.unlocked = true;
	}
	/**
	 * method to remove the AccessRights of the {@link UserType} owned by the {@link User}
	 * Sideeffect: sets unlocked to false for the {@link User}
	 */
	public void deapplyUserType(){
		//remove all previous roles
		Set<Role> oldRoles = new HashSet<Role>(this.userAccount.getRoles().toSet());
		oldRoles.forEach(this.userAccount::remove);

		//set status to locked
		this.unlocked = false;
	}

	//getter
	public long getId() {
		return id;
	}
	public String getName(){
		return name;
	}
	public UserType getUserType() {
		return userType;
	}
	public UserAccount getUserAccount() {
		return userAccount;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public boolean isUnlocked() {
		return unlocked;
	}
	public String getUnlockPassword(){
		return unlockPassword;
	}
	public UUID getUuid() {
		return uuid;
	}

	//setter
	public void setName(String name) {
		this.name = name;
		this.userAccount.setFirstname(name);
	}
	public void setUnlockPassword(String unlockPassword){
		this.unlockPassword = unlockPassword;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}
	public void setUnlocked(Boolean unlocked){
		this.unlocked = unlocked;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
