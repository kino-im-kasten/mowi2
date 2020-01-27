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
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventData;
import kik.event.data.specialEvent.SpecialEventForm;
import kik.event.data.specialEvent.SpecialEventRepository;
import kik.event.management.event.EventInitializer;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A {@link SpecialEventInitializer} to create {@link SpecialEvent}s
 *
 * Called from / after {@link SpecialEventManagement} distributed
 * the creation of an {@link SpecialEvent}
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
@Service
public class SpecialEventInitializer extends EventInitializer {
	
	private SpecialEventRepository specialEventRepository;
	private DutyPlanManagement dutyPlanManagement;
	
	/**
	 * Default constructor of an {@link SpecialEventInitializer}
	 *
	 * @param specialEventRepository a repository containing all {@link SpecialEvent}s
	 */
	public SpecialEventInitializer(SpecialEventRepository specialEventRepository,
								   DutyPlanManagement dutyPlanManagement) {
		Assert.notNull(specialEventRepository, "specialEventRepository must not be null!" +
				" ~ in event.management.specialEvent.SpecialEventInitializer::SpecialEventInitializer");
		this.specialEventRepository = specialEventRepository;
		this.dutyPlanManagement = dutyPlanManagement;
	}
	
	/**
	 * Default factory method to create a new {@link SpecialEvent}
	 *
	 * @param specialEventForm Holds the neccessary parameters for the
	 *                         creation of an event, validated by {@link SpecialEventValidation}
	 * @return a new {@link SpecialEvent} instance
	 */
	public SpecialEvent initializeSpecialEvent(SpecialEventForm specialEventForm) {
		SpecialEventData specialEventData = super.initializeEventData(specialEventForm).toSED();
		
		specialEventData.setEventType(specialEventForm.getEventType());
		specialEventData.setEventPublicity(specialEventForm.getEventPublicity());
		specialEventData.setAdditionalName(specialEventForm.getAdditionalName());
		specialEventData.setFullyQualifiedName(specialEventForm.getAdditionalName());
		
		if (specialEventForm.getDescription() == null) {
			specialEventForm.setDescription("");
		}
		
		specialEventData.setDescription(specialEventForm.getDescription());
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime expectedEnd = LocalTime.parse(specialEventForm.getExpectedEnd(),
				DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		specialEventData.setExpectedEnd(expectedEnd);
		
		SpecialEvent specialEvent = new SpecialEvent(specialEventData);
		this.specialEventRepository.save(specialEvent);

		//handles and adds a dutyplan to the Event
		specialEvent = (SpecialEvent) super.handleDutyPlan(specialEvent,specialEventRepository, dutyPlanManagement);

		return specialEvent;
	}
	
	/**
	 * Handles the editing of an {@link SpecialEvent} und updates it
	 *
	 * @param specialEvent     the {@link SpecialEvent} that is to be edited
	 * @param specialEventForm the {@link SpecialEventForm} containing the changes
	 * @return the updates {@link SpecialEvent}
	 */
	public SpecialEvent updateSpecialEvent(SpecialEvent specialEvent, SpecialEventForm specialEventForm) {
		SpecialEventData sed = new SpecialEventData();
		
		specialEvent.setEventType(specialEventForm.getEventType());
		specialEvent.setEventPublicity(specialEventForm.getEventPublicity());
		
		specialEvent.setAdditonalName(specialEventForm.getAdditionalName());
		specialEvent.setFullyQualifiedName((specialEventForm.getAdditionalName()));
		
		specialEvent.setDescription(specialEventForm.getDescription());
		
		super.handleTime(specialEventForm, sed);
		specialEvent.setDate(sed.getDate());
		specialEvent.setStart(sed.getStart());
		specialEvent.setExpectedEnd(sed.getExpectedEnd());
		specialEvent.setOdt(sed.getOdt());
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		OffsetTime expectedEnd = LocalTime.parse(specialEventForm.getExpectedEnd(),
				DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		specialEvent.setExpectedEnd(expectedEnd);
		
		return specialEvent;
	}
}
