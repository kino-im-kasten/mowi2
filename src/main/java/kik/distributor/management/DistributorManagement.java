package kik.distributor.management;

import kik.booking.data.Booking;
import kik.booking.management.BookingManagement;
import kik.distributor.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * a {@link DistributorManagement} for working with {@link Distributor}s and {@link ContactPerson}s.
 * @author Jonas Höpner
 * @version 0.2.0
 */
@Service
public class DistributorManagement {
	private DistributorRepository distributors;
	private ContactPersonRepository contactPersons;
	private BookingManagement bookingManagement;

	/**
	 * thrown when an Object already exists in the Repository.
	 */
	public class DuplicateException extends Exception {
		public DuplicateException(String message) {
			super(message);
		}

		public DuplicateException() {}
	}
	/**
	 * default constructor of the distributor Management
	 * @param distributors the Repository of Distributor
	 * @param contactPersons the Repository of Contact Persons
	 */
	@Autowired
	public DistributorManagement(DistributorRepository distributors,
								 ContactPersonRepository contactPersons,
								 BookingManagement bookingManagement) {
		this.contactPersons = contactPersons;
		this.distributors = distributors;
		this.bookingManagement = bookingManagement;
	}

	/**
	 * checks, if an distributor with given name exists in the {@link DistributorRepository}
	 * @param name name to be checked
	 * @return true if existent, false otherwise
	 */
	private boolean existByName(String name) {			//had to be changed from Iterator to List because of Jenkins.
														// This works now, wont touch it anymore
		List<Distributor> list = new ArrayList<>();
		for (Distributor d : distributors.findAll()) {
			list.add(d);
		}
		String check;
		for ( int i = 0; i < list.size(); i++) {
			check = list.get(i).getName();
			if (check != null && name.equals(check)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * used to create Distributors
	 *
	 * @param form the object containing all information needed to create a Distributor
	 * @return ID of the created distributor
	 * @throws DuplicateException when a Distributor with given name is already present
	 */
	@Nullable
	public Long createDistributor(DistributorForm form) throws DuplicateException {
		Distributor dist;
		if (form != null) {
			if (!existByName(form.getName())) {
				dist = new Distributor(form);
				distributors.save(dist);
				return dist.getId();
			} else {
				throw new DuplicateException("This Distributor exists already!");
			}
		}
		return null;
	}

	/**
	 * used to edit entries in the Distributor repository.
	 * @param form contains all information of a distributor
	 * @param id the ID of the distributor to be changed
	 */
	@Nullable
	public boolean editDistributor(DistributorForm form, Long id) {
		if (distributors.existsById(id)) {
			Distributor edit = distributors.findById(id).get();

			edit.setName(form.getName());
			edit.setPhoneNumber(form.getPhoneNumber());
			edit.setAddress(form.getAddress());
			edit.setFaxNumber(form.getFaxNumber());
			edit.setEmail(form.getEmail());
			edit.setId(id);
			return distributors.save(edit) != null; //update entry
		}
		return false;
	}

	/**
	 * used to delete a distributor from the repository.
	 * @param id the ID of the distributor
	 * @return true if successful, false otherwise.
	 */
	public boolean deleteDistributor(Long id) {
		if (distributors.existsById(id))  {
			Distributor d = distributors.findById(id).get();

			//checking, if there is a booking referencing this distributor
			for (Booking b : bookingManagement.getAllBookings()) {
				if ( d.getName().equals(b.getDistributor().getName())) {
					// we cannot delete this distributor!
					return false;
				}
			}

			//because no one is referencing this, we can delete the distributor
			for (ContactPerson c : d.getContactPersons()) {
				d.removeContactPerson(c.getId());
			}

			distributors.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * for my convenience, converts an iterator to a list.
	 * @param iterator the iterator
	 * @param <T> may be everything which can be in a list
	 * @return a List with the objects of the iterator
	 */
	public static <T> List<T> getListFromIterator(Iterator<T> iterator) {
		List<T> list = new ArrayList<>();
		iterator.forEachRemaining(list::add);
		return list;
	}

	/**
	 *
	 * @return all Distributors in the {@link DistributorRepository}
	 */
	public List<Distributor> getAllDistributors() {
		return getListFromIterator(distributors.findAll().iterator());
	}

	/**
	 *
	 * @param Id Id of searched {@link Distributor}
	 * @return an Optional containing the distributor
	 */
	public Optional<Distributor> getDistributorById(Long Id) {
		return distributors.findById(Id);
	}

	/**
	 * adds a new Contactperson to given Distributor.
	 * @param form the {@link ContactPersonForm} of the Contact person
	 * @param distributor the {@link Distributor}, to whom the Contact person shall be added
	 * @throws DuplicateException if ContactPerson is already present
	 */
	public boolean addContactPersonToDistributor(ContactPersonForm form,
											  Distributor distributor) throws DuplicateException {
		ContactPerson person = new ContactPerson(form);
		if (contactPersonNameToId(person.getName()) != null) {
			throw new DuplicateException();
		}
		contactPersons.save(person);
		Distributor dist = distributors.findById(distributor.getId()).get();
		dist.addContactPerson(person,contactPersonNameToId(person.getName()));
		return distributors.save(dist) != null;
	}

	/**
	 * adds a {@link ContactPerson} to an {@link Distributor}
	 * @param form Form containing data for the Contact Person
	 * @param did ID of the Distributor
	 * @throws DuplicateException when ContactPerson is already present
	 */
	public Long addContactPersonToDistributor(ContactPersonForm form, Long did) throws DuplicateException {
		ContactPerson person = new ContactPerson(form);
		if (contactPersonNameToId(person.getName()) != null) {
			throw new DuplicateException("This ContactPerson exists already in "
				+ distributors.findById(did).get().getName());
		}
		contactPersons.save(person);
		Distributor dist = distributors.findById(did).get();
		dist.addContactPerson(person,contactPersonNameToId(person.getName()));
		distributors.save(dist);
		return person.getId();
	}

	/**
	 * searches for the name of the Contactperson in the {@link ContactPersonRepository} and returns its Id.
	 * @param name the person's name searched for
	 * @return Long Id of the person, null if nonexistent
	 */
	public Long contactPersonNameToId(String name) {
		Iterator<ContactPerson> iter = contactPersons.findAll().iterator();
		//has to be rewritten if tested,
		// it works, but jenkins seems not to like iterators.
		//Confusing.

		while (iter.hasNext()) {
			ContactPerson check = iter.next();
			if (check.getName().equals(name)) {
				return check.getId();
			}
		}
		return null;
	}

	/**
	 * Provides the function to edit a contact person
	 * @param did The Id of the {@link Distributor} in which the contact person is saved
	 * @param cid The Id of the Contact person which shall be edited
	 * @param form contains all information of the contact person
	 * @return true if successful, false otherwise
	 */
	public boolean editContactPerson(Long did, Long cid, ContactPersonForm form) {
		ContactPerson person = contactPersons.findById(cid).get();

		Distributor dist = distributors.findById(did).get();
		dist.removeContactPerson(cid);

		person.setEmailAddress(form.getEmailAddress());
		person.setName(form.getName());
		person.setPhoneNumber(form.getPhoneNumber());
		person.setRole(form.getRole());

		//update entries
		dist.addContactPerson(person,cid);
		return contactPersons.save(person)  != null && distributors.save(dist) != null;
	}

	/**
	 * adds a Contact person to the {@link ContactPersonRepository}
	 * @param person person to be added
	 * @return the Id of the saved Person
	 */
	public Long addContactPerson (ContactPersonForm person) {
		return contactPersons.save(new ContactPerson(person)).getId();
	}

	/**
	 * adds a Contact person to the {@link ContactPersonRepository}
	 * @param person person to be added
	 * @return the Id of the saved Person
	 */
	public Long addContactPerson (ContactPerson person) {
		return contactPersons.save(person).getId();
	}

	/**
	 * removes a {@link ContactPerson} from a {@link Distributor}
	 * @param did the distributor containing the contactperson
	 * @param cid the ContactPerson
	 * @return true if successful, false otherwise
	 */
	public boolean removeContactPersonFromDistributor(Long did, Long cid) {
		Optional<Distributor> od = distributors.findById(did);
		if (od.isPresent()) {
			Distributor d = od.get();

			if (d.containsContactPersonById(cid)) {
				d.removeContactPerson(cid);
				contactPersons.deleteById(cid); //oder behalten?
				distributors.save(d);
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @return all contact persons in the {@link ContactPersonRepository}
	 */
	public List<ContactPerson> getAllContactPersons() {
		return getListFromIterator(contactPersons.findAll().iterator());
	}

	/**
	 * searches in the {@link ContactPersonRepository} for the person
 	 * @param id id of person to be searched
	 * @return the {@link ContactPerson}
	 */
	public ContactPerson getContactPersonById(Long id) {
		if (contactPersons.existsById(id)) {
			return contactPersons.findById(id).get();
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param input String which should be searched for
	 * @return filtered List of Distributors, filtered by input
	 */
	public List<Distributor> filterDistributorsByAttribute(String input) {
		List<Distributor> returnList = new ArrayList<>();
		input = input.toLowerCase();
		for (Distributor d : distributors.findAll()) {
			if (d.getName() != null && d.getName().toLowerCase().contains(input)) {
				returnList.add(d);
			}
			if(d.getAddress()!=null && d.getAddress().toLowerCase().contains(input)){
				returnList.add(d);
			}
			if(d.getPhoneNumber() != null && d.getPhoneNumber().toLowerCase().contains(input)){
				returnList.add(d);
			}
		}

		return returnList;
	}
}
