package kik.distributor.controller;

import kik.distributor.data.ContactPerson;
import kik.distributor.data.ContactPersonForm;
import kik.distributor.data.DistributorForm;
import kik.distributor.management.DistributorFilter;
import kik.distributor.management.DistributorManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * the controller class for GetMappings
 * @author Jonas Höpner
 * @version 0.2.0
 */
@Controller
public class DistributorGetController {

	private DistributorManagement distributorManagement;

	public DistributorGetController(DistributorManagement distributorManagement) {
		this.distributorManagement = distributorManagement;
	}

	/**
	 * Startpage for the distributor overview. Input is a string, which is used in an input field to filter data
	 * @param model the model of the webpage, filled by Spring
	 * @return the template distributor.html
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/distributor")
	public String distributor(Model model, @RequestParam(required = false) String deletionError,
							  @RequestParam(required = false) Integer currentDistCount,
							  @RequestParam(required = false) Boolean backwards,
							  @RequestParam(required = false) Boolean switched) {
		model.addAttribute("input","");
		DistributorFilter.switchCount(model,
			distributorManagement.getAllDistributors(),
			currentDistCount,
			backwards,
			switched);


		if (deletionError == null) {
			model.addAttribute("deletionError","");
		} else {
			model.addAttribute("deletionError",deletionError);
		}
		return "distributor/distributor";
	}

	/**
	 * Provides the form to add a new Distributor
	 * @param model the model of the webpage, filled by Spring
	 * @param form the {@link DistributorForm} to create a new Distributor
	 * @return the template registerDistributor.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping(value = "/registerDistributor")
	public String registerDistributor(Model model, DistributorForm form) {
		model.addAttribute("distForm",form);
		return "distributor/registerDistributor";
	}


	/**
	 * Provides the form to edit a distributor
	 * @param model the model of the webpage, filled by Spring
	 * @param form the {@link DistributorForm} containing updated data
	 * @param Id the ID of the distributor
	 * @return the template editDistributor.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping(value="/distributor/edit/{Id}")
	public String editDistributor(Model model, DistributorForm form, @PathVariable Long Id) {
		form.setAddress(distributorManagement.getDistributorById(Id).get().getAddress());
		form.setName(distributorManagement.getDistributorById(Id).get().getName());
		form.setPhoneNumber(distributorManagement.getDistributorById(Id).get().getPhoneNumber());
		form.setFaxNumber(distributorManagement.getDistributorById(Id).get().getFaxNumber());
		form.setEmail(distributorManagement.getDistributorById(Id).get().getEmail());

		model.addAttribute("Id",Id.toString());
		model.addAttribute("distForm",form);

		return "distributor/editDistributor";
	}


	/***
	 * Provides a page with details of a selected distributor
	 * @param model the model of the webpage, filled by Spring
	 * @param id the id of the shown distributor
	 * @return the template detailsDistributor.html
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/distributor/details/{id}")
	public String detailsDistributor(Model model, @PathVariable Long id) {
		model.addAttribute("distributor", distributorManagement.getDistributorById(id).get());

		return "distributor/detailsDistributor";
	}


	/**
	 * Provides a form to add a {@link ContactPerson} to a {@link kik.distributor.data.Distributor}
	 * @param model the model of the webpage, filled by Spring
	 * @param id the id of the distributor
	 * @param form the {@link ContactPersonForm} containing needed data
	 * @return the template contactPersonAdd
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping(value = "/distributor/addContactPerson/{id}")
	public String addContactPerson(Model model, @PathVariable Long id, ContactPersonForm form) {
		model.addAttribute("form", form);
		model.addAttribute("id",id);
		return "distributor/contactPersonAdd";
	}

	/**
	 * provides a form to edit a {@link ContactPerson} of a {@link kik.distributor.data.Distributor}
	 * @param model the model of the webpage, filled by Spring
	 * @param form the {@link ContactPersonForm}
	 * @param did the Id of the {@link kik.distributor.data.Distributor}
	 * @param cid the Id of the {@link ContactPerson}
	 * @return the template editContactPerson.html
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping("/distributor/details/{did}/editContactPerson/{cid}")
	public String editContactPerson(Model model,ContactPersonForm form, @PathVariable Long did, @PathVariable Long cid) {
		ContactPerson person = distributorManagement.getContactPersonById(cid);
		form.setName(person.getName());
		form.setEmailAddress(person.getEmailAddress());
		form.setPhoneNumber(person.getPhoneNumber());
		form.setRole(person.getRole());
		model.addAttribute("form",form);
		model.addAttribute("distributor", distributorManagement.getDistributorById(did).get());
		model.addAttribute("person",distributorManagement.getContactPersonById(cid));

		return "distributor/editContactPerson";
	}

	/**
	 * provides a page showing all {@link ContactPerson}s
	 * @param model the model of the webpage, filled by Spring
	 * @return the template contactPersons.html
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("contactPersons")
	public String getAllContactPersons(Model model) {
		model.addAttribute("persons",distributorManagement.getAllContactPersons());
		return "distributor/contactPersons";
	}


	/**
	 * provides the functionality to delete a contactperson from a distributor
	 * @param did the id of the {@link kik.distributor.data.Distributor}
	 * @param cid the id of the {@link ContactPerson}
	 * @return redirects back to the details of the chosen Distributor
	 */
	@PreAuthorize("hasRole('ORGA')")
	@GetMapping("/distributor/{did}/deleteContactPerson/{cid}")
	public String removeContactPerson(@PathVariable Long did, @PathVariable Long cid) {
		distributorManagement.removeContactPersonFromDistributor(did,cid);

		return "redirect:/distributor/details/"+did;
	}


}
