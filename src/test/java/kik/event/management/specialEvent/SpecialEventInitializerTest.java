package kik.event.management.specialEvent;

import kik.booking.data.Booking;
import kik.event.SpecialEventSampleTest;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;
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
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Transactional
public class SpecialEventInitializerTest extends SpecialEventSampleTest {
	
	private final SpecialEventInitializer specialEventInitializer;
	private SpecialEventForm specialEventForm;
	
	@Autowired
	SpecialEventInitializerTest(SpecialEventManagement specialEventManagement,
								SpecialEventInitializer specialEventInitializer) {
		super(specialEventManagement);
		
		this.specialEventInitializer = specialEventInitializer;
	}
	
	@BeforeEach
	void setup() {
		LocalDate localDate = LocalDate.now();
		this.specialEventForm = new SpecialEventForm(
				"Additioal test name",
				localDate,
				"06:15",
				"Haha",
				"22:15",
				"SPECIAL",
				"PRIVATE");
	}
	
	@Test
	void iSpecialEventInitializerInitSpecialEventP1() {
		SpecialEvent specialEvent;
		try {
			specialEvent = this.specialEventInitializer.initializeSpecialEvent(this.specialEventForm);
		} catch (Exception e) {
			specialEvent = null;
			fail("A specialEventForm with correct data should throw no exception!");
		}
		
		Errors errors = new BeanPropertyBindingResult(specialEvent, "specialEvent");
		
		assertEquals("There should be no errors in the movieEvent",
				0,
				errors.getErrorCount());
	}
	
	@Test
	void iSpecialEventInitializerUpdateSpecialEventP1() {
		SpecialEvent specialEvent;
		try {
			specialEvent = this.specialEventInitializer.initializeSpecialEvent(this.specialEventForm);
		} catch (Exception e) {
			specialEvent = null;
			fail("A specialEventForm with correct data should throw no exception!");
		}
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		LocalDate newLocalDate = LocalDate.now();
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime odt = start.atDate(newLocalDate);
		
		String newAdditionalName = "New additional name";
		
		SpecialEventForm newSpecialEventForm = new SpecialEventForm(
				newAdditionalName,
				newLocalDate,
				start.format(DateTimeFormatter.ofPattern("HH:mm")),
				"Haha",
				"22:15",
				"SPECIAL",
				"PRIVATE"
		);
		
		this.specialEventInitializer.updateSpecialEvent(specialEvent, newSpecialEventForm);
		
		assertEquals(specialEvent.getAdditonalName(), newAdditionalName);
		assertEquals(specialEvent.getFullyQualifiedName(),  specialEvent.getFullyQualifiedName());
		assertEquals(specialEvent.getDate(), newLocalDate);
		assertEquals(specialEvent.getStart(), start);
		assertEquals(specialEvent.getOdt(), odt);
		assertEquals(specialEvent.getEventPublicity(), EventPublicity.PRIVATE);
		assertEquals(specialEvent.getEventType(), EventType.SPECIAL);
	}
}
