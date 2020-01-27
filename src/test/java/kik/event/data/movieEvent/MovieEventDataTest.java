package kik.event.data.movieEvent;

import kik.booking.data.Booking;

import kik.event.MovieEventSampleTest;
import kik.event.data.EventPlanningStatus;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;

import kik.event.management.movieEvent.MovieEventManagement;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
public class MovieEventDataTest extends MovieEventSampleTest {
	
	MovieEventData movieEventData;
	
	@Autowired
	MovieEventDataTest(MovieEventManagement movieEventManagement) {
		super(movieEventManagement);
	}
	
	@BeforeEach
	void setup() {
		this.movieEventData = new MovieEventData();
	}
	
	@Test
	void uMovieEventDataCreateDataAndTypeP1() {
		assertEquals("The eventType should match!",
				this.movieEventData.getEventType(),
				EventType.MOVIE);
	}
	
	@Test
	void uMovieEventDataGetterSetterP1() {
		Booking newBooking = this.movieEventManagement
				.getBookingManagement()
				.findByTbNumber("31415926")
				.get();
		
		this.movieEventData.setEventPublicity(EventPublicity.PRIVATE);
		this.movieEventData.setEventType(EventType.MOVIE);
		this.movieEventData.setBooking(newBooking);
				
		this.movieEventData.setAdditionalName("Additional test name");
		this.movieEventData.setFullyQualifiedName("Fully qualified test.");
		this.movieEventData.setEventPlanningStatus(EventPlanningStatus.CANCELED);
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		LocalDate localDate = LocalDate.now();
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime odt = start.atDate(localDate);
		
		this.movieEventData.setDate(localDate);
		this.movieEventData.setExpectedEnd(start);
		this.movieEventData.setOdt(odt);
		
		assertEquals(this.movieEventData.getEventPublicity(), EventPublicity.PRIVATE);
		assertEquals(this.movieEventData.getEventType(), EventType.MOVIE);
		assertEquals(this.movieEventData.getBooking(), newBooking);
		
		assertEquals(this.movieEventData.getAdditionalName(), "Additional test name");
		assertEquals(this.movieEventData.getFullyQualifiedName(), "Fully qualified test.");
		assertEquals(this.movieEventData.getEventPlanningStatus(), EventPlanningStatus.CANCELED);
		
		assertEquals(this.movieEventData.getDate(), localDate);
		assertEquals(this.movieEventData.getExpectedEnd(), start);
		assertEquals(this.movieEventData.getOdt(), odt);
	}
	
	@Test
	void uMovieEventDataSetWrongTypeN1() {
		this.movieEventData.setEventType(EventType.SPECIAL);
		assertEquals(this.movieEventData.getEventType(), EventType.MOVIE);
	}
}
