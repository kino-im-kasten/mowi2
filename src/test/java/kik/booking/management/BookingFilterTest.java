package kik.booking.management;

import kik.booking.data.Booking;
import kik.booking.data.Conditions;
import kik.booking.management.BookingFilter;
import kik.distributor.data.Distributor;
import kik.movie.data.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingFilterTest {
	private BookingFilter bookingFilter;

	private Booking b1;
	private Booking b2;
	private Booking b3;

	private Streamable<Booking> allBookings;

	private boolean initialized = false;

	@BeforeEach
	void setup(){
		if(!initialized) {
			initialized = true;

			bookingFilter = new BookingFilter();

			Movie m1 = new Movie("Blade Runner 2048", "Blade Runner 2048", "2014", "",
				"", 180,"-", 0);
			Movie m2 = new Movie("Matrix", "Matrix", "2013", "",
				"", 170,"-", 0);
			Movie m3 = new Movie("English Title", "Deutscher Titel", "2014", "",
				"", 180,"-", 0);
			Distributor d1 = new Distributor("Warner Bros", "Dresden", "123123123", "");
			Distributor d2 = new Distributor("21Century", "Dresden", "123123123", "");
			Distributor d3 = new Distributor("Alpenway Media GmbH", "Dresden", "123123123", "alpen@berg.de");
			Conditions conditions = new Conditions(15, 20);

			b1 = new Booking("12345678", m1, d1, LocalDate.now().plusDays(7),
				LocalDate.now().plusDays(14), conditions);
			b2 = new Booking("87654329", m2, d2, LocalDate.now().plusDays(7),
				LocalDate.now().plusDays(14), conditions);
			b3 = new Booking("32145658", m3, d3, LocalDate.now().plusDays(7),
				LocalDate.now().plusDays(14), conditions);

			allBookings = Streamable.of(b1, b2, b3);
		}
	}

	@Test
	void uBookingFilterFilterP1(){
		//tests filter by movie name
		Streamable<Booking> bookings1 = bookingFilter.filter(allBookings, "unner");

		assertEquals(1, bookings1.toList().size());
		assertTrue(bookings1.toList().contains(b1));
	}

	@Test
	void uBookingFilterFilterP2(){
		//tests filter by distributor name and TB number
		Streamable<Booking> bookings2 = bookingFilter.filter(allBookings, "21");

		assertEquals(2, bookings2.toList().size());
		assertTrue(bookings2.toList().contains(b2));
		assertTrue(bookings2.toList().contains(b3));
	}

	@Test
	void uBookingFilterFilterP3(){
		//tests filter by german movie title
		Streamable<Booking> bookings3 = bookingFilter.filter(allBookings, "eutsch");

		assertEquals(1, bookings3.toList().size());
		assertTrue(bookings3.toList().contains(b3));
	}
}
