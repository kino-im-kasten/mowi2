package kik.distributor.data;


/**
 * a {@link ContactPersonForm} for the creation of {@link ContactPerson}s assigned to a distributor.
 * @author Jonas Höpner
 * @version 0.2.0
 */
public class ContactPersonForm {
	private String name;
	private String phoneNumber;
	private String emailAddress;
	private String role;


	/**
	 * default constructor for creating a new ContactPersonForm
	 * @param name The name of the Person
	 * @param phoneNumber their phone number
	 * @param emailAddress their e-mail Address
	 */
	public ContactPersonForm(String name, String phoneNumber, String emailAddress) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		role  = "";
	}

	/**
	 * default constructor for creating a new ContactPersonForm
	 * @param name The name of the Person
	 * @param phoneNumber their phone number
	 * @param emailAddress their e-mail Address
	 * @param role the role of the contact person
	 */
	public ContactPersonForm(String name, String phoneNumber, String emailAddress, String role) {
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.role = role;
	}



	public ContactPersonForm()    {}


	// Getter & Setter

	/**
	 * getter for name
	 * @return String Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * getter for PhoneNumber
	 * @return String PhoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * getter for EmailAddress
	 * @return String EmailAddress
	 */
	public String getEmailAddress() {
		return emailAddress == null ? "" : emailAddress;
	}

	/**
	 * getter for Role
	 * @return String Role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * setter for Role
	 * @param role String Role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * setter for Role
	 * @param name String Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * setter for Role
	 * @param phoneNumber String phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * setter for Role
	 * @param emailAddress String emailAddress
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
