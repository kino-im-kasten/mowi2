package kik.pdfGenerator;

import kik.Application;
import kik.booking.data.Booking;
import kik.booking.management.BookingManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = Application.class)
public class pdfGeneratorTest {
	@Autowired
	MockMvc mvc;
	private BookingManagement management;

	@Autowired
	public pdfGeneratorTest(BookingManagement management) {
		this.management = management;
	}

	@Test
	@WithMockUser(roles={"ORGA","ADMIN"})
	void pdfControllerTestP1() throws Exception {
		Booking b1 = management.getAllBookings().get().findFirst().get();


		MvcResult response = mvc.perform(
			get("/generatePDF/?id="+b1.getId())
		).andReturn();

		assertEquals(200, response.getResponse().getStatus());
		assertEquals( "application/pdf", response.getResponse().getContentType());

	}

}
