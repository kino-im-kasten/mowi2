package kik.booking.data;

import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.tickets.Tickets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {
	private Booking booking;

	private MovieEvent event1;
	private MovieEvent event2;

	BookingTest(){
		event1 = new MovieEvent();
		Tickets tickets1 = new Tickets();
		tickets1.setCardNormalStartNumber(3);
		tickets1.setCardNormalEndNumber(5);
		tickets1.setCardReducedStartNumber(2);
		tickets1.setCardReducedEndNumber(3);
		tickets1.setNormalPrice(3);
		tickets1.setReducedPrice(2.5);

		event1.setTickets(tickets1);
		event1.setId(1);

		event2 = new MovieEvent();
		Tickets tickets2 = new Tickets();
		tickets2.setCardNormalStartNumber(35);
		tickets2.setCardNormalEndNumber(38);
		tickets2.setCardReducedStartNumber(44);
		tickets2.setCardReducedEndNumber(51);
		tickets2.setNormalPrice(1.5);
		tickets2.setReducedPrice(1);

		event2.setTickets(tickets2);
		event2.setId(2);
	}

	@BeforeEach
	void setup(){
		this.booking = new Booking();
	}

	@Test
	void uBookingAddEventN(){
		assertThrows(IllegalArgumentException.class, () -> booking.addEvent(null));
	}

	@Test
	void uBookingRemoveEventN(){
		assertThrows(IllegalArgumentException.class, () -> booking.removeEvent(null));
	}

	@Test
	void uBookingAddAndRemoveEventP(){
		assertFalse(booking.removeEvent(event1));

		booking.addEvent(event1);

		assertTrue(booking.getEvents().contains(event1));

		assertTrue(booking.removeEvent(event1));
		assertFalse(booking.getEvents().contains(event1));
	}

	@Test
	void uBookingGetTotalRevenueP(){
		booking.addEvent(event1);
		booking.addEvent(event2);

		assertEquals(event1.calculateRawTicketsRevenue() + event2.calculateRawTicketsRevenue(), booking.getTotalRevenue());
	}

	@Test
	void uBookingSetStateTestP(){
		booking.setState(BookingState.CANCELED);
		assertEquals(BookingState.CANCELED, booking.getState());
		assertTrue(booking.isArchived());
	}

	@Test
	void uBookingUpdateStateP1(){
		booking.setState(BookingState.SETTLEDUP);
		booking.updateState();

		assertEquals(BookingState.SETTLEDUP, booking.getState());
	}

	@Test
	void uBookingUpdateStateP2(){
		booking.setState(BookingState.ACTIVE);

		booking.setStartDate(LocalDate.now().minusDays(8));
		booking.setEndDate(LocalDate.now().minusDays(1));

		booking.updateState();

		assertEquals(BookingState.OVER, booking.getState());
	}

	@Test
	void uBookingAllEventsSettledUpP1(){
		assertTrue(booking.allEventsSettledUp());
	}

//	@Test
//	void uBookingAllEventsSettledUpP2(){
//		MovieEvent e = new MovieEvent();
//		e.setTickets(new Tickets());
//		booking.addEvent(e);
//
//		assertFalse(booking.allEventsSettledUp());
//		booking.removeEvent(e);
//		booking.addEvent(event1);
//
//		assertTrue(booking.allEventsSettledUp());
//	}

	@Test
	void uBookingGetColourStringP(){
		assertEquals("", booking.getColourString());

		booking.setState(BookingState.ACTIVE);

		assertEquals("green", booking.getColourString());

		booking.setState(BookingState.OVER);

		MovieEvent e = new MovieEvent();
		booking.addEvent(e);
		assertEquals("red", booking.getColourString());

		booking.removeEvent(e);
		booking.addEvent(event1);
		assertEquals("orange", booking.getColourString());
	}
}
