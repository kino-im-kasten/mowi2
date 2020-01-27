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
package kik.event.controller.movieEvent;

import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.data.movieEvent.tickets.Tickets;
import kik.event.data.movieEvent.tickets.TicketsForm;

import kik.event.management.movieEvent.MovieEventManagement;
import kik.overview.management.OverviewManagement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import com.mysema.commons.lang.Assert;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * {@link MovieEventPostController} handling HTTP::POST requests for {@link MovieEvent}s
 * <p>
 * This class provides all functionality to handle POST requests by proxying
 * these requests to the management layer. Therefore, there is no business
 * logic to be implemented here.
 *
 * @author Georg Lauterbach
 * @version 0.1.8
 */
@Controller
public class MovieEventPostController {
	
	private final MovieEventManagement movieEventManagement;
	private final OverviewManagement overviewManagement;
	
	/**
	 * Default Constructor of an {@link MovieEventPostController}
	 *
	 * @param movieEventManagement Business logic for {@link MovieEvent}s
	 */
	public MovieEventPostController(MovieEventManagement movieEventManagement, OverviewManagement overviewManagement) {
		Assert.notNull(movieEventManagement, "eventManagement must not be null!" +
				"~ in event.controller.movieEvent.MovieEventPostController::MovieEventPostController");
		this.movieEventManagement = movieEventManagement;
		this.overviewManagement = overviewManagement;
	}
	
	/**
	 * Handles the request for HTTP::POST on /movieEvents/
	 * Lists all {@link MovieEvent}s that match the filtering string
	 *
	 * @param input the {@link String} sorted after
	 * @param model data attribute model of HTML5 page
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@PostMapping(value = "/movieEvents/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String filterMovieEvents(@RequestParam(name = "input") String input, Model model) {
		model.addAttribute("movieEvents", this.movieEventManagement.getFilteredMovieEvents(input));
		
		return "event/movieEvents";
	}
	
	/**
	 * Handles the request for HTTP::POST on /createMovieEvent/
	 * Processes the page for creating a new {@link MovieEvent}
	 *
	 * @param movieEventForm     holds necessary parameters for the creation
	 *                           of an {@link MovieEvent} as a form
	 * @param errors             to propagate errors throughout the methods
	 * @param redirectAttributes for propagation of errors
	 * @return a redirect to the overview page
	 */
	@PostMapping(value = "/movieEvents/createMovieEvent/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String createNewMovieEvent(@Valid @ModelAttribute("movieEventForm") MovieEventForm movieEventForm,
							   Errors errors,
							   RedirectAttributes redirectAttributes) {
		this.movieEventManagement.createAndSaveMovieEvent(movieEventForm, errors);
		
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("movieEventForm", movieEventForm);
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:/movieEvents/createMovieEvent/";
		}
		
		this.overviewManagement.update();
		return "redirect:/movieEvents/";
	}
	
	/**
	 * Handles the request for HTTP::POST on /movieEvents/edit/{id}/
	 * Processes the page for editing the properties of an {@link MovieEvent}
	 *
	 * @param id                 id of the {@link MovieEvent} that is to be edited
	 * @param redirectTo		 where to redirect to
	 * @param movieEventId 		 id of the given {@link MovieEvent}
	 * @param movieEventForm     holds necessary parameters for the creation
	 *                           of an {@link MovieEvent} as a form
	 * @param errors             to propagate errors throughout the methods
	 * @param redirectAttributes for propagation of errors
	 * @return a redirect to the overview page
	 */
	@PostMapping(value = "/movieEvents/edit/{id}/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String editMovieEvent(@PathVariable long id,
						  @RequestParam(name = "redirectTo", required = false) Integer redirectTo,
						  @RequestParam(name = "movieEventId", required = false) Integer movieEventId,
						  MovieEventForm movieEventForm,
						  Errors errors,
						  RedirectAttributes redirectAttributes) {
		var tmp = this.movieEventManagement.findMovieEventById(id);
		if (tmp.isEmpty()) {
			return "error";
		}
		
		MovieEvent movieEvent = tmp.get();
		movieEventForm.setBookingID(movieEvent.getBooking().getId());
		this.movieEventManagement.editMovieEvent(movieEventForm, id, errors);
		
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("movieEvent", movieEvent);
			redirectAttributes.addFlashAttribute("movieEventForm", movieEventForm);
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:/movieEvents/edit/" + id + "/";
		}
		
		this.overviewManagement.update();
		return this.movieEventManagement.calculateRedirection(redirectTo, movieEventId, true);
	}
	
	/**
	 * Handles the request for HTTP::POST on /movieEvents/delete/{id}/
	 * Processes the request for deleting an {@link MovieEvent}
	 *
	 * @param id id of the {@link MovieEvent} that is to be deleted
	 * @return a redirect to the overview page
	 */
	@PostMapping(path = "/movieEvents/delete/{id}/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String deleteMovieEvent(@PathVariable long id) {
		this.movieEventManagement.deleteMovieEvent(id);
		
		this.overviewManagement.update();
		return "redirect:/movieEvents/";
	}
	
	
	/**
	 * Handles the request for HTTP::POST on /movieEvents/tickets/{id}/
	 *
	 * @param id          id of the {@link MovieEvent} that is to be edited
	 * @param redirectTo		 where to redirect to
	 * @param ticketsForm holds necessary parameters for the creation
	 *                    of {@link Tickets}
	 * @param movieEventId 		 id of the given {@link MovieEvent}
	 * @return a redirect to the overview page
	 */
	@PostMapping(value = "/movieEvents/tickets/{id}/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String editMovieEventTickets(@PathVariable long id,
								 @RequestParam(name = "redirectTo", required = false) Integer redirectTo,
								 @RequestParam(name = "movieEventId", required = false) Integer movieEventId,
								 TicketsForm ticketsForm,
								 Errors errors,
								 RedirectAttributes redirectAttributes) {
		var tmp = this.movieEventManagement.findMovieEventById(id);
		if (tmp.isEmpty()) {
			return "error";
		}
		
		MovieEvent movieEvent = tmp.get();
		this.movieEventManagement.editTicketNumbers(movieEvent, ticketsForm, errors);
		
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("movieEvent", movieEvent);
			redirectAttributes.addFlashAttribute("ticketsForm", ticketsForm);
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:/movieEvents/tickets/" + id + "/";
		}
		
		this.overviewManagement.update();
		return this.movieEventManagement.calculateRedirection(redirectTo, movieEventId, true);
	}
}
