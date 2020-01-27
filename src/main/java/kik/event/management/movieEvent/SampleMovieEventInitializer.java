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

package kik.event.management.movieEvent;

import kik.booking.data.Booking;
import kik.user.data.user.User;

import kik.dutyplan.data.dutyplan.DutyPlan;
import kik.dutyplan.management.DutyPlanManagement;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;

import kik.user.data.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.commons.lang.Assert;
import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

/**
 * Creates sample {@link kik.event.data.movieEvent.MovieEvent}s
 *
 * @author Georg Lauterbach
 * @version 0.3.3
 */
@Component
public class SampleMovieEventInitializer implements DataInitializer {
	
	private static final Logger LOG = LoggerFactory.getLogger(SampleMovieEventInitializer.class);
	private MovieEventManagement movieEventManagement;
	private DutyPlanManagement dutyPlanManagement;
	private UserRepository userRepository;
	
	/**
	 * Default constructor of an {@link SampleMovieEventInitializer}
	 *
	 * @param movieEventManagement Management layer to access the process of
	 *                             creating an event and "intercepting" it
	 * @param dutyPlanManagement   Management layer to distribute functionality for {@link DutyPlan}s
	 * @param userRepository       a repository containing all {@link User}s
	 */
	public SampleMovieEventInitializer(MovieEventManagement movieEventManagement, DutyPlanManagement dutyPlanManagement,
									   UserRepository userRepository) {
		Assert.notNull(movieEventManagement, "eventRepository must not be null!" +
				"~ in event.standard.SampleEventInitializer::SampleEventInitializer");
		this.movieEventManagement = movieEventManagement;
		this.dutyPlanManagement = dutyPlanManagement;
		this.userRepository = userRepository;
	}
	
	/**
	 * Creates sample data to get an overview of all {@link MovieEvent}s
	 *
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {
		if (movieEventManagement.getMovieEventRepository().findAll().iterator().hasNext()) {
			return;
		}
		
		LOG.info("Creating default movie-events for showcasing purposes.");
		var bookingManagement = this.movieEventManagement.getBookingManagement();
		
		if (this.movieEventManagement.getBookingManagement()
				.findByTbNumber("tr-254025")
				.isPresent()) {
			Booking sampleBookingA = bookingManagement
					.findByTbNumber("tr-254025")
					.get();
			
			this.movieEventManagement
					.createAndSaveMovieEvent(
							new MovieEventForm(
									"Batman trilogy pt. 2",
									sampleBookingA.getStartDate().plusDays((long) (Math.random() * 10000) % 3 + 1),
									"20:30",
									String.valueOf(sampleBookingA.getId()),
									"PRIVATE"),
							null);
		}
		var tmp = bookingManagement
				.findByTbNumber("11235813");
		if (tmp.isPresent()) {
			Booking sampleBookingB = tmp.get();
			
			for (int i = 4; i < 8; ++i) {
				this.movieEventManagement
						.createAndSaveMovieEvent(
								new MovieEventForm(
										"[OV] - " + i + "th COPY",
										sampleBookingB.getStartDate().plusDays((long) (Math.random() * 1000) % 4 + 1),
										"22:45",
										String.valueOf(sampleBookingB.getId()),
										"PUBLIC"),
								null);
			}
		}
		
		if (bookingManagement
				.findByTbNumber("31415926")
				.isPresent()) {
			Booking sampleBookingC = this.movieEventManagement
					.getBookingManagement()
					.findByTbNumber("31415926")
					.get();
			
			this.movieEventManagement
					.createAndSaveMovieEvent(
							new MovieEventForm(
									"",
									sampleBookingC.getStartDate().plusDays((long) (Math.random() * 10000) % 3 + 1),
									"18:15",
									String.valueOf(sampleBookingC.getId()),
									"PRIVATE"),
							null);
		}
	}
}
