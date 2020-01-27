package kik.distributor.controller;

import kik.distributor.data.*;
import kik.distributor.management.DistributorManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class DistributorGetControllerTest {
	@Autowired
	MockMvc mvc;
	@Autowired
	DistributorRepository distributorRepository;
	@Autowired
	DistributorManagement distributorManagement;



	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iDistributorGetControllerTestGetOverview() throws Exception {
		MvcResult result = this.mvc.perform(get("/distributor/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		Object distributors = result.getModelAndView().getModelMap().getAttribute("distributors");
		assertTrue(distributors instanceof List<?>);

		for (Distributor d : distributorRepository.findAll()) {
			if (!(((List<Distributor>) distributors).contains(d))){
				fail("List of Distributors was not complete, " + d.getName() + " was not in List!");
			}
		}

	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iDistributorGetControllerTestCreateP1() throws Exception {
		MvcResult result = this.mvc.perform(get("/registerDistributor/")).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute("distForm")
			instanceof DistributorForm);
	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iDistributorGetControllerTestEditP1() throws Exception {
		Long id = distributorManagement.createDistributor(
			new DistributorForm("Test","Testaddres", "0351",
				"04124","ich@ich.de"));
		Distributor d = distributorManagement.getDistributorById(id).get();
		MvcResult result = this.mvc.perform(get("/distributor/edit/" + id.toString())).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute(
			"distForm") instanceof DistributorForm);
		DistributorForm form = (DistributorForm) result.getModelAndView().getModelMap().getAttribute(
			"distForm");
		assertEquals(form.getName(), d.getName());
		assertEquals(form.getAddress(), d.getAddress());
		assertEquals(form.getEmail(), d.getEmail());
		assertEquals(form.getFaxNumber(), d.getFaxNumber());
		assertEquals(form.getPhoneNumber(), d.getPhoneNumber());
	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void iDistributorGetControllerTestGetDetailsP1() throws Exception {
		Long id = distributorManagement.createDistributor(new DistributorForm("Test1","Testaddres",
			"0351", "04124","ich@ich.de"));
		Distributor d = distributorManagement.getDistributorById(id).get();
		MvcResult result = this.mvc.perform(get("/distributor/details/" + id.toString())).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute("distributor") instanceof Distributor);
		Distributor got = (Distributor) result.getModelAndView().getModelMap().getAttribute("distributor");
		assertEquals(got.getName(), d.getName());
		assertEquals(got.getAddress(), d.getAddress());
		assertEquals(got.getEmail(), d.getEmail());
		assertEquals(got.getFaxNumber(), d.getFaxNumber());
		assertEquals(got.getPhoneNumber(), d.getPhoneNumber());
	}



	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uDistributorGetControllerTestAddContactPerson() throws Exception {
		Long id = distributorManagement.createDistributor(new DistributorForm(
			"Test2","Testaddres", "0351", "04124","ich@ich.de"));
		Distributor d = distributorManagement.getDistributorById(id).get();
		MvcResult result = this.mvc.perform(get("/distributor/addContactPerson/" + id.toString())).andReturn();
		assertEquals(200, result.getResponse().getStatus());

		assertTrue(result.getModelAndView().getModelMap().getAttribute("form") instanceof ContactPersonForm);

	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uDistributorGetControllerTestEditContactPerson() throws Exception {
		Long id = distributorManagement.createDistributor(new DistributorForm(
			"Test3","Testaddres", "0351", "04124","ich@ich.de"));
		Distributor d = distributorManagement.getDistributorById(id).get();
		Long cpId = distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
			"Gustav","01230","ich@ich"), id);
		ContactPerson cp = distributorManagement.getContactPersonById(cpId);

		MvcResult result = this.mvc.perform(get(
			"/distributor/details/" + id.toString() + "/editContactPerson/" + cpId.toString())).andReturn();
		assertEquals(200, result.getResponse().getStatus());
		assertTrue(result.getModelAndView().getModelMap().getAttribute("form") instanceof ContactPersonForm);
		ContactPersonForm form = (ContactPersonForm) result.getModelAndView().getModelMap().getAttribute("form");

		assertEquals(form.getName(), cp.getName());
		assertEquals(form.getPhoneNumber(), cp.getPhoneNumber());
		assertEquals(form.getEmailAddress(), cp.getEmailAddress());
		assertEquals(form.getRole(), cp.getRole());

	}

	@Test
	@WithMockUser(roles = {"USER", "ORGA", "ADMIN"})
	void uDistributorGetControllerTestRemoveContactPerson() throws Exception {

		Long id = distributorManagement.createDistributor(new DistributorForm(
			"Test4","Testaddres", "0351", "04124","ich@ich.de"));
		Distributor d = distributorManagement.getDistributorById(id).get();
		Long cpId = distributorManagement.addContactPersonToDistributor(new ContactPersonForm(
			"Gustav","01230","ich@ich"), id);
		ContactPerson cp = distributorManagement.getContactPersonById(cpId);
		MvcResult result = this.mvc.perform(get(
			"/distributor/" + id.toString() + "/deleteContactPerson/" + cpId.toString())).andReturn();
		assertEquals(302, result.getResponse().getStatus()); //redirect back

		assertTrue(!distributorManagement.getAllContactPersons().contains(cp));


	}
}