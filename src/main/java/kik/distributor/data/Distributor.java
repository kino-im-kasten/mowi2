package kik.distributor.data;

import kik.distributor.management.DistributorManagement;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * a {@link Distributor} for the distributors.
 * @author Jonas Höpner
 * @version 0.2.0
 */
@Entity
public class Distributor {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String address;
	private String phoneNumber;
	private String faxNumber;
	private String email;

	/**
	 * simple list containing all assigned contact persons
	 */
	@Lob
	@Column( length = 100000 )
	private HashMap<Long,ContactPerson> contactPersons = new HashMap<>();

	public Distributor()  {}

	/**
	 * alternative constructor, default is Distributor.Distributor(DistributorForm form)
	 * @param name Name of the company
	 * @param address Address of the company
	 * @param phoneNumber Number which can be called,
	 * @param email the email of an distributor
	 */
	public Distributor(String name, String address, String phoneNumber, String email) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.faxNumber = "";
		this.email = email;
	}

	/**
	 * alternative constructor, default is Distributor.Distributor(DistributorForm form)
	 * @param name Name of the company
	 * @param address Address of the company
	 * @param phoneNumber Number which can be called,
	 */
	public Distributor(String name, String address, String phoneNumber) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.faxNumber = "";
		this.email = "";
	}

	/**
	 * another alternative for manual creation of a Distributor
	 * @param name Name of the company
	 * @param address Address of the company
	 * @param phoneNumber Number which can be called,
	 * @param faxNumber Number for Facsimiles
	 * @param email the email of an distributor
	 */
	public Distributor(String name, String address, String phoneNumber,String email, String faxNumber) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.email = email;
	}

	/**
	 * default constructor for Distributor
	 * @param form The {@link DistributorForm} contains all information needed to create a new Distributor
	 */
	public Distributor(DistributorForm form) {
		this.name= form.getName();
		this.address = form.getAddress();
		this.phoneNumber = form.getPhoneNumber();
		this.faxNumber = form.getFaxNumber();
		email = form.getEmail();
	}

	/**
	 * checks, if a {@link ContactPerson} exists within this
	 * @param id the {@link ContactPerson} to be checked
	 * @return true if existent, false otherwise
	 */
	public boolean containsContactPersonById(Long id) {
		return contactPersons.values().stream().anyMatch(c -> c.getId().equals(id));
	}

	/**
	 * adds a {@link ContactPerson} to this Distributor
	 * @param person Person to be added
	 * @param cid ID of the {@link ContactPerson} to be added
	 */
	public void addContactPerson(ContactPerson person, Long cid) {
		contactPersons.put(cid,person);
	}

	/**
	 * removes a {@link ContactPerson} from this
	 * @param cid ContactPerson to be removed
	 */
	public void removeContactPerson(Long cid) {
		contactPersons.remove(cid);
	}

	/**
	 * gets all ContactPersons in this Distributor
	 * @return a List containing all {@link ContactPerson}s
	 */
	public List<ContactPerson> getContactPersons() {
		return DistributorManagement.getListFromIterator(contactPersons.values().iterator());
	}

	/**
	 * checks via ID if it is the same Distributor
	 * @param other Object to be checked against
	 * @return
	 */
	@Override public boolean equals(Object other) {
		return (other instanceof Distributor) && ((Distributor)other).getId().equals(id);
	}




	// Getter & Setter


	/**
	 * getter for ID
	 * @return this ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * setter for ID
	 * @param id Long ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getter for Name
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter for Name
	 * @param name String new Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getter for Address
	 * @return String address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * setter for address
	 * @param address String new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * getter for PhoneNumber
	 * @return String PhoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * setter for Phone Number
	 * @param phoneNumber new String PhoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * getter for FaxNumber
	 * @return "" if FaxNumber is not set
	 */
	public String getFaxNumber() {
		if (faxNumber == null) {
			return "";
		}
		return faxNumber;
	}

	/**
	 * setter for FaxNumber
	 * @param faxNumber new string FaxNumber
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * getter for Email
	 * @return the EMail of the Distributor
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * setter for Email
	 * @param email the new Email
	 */
	public void setEmail(String email) {
		this.email = email;
	}



}
