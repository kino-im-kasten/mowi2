package kik.distributor.data;

import javax.persistence.*;

import java.io.Serializable;

/**
 * a {@link ContactPerson} for the contact persons assigned to a distributor.
 * After instantiating, this MUST be saved to the {@link ContactPersonRepository}!
 * @author Jonas Höpner
 * @version 0.2.0
 */

@Entity
public class ContactPerson implements Serializable {

	@Column( length = Long.SIZE )
	@GeneratedValue	@Id private Long id;
	private String name;
	private String emailAddress;
	private String phoneNumber;
	private String role;

	/**
	 * spring default constructor, unused otherwise
	 */
	public ContactPerson() {}

	/**
	 * instantiates a new ContactPerson. Don't forget to save it in the {@link ContactPersonRepository}!
	 * @param name The Name of the Person
	 * @param emailAddress the Email-Address of the Person
	 * @param phoneNumber the Phone Number of the Person
	 * @param role optional text field for the role of a contactperson
	 */
	public ContactPerson(String name, String emailAddress, String phoneNumber, String role) {
		this.name = name;
		this.emailAddress = emailAddress;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	/**
	 * the default constructor for instantiating a contact Person.
	 * Don't forget to save it in the {@link ContactPersonRepository}!
	 * @param form The form which contains the data needed to create a contact person
	 */
	public ContactPerson(ContactPersonForm form) {
		this.name = form.getName();
		this.emailAddress = form.getEmailAddress();
		this.phoneNumber = form.getPhoneNumber();
		this.role = form.getRole();
	}


	/**
	 *  setter for id
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * setter for email
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * setter for phone number
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * setter for role
	 * @param role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * getter for 
	 * @return role string
	 */
	public String getRole() {
		return role;
	}

	/**
	 * getter for id
	 * @return long id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * getter for name
	 * @return name string
	 */
	public String getName() {
		return name;
	}

	/**
	 * getter for email
	 * @return email as string
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * getter for phone number
	 * @return phone number as string
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

}
