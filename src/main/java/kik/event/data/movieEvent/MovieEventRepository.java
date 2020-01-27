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
package kik.event.data.movieEvent;

import kik.booking.data.Booking;
import kik.event.data.EventPlanningStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;

/**
 * A repository interface to manage {@link MovieEvent} instances
 *
 * @author Georg Lauterbach
 * @version 0.1.0
 */
public interface MovieEventRepository extends CrudRepository<MovieEvent, Long> {
 
	/**
     * Re-declared {@link CrudRepository#findAll()}
	 * to return a {@link Streamable} instead of {@link Iterable}.
     */
    @Override
	@NonNull
    Streamable<MovieEvent> findAll();
	
	/**
	 * Find by additonal name and booking and date and start movie event.
	 *
	 * @param addtionalName the addtional name
	 * @param booking       the booking
	 * @param date          the date
	 * @param offsetTime    the offset time
	 * @return the movie event
	 */
	MovieEvent findByAdditonalNameAndBookingAndDateAndStart(
    		String addtionalName,
			Booking booking,
			LocalDate date,
			OffsetTime offsetTime);
	
	/**
	 * Find all streamable.
	 *
	 * @param sort the sort
	 * @return the streamable
	 */
	Streamable<MovieEvent> findAll(Sort sort);
	
	/**
	 * Find by event planning status in streamable.
	 *
	 * @param eventStatuses the event statuses
	 * @return the streamable
	 */
	Streamable<MovieEvent> findByEventPlanningStatusIn(List<EventPlanningStatus> eventStatuses);
	
	/**
	 * Find by event planning status as string streamable.
	 *
	 * @param planningStatusAsString the planning status as string
	 * @param sort                   the sort
	 * @return the streamable
	 */
	Streamable<MovieEvent> findByEventPlanningStatusAsString(String planningStatusAsString, Sort sort);
}
