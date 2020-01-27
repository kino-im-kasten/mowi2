package kik.booking.management;

import kik.booking.data.BookingForm;
import kik.booking.data.Conditions;
import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingValidationTest {
	private BookingValidation validator;

	private MovieRepository movieRepository;
	private DistributorRepository distributorRepository;

	private long movieId;
	private long distributorId;

	private boolean initialized;

	@Autowired
	BookingValidationTest(MovieRepository movieRepository, DistributorRepository distributorRepository){
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;

		initialized = false;
	}

	@BeforeEach
	void setUp(){
		if(!initialized) {
			initialized = true;

			Movie m1 = new Movie("a", "b", "c", "d", "e", 1,"-", 1);
			movieRepository.save(m1);
			movieId = m1.getId();

			Distributor d1 = new Distributor();
			distributorRepository.save(d1);
			distributorId = d1.getId();

			validator = new BookingValidation();
		}
	}

	@Test
	void uBookingValidationValidateP1(){
		BookingForm bf = new BookingForm("2345", movieId, distributorId,
			LocalDate.of(2019, 12, 24), LocalDate.of(2019, 12, 31),
			new Conditions(25, 5));

		Errors errors = new BeanPropertyBindingResult(bf, "Booking Form");

		validator.validate(bf, movieRepository, distributorRepository, errors);

		assertFalse(errors.hasErrors());
	}

	@Test
	void uBookingValidationValidateP2(){
		BookingForm bf = new BookingForm("", 12345, 12345,
			LocalDate.of(2019, 12, 24), LocalDate.of(2019, 12, 17),
			new Conditions(-25, 500, -2, -2.45f, -3, -25.4f));

		Errors errors = new BeanPropertyBindingResult(bf, "Booking Form");

		validator.validate(bf, movieRepository, distributorRepository, errors);

		assertEquals("booking.error.not_empty", errors.getFieldError("tbNumber").getCode());
		assertEquals("booking.error.not_negative", errors.getFieldError("conditions.minimumGuarantee").getCode());
		assertEquals("booking.error.not_negative", errors.getFieldError("conditions.freight").getCode());
		assertEquals("booking.error.not_negative", errors.getFieldError("conditions.advertisement").getCode());
		assertEquals("booking.error.not_negative", errors.getFieldError("conditions.spio").getCode());
		assertEquals("booking.error.not_negative", errors.getFieldError("conditions.other").getCode());
		assertEquals("booking.error.illegal_date", errors.getFieldError("endDate").getCode());
		assertEquals("booking.error.illegal_id", errors.getFieldError("movieID").getCode());
		assertEquals("booking.error.illegal_id", errors.getFieldError("distributorID").getCode());
		assertEquals("booking.error.too_high_percentage", errors.getFieldError("conditions.percentage").getCode());
	}
}
