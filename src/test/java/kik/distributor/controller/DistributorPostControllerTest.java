package kik.distributor.controller;

import kik.distributor.data.ContactPerson;
import kik.distributor.data.ContactPersonForm;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorForm;
import kik.distributor.management.DistributorManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class DistributorPostControllerTest {
	@Autowired
	MockMvc mvc;
	@Autowired
	DistributorManagement distributorManagement;

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iDistributorSearchTestP1() throws Exception {
		Long did = distributorManagement.createDistributor(new DistributorForm(
			"Test1", "est","etw","etew","setse"));


		MvcResult result = this.mvc.perform(post("/distributor/?input=test")).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		assertTrue(result.getModelAndView().getModelMap().getAttribute("distributors") instanceof List<?>);
		List<Distributor> list = (List<Distributor>) result.getModelAndView().getModelMap().getAttribute("distributors");
		assertTrue(list.contains(distributorManagement.getDistributorById(did).get()));
	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uRegisterDistributor() throws Exception {
		MvcResult result = this.mvc.perform(
			multipart("/registerDistributor/")
				.param("name", "Test5")
				.param("address", "Im Bau")
				.param("phoneNumber", "0315")
				.param("faxNumber", "0123123")
				.param("email","ich@iich"))
			.andReturn();
		assertEquals(302, result.getResponse().getStatus());

		for (Distributor d : distributorManagement.getAllDistributors()) {
			if (d.getName().equals("Test5")) {
				return; //success
			}
		}
		fail("Created Distributor could not be found!");
	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uEditDistributor() throws Exception {
		Long did = distributorManagement.createDistributor(new DistributorForm(
			"Test5", "est","etw","etew","setse"));


		MvcResult result = this.mvc.perform(multipart("/distributor/edit/" + did)
			.param("name", "Test51")
			.param("address", "Im Bau")
			.param("phoneNumber", "0315")
			.param("faxNumber", "0123123")
			.param("email","ich@iich"))
			.andReturn();
		assertEquals(302, result.getResponse().getStatus());

		Distributor d = distributorManagement.getDistributorById(did).get();
		assertEquals(d.getName(), "Test51");
		assertEquals(d.getAddress(), "Im Bau");
		assertEquals(d.getPhoneNumber(), "0315");
		assertEquals(d.getFaxNumber(), "0123123");
		assertEquals(d.getEmail(), "ich@iich");


	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uAddContactPerson() throws Exception {
		Long did = distributorManagement.createDistributor(new DistributorForm(
			"Test6", "est","etw","etew","setse"));
		MvcResult result = this.mvc.perform(multipart("/distributor/addContactPerson/" + did)
			.param("name", "CP1")
			.param("role", "Hausmeister")
			.param("phoneNumber", "0315")
			.param("emailAddress","ich@iich"))
			.andReturn();
		assertEquals(302, result.getResponse().getStatus());

		Distributor d = distributorManagement.getDistributorById(did).get();

		assertTrue(!d.getContactPersons().isEmpty());
		assertEquals(d.getContactPersons().get(0).getName(),"CP1");

	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uDeleteDistributor() throws Exception {
		Long did = distributorManagement.createDistributor(new DistributorForm(
			"Test7", "est","etw","etew","setse"));


		MvcResult result = this.mvc.perform(post("/distributor/delete/" + did)).andReturn();
		assertEquals(302, result.getResponse().getStatus());

		assertTrue(!distributorManagement.getDistributorById(did).isPresent());

	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uEditContactPerson() throws Exception {
		Long did = distributorManagement.createDistributor(new DistributorForm(
			"Test5", "est","etw","etew","setse"));
		Long cid = distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
			"name", "address", "email"
		), did);

		MvcResult result = this.mvc.perform(multipart("/distributor/details/" + did + "/editContactPerson/" + cid)
			.param("name", "CP1")
			.param("role", "Hausmeister")
			.param("phoneNumber", "0315")
			.param("emailAddress","ich@iich"))
			.andReturn();
		assertEquals(302, result.getResponse().getStatus());

		ContactPerson c = distributorManagement.getContactPersonById(cid);
		assertEquals(c.getName(), "CP1");
		assertEquals(c.getRole(), "Hausmeister");
		assertEquals(c.getPhoneNumber(), "0315");
		assertEquals(c.getEmailAddress(), "ich@iich");

	}
}