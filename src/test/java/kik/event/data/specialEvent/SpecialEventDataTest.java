package kik.event.data.specialEvent;

import kik.event.SpecialEventSampleTest;
import kik.event.data.EventPlanningStatus;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEventData;
import kik.event.management.specialEvent.SpecialEventManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@SpringBootTest
@Transactional
public class SpecialEventDataTest extends SpecialEventSampleTest {
	
	SpecialEventData specialEventData;
	
	@Autowired
	SpecialEventDataTest(SpecialEventManagement specialEventManagement) {
		super(specialEventManagement);
	}
	
	@BeforeEach
	void setup() {
		this.specialEventData = new SpecialEventData();
	}
	
	@Test
	void uSpecialEventDataCreateDataAndTypeP1() {
		assertEquals("The eventType should be null on initialization",
				EventType.SPECIAL,
				this.specialEventData.getEventType());
	}
	
	@Test
	void uSpecialEventDataGetterAndSetterP1() {
		this.specialEventData.setEventPublicity(EventPublicity.PRIVATE);
		this.specialEventData.setEventType(EventType.SPECIAL);
		
		this.specialEventData.setAdditionalName("Additional test name");
		this.specialEventData.setFullyQualifiedName("Fully qualified test.");
		this.specialEventData.setEventPlanningStatus(EventPlanningStatus.CANCELED);
		
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		LocalDate localDate = LocalDate.now();
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		OffsetDateTime odt = start.atDate(localDate);
		
		this.specialEventData.setDate(localDate);
		this.specialEventData.setExpectedEnd(start);
		this.specialEventData.setOdt(odt);
		
		assertEquals(this.specialEventData.getEventPublicity(), EventPublicity.PRIVATE);
		assertEquals(this.specialEventData.getEventType(), EventType.SPECIAL);
	
		assertEquals(this.specialEventData.getAdditionalName(), "Additional test name");
		assertEquals(this.specialEventData.getFullyQualifiedName(), "Fully qualified test.");
		assertEquals(this.specialEventData.getEventPlanningStatus(), EventPlanningStatus.CANCELED);
		
		assertEquals(this.specialEventData.getDate(), localDate);
		assertEquals(this.specialEventData.getExpectedEnd(), start);
		assertEquals(this.specialEventData.getOdt(), odt);
	}
	
	@Test
	void uSpecialEventDataSetWrongTypeN1() {
		this.specialEventData.setEventType(EventType.SPECIAL);
		this.specialEventData.setEventType(EventType.MOVIE);
		assertEquals(this.specialEventData.getEventType(), EventType.SPECIAL);
		
		this.specialEventData.setEventType(null);
		assertEquals(this.specialEventData.getEventType(), EventType.SPECIAL);
	}
}
