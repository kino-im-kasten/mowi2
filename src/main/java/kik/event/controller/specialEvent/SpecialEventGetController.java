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

import com.google.common.collect.Lists;
import com.mysema.commons.lang.Assert;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;
import kik.event.management.specialEvent.SpecialEventManagement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link SpecialEventGetController} handling HTTP::GET requests for {@link SpecialEvent}s
 * <p>
 * This class provides aöö functionality to handle GET requests by proxying these requests to
 * the management layer. Therefore, no business logic to be implemented here.
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
@Controller
public class SpecialEventGetController {
	
	private final SpecialEventManagement specialEventManagement;
	
	/**
	 * Default constructor of an {@link SpecialEventGetController}
	 *
	 * @param specialEventManagement management layer to distribute functionality
	 */
	public SpecialEventGetController(SpecialEventManagement specialEventManagement) {
		Assert.notNull(specialEventManagement, "specialEventManagement must not be null!" +
				" ~ in event.controller.specialEvent.SpecialEventGetController::SpecialEventGetController");
		
		this.specialEventManagement = specialEventManagement;
	}
	
	/**
	 * Handles the request for HTTP::GET on /specialEvents/
	 * Lists all {@link SpecialEvent}s serving an overview page
	 *
	 * @param model             data attribute model of HTML5 page
	 * @param sort              attribute by which is to sorted
	 * @param asc               ordering of the sort
	 * @param currentEventCount count of currently displayed {@link SpecialEvent}s
	 * @param backwards         indicates whether to move forwards or backwards
	 * @param switched          indicates whether the user switched direction
	 * @param showOld           indicates whether to show old events or not
	 * @return name of the HTML5-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/specialEvents/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String specialEventsOverview(Model model,
								 @RequestParam(required = false) String sort,
								 @RequestParam(required = false) Boolean asc,
								 @RequestParam(required = false) Integer currentEventCount,
								 @RequestParam(required = false) Boolean backwards,
								 @RequestParam(required = false) Boolean switched,
								 @RequestParam(required = false) Boolean showOld) {
		if (sort == null) {
			sort = "odt";
		}
		if (asc == null) {
			asc = true;
		}
		if (showOld == null) {
			showOld = false;
		}
		
		List<SpecialEvent> specialEvents;
		if (showOld) {
			specialEvents = this.specialEventManagement
					.getSpecialEventsSorted(sort, asc)
					.filter(SpecialEvent::isOld)
					.toList();
			
		} else {
			specialEvents = this.specialEventManagement
					.getSpecialEventsSorted(sort, asc)
					.filter(SpecialEvent::isNotOld)
					.toList();
			specialEvents = Lists.reverse(specialEvents);
		}
		
		kik.event.controller.event
				.IndexGetController
				.switchCount(model,
						specialEvents,
						currentEventCount,
						backwards,
						switched);
		
		model.addAttribute("asc", asc);
		model.addAttribute("sortedBy", sort);
		model.addAttribute("showOld", showOld);
		model.addAttribute("backwards", backwards);
		
		return "event/specialEvents";
	}
	
	/**
	 * Handles the request for HTTP::GET on /specialEvents/createSpecialEvent/
	 * Serves the page for creating a {@link SpecialEvent}
	 *
	 * @param model            data attribute model of HTML5 page
	 * @param specialEventForm holds necessary parameters for the creation
	 *                         of a {@link SpecialEvent} as a form
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/specialEvents/createSpecialEvent/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String createSpecialEvent(Model model, SpecialEventForm specialEventForm) {
		specialEventForm.setEventType(EventType.SPECIAL);
		specialEventForm.setEventPublicity(EventPublicity.PRIVATE);
		
		model.addAttribute("specialEventForm", specialEventForm);
		
		return "event/createSpecialEvent";
	}
	
	/**
	 * Handles the request for HTTP::GET on /specialEvents/details/{id}
	 * Serves the page for viewing details of an {@link SpecialEvent}
	 *
	 * @param id    id of the {@link SpecialEvent} that is to be displayed
	 * @param model data attribute model of the HTML-5 page
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "specialEvents/details/{id}")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String getSpecialEventDetails(@PathVariable long id, Model model) {
		Optional<SpecialEvent> optionalSpecialEvent = this.specialEventManagement
				.findSpecialEventById(id);
		
		if (optionalSpecialEvent.isEmpty()) {
			return "event/specialEvents";
		}
		
		model.addAttribute("specialEvent", optionalSpecialEvent.get());
		
		return "event/detailedSpecialEvent";
	}
	
	/**
	 * Handles the request for HTTP::GET on /specialEvents/edit/{id}
	 * Serves the page for editing a {@link SpecialEvent}
	 *
	 * @param id             the id of the {@link SpecialEvent} that is to be edited
	 * @param model          data attribute model of the HTML-5 page
	 * @param redirectTo     where to redirect
	 * @param specialEventId which {@link SpecialEvent} is to be handled
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/specialEvents/edit/{id}/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String editSpecialEvent(@PathVariable long id,
							@RequestParam(name = "redirectTo", required = false) Integer redirectTo,
							@RequestParam(name = "specialEventId", required = false) Integer specialEventId,
							Model model) {
		Optional<SpecialEvent> optionalSpecialEvent = this.specialEventManagement
				.findSpecialEventById(id);
		
		if (optionalSpecialEvent.isEmpty()) {
			return "event/specialEvents";
		}
		
		SpecialEvent specialEvent = optionalSpecialEvent.get();
		this.specialEventManagement.fillModel(model,
				redirectTo,
				specialEventId,
				specialEvent);
		model.addAttribute("specialEventForm", this.specialEventManagement
				.fillSpecialEventForm(specialEvent));
		
		return "event/editSpecialEvent";
	}
}
