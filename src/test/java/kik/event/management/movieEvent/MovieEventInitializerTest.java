package kik.event.management.movieEvent;

import kik.booking.data.Booking;
import kik.event.MovieEventSampleTest;
import kik.event.data.EventPublicity;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
public class MovieEventInitializerTest extends MovieEventSampleTest {
	
	private final MovieEventInitializer movieEventInitializer;
	private MovieEventForm movieEventForm;
	
	@Autowired
	MovieEventInitializerTest(MovieEventManagement movieEventManagement,
							  MovieEventInitializer movieEventInitializer) {
		super(movieEventManagement);
		
		this.movieEventInitializer = movieEventInitializer;
	}
	
	@BeforeEach
	void setup() {
		LocalDate localDate = LocalDate.now();
		this.movieEventForm = new MovieEventForm(
				"Additioal test name",
				localDate,
				"06:15",
				String.valueOf(this.booking.getId()),
				"PUBLIC");
	}
	
	@Test
	void uMovieEventInitializerInitMovieEventP1() {
		MovieEvent movieEvent;
		try {
			movieEvent = this.movieEventInitializer.initializeMovieEvent(this.movieEventForm);
		} catch (Exception e) {
			movieEvent = null;
			fail("A movieEventForm with correct data should throw no exception!");
		}
		
		Errors errors = new BeanPropertyBindingResult(movieEvent, "movieEvent");
		
		assertEquals("There should be no errors in the movieEvent",
				0,
				errors.getErrorCount());
		
		assertTrue("The movieEvent should have linked itself into the movie",
				movieEvent.getBooking()
						.getMovie()
						.getLinkedEvents()
						.contains(movieEvent.getId()));
		
		assertTrue("The movieEvent should have linked itself into the booking",
				movieEvent.getBooking()
						.getEvents().contains(movieEvent));
	}
	
	@Test
	void iMovieEventInitializerUpdateMovieEventP1() {
		MovieEvent movieEvent;
		try {
			movieEvent = this.movieEventInitializer.initializeMovieEvent(movieEventForm);
		} catch (Exception e) {
			movieEvent = null;
			fail("A movieEventForm with correct data should throw no exception!");
		}

		Booking newBooking = this.movieEventManagement
				.getBookingManagement()
				.findByTbNumber("11235813")
				.get();

		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		LocalDate newLocalDate = LocalDate.now();
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime odt = start.atDate(newLocalDate);

		String newAdditionalName = "New additional name";

		MovieEventForm newMovieEventForm = new MovieEventForm(
				newAdditionalName,
				newLocalDate,
				start.format(DateTimeFormatter.ofPattern("HH:mm")),
				String.valueOf(newBooking.getId()),
				"PUBLIC");

		movieEvent = this.movieEventInitializer.updateMovieEvent(movieEvent, newMovieEventForm);

		assertEquals(movieEvent.getBooking().getId(), newBooking.getId());
		assertEquals(movieEvent.getAdditonalName(), newAdditionalName);
		assertEquals(movieEvent.getFullyQualifiedName(),  movieEvent.getBooking().getMovie().getOriginalName() + " - " + newAdditionalName);
		assertEquals(movieEvent.getDate(), newLocalDate);
		assertEquals(movieEvent.getStart(), start);
		assertEquals(movieEvent.getOdt(), odt);
		assertEquals(movieEvent.getEventPublicity(), EventPublicity.PUBLIC);
	}
	
	@Test
	void iMovieEventInitializerUpdateEventWrongBookingIDN1() {
		MovieEvent movieEvent;
		try {
			movieEvent = this.movieEventInitializer.initializeMovieEvent(movieEventForm);
		} catch (Exception e) {
			movieEvent = null;
			fail("A movieEventForm with correct data should throw no exception!");
		}
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		LocalDate newLocalDate = LocalDate.now();
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime odt = start.atDate(newLocalDate);
		
		String newAdditionalName = "New additional name";
		
		MovieEventForm newMovieEventForm = new MovieEventForm(
				newAdditionalName,
				newLocalDate,
				start.format(DateTimeFormatter.ofPattern("HH:mm")),
				"01010101",
				"PUBLIC");
		
		movieEvent = this.movieEventInitializer.updateMovieEvent(movieEvent, newMovieEventForm);
		
		assertEquals(movieEvent.getBooking().getId(), this.booking.getId());
		assertEquals(movieEvent.getAdditonalName(), newAdditionalName);
		assertEquals(movieEvent.getFullyQualifiedName(),  movieEvent.getBooking().getMovie().getOriginalName() + " - " + newAdditionalName);
		assertEquals(movieEvent.getDate(), newLocalDate);
		assertEquals(movieEvent.getStart(), start);
		assertEquals(movieEvent.getOdt(), odt);
	}
}
