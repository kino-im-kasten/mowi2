/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kik.event.controller.specialEvent;

import com.mysema.commons.lang.Assert;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;
import kik.event.management.specialEvent.SpecialEventManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * {@link SpecialEventPostController} handling HTTP::POST requests for {@link SpecialEvent}s
 * <p>
 * This class provides all functionality to handle POST requests by proxying
 * these requests to the management layer. Therefore, there is no business
 * logic to be implemented here.
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
@Controller
public class SpecialEventPostController {
	
	private final SpecialEventManagement specialEventManagement;
	
	/**
	 * Default constructor of an {@link SpecialEventPostController}
	 *
	 * @param specialEventManagement Business-logic for {@link SpecialEvent}s
	 */
	public SpecialEventPostController(SpecialEventManagement specialEventManagement) {
		Assert.notNull(specialEventManagement, "specialEventManagement must not be null!" +
				" ~ in event.controller.specialEvent.SpecialEventPostController::SpecialEventPostController");
		
		this.specialEventManagement = specialEventManagement;
	}
	
	/**
	 * Handles the request for HTTP::POST on /specialEvents/
	 * Lists all {@link SpecialEvent}s that match teh filtering string
	 *
	 * @param input the {@link String} sorted after
	 * @param model data attribute model of HTML5 page
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@PostMapping(value = "/specialEvents/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String filterMovieEvents(@RequestParam(name = "input") String input, Model model) {
		model.addAttribute("specialEvents", this.specialEventManagement.getFilteredSpecialEvents(input));
		
		return "event/specialEvents";
	}
	
	/**
	 * Handles the request for HTTP::POST on /specialEvents/createSpecialEvent/
	 * Processes the page for creating a new
	 *
	 * @param specialEventForm   holds necessary parameters for the creation
	 *                           of an {@link SpecialEvent} as a form
	 * @param errors             to propagate errors throughout the methods
	 * @param redirectAttributes for propagation of errors
	 * @return a redirect to the overview page
	 */
	@PostMapping(value = "/specialEvents/createSpecialEvent/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String createNewSpecialEvent(@Valid @ModelAttribute("specialEventForm") SpecialEventForm specialEventForm,
								 Errors errors,
								 RedirectAttributes redirectAttributes) {
		this.specialEventManagement.createAndSaveSpecialEvent(specialEventForm, errors);
		
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("specialEventForm", specialEventForm);
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:/specialEvents/createSpecialEvent/";
		}
		
		return "redirect:/specialEvents/";
	}
	
	/**
	 * Handles the request for HTTP::POST on /specialEvents/edit/{id}/
	 * Processes the page for editing the properties of an {@link SpecialEvent}
	 *
	 * @param id                 id of the {@link SpecialEvent} that is to be edited
	 * @param redirectTo         where to redirect
	 * @param specialEventId     which {@link SpecialEvent} is to be handled
	 * @param specialEventForm   holds the necessary parameters for the creation
	 *                           of an {@link SpecialEvent} as a form
	 * @param errors             to propagate errors throughout methods
	 * @param redirectAttributes for propagation of errors
	 * @return a redirect to the overview page
	 */
	@PostMapping(value = "/specialEvents/edit/{id}")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String editSpecialEvent(@PathVariable long id,
							@RequestParam(name = "redirectTo", required = false) Integer redirectTo,
							@RequestParam(name = "specialEventId", required = false) Integer specialEventId,
							SpecialEventForm specialEventForm,
							Errors errors,
							RedirectAttributes redirectAttributes) {
		var tmp = this.specialEventManagement.findSpecialEventById(id);
		if (tmp.isEmpty()) {
			return "/error";
		}
		
		SpecialEvent specialEvent = tmp.get();
		this.specialEventManagement.editSpecialEvent(specialEventForm, id, errors);
		
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("specialEvent", specialEvent);
			redirectAttributes.addFlashAttribute("specialEventForm", specialEventForm);
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:/specialEvents/edit/" + id + "/";
		}
		
		return this.specialEventManagement.calculateRedirection(redirectTo, specialEventId, false);
	}
	
	/**
	 * Handles the request for HTTP::POST on /specialEvents/delete/{id}
	 * Processes the request for deleting an {@link SpecialEvent}
	 *
	 * @param id id of the {@link SpecialEvent} that is to be deleted
	 * @return a redirect to the overview page
	 */
	@PostMapping(value = "/specialEvents/delete/{id}")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String deleteSpecialEvent(@PathVariable long id) {
		this.specialEventManagement.deleteSpecialEvent(id);
		
		return "redirect:/specialEvents/";
	}
}
