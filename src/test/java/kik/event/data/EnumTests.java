package kik.event.data;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

@SpringBootTest
public class EnumTests {
	
	@Test
	void uEventPlanningStatusToStringP1() {
		EventPlanningStatus eventPlanningStatus = EventPlanningStatus.BOOKED;
		assertEquals(eventPlanningStatus.toString(), "Gebucht");
		
		eventPlanningStatus = EventPlanningStatus.IN_PLANNING;
		assertEquals(eventPlanningStatus.toString(), "In Planung");
		
		eventPlanningStatus = EventPlanningStatus.SPECIAL;
		assertEquals(eventPlanningStatus.toString(), "Spezial");
		
		eventPlanningStatus = EventPlanningStatus.SETTLED;
		assertEquals(eventPlanningStatus.toString(), "Abgebucht");
		
		eventPlanningStatus = EventPlanningStatus.PRESENTED;
		assertEquals(eventPlanningStatus.toString(), "Präsentiert");
		
		eventPlanningStatus = EventPlanningStatus.CANCELED;
		assertEquals(eventPlanningStatus.toString(), "Abgesagt");
	}
	
	@Test
	void uEventTypeToStringP1() {
		EventType eventType = EventType.MOVIE;
		assertEquals(eventType.toString(), "Film");
		
		eventType = EventType.SPECIAL;
		assertEquals(eventType.toString(), "Spezialveranstaltung");
		
		eventType = EventType.INTERN;
		assertEquals(eventType.toString(), "Interne Veranstaltung");
	}
	
	@Test
	void uEventTypeToTypeP1() {
		assertEquals("Spezialveranstaltung", EventType.toType(null).toString());
		assertEquals("Spezialveranstaltung", EventType.toType("SPECIAL").toString());
		assertEquals("Interne Veranstaltung", EventType.toType("INTERN").toString());
		assertNotEquals("Film", EventType.toType("MOVIE").toString());
	}
	
	@Test
	void uEventPublicityToStringP1() {
		EventPublicity eventPublicity = EventPublicity.PUBLIC;
		assertEquals(eventPublicity.toString(), "Öffentlich");
		
		eventPublicity = EventPublicity.PRIVATE;
		assertEquals(eventPublicity.toString(), "Privat");
	}
	
	@Test
	void uEventPublicityToTypeP1() {
		assertEquals("Öffentlich", EventPublicity.toPublicity("PUBLIC").toString());
		assertEquals("Privat", EventPublicity.toPublicity("PRIVATE").toString());
	}
}
