package kik.movie.management;

import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import kik.movie.data.Movie;

/**
 * Class for handling all filter requests for {@link Movie}.
 *
 * @author Felix Rülke
 */
public class MovieFilter {
	/**
	 * Filters the given {@link Movie}'s by the given input {@link String}. Large /
	 * Lower case doesn't matter.
	 *
	 * @param movies to be filtered {@link Movie}
	 * @param input  filter {@link String}
	 * @return filtered {@link Movie}'s as {@link Streamable}
	 */
	public static Streamable<Movie> filter(Streamable<Movie> movies, String input) {
		Assert.notNull(input, "input must not be null!");

		final String searchString = input.toLowerCase();

		return movies.filter(m -> {
			String originalMovieName = m.getOriginalName().toLowerCase();
			String germanMovieName = m.getGermanName().toLowerCase();
			String releaseYear = m.getReleaseYear().toLowerCase();
			String runTime = String.valueOf(m.getRunTimeInMin());

			return (originalMovieName.contains(searchString) || germanMovieName.contains(searchString)
					|| releaseYear.contains(searchString) || runTime.contains(searchString));
		});
	}
}