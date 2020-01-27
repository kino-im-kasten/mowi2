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
package kik.event.management.specialEvent;

import com.mysema.commons.lang.Assert;
import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.event.Event;
import kik.event.data.event.EventRepository;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;
import kik.event.data.specialEvent.SpecialEventRepository;

import kik.event.management.EventFilter;
import kik.event.management.event.EventManagement;

import kik.event.controller.specialEvent.SpecialEventGetController;

import kik.event.management.movieEvent.MovieEventManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Management layer for all logic involving {@link SpecialEvent}s
 * 
 * Manages requests from {@link SpecialEventGetController}
 */
@Service
@Qualifier("specialEventManagement")
public class SpecialEventManagement extends EventManagement {
	
	private static final Logger LOG = LoggerFactory.getLogger(MovieEventManagement.class);
	
	private final SpecialEventRepository specialEventRepository;
	private final SpecialEventInitializer specialEventInitializer;
	private final SpecialEventValidation specialEventValidation;
	private final DutyPlanManagement dutyPlanManagement;
	private EventFilter eventFilter = new EventFilter();
	
	/**
	 * The default constructor of an {@link SpecialEventManagement}
	 *
	 * @param eventRepository         A repository containing all {@link Event}s
	 * @param specialEventRepository  A repository containing all {@link SpecialEvent}s
	 * @param specialEventInitializer Factory class of a {@link SpecialEvent}
	 * @param specialEventValidation  A {@link Validator} to validate {@link SpecialEvent}s
	 * @param dutyPlanManagement	  Management layer for all {@link DutyPlan}s
	 */
	public SpecialEventManagement(EventRepository eventRepository,
								  SpecialEventRepository specialEventRepository,
								  SpecialEventInitializer specialEventInitializer,
								  SpecialEventValidation specialEventValidation,
								  DutyPlanManagement dutyPlanManagement) {
		super(eventRepository);
		
		Assert.notNull(specialEventRepository, "specialEventRepository must not be null!" +
				"~ in kik.event.management.specialEvent.SpecialEventManagement::SpecialEventManagement");
		this.specialEventRepository = specialEventRepository;
		
		Assert.notNull(specialEventInitializer, "specialEventInitializer must not be null!" +
				"~ in kik.event.management.specialEvent.SpecialEventManagement::SpecialEventManagement");
		this.specialEventInitializer = specialEventInitializer;
		
		Assert.notNull(specialEventValidation, "specialEventValidation must not be null!" +
				"~ in kik.event.management.specialEvent.SpecialEventManagement::SpecialEventManagement");
		this.specialEventValidation = specialEventValidation;
		
		Assert.notNull(dutyPlanManagement, "dutyPlanManagement must not be null!" +
				"~ in kik.event.management.specialEvent.SpecialEventManagement::SpecialEventManagement");
		this.dutyPlanManagement = dutyPlanManagement;
	}
	
	/**
	 * Creates a new {@link SpecialEvent} using the information given
	 * in the {@link SpecialEventForm} and saves it
	 *
	 * @param specialEventForm Holds the neccessary parameters for the creation of an {@link SpecialEvent}
	 * @param errors           to propagate errors throughout the methods
	 * @return the newly created {@link SpecialEvent}
	 */
	public SpecialEvent createAndSaveSpecialEvent(SpecialEventForm specialEventForm,
												  Errors errors) {
		Assert.notNull(specialEventForm, "specialEventForm must not be null!" +
				"~ in kik.event.management.specialEvent.SpecialEventManagement::createAndSaveSpecialEvent");
		
		this.specialEventValidation.validate(specialEventForm, errors);
		if (errors.hasErrors()) {
			return null;
		}
		
		SpecialEvent specialEvent = this.specialEventInitializer.initializeSpecialEvent(specialEventForm);
		this.specialEventRepository.save(specialEvent);
		super.saveSpecialEvent(specialEvent);
		
		return specialEvent;
	}
	
	/**
	 * Calls filter logic to filter all {@link SpecialEvent}s by a
	 * given {@link String} in "asc" order
	 *
	 * @param input serach {@link String}
	 * @return all {@link SpecialEvent}s that match the filter {@link String}
	 */
	public Streamable<SpecialEvent> getFilteredSpecialEvents(String input) {
		return this.eventFilter
				.filterSpecialEvents(this.specialEventRepository.findAll(), input);
	}
	
	/**
	 * Evaluates changes to an {@link SpecialEvent}, (validates) and saves them
	 *
	 * @param specialEventForm the input from the user-iput, validated
	 * @param id               the id of the event that belongs to the form
	 * @param errors           to propagate errors throughout the methods
	 */
	public void editSpecialEvent(SpecialEventForm specialEventForm, long id, Errors errors) {
		Optional<SpecialEvent> tmp = findSpecialEventById(id);
		if (tmp.isEmpty()) {
			return;
		}
		var specialEvent = tmp.get();
		
		this.specialEventValidation.validate(specialEventForm, errors);
		if(errors.hasErrors()) {
			return;
		}
		
		SpecialEvent updatedSpecialEvent = this.specialEventInitializer
				.updateSpecialEvent(specialEvent, specialEventForm);
		
		this.specialEventRepository.save(updatedSpecialEvent);
	}
	
	/**
	 * Find all streamable.
	 *
	 * @return the streamable
	 */
	public Streamable<SpecialEvent> findAll() {
		return this.specialEventRepository.findAll();
	}
	
	/**
	 * Fill special event form special event form.
	 *
	 * @param specialEvent the special event
	 * @return the special event form
	 */
	public SpecialEventForm fillSpecialEventForm(SpecialEvent specialEvent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		
		return new SpecialEventForm(
				specialEvent.getAdditonalName(),
				specialEvent.getDate(),
				specialEvent.getStart().format(formatter),
				specialEvent.getDescription(),
				specialEvent.getExpectedEnd().format(formatter),
				specialEvent.getEventType().name(),
				specialEvent.getEventPublicity().name()
		);
	}
	
	public boolean deleteSpecialEvent(long id) {
		Optional<SpecialEvent> tmp = findSpecialEventById(id);
		if (tmp.isEmpty()) {
			LOG.warn("The specialEvent with id : " + id + " that was supposed to be" +
					" deleted was not found.");
			return false;
		}
		SpecialEvent specialEvent = tmp.get();
		
		// ? referential integrity violations need to be resolved
		Long dpId = specialEvent.getDutyPlan().getId();
		dutyPlanManagement.clearDutyPlan(specialEvent.getDutyPlan());
		specialEvent.setDutyPlan(null);
		this.dutyPlanManagement.deleteDutyPlan(dpId);
		this.specialEventRepository.deleteById(id);
		super.deleteSpecialEvent(id);
		
		return true;
	}
	
	/**
	 * Find special event by id optional.
	 *
	 * @param id the id
	 * @return the optional
	 */
	public Optional<SpecialEvent> findSpecialEventById(long id) {
		return this.specialEventRepository
				.findById(id);
	}
	
	/**
	 *
	 *
	 * @param sort attribute to be sorted by
	 * @param asc  ordering
	 * @return {@link SpecialEvent}s, but limited for teh index /
	 */
	public List<SpecialEvent> getSpecialEventsForIndex(String sort, Boolean asc) {
		int listLen = 5;
		List<SpecialEvent> suitedEvents = new ArrayList<>(listLen);
		int i = 0;
		
		for (SpecialEvent specialEvent : this.specialEventRepository
				.findAll(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort))) {
			if (specialEvent.isNotOld()) {
				suitedEvents.add(specialEvent);
				++i;
			
				if (i == listLen) {
					break;
				}
			}
		}
		return suitedEvents;
	}
	
	/**
	 * Sorts ass {@link SpecialEvent}s according the the given
	 * {@link String} "sort"
	 *
	 * @param sort attribute to be sorted by
	 * @param asc  ordering
	 * @return all {@link SpecialEvent}s, but sorted
	 */
	public Streamable<SpecialEvent> getSpecialEventsSorted(String sort, Boolean asc) {
		return this.specialEventRepository
				.findAll(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort))
				.filter(SpecialEvent::isNotOld);
	}
	
	/**
	 * Gets special event repository.
	 *
	 * @return the special event repository
	 */
	public SpecialEventRepository getSpecialEventRepository() {
		return this.specialEventRepository;
	}
	
	/**
	 * Shall update all {@link SpecialEvent}s, check for status-updates, etc.
	 */
	@Scheduled(cron = "0 30 1 * * ?")
	public void updateSpecialEvents() {
		checkForDueDate();
	}
	
	/**
	 * A scheduled method to check whether a {@link MovieEvent}'s
	 * date has already gone by ( + 1 day).
	 */
	private void checkForDueDate() {
		LocalDate localDatePlusOffset = LocalDate.now().minusDays(1);
		
		for (SpecialEvent specialEvent: findAll()) {
			if (specialEvent.getDate().isBefore(localDatePlusOffset)) {
				specialEvent.setIsOld(true);
			}
		}
	}
}
