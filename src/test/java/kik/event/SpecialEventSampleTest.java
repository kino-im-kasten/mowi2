package kik.event;

import kik.booking.data.Booking;
import kik.event.data.specialEvent.SpecialEvent;
import kik.event.management.movieEvent.MovieEventManagement;
import kik.event.management.specialEvent.SpecialEventManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SpecialEventSampleTest {
	
	protected SpecialEventManagement specialEventManagement;
	
	@Autowired
	public SpecialEventSampleTest(SpecialEventManagement specialEventManagement) {
		this.specialEventManagement = specialEventManagement;
	}
}
