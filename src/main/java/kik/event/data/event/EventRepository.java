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
package kik.event.data.event;

import kik.event.data.EventPlanningStatus;
import kik.event.data.movieEvent.MovieEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * A repository interface to manage {@link Event} instances.
 *
 * @author Georg Lauterbach
 * @version 0.1.0
 */
public interface EventRepository extends CrudRepository<Event, Long> {
 
	/**
     * Re-declared {@link CrudRepository#findAll()}
	 * to return a {@link Streamable} instead of {@link Iterable}.
     */
    @Override
	@NonNull
    Streamable<Event> findAll();
	
	/**
	 * Find all streamable.
	 *
	 * @param sort the sort
	 * @return the streamable
	 */
	Streamable<Event> findAll(Sort sort);


	/**
	 * Find by event planning status in streamable.
	 *
	 * @param eventStatuses the event statuses
	 * @return the streamable
	 */
	Streamable<Event> findByEventPlanningStatusIn(List<EventPlanningStatus> eventStatuses);
}
