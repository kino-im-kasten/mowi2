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
package kik.event.management.event;

import com.mysema.commons.lang.Assert;
import kik.event.data.EventPlanningStatus;
import kik.event.data.event.EventRepository;
import org.springframework.data.util.Streamable;
import org.springframework.ui.Model;

import kik.user.data.user.User;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;

import java.util.List;

/**
 * Management layer for all abstract logic involving {@link Event}s
 *
 * Business logic is located here. Validation and factories are called
 * from this location, if possible.
 *
 * @author Georg Lauterbach
 * @version 0.4.3
 */
public abstract class EventManagement {
	
	private final EventRepository eventRepository;
	
	/**
	 * Default constructor of an {@link EventManagement}
	 *
	 * @param eventRepository A repository containing all {@link Event}s
	 */
	public EventManagement(EventRepository eventRepository) {
		Assert.notNull(eventRepository, "eventRepository must not be null!" +
				"~ in event.standard.EventManagement::EventManagement");
		this.eventRepository = eventRepository;
	}
	
	/**
	 * Saves a {@link MovieEvent} into the eventRepository
	 *
	 * @param movieEvent a {@link MovieEvent} to be saved
	 */
	protected void saveMovieEvent(MovieEvent movieEvent) {
		this.eventRepository
				.save(movieEvent);
	}
	
	/**
	 * Saves a {@link SpecialEvent} into the {@link EventRepository}
	 *
	 * @param specialEvent a {@link SpecialEvent} to be saved
	 */
	protected void saveSpecialEvent(SpecialEvent specialEvent) {
		this.eventRepository
				.save(specialEvent);
	}
	
	/**
	 * If possible and present, delete the {@link MovieEvent}
	 * which is associated with the given id
	 *
	 * @param id id associated with the event that is to be deleted
	 * @return true when no errors occured, false otherwise
	 */
	protected boolean deleteMovieEvent(long id) {
		if (this.eventRepository.existsById(id)) {
			this.eventRepository.deleteById(id);
			return true;
		}
		
		return false;
	}
	
	/**
	 * If possible and present, delete the {@link SpecialEvent}
	 * which is associated with the given id
	 *
	 * @param id id associated with the {@link SpecialEvent} that is to be deleted
	 * @return true when no errors occured, false otherwise
	 */
	protected boolean deleteSpecialEvent(long id) {
		if (this.eventRepository.existsById(id)) {
			this.eventRepository.deleteById(id);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets event repository.
	 *
	 * @return the event repository
	 */
	public EventRepository getEventRepository() {
		return this.eventRepository;
	}
	
	/**
	 * Fill model.
	 *
	 * @param model      the model
	 * @param redirectTo the redirect to
	 * @param eventId    the event id
	 * @param event      the event
	 */
	public void fillModel(Model model, Integer redirectTo,
						  Integer eventId, Event event) {
		if (redirectTo == null) {
			redirectTo = 0;
		}
		if (eventId == null) {
			eventId = -1;
		}
		
		model.addAttribute("redirectTo", redirectTo);
		model.addAttribute("movieEventId", eventId);
		
		if (event instanceof MovieEvent) {
			model.addAttribute("movieEvent", event);
		} else {
			model.addAttribute("specialEvent", event);
		}
	}
	
	/**
	 * Calculate redirection string.
	 *
	 * @param redirectTo   the redirect to
	 * @param eventId      the event id
	 * @param isMovieEvent the is movie event
	 * @return the string
	 */
	public String calculateRedirection(Integer redirectTo, Integer eventId, boolean isMovieEvent) {
		String redirection;
		
		if (redirectTo == null) {
			return "redirect:/";
		}
		
		switch (redirectTo) {
			case 0:
				redirection = "redirect:/";
				break;
			case 1: {
				redirection = isMovieEvent ? "redirect:/movieEvents/" : "redirect:/specialEvents/";
			}
			break;
			case 2: {
				redirection = isMovieEvent ? "redirect:/movieEvents/details/" : "redirect:/specialEvents/details/";
				redirection += eventId + "/";
			}
			break;
			default:
				redirection = "/";
		}
		
		return redirection;
	}

	/**
	 * filters all {@link MovieEvent}s that are currently booked or in planning,
	 * that have at least one {@link kik.dutyplan.data.job.Job} which contains the specified {@link User}
	 *
	 * @param user {@link User} to filter the events by
	 * @return Stream of Events that fit the filter
	 */
	public Streamable<Event> findUserRelatedEvents(User user) {
		return eventRepository.findAll()
			.filter(movieEvent -> movieEvent
				.getDutyPlan()
				.getAssignedJobs()
				.stream()
				.anyMatch(job -> job.getWorker() == user));
	}
}
