package kik.movie.data;

import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import kik.picture.data.PictureRepository;
import kik.picture.data.Picture;

import java.util.List;

@Component
@Order(20)
public class MovieInitializer implements DataInitializer {

	private MovieRepository movieRepository;
	private PictureRepository pictureRepository;

	/**
	 * Default constructor of an {@link MovieInitializer}
	 * 
	 * @param movieRepository   A repository containing all {@link Movie}'s
	 * @param pictureRepository A repository containing all {@link Picture}'s
	 */
	MovieInitializer(MovieRepository movieRepository, PictureRepository pictureRepository) {
		this.movieRepository = movieRepository;
		this.pictureRepository = pictureRepository;
	}

	/**
	 * Creates sample data for Movies
	 *
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {
		if (movieRepository.findAll().iterator().hasNext()) {
			return;
		}


		List.of(new Movie("The Dark Knight", "The Dark Knight", "2008", "Christopher Nolan",
				"The Dark Knight (dt. Der dunkle Ritter) ist ein US-amerikanisch-britisches Actiondrama" + 
				"und eine Comicverfilmung des Regisseurs Christopher Nolan aus dem Jahr 2008, das auf " +
				"dem von Bob Kane und Bill Finger erschaffenen Batman-Mythos basiert.",
				153,  "-",pictureRepository.findAllByName("batman.jpg").get(0).getId()),
				new Movie("Blade Runner 2049", "Blade Runner 2049", "2017", "Denis Villeneuve",
						"Blade Runner 2049 ist ein US-amerikanischer Science-Fiction-Film aus dem Jahr " +
						"2017 und die Fortsetzung von Blade Runner (1982).",
						164, "-",pictureRepository.findAllByName("bladerunner.jpg").get(0).getId())

		).forEach(this.movieRepository::save);
	}
}
