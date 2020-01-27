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
package kik.event.management;

import kik.event.data.event.Event;

import com.mysema.commons.lang.Assert;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.specialEvent.SpecialEvent;
import org.springframework.data.util.Streamable;

import java.time.format.DateTimeFormatter;

/**
 * Handles all filtering requests for {@link Event}s
 *
 * @author Georg Lauterbach
 * @version 0.0.3
 */
public class EventFilter {
	/**
	 * Filter {@link MovieEvent}s by a given {@link String}
	 *
	 * @param events the events
	 * @param input  the input
	 * @return the streamable
	 */
	public Streamable<MovieEvent> filterMovieEvents(Streamable<MovieEvent> events, String input) {
		Assert.notNull(input,"input must not be null!" +
				"~ in kik.event.management.EventFilter::filterMovieEvents");
		
		final String search = input.toLowerCase();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.HH:mm");
		
		return events.filter(m -> {
			String fqn = m.getFullyQualifiedName().toLowerCase();
			String odt = m.getOdt().format(formatter).toLowerCase();
			String booking = m.getBooking().getOverviewDisplayName();
			
			return (fqn.contains(search) || odt.contains(search) || booking.contains(search));
		});
	}
	
	/**
	 * Filter {@link SpecialEvent}s by a given {@link String}
	 *
	 * @param events the events
	 * @param input  the input
	 * @return the streamable
	 */
	public Streamable<SpecialEvent> filterSpecialEvents(Streamable<SpecialEvent> events, String input) {
		Assert.notNull(input, "input must not be null!" +
				"~ in kik.event.management.EventFilter::filterSpecialEvents");
		
		final String search = input.toLowerCase();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.HH:mm");
		
		return events.filter(m -> {
			String fqn = m.getFullyQualifiedName().toLowerCase();
			String odt = m.getOdt().format(formatter).toLowerCase();
			
			return (fqn.contains(search) || odt.contains(search));
		});
	}
}
