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

import kik.booking.data.Booking;
import kik.event.data.movieEvent.MovieEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.OffsetTime;

/**
 * A repository interface to manage {@link SpecialEvent} instances
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
public interface SpecialEventRepository extends CrudRepository<SpecialEvent, Long> {
	
	/**
	 * Re-declared {@link CrudRepository#findAll()}
	 * to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	@NonNull
	Streamable<SpecialEvent> findAll();
	
	/**
	 * Find all streamable.
	 *
	 * @param sort the sort
	 * @return the streamable
	 */
	Streamable<SpecialEvent> findAll(Sort sort);
	
	/**
	 * Find by event planning status as string streamable.
	 *
	 * @param planningStatusAsString the planning status as string
	 * @param sort                   the sort
	 * @return the streamable
	 */
	Streamable<SpecialEvent> findByEventPlanningStatusAsString(String planningStatusAsString, Sort sort);
	
	/**
	 * Find by description and expected end special event.
	 *
	 * @param description the description
	 * @param expectedEnd the expected end
	 * @return the special event
	 */
	SpecialEvent findByDescriptionAndExpectedEnd(
			String description,
			OffsetTime expectedEnd);
}
