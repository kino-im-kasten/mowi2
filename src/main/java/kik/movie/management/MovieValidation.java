package kik.movie.management;

//import java.time.LocalDate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import kik.movie.data.Movie;
import kik.movie.data.MovieForm;
import kik.movie.data.MovieRepository;

/**
 * A {@link Validator} implementation that will check the form
 *
 * @author Felix Rülke
 * @version 0.0.2
 */
public class MovieValidation {

	/**
	 * A special and explicit validation function Checks the form without picture to
	 * be correctly validated
	 *
	 * @param form            {@link MovieForm} to be validated
	 * @param errors          {@link Errors} errors for propagation
	 * @param movieRepository {@link MovieRepository} which stores all movies
	 * @param isEdit          States if the request is a creation or an edit
	 */
	public static void validateWithoutPicture(MovieForm form, Errors errors, MovieRepository movieRepository,
			boolean isEdit) {

		validationExcluded(form, errors);

		if (form.getRegie() != null && form.getRegie().length() >= 1000) {
			errors.rejectValue("regie", "movie.create.error.regieToLong");
		}

		if (form.getTrailer() != null && form.getTrailer().length() >= 1000) {
			errors.rejectValue("trailer", "movie.create.error.trailerToLong");
		}

		if (errors.hasErrors()) {
			return;
		}

		if (!isEdit) {
			String gn = form.getGermanName().strip();
			String ry = form.getReleaseYear().strip();
			int rt = Integer.parseInt(form.getRunTimeInMin());

			for (Movie movie : movieRepository.findAll()) {
				if (movie.getGermanName().equals(gn) && movie.getReleaseYear().equals(ry)
						&& movie.getRunTimeInMin() == rt) {
					errors.rejectValue("germanName", "movie.create.error.duplicate_movie");
					errors.rejectValue("releaseYear", "movie.create.error.duplicate_movie");
					errors.rejectValue("runTimeInMin", "movie.create.error.duplicate_movie");
					return;
				}
			}
		}

	}

	/**
	 * A special and explicit validation function Checks the form to be correctly
	 * validated
	 *
	 * @param form            {@link MovieForm} to be validated
	 * @param errors          {@link Errors} errors for propagation
	 * @param movieRepository {@link MovieRepository} which stores all movies
	 * @param isEdit          States if the request is a creation or an edit
	 */
	public static void validateWithPicture(MovieForm form, Errors errors, MovieRepository movieRepository,
			boolean isEdit) {
		validateWithoutPicture(form, errors, movieRepository, isEdit);

		if (form.getPicture().getOriginalFilename() == "") {
			errors.rejectValue("picture", "movie.create.file_not_found_error");
		} else if (form.getPicture().getSize() > 10000000) {
			errors.rejectValue("picture", "movie.create.file_size_error");
		} else if (!form.getPicture().getContentType().matches("^image\\/[a-zA-Z]+$")) {
			errors.rejectValue("picture", "movie.create.file_type_error");
		}
	}

	/**
	 * Outsurced validation to fulfill sonarqubes regulations
	 * 
	 * @param form   {@link MovieForm} to be validated
	 * @param errors {@link Errors} errors for propagation
	 */
	private static void validationExcluded(MovieForm form, Errors errors) {
		// if (form.getOriginalName() == null ||
		// form.getOriginalName().strip().equals("")) {
		// errors.rejectValue("originalName", "movie.create.error.title_og");
		// }

		if (form.getGermanName() == null || form.getGermanName().strip().equals("")) {
			errors.rejectValue("germanName", "movie.create.error.title_de");
		} else if (form.getGermanName().length() >= 1000) {
			errors.rejectValue("germanName", "movie.create.error.germanToLong");
		}

		if (form.getReleaseYear() == null || !form.getReleaseYear().matches("^[0-9]*$")) {
			errors.rejectValue("releaseYear", "movie.create.release_year_error");
		} else if (form.getReleaseYear().length() > 4) {
				//|| Integer.parseInt(form.getReleaseYear()) > LocalDate.now().getYear()) {
			errors.rejectValue("releaseYear", "movie.create.error.releaseInFuture");
		}

		if (form.getRunTimeInMin() == null || !String.valueOf(form.getRunTimeInMin()).matches("^[1-9]+[0-9]*$")) {
			errors.rejectValue("runTimeInMin", "movie.create.error.runTimeInMin");
		} else if (form.getRunTimeInMin().length() > 5) {
			errors.rejectValue("runTimeInMin", "movie.create.error.runtimeToLong");
		}

		if (form.getOriginalName() != null && form.getOriginalName().length() >= 1000) {
			errors.rejectValue("originalName", "movie.create.error.originalToLong");
		}

		if (form.getDescriptionText() != null && form.getDescriptionText().length() >= 15000) {
			errors.rejectValue("descriptionText", "movie.create.error.descriptionToLong");
		}
	}
}
