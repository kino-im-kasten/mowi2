package kik.movie.management;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import com.mysema.commons.lang.Assert;

import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import kik.movie.data.Movie;
import kik.movie.data.MovieForm;
import kik.movie.data.MovieRepository;
import kik.picture.data.Picture;
import kik.picture.data.PictureRepository;

@Service
public class MovieManagement {

    private final MovieRepository movieRepository;
    private final PictureRepository pictureRepository;

    /**
     * Default constructor of an {@link MovieManagement}
     * 
     * @param movieRepository   A repository containing all {@link Movie}'s
     * @param pictureRepository A repository containing all {@link Picture}'s
     */
    public MovieManagement(MovieRepository movieRepository, PictureRepository pictureRepository) {
        Assert.notNull(movieRepository, "MovieRepository must not be null!");
        this.movieRepository = movieRepository;
        this.pictureRepository = pictureRepository;
    }

    /**
     * Creates a new {@link Movie}
     * 
     * @param movieForm Stores the user input for movie creations and edit's
     * @param file      A movie cover picture
     * @param errors    Possible, propagated errors during the filling of the form
     * @return Returns the new created {@link Movie} object
     */
    public Movie createMovie(MovieForm movieForm, MultipartFile file, Errors errors) {

        Picture p = getInitialPicture();

        if (file.getSize() != 0) {
            movieForm.setPicture(file);
            MovieValidation.validateWithPicture(movieForm, errors, this.movieRepository, false);

            //override noposter
            p = this.loadPicture(file, errors);

            if (errors.hasErrors()) {
                return null;
            }

            this.pictureRepository.save(p);

        } else {
            MovieValidation.validateWithoutPicture(movieForm, errors, this.movieRepository, false);

            if (errors.hasErrors()) {
                return null;
            }
        }

        Movie movie = new Movie(movieForm, p.getId());
        this.movieRepository.save(movie);
        p.increaseCount();
        return movie;
    }

    /**
     * Edit {@link Movie} data
     * 
     * @param id        The movie id
     * @param movieForm Stores the user input for movie creations and edit's
     * @param file      A movie cover picture
     * @param errors    Possible, propagated errors during the filling of the form
     */
    public void editMovie(Long id, MovieForm movieForm, MultipartFile file, Errors errors) {

        // if a picture was uploaded -> validate it
        movieForm.setPicture(file);
        if (file.getSize() != 0 || !file.getOriginalFilename().equals("")) {
            MovieValidation.validateWithPicture(movieForm, errors, this.movieRepository, true);
        } else {
            MovieValidation.validateWithoutPicture(movieForm, errors, this.movieRepository, true);
        }

        if (errors.hasErrors()) {
            return;
        }

        Movie movie = findMovieById(id);

        if (file.getSize() != 0) {

            Picture p = this.loadPicture(file, errors);

            if (errors.hasErrors()) {
                return;
            }

            // new picture already has count=1
            this.pictureRepository.save(p);

            // get current picture
            Picture current = this.getPictureByMovieId(movie.getId());

            // override picture id
            movie.editData(movieForm, p.getId());

            // delete if no longer used
            // -1 bcs it was assigned to this movie
            if (current.getCountUsedByMovie() - 1 <= 0 && !current.getName().equals("noposter300.png")) {
                this.pictureRepository.delete(current);
            } else {
                current.decreaseCount();
                this.pictureRepository.save(current);
            }

        } else {
            movie.editData(movieForm, movie.getPictureId());
        }

        movieRepository.save(movie);
    }

    /**
     * Fill {@link MovieForm} with movie data
     * 
     * @param movie Existing movie from the database
     * @return Form filled with data
     */
    public MovieForm fillForm(Movie movie) {
        return new MovieForm(movie.getOriginalName(), movie.getGermanName(), movie.getReleaseYear(), movie.getRegie(),
                movie.getDescriptionText(), String.valueOf(movie.getRunTimeInMin()), movie.getTrailer(), null);
    }

    /**
     * Delete a movie from the database
     * 
     * @param id Movie id from the database
     * @return conditional if the movies is correctly deleted in the
     *         {@link MovieRepository}
     */
    public boolean deleteMovie(long id) {
        Picture picture = this.getPictureByMovieId(id);
        Movie movie = null;

        try {
            movie = this.movieRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return false;
        }

        if (picture == null || movie.linkedWithEvents()) {
            return false;
        }

        movieRepository.deleteById(id);

        // delete picture if no longer used
        if (!picture.getName().equals("noposter300.png")) {
            if (picture.getCountUsedByMovie() == 1) {
                this.pictureRepository.delete(picture);
            } else {
                picture.decreaseCount();
                this.pictureRepository.save(picture);
            }
        }

        return true;
    }

    /**
     * Get all movies from the database
     * 
     * @return An iterable of movies
     */
    public Iterable<Movie> findAll() {
        return movieRepository.findAll();
    }

    /**
     * Find a movie by it's id
     * 
     * @param id movie id from the database
     * @return Movie with given id
     */
    public Movie findMovieById(long id) {
        return movieRepository.findById(id).get();
    }

    /**
     * Returns the {@link MovieRepository} repository
     * 
     * @return The {@link Movie Repository}
     */
    public MovieRepository getMovieRepository() {
        return this.movieRepository;
    }

    /**
     * Returns the matching picture from a movie
     * 
     * @param id The movie id
     * @return The picture object
     */
    public Picture getPictureByMovieId(long id) {
        try {
            return this.pictureRepository.findById(this.findMovieById(id).getPictureId()).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Returns all {@link Movie}s currently available in the system sorted by the
     * given parameters.
     *
     * @param input String to filter by
     * @return all filtered {@link Movie} entities in a {@link Streamable}
     */
    public Streamable<Movie> getFilteredMovies(String input) {
        return MovieFilter.filter(this.movieRepository.findAll(), input);
    }

    /**
     * Returns all {@link Movie}'s currently available in the system sorted by the
     * given parameters.
     *
     * @param sort attribute to be sorted by
     * @param asc  ascending or descending
     * @return all {@link Movie} entities in a sorted {@link Streamable}
     */
    public Streamable<Movie> getAllMovies(String sort, Boolean asc) {
        return this.movieRepository.findAll(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sort));
    }

    /**
     * Returns the default image or restores it if deleted by mistake
     * 
     * @return the default image
     */
    public Picture getInitialPicture() {
        List<Picture> pictures = this.pictureRepository.findAllByName("noposter300.png");
        Picture p = null;

        if (pictures.size() == 0) {
            try {
                p = new Picture(
                        System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
                this.pictureRepository.save(p);
            } catch (IOException e) {
                System.out
                        .println("#####\n\n\n\n please check the given file paths in PictureInitializer \n\n\n\n#####");
            }
        } else {
            p = pictures.get(0);
        }
        return p;
    }


    public Picture loadPicture(MultipartFile file, Errors errors) {
        Picture p = null;
            try {
                p = new Picture(file);
            } catch (IOException e) {
                errors.rejectValue("picture", "movie.edit.unknown_file_error");
            } // catch (IllegalStateException e) {
            //     errors.rejectValue("picture", "kik.unknown_system_error");
            // }
        return p;
    }
}
