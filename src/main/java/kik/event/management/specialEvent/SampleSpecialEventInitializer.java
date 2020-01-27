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
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEventForm;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Creates sample {@link MovieEvent}s
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
@Component
public class SampleSpecialEventInitializer implements DataInitializer {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpecialEventInitializer.class);
	private SpecialEventManagement specialEventManagement;
	
	/**
	 * Default constructor of an {@link SpecialEventInitializer}
	 *
	 * @param specialEventManagement Management layer to access the process of
	 *                               creating an event and "intercepting" it
	 */
	public SampleSpecialEventInitializer(SpecialEventManagement specialEventManagement) {
		Assert.notNull(specialEventManagement, "eventRepository must not be null!" +
				"~ in event.standard.SampleEventInitializer::SampleEventInitializer");
		this.specialEventManagement = specialEventManagement;
	}
	
	/**
	 * Creates sample data to get an overview of all {@link MovieEvent}s
	 *
	 * @see DataInitializer#initialize()
	 */
	@Override
	public void initialize() {
		if (specialEventManagement.getSpecialEventRepository().findAll().iterator().hasNext()) {
			return;
		}
		
		Errors movieEventFormErrors = new BeanPropertyBindingResult(LOG, "LOG");
		LocalDate nowPlusOneDay = LocalDate.now().plusDays(4);
		
		LOG.info("Creating default special-events for showcasing purposes.");
		
		this.specialEventManagement
				.createAndSaveSpecialEvent(
						new SpecialEventForm(
								"First Special Event - a.k.a. special",
								nowPlusOneDay,
								"20:30",
								"I'm so special, even my desc is special.",
								"22:30",
								"SPECIAL",
								"PUBLIC"),
						movieEventFormErrors);
	}
}
