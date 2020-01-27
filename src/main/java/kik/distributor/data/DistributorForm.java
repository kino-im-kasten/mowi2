package kik.distributor.data;

/**
 * a {@link DistributorForm} for the creation of distributors, contains all data needed for creation of a {@link Distributor}
 * @author Jonas Höpner
 * @version 0.2.0
 */
public class DistributorForm {
	private String name;
	private String address;
	private String faxNumber;
	private String phoneNumber;
	private String email;


	/**
	 * default constructor for creating a new DistributorForm
	 * @param name Name of the company
	 * @param address the address of the company
	 * @param phoneNumber the phone Number
	 * @param faxNumber the fax number
	 * @param email the email of an distributor
	 */
	public DistributorForm(String name, String address, String phoneNumber,String email, String faxNumber) {
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber == null ? "" : faxNumber;
		this.email = email;
	}

	/**
	 * standard constructor, used to instantiate empty forms
	 */
	public DistributorForm()  {}

	//Getter & Setter
	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
