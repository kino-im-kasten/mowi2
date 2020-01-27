/*
 * Copyright 2014-2019 the original author or authors.
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

package kik.booking.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A {@link BookingRepository} used to store Bookings.
 *
 * @author Erik Schönherr
 */
@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	@NonNull
	Streamable<Booking> findAll();

	/**
	 * Returns all bookings as a {@link Streamable} sorted.
	 *
	 * @param sort Sort
	 * @return sorted Streamable of all bookings
	 */
	Streamable<Booking> findAll(Sort sort);

	/**
	 * Returns all bookings that are not archived as a {@link Streamable}.
	 *
	 * @return Streamable of all bookings that are not archived
	 */
	Streamable<Booking> findBookingsByArchivedFalse();

	/**
	 * Returns all bookings that are not archived as a {@link Streamable}. sorted.
	 *
	 * @param sort Sort
	 * @return sorted Streamable of all bookings that are not archived
	 */
	Streamable<Booking> findBookingsByArchivedFalse(Sort sort);

	/**
	 * Returns the Booking with the given tb number in an {@link Optional}.
	 *
	 * @param tbNumber the tb number
	 * @return Optional containing a Booking if there's a Booking with the given tb number
	 */
	Optional<Booking> findByTbNumber(String tbNumber);

	/**
	 * Returns the Booking with the given id in an {@link Optional}.
	 *
	 * @param id the id
	 * @return Optional containing a Booking if there's a Booking with the given id
	 */
	Optional<Booking> findById(long id);
}