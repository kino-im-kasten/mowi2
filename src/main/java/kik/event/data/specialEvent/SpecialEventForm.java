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
package kik.event.data.specialEvent;

import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.event.EventForm;
import kik.event.data.movieEvent.MovieEvent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * A {@link SpecialEventForm} to work with user input
 * when creating a new {@link SpecialEvent}
 * <p>
 * There is no business logic or validation to
 * be implemented here.
 *
 * @author Georg Lauterbach
 * @version 0.0.1
 */
public class SpecialEventForm extends EventForm {
	
	private String description;
	private String expectedEnd;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SuppressWarnings("unused")
	private LocalDate date;
	
	/**
	 * Default constructor of an {@link SpecialEventForm}
	 *
	 * @param additionalName Name of the {@link MovieEvent}
	 * @param date           Date as {@link LocalDate}
	 * @param start          Time of start
	 * @param description    A description of the {@link SpecialEvent}
	 * @param expectedEnd    Time of expected end
	 * @param type           Type of the {@link SpecialEvent}
	 * @param eventPublicity the event publicity
	 */
	public SpecialEventForm(String additionalName,
							LocalDate date,
							String start,
							String description,
							String expectedEnd,
							String type,
							String eventPublicity) {
		super(additionalName, date, start, EventType.toType(type));
		
		this.description = description;
		this.expectedEnd = expectedEnd;
		setEventPublicity(EventPublicity.toPublicity(eventPublicity));
	}
	
	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets expected end.
	 *
	 * @return the expected end
	 */
	public String getExpectedEnd() {
		return this.expectedEnd;
	}
	
	/**
	 * Sets expected end.
	 *
	 * @param expectedEnd the expected end
	 */
	public void setExpectedEnd(String expectedEnd) {
		this.expectedEnd = expectedEnd;
	}
}
