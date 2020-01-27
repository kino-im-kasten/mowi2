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

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.event.Event;
import kik.event.data.event.EventData;
import kik.event.data.event.EventForm;
import kik.event.data.event.EventRepository;
import kik.event.data.movieEvent.MovieEvent;

import kik.event.data.movieEvent.MovieEventRepository;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventRepository;
import kik.event.management.movieEvent.MovieEventManagement;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A {@link EventInitializer} to formalize the creation of
 * an abstract {@link Event}
 *
 * Called from / after {@link MovieEventManagement} distributed
 * the creation of an {@link MovieEvent}
 *
 * @author Georg Lauterbach
 * @version 0.0.4
 */
@Service
public abstract class EventInitializer {
	
	/**
	 * Default constructor of an {@link EventInitializer}
	 */
	protected EventInitializer() {}
	
	/**
	 * Default factory method to create abstract data
	 * in the form of {@link EventData}
	 *
	 * @param eventForm Holds parameters for the creation                  of an abstract event
	 * @return A new meta-object {@link EventData}, which contains information all {@link Event}s have in common
	 */
	protected EventData initializeEventData(EventForm eventForm) {
		EventData eventData = new EventData();
		
		eventData.setAdditionalName(eventForm.getAdditionalName());
		handleTime(eventForm, eventData);
		
		return eventData;
	}
	
	/**
	 * Creates all neccessary time-stamps for {@link EventData}
	 *
	 * @param eventForm olds parameters for the creation                  of an abstract event
	 * @param eventData A meta-object containing information about                  the to be created event
	 */
	protected void handleTime(EventForm eventForm, EventData eventData) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		
		LocalDate date = LocalDate.parse(eventForm.getDate().format(formatter), formatter);
		OffsetTime start = LocalTime.parse(eventForm.getStart(), DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime odt = start.atDate(date);
		
		eventData.setDate(date);
		eventData.setStart(start);
		eventData.setOdt(odt);
	}


	protected Event handleDutyPlan(Event event, CrudRepository repository, DutyPlanManagement dutyPlanManagement) {
		// * handling the assigned duty-plan
		repository.save(event); //has to be saved so that it can be saved in DutyPlan.
		// otherwise it is "transient" and breaks it all

		DutyPlan dutyPlan = dutyPlanManagement.createDutyPlanObject(event.toString(),event);

		dutyPlan.setEvent(event);
		dutyPlanManagement.update(dutyPlan);
		event.setDutyPlan(dutyPlan);

		repository.save(event);
		return event;
	}


}
