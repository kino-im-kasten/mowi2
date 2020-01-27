package kik.rest.data;

import kik.event.data.event.Event;
import kik.movie.data.Movie;
import kik.event.data.movieEvent.MovieEvent;
import kik.booking.data.Booking;

/**
 * {@link RestMovieEvent} Extends {@link RestEvent} to also contain a
 * {@link Movie} Will be used by spring to create a json string.
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestMovieEvent extends RestEvent {

    private Movie movie;

    /**
     * Default constructor of a {@link RestMovieEvent}. Automatically fetches the
     * {@link Movie} from the {@link Booking}
     * 
     * @param event The {@link Event} to parse
     */
    public RestMovieEvent(Event event) {
        super(event);
        if (event instanceof MovieEvent) {
            this.movie = ((MovieEvent) event).getBooking().getMovie();
        }
    }

    /**
     * Getter for the movie
     * 
     * @return the movie
     */
    public Movie getMovie() {
        return this.movie;
    }
}