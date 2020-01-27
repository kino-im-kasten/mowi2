package kik.event.management.specialEvent;

import kik.event.SpecialEventSampleTest;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.data.specialEvent.SpecialEventForm;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
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
public class SpecialEventManagementTest extends SpecialEventSampleTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	
	@Autowired
	SpecialEventManagementTest(SpecialEventManagement specialEventManagement) {
		super(specialEventManagement);
	}
	
	@Test
	void uMovieEventManagementCreateEventP1() {
		ZoneOffset zoneOffSet = ZoneId.of("Europe/Berlin").getRules().getOffset(LocalDateTime.now());
		LocalDate newLocalDate = LocalDate.now();
		OffsetTime start = LocalTime.parse("05:15", DateTimeFormatter.ofPattern("HH:mm")).atOffset(zoneOffSet);
		
		String newAdditionalName = "New additional name";
		SpecialEventForm specialEventForm = new SpecialEventForm(
				newAdditionalName,
				newLocalDate,
				start.format(DateTimeFormatter.ofPattern("HH:mm")),
				"Haha",
				"22:15",
				"SPECIAL",
				"PRIVATE"
		);
		
		Errors specialEventFormErrors = new BeanPropertyBindingResult(
				specialEventForm,
				"movieEventForm");
		
		SpecialEvent specialEvent = this.specialEventManagement.createAndSaveSpecialEvent(specialEventForm, specialEventFormErrors);
		
		if (specialEvent == null) {
			System.out.println(specialEventFormErrors);
			fail("movieEventCreation failed! [Here should be the opportunity to check for errors!");
		}
		
		SpecialEvent specialEventFromRepository;
		if (this.specialEventManagement.getSpecialEventRepository().findById(specialEvent.getId()).isPresent()) {
			specialEventFromRepository = this.specialEventManagement
					.getSpecialEventRepository()
					.findById(specialEvent.getId())
					.get();
		} else {
			specialEventFromRepository = null;
			fail("A movieEvent from the repository should have been found!");
		}
		
		assertEquals("There should be no errors in movieEventForm",
				0,
				specialEventFormErrors.getErrorCount());
		assertEquals("The created movie should match the one saved in the database",
				specialEvent,
				specialEventFromRepository);
	}
	
	@Test
	void uEventManagementDeleteSpecialEvent() {
		assertFalse(this.specialEventManagement.deleteSpecialEvent(-1));
		
		for (int i = 0; i < 100000; ++i) {
			boolean ret = this.specialEventManagement.deleteSpecialEvent(i);
			if (ret) {
				return;
			}
		}
		fail("Some event should have been deleted!");
	}
}
