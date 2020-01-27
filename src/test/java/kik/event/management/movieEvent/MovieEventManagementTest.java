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

import kik.event.MovieEventSampleTest;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
public class MovieEventManagementTest extends MovieEventSampleTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Autowired
	MovieEventManagementTest(MovieEventManagement movieEventManagement) {
		super(movieEventManagement);
	}
	
	@Test
	void iMovieEventManagementCreateMovieEventP1() {
		MovieEventForm movieEventForm = new MovieEventForm(
				"AdditionalTestName",
				LocalDate.now(),
				"20:15",
				String.valueOf(this.booking.getId()),
				"PUBLIC");
		
		Errors movieEventFormErrors = new BeanPropertyBindingResult(
				movieEventForm,
				"movieEventForm");
		
		MovieEvent movieEvent = this.movieEventManagement.createAndSaveMovieEvent(movieEventForm, movieEventFormErrors);
		
		if (movieEvent == null) {
			fail("movieEventCreation failed! [Here should be the opportunity to check for errors!");
		}
		
		MovieEvent movieEventFromRepository;
		if (this.movieEventManagement.getMovieEventRepository().findById(movieEvent.getId()).isPresent()) {
			movieEventFromRepository = this.movieEventManagement
					.getMovieEventRepository()
					.findById(movieEvent.getId())
					.get();
		} else {
			movieEventFromRepository = null;
			fail("A movieEvent from the repository should have been found!");
		}
		
		assertEquals("There should be no errors in movieEventForm",
				0,
				movieEventFormErrors.getErrorCount());
		assertEquals("The created movie should match the one saved in the database",
				movieEvent,
				movieEventFromRepository);
	}
	
	@Test
	void uMovieEventManagementDeleteMovieEventN1() {
		assertFalse(this.movieEventManagement.deleteMovieEvent(-1));
		
		for (int i = 0; i < 100000; ++i) {
			boolean ret = this.movieEventManagement.deleteMovieEvent(i);
			if (ret) {
				return;
			}
		}
		fail("Some event should have been deleted!");
	}
}