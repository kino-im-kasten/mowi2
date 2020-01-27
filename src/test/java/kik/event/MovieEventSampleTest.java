package kik.event;

import kik.booking.data.Booking;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.management.movieEvent.MovieEventManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
public class MovieEventSampleTest {
	
	protected MovieEventManagement movieEventManagement;
	protected final Booking booking;
	
	@Autowired
	public MovieEventSampleTest(MovieEventManagement movieEventManagement) {
		this.movieEventManagement = movieEventManagement;
		
		if (movieEventManagement
				.getBookingManagement()
				.findByTbNumber(String.valueOf(31415926))
			.isEmpty()) {
		throw new RuntimeException("Constructor of MovieEventDataTets could not find the required booking! Check the booking's TB-Number!");
	}
		
		this.booking = movieEventManagement
			.getBookingManagement()
			.findByTbNumber(String.valueOf(31415926))
			.get();
	}
	
	@Test
	void uEventManagementCalculateRedirectionP1() {
		assertEquals("redirect:/", this.movieEventManagement.calculateRedirection(null, null, false));
		assertEquals("redirect:/", this.movieEventManagement.calculateRedirection(0, null, false));
		
		assertEquals("redirect:/movieEvents/", this.movieEventManagement.calculateRedirection(1, null, true));
		assertEquals("redirect:/specialEvents/", this.movieEventManagement.calculateRedirection(1, null, false));
		
		assertEquals("redirect:/movieEvents/details/" + -1 + "/", this.movieEventManagement.calculateRedirection(2, -1, true));
		assertEquals("redirect:/specialEvents/details/" + -1 + "/", this.movieEventManagement.calculateRedirection(2, -1, false));
	}
}
