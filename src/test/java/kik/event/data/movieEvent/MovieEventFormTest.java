package kik.event.data.movieEvent;

import kik.event.MovieEventSampleTest;
import kik.event.management.movieEvent.MovieEventManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
public class MovieEventFormTest extends MovieEventSampleTest {
	
	MovieEventForm movieEventForm;
	private final static LocalDate localDate = LocalDate.now();
	
	@Autowired
	MovieEventFormTest(MovieEventManagement movieEventManagement) {
		super(movieEventManagement);
	}
	
	@BeforeEach
	void setup() {
		this.movieEventForm = new MovieEventForm("Additonal test name",
				localDate,
				"20:15",
				"31415926",
				"PUBLIC");
	}
	
	@Test
	void uMovieEventFormCreateFormP1() {
		assertEquals(this.movieEventForm.getBookingID().toString(), "31415926");
	}
	
	@Test
	void uMovieEventFormGetterSetterP1() {
		Long longValue = Long.valueOf(00000000);
		this.movieEventForm.setBookingID(longValue);
		
		assertEquals(this.movieEventForm.getBookingID(), longValue);
	}
}
