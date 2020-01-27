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
package kik.event.controller.event;

import com.mysema.commons.lang.Assert;

import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.management.specialEvent.SpecialEventManagement;

import kik.user.data.forms.UnlockAccountForm;
import kik.user.data.user.User;
import kik.user.management.UserManagement;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.List;

/**
 * {@link IndexGetController} handling HTTP::GET requests for {@link Event}s
 * <p>
 * This controller shall only welcome users on a landing page.
 *
 * @author Georg Lauterbach
 * @version 0.2.0
 */
@Controller
public class IndexGetController {
	
	private final static int PADDING = 10;
	
	private final UserManagement userManagement;
	
	@Qualifier("movieEventManagement")
	private final MovieEventManagement movieEventManagement;
	
	@Qualifier("specialEventManagement")
	private final SpecialEventManagement specialEventManagement;
	
	/**
	 * Default constructor of an {@link IndexGetController}
	 *
	 * @param userManagement         Management layer to distribute functionality for{@link MovieEvent}s
	 * @param movieEventManagement   Management layer to distribute functionality for {@link Event}s
	 * @param specialEventManagement Management layer to distribute functionaly for {@link SpecialEvent}s
	 */
	public IndexGetController(UserManagement userManagement,
							  MovieEventManagement movieEventManagement,
							  SpecialEventManagement specialEventManagement) {
		Assert.notNull(userManagement, "userManagement must not be null!" +
				" ~ in event.controller.event.EventGetController::EventGetController");
		this.userManagement = userManagement;
		
		Assert.notNull(movieEventManagement, "eventManagement must not be null!" +
				" ~ in event.controller.event.EventGetController::EventGetController");
		this.movieEventManagement = movieEventManagement;
		
		Assert.notNull(specialEventManagement, "specialEventManagement must not be null!" +
				"~ in event.controller.event.indexPostController::indexPostController");
		this.specialEventManagement = specialEventManagement;
	}
	
	/**
	 * Handles the request for HTTP::GET on /
	 * Lists as {@link Event}s serving an overview page
	 *
	 * @param model   data attribute model of HTML5 page
	 * @param session to add the username (displayed in template.html)
	 * @param form    idk
	 * @param sort    attribute by which is to sorted
	 * @param asc     ordering of the sort
	 * @return name of the HTML5-file in the web-server's file-root folder
	 */
	@GetMapping("/")
	@PreAuthorize("isAuthenticated()")
	public String index(Model model,
						HttpSession session,
						UnlockAccountForm form,
						@RequestParam(required = false) String sort,
						@RequestParam(required = false) Boolean asc) {
		
		Optional<User> oUser = userManagement.getCurrentUser();
		if (oUser.isEmpty()) {
			return "redirect:/login";
		} else if (!oUser.get().isUnlocked()) {
			model.addAttribute("unlockAccountForm", form);
			return "user/unlockAccount";
		}
		session.setAttribute("username", oUser.get().getName());
		if (sort == null) {
			sort = "odt";
		}
		if (asc == null) {
			asc = true;
		}
		
		var movieEvents = this.movieEventManagement.getMovieEventsForIndex(sort, asc);
		var specialEvents = this.specialEventManagement.getSpecialEventsForIndex(sort, asc);
		
		model.addAttribute("asc", asc);
		model.addAttribute("sortedBy", sort);
		model.addAttribute("movieEvents", movieEvents);
		model.addAttribute("specialEvents", specialEvents);
		
		return "event/index";
	}
	
	/**
	 * Calculates the amount of {@link Event} and which {@link Event}s are to be
	 * shown on the index and then overview pages for{@link Event}s
	 *
	 * @param model             data attribute model of HTML5 page
	 * @param events            the list with currently available {@link Event}s which are not old
	 * @param currentEventCount current position in this list
	 * @param backwards         indicates the movement direction
	 * @param switched 			indicates whether the user switched direction
	 * @param <T>               generic parameter, so {@link MovieEvent}s and {@link SpecialEvent}s can use this method
	 */
	public static <T extends Event> void switchCount(Model model,
													 List<T> events,
													 Integer currentEventCount,
													 Boolean backwards,
													 Boolean switched) {
		if (events.isEmpty()) {
			model.addAttribute("currentEventCount", 0);
			return;
		}
		
		if (currentEventCount == null) {
			currentEventCount = 0;
		}
		if (backwards == null) {
			backwards = false;
		}
		if (switched == null) {
			switched = false;
		}
		
		int listSize = events.size();
		int newEventCount = backwards ? currentEventCount - PADDING : currentEventCount + PADDING;
		
		if (switched && backwards) {
//			currentEventCount -= PADDING;
//			newEventCount -= PADDING;
		}  else if (switched) {
			currentEventCount += PADDING;
			newEventCount += PADDING;
		}
		
		if (newEventCount >= listSize) {
			int from = Math.max(0, listSize - PADDING);
			model.addAttribute("movieEvents", events.subList(from, listSize));
			model.addAttribute("specialEvents", events.subList(from, listSize));
			newEventCount = from;
		} else if (newEventCount < 0) {
			model.addAttribute("movieEvents", events.subList(0, Math.min(PADDING, listSize)));
			model.addAttribute("specialEvents", events.subList(0, Math.min(PADDING, listSize)));
			newEventCount = 0;
		} else {
			if (backwards) {
				model.addAttribute("movieEvents", events.subList(newEventCount, currentEventCount));
				model.addAttribute("specialEvents", events.subList(newEventCount, currentEventCount));
			} else {
				model.addAttribute("movieEvents", events.subList(currentEventCount, newEventCount));
				model.addAttribute("specialEvents", events.subList(currentEventCount, newEventCount));
			}
		}
		
		model.addAttribute("currentEventCount", newEventCount);
	}
}
