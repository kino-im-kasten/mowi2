package kik.booking.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingStateTest {
	@Test
	void uBookingStateToStringP(){
		assertEquals("Anstehend", BookingState.PENDING.toString());
		assertEquals("Aktiv", BookingState.ACTIVE.toString());
		assertEquals("Abgelaufen", BookingState.OVER.toString());
		assertEquals("Abgesagt", BookingState.CANCELED.toString());
		assertEquals("Abgerechnet", BookingState.SETTLEDUP.toString());
	}
}
