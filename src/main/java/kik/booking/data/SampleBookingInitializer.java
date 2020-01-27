package kik.booking.data;

import kik.distributor.data.Distributor;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

/**
 * Initializes sample bookings for testing purposes.
 *
 * @author Erik Schönherr
 */
@Component
@Order
public class SampleBookingInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(SampleBookingInitializer.class);

	private final BookingRepository bookingRepository;
	private final MovieRepository movieRepository;
	private final DistributorRepository distributorRepository;

	/**
	 * Default constructor of the SampleBookingInitializer.
	 *
	 * @param bookingRepository Repository storing all {@link Booking}s
	 * @param movieRepository Repository storing all {@link Movie}s
	 * @param distributorRepository Repository storing all {@link Distributor}s
	 */
	public SampleBookingInitializer(BookingRepository bookingRepository, MovieRepository movieRepository,
									DistributorRepository distributorRepository){
		Assert.notNull(bookingRepository, "bookingRepository must not be null!");
		Assert.notNull(movieRepository, "movieRepository must not be null!");
		Assert.notNull(distributorRepository, "distributorRepository must not be null!");

		this.bookingRepository = bookingRepository;
		this.movieRepository = movieRepository;
		this.distributorRepository = distributorRepository;
	}

	/**
	 * Initializes a bunch of sample Bookings.
	 */
	@Override
	public void initialize() {
		if (bookingRepository.findAll().iterator().hasNext()) {
			return;
		}

		List<Movie> movies = movieRepository.findAll().toList();
		Iterator<Distributor> distributors = distributorRepository.findAll().iterator();

		Distributor d1 = distributors.next();
		Distributor d2 = distributors.next();

		Conditions c = new Conditions(25, 5);

		LOG.info("Creating default bookings for showcasing puposes.");

		Booking b1 = new Booking("2543859er", movies.get(0), d1,
			LocalDate.now().minusDays(14),
			LocalDate.now().minusDays(7), c);
		b1.setState(BookingState.CANCELED);

		Booking b2 = new Booking("tr-121212", movies.get(0), d1,
			LocalDate.now().minusDays(12),
			LocalDate.now().minusDays(5), c);
		b2.setState(BookingState.SETTLEDUP);

		Booking b3 = new Booking("11235813", movies.get(0), d1,
			LocalDate.now().minusDays(14),
			LocalDate.now().minusDays(7), c);

		Booking b4 = new Booking("tr-254025", movies.get(0), d1,
			LocalDate.now().minusDays(2),
			LocalDate.now().plusDays(6), c);

		Booking b5 = new Booking("31415926", movies.get(1), d2,
			LocalDate.now().minusDays(4),
			LocalDate.now().plusDays(3), c);

		Booking b6 = new Booking("24816326", movies.get(1), d2,
			LocalDate.now().plusDays(7),
			LocalDate.now().plusDays(14), c);

		List.of(b1, b2, b3, b4, b5, b6).forEach(bookingRepository::save);

		for(int i = 1; i <= 50; i++){
			long startDateOffset = (long) ((Math.random() * 1000) % 4 + 1);
			Booking b = new Booking("copy-" + i, movies.get(0), d1,
				LocalDate.now().minusDays(startDateOffset),
				LocalDate.now().plusDays(7 - startDateOffset), c);
			bookingRepository.save(b);
		}
	}
}
