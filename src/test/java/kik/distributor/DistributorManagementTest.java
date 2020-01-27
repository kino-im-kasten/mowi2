package kik.distributor;

import kik.distributor.data.*;
import kik.distributor.management.DistributorManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class DistributorManagementTest {

	private DistributorManagement distributorManagement;
	private DistributorRepository distributorRepository;


	@Autowired
	DistributorManagementTest(DistributorManagement distributorManagement, DistributorRepository distributorRepository) {
		this.distributorManagement = distributorManagement;
		this.distributorRepository = distributorRepository;
	}


	@Test
	void deleteDistributor() throws DistributorManagement.DuplicateException {
		Distributor d = distributorManagement.filterDistributorsByAttribute("Gustav Krebs").get(0);
		distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
			"Name", "Adresse", "email"
		),d.getId());

		int size = distributorManagement.getAllDistributors().size();
		distributorManagement.deleteDistributor(d.getId());
		assertTrue(!distributorManagement.getDistributorById(d.getId()).isPresent());
		assertTrue(distributorManagement.getAllDistributors().size() == size - 1);
	}

	@Test
	void getAllDistributors() {
		assertTrue(distributorManagement.getAllDistributors() instanceof ArrayList);
		assertTrue(!distributorManagement.getAllDistributors().isEmpty());
	}

	@Test
	void getDistributorById() throws DistributorManagement.DuplicateException {
		Long did = distributorManagement.createDistributor(new DistributorForm("Tester","Am Graben","03213123","",""));
		Distributor check = distributorManagement.getDistributorById(did).get();
		assertEquals("Tester",check.getName(), "The returned distributor has the wrong name!");
	}

	@Test
	void addContactPersonToDistributor() throws DistributorManagement.DuplicateException {
		Long did = distributorManagement.createDistributor(new DistributorForm("Gustav", "asa", "adasdasd", "sadasd",""));

		distributorManagement.addContactPersonToDistributor(new ContactPersonForm("TestPEron2","sadasdasd","asdasdasd"),did);

		assertTrue(distributorManagement.getDistributorById(did).get().containsContactPersonById(distributorManagement.contactPersonNameToId("TestPEron2")));
	}

	@Test
	void contactPersonNameToId() {
		Long id = distributorManagement.addContactPerson(new ContactPersonForm("TestPerson","Am Graben 24","test@test.com"));
		assertEquals(id,distributorManagement.contactPersonNameToId("TestPerson"));
	}

	@Test
	void editContactPerson() throws DistributorManagement.DuplicateException {
		if (!distributorManagement.getAllDistributors().isEmpty()) {

			Distributor d = distributorManagement.getAllDistributors().get(0);

			System.out.println("Distriutor: " + d.getName());

			Long cid = distributorManagement.addContactPersonToDistributor(new ContactPersonForm("Hans", "sfpojpoj2sffpdoj", "22"), d.getId());

			//test
			distributorManagement.editContactPerson(d.getId(), cid, new ContactPersonForm("Hans", "0351123", "hans@web.de"));

			assertEquals("0351123", distributorManagement.getContactPersonById(cid).getPhoneNumber());
			assertEquals("Hans", distributorManagement.getContactPersonById(cid).getName());
			assertEquals("hans@web.de", distributorManagement.getContactPersonById(cid).getEmailAddress());

			d = distributorManagement.getAllDistributors().get(0);


			for (ContactPerson p : d.getContactPersons()) {
				if (p.getName().equals("Hans")) return; // test success, last statement
			}
			fail("Contact Person not in Distributor");
		}
	}
/*
	@Test
	void removeContactPersonFromDistributor() {
	}

	@Test
	void getContactPersonById() {
	}
*/


	@Test
	public void createDistributor() throws DistributorManagement.DuplicateException { //AT0701

		DistributorForm distForm = new DistributorForm("Name1", "Adresse", "031321124", "","");
		System.out.println("distFrom: " + distForm);
		Long id = 0l;
		System.out.println("manager: " + distributorManagement.toString());
		try {
			id = this.distributorManagement.createDistributor(distForm);
		} catch (Exception e) {
			fail(e.getMessage() + "\nID = " + id);
		}
		assertTrue(distributorRepository.existsById(id) && id != null);
	}

	@Test
	void duplicateDistributor() { //AT0702
		DistributorForm distForm = new DistributorForm("Name", "Adresse", "031321124", "03512312124","");
		DistributorForm distForm2 = new DistributorForm("Name", "Adresse", "031321124", "03512312124","");
		try {
			this.distributorManagement.createDistributor(distForm2); //shouldn't fail
			distributorManagement.createDistributor(distForm); //fail
		} catch (DistributorManagement.DuplicateException e) {
			//success
		} catch (Exception e) {
			fail("Exception has been thrown! " + e.getMessage());
		}

	}

	@Test
	void editDistributorAddress() { //AT0703
		DistributorForm distForm = new DistributorForm("Name", "Adresse_CHANGED", "031321124", "03512312124","");
		Long id = distributorManagement.getAllDistributors().get(0).getId(); //should exist after initialization
		if (id == null) {
			//things are really broken!
			id = distributorRepository.findAll().iterator().next().getId(); //should do the same job
		}
		distributorManagement.editDistributor(distForm, id);
		assertTrue(distributorRepository.findById(id).get().getAddress().equals(distForm.getAddress()));
	}

	@Test
	void editContactPersonPhoneNumber() { //AT0704
		ContactPersonForm conForm = new ContactPersonForm("Name", "Adresse", "031321124");
		Long id = distributorManagement.getAllDistributors().get(0).getId(); //should exist after initialization
		if (id == null) {
			id = distributorRepository.findAll().iterator().next().getId(); //should do the job
		}
		distributorManagement.editContactPerson(id, distributorManagement.getDistributorById(id).get().getContactPersons().get(0).getId(), conForm);
		assertTrue(distributorRepository.findById(id).get().getContactPersons().get(0).getPhoneNumber().equals(conForm.getPhoneNumber()));
	}

	@Test
	void filterDistributors() { //AT0705

		DistributorForm form = new DistributorForm("Test123456789", "Uni", "013231", "03123123",""); //should be the only one named "test"

		try {
			this.distributorManagement.createDistributor(form);
		} catch (DistributorManagement.DuplicateException e) {
			System.out.println(e.getMessage());
			//not a problem for this test, but shouldn't occur
		}

		assertTrue(!distributorManagement.filterDistributorsByAttribute("Test123456789").isEmpty());
		assertTrue(distributorManagement.filterDistributorsByAttribute("Test123456789").get(0).getName().equals("Test123456789"));
	}


	@Test
	void ContactPersonFormConstructorTest() {
		ContactPerson c = new ContactPerson(new ContactPersonForm("name","address","email","role"));
		assertEquals(c.getName(),"name");
		assertEquals(c.getPhoneNumber(),"address");
		assertEquals(c.getEmailAddress(),"email");
		assertEquals(c.getRole(),"role");


	}

	@Test
	void ContactPersonFormSetterTest() {
		ContactPerson c = new ContactPerson();
		c.setPhoneNumber("address");
		c.setName("name");
		c.setEmailAddress("email");
		c.setRole("role");

		assertEquals(c.getName(),"name");
		assertEquals(c.getPhoneNumber(),"address");
		assertEquals(c.getEmailAddress(),"email");
		assertEquals(c.getRole(),"role");
	}

	@Test
	void DistributorConstructorTests() {
		Distributor d1 = new Distributor("name", "address", "phone");
		Distributor d2 = new Distributor("name", "address", "phone", "mail", "fax");

		assertEquals(d1.getName(), "name");
		assertEquals(d1.getAddress(), "address");
		assertEquals(d1.getPhoneNumber(), "phone");
		assertEquals(d1.getFaxNumber(),"");
		assertEquals(d1.getEmail(),"");

		assertEquals(d2.getName(), "name");
		assertEquals(d2.getAddress(), "address");
		assertEquals(d2.getPhoneNumber(), "phone");
		assertEquals(d2.getFaxNumber(), "fax");
		assertEquals(d2.getEmail(), "mail");

	}
}