package kik.event.data.specialEvent;

import kik.event.SpecialEventSampleTest;
import kik.event.data.EventPublicity;
import kik.event.data.EventType;
import kik.event.data.movieEvent.MovieEventForm;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.event.management.specialEvent.SpecialEventManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
public class SpecialEventFormTest extends SpecialEventSampleTest {
	SpecialEventForm specialEventForm;
	private final static LocalDate localDate = LocalDate.now();
	
	@Autowired
	SpecialEventFormTest(SpecialEventManagement specialEventManagement) {
		super(specialEventManagement);
	}
	
	@BeforeEach
	void setup() {
		this.specialEventForm = new SpecialEventForm("Additonal test name",
				localDate,
				"20:15",
				"Haha",
				"22:25",
				"SPECIAL",
				"PUBLIC");;
	}
	
	@Test
	void uMovieEventFormGetterSetterP1() {
		assertEquals("Haha", this.specialEventForm.getDescription());
		assertEquals("22:25", this.specialEventForm.getExpectedEnd());
	}
}
