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

import com.google.common.collect.Lists;
import com.mysema.commons.lang.Assert;

import kik.event.data.EventPublicity;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.management.movieEvent.MovieEventManagement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link MovieEventGetController} handling HTTP::GET requests for {@link MovieEvent}s
 * <p>
 * This class provides all functionality to handle GET requests by proxying
 * these requests to the management layer. Therefore, there is no business
 * logic to be implemented here.
 *
 * @author Georg Lauterbach
 * @version 0.2.0
 */
@Controller
public class MovieEventGetController {
	
	private final MovieEventManagement movieEventManagement;
	
	/**
	 * Default constructor of an {@link MovieEventGetController}
	 *
	 * @param movieEventManagement Management layer to distribute functionality
	 */
	public MovieEventGetController(MovieEventManagement movieEventManagement) {
		Assert.notNull(movieEventManagement, "movieEventManagement must not be null!" +
				" ~ in event.controller.movieEvent.MovieEventGetController::MovieEventGetController");
		this.movieEventManagement = movieEventManagement;
	}
	
	/**
	 * Handles the request for HTTP::GET on /events/
	 * Lists all {@link MovieEvent}s serving an overview page
	 *
	 * @param model                  data attribute model of HTML5 page
	 * @param sort                   attribute by which is to sorted
	 * @param asc                    ordering of the sort
	 * @param currentEventCount 	 count of currently displayed {@link MovieEvent}s
	 * @param backwards 	   		 indicates whether to move forwards or backwards
	 * @param switched 				 indicates whether the user switched direction
	 * @param showOld 				 indicates whether to show old events or not
	 * @return name of the HTML5-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/movieEvents/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String movieEventsOverview(
			Model model,
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
		
		List<MovieEvent> movieEvents;
		if (showOld) {
			movieEvents = this.movieEventManagement
					.getMovieEventsSorted(sort, asc)
					.filter(MovieEvent::isOld)
					.toList();
			movieEvents = Lists.reverse(movieEvents);
		} else {
			movieEvents = this.movieEventManagement
					.getMovieEventsSorted(sort, asc)
					.filter(MovieEvent::isNotOld)
					.toList();
		}
		
		kik.event.controller.event
				.IndexGetController
				.switchCount(model,
						movieEvents,
						currentEventCount,
						backwards,
						switched);
		
		model.addAttribute("asc", asc);
		model.addAttribute("sortedBy", sort);
		model.addAttribute("showOld", showOld);
		model.addAttribute("backwards", backwards);
		
		return "event/movieEvents";
	}
	
	
	/**
	 * Handles the request for HTTP::GET on /movieEvents/createMovieEvent/
	 * Serves the page for creating a new {@link MovieEvent}
	 *
	 * @param model          data attribute model of HTML5 page
	 * @param movieEventForm holds necessary parameters for the creation
	 *                       of an {@link MovieEvent} as a form
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/movieEvents/createMovieEvent/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String createMovieEvent(Model model, MovieEventForm movieEventForm) {
		var tmp = this.movieEventManagement.getBookingManagement()
				.getAllNotArchivedBookings("movie.originalName", true);
		if (tmp.isEmpty()) {
			return "event/createMovieEvent";
		}
		movieEventForm.setEventPublicity(EventPublicity.PUBLIC);
		
		model.addAttribute("movieEventForm", movieEventForm);
		model.addAttribute("bookings", tmp);
		
		return "event/createMovieEvent";
	}
	
	/**
	 * Handles the request for HTTP::GET on /movieEvents/details/{id}/
	 * Serves the page for viewing details of a {@link MovieEvent}
	 *
	 * @param id    id of the {@link MovieEvent} that is to be displayed
	 * @param model data attribute model of HTML5 page
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/movieEvents/details/{id}/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String getMovieEventDetails(@PathVariable long id, Model model) {
		Optional<MovieEvent> optionalMovieEvent = this.movieEventManagement.findMovieEventById(id);
		
		if (optionalMovieEvent.isEmpty()) {
			return "event/movieEvents";
		}
		
		model.addAttribute("movieEvent", optionalMovieEvent.get());
		
		return "event/detailedMovieEvent";
	}
	
	/**
	 * Handles the request for HTTP::GET on /events/edit/{id}/
	 * Serves the page for editing the properties of an {@link MovieEvent}
	 *
	 * @param id           id of the {@link MovieEvent} that is to be edited
	 * @param model        data attribute model of HTML5 page
	 * @param redirectTo   where to redirect
	 * @param movieEventId which {@link MovieEvent} is to be handled
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/movieEvents/edit/{id}/")
	@PreAuthorize("hasRole('ORGA')")
	@SuppressWarnings("unused")
	String editMovieEvent(@PathVariable long id,
						  @RequestParam(name = "redirectTo", required = false) Integer redirectTo,
						  @RequestParam(name = "movieEventId", required = false) Integer movieEventId,
						  Model model) {
		Optional<MovieEvent> optionalMovieEvent = this.movieEventManagement.findMovieEventById(id);
		
		if (optionalMovieEvent.isEmpty()) {
			return "event/movieEvents";
		}
		var movieEvent = optionalMovieEvent.get();
		
		this.movieEventManagement.fillModel(model, redirectTo, movieEventId, movieEvent);
		model.addAttribute("movieEventForm", this.movieEventManagement
				.fillMovieEventForm(movieEvent));
		
		return "event/editMovieEvent";
	}
	
	/**
	 * Handles the request for HTTP::GET on /events/tickets/{id}/
	 *
	 * @param id           id of the {@link MovieEvent} that is to be edited
	 * @param model        data attribute model of HTML5 page
	 * @param redirectTo   where to redirect
	 * @param movieEventId which {@link MovieEvent} is to be handled
	 * @return name of the html-file in the web-server's file-root folder
	 */
	@GetMapping(value = "/movieEvents/tickets/{id}/")
	@PreAuthorize("hasRole('USER')")
	@SuppressWarnings("unused")
	String editMovieEventTickets(@PathVariable long id,
								 @RequestParam(name = "redirectTo", required = false) Integer redirectTo,
								 @RequestParam(name = "movieEventId", required = false) Integer movieEventId,
								 Model model) {
		Optional<MovieEvent> optionalMovieEvent = this.movieEventManagement.findMovieEventById(id);
		
		if (optionalMovieEvent.isEmpty()) {
			return "event/movieEvents";
		}
		var movieEvent = optionalMovieEvent.get();
		
		this.movieEventManagement.fillModel(model, redirectTo, movieEventId, movieEvent);
		model.addAttribute("ticketsForm", this.movieEventManagement.fillTicketsForm(movieEvent));
		
		return "event/editMovieEventTickets";
	}
}
