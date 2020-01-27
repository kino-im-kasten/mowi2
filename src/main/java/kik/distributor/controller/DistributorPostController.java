package kik.distributor.controller;

import kik.distributor.data.ContactPerson;
import kik.distributor.data.ContactPersonForm;
import kik.distributor.data.DistributorForm;
import kik.distributor.management.DistributorManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * a {@link DistributorPostController} for the frontend.
 * @author Jonas Höpner
 * @version 0.2.0
 */
@Controller
public class DistributorPostController {
	private DistributorManagement distributorManagement;

	public DistributorPostController(DistributorManagement distributorManagement) {
		this.distributorManagement = distributorManagement;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/distributor")
	public String distributor(Model model, @RequestParam(required = false) String input) {
		if (input == null || input == "") {
			model.addAttribute("distributors", distributorManagement.getAllDistributors());
		} else {
			//add only filtered distributors
			model.addAttribute("distributors", distributorManagement.filterDistributorsByAttribute(input));
		}
		model.addAttribute("input",input);
		model.addAttribute("deletionError","");
		return "distributor/distributor";

	}
	@PreAuthorize("hasRole('ORGA')")
	@PostMapping(value = "/registerDistributor")
	public String registerDistributor(Model model, @ModelAttribute("distForm") DistributorForm form, Errors errors){
		if(errors.hasErrors()) {
			return "distributor/registerDistributor";
		}

		try {
			distributorManagement.createDistributor(form);
		} catch (DistributorManagement.DuplicateException e) {
			return "alertDuplicate";
		}
		return "redirect:/distributor";
	}

	@PreAuthorize("hasRole('ORGA')")
	@PostMapping(value="/distributor/edit/{Id}")
	public String editDistributor(@ModelAttribute("distForm") DistributorForm form, @PathVariable Long Id, Errors result) {
		if(result.hasErrors()) {
			return "/distributor/edit/"+Id.toString();
		}

		distributorManagement.editDistributor(form, Id);
		return "redirect:/distributor";
	}

	@PreAuthorize("hasRole('ORGA')")
	@PostMapping("/distributor/addContactPerson/{id}")
	public String addContactPerson(@PathVariable Long id, @ModelAttribute ContactPersonForm form, Errors result) {
		if (result.hasErrors()) {
			return "/distributor/addContactPerson/" + id;
		}
		ContactPerson person = new ContactPerson(form);
		try {
			distributorManagement.addContactPersonToDistributor(form,distributorManagement.getDistributorById(id).get());
		} catch (DistributorManagement.DuplicateException e) {
			return "alertDuplicate";
		}
		return "redirect:/distributor/details/"+id.toString();
	}


	@PreAuthorize("hasRole('ORGA')")
	@PostMapping("/distributor/delete/{Id}")
	public RedirectView deleteDistributor(Model model, @PathVariable Long Id, RedirectAttributes attributes) {
		if (!distributorManagement.deleteDistributor(Id)) {
			attributes.addAttribute("deletionError", "error");
		}
		return new RedirectView("/distributor");

	}
	@PreAuthorize("hasRole('ORGA')")
	@PostMapping("/distributor/details/{did}/editContactPerson/{cid}")
	public String editContactPerson(@ModelAttribute ContactPersonForm form,
									@PathVariable Long did,
									@PathVariable Long cid,
									Errors result) {
		if (result.hasErrors()) {
			return "/distributor/details/" + did.toString() + "/editContactPerson/" + cid;
		}
		distributorManagement.editContactPerson(did,cid,form);

		return "redirect:/distributor/details/"+did.toString();
	}

}
