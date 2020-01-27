package kik.movie.controller;

import com.mysema.commons.lang.Assert;
import kik.movie.data.MovieForm;
import kik.movie.data.Movie;
import kik.movie.management.MovieManagement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import javax.validation.Valid;

/**
 * {@link MoviePostController} handling HTTP::POST requests for movies
 *
 * @author Felix Rülke
 * @version 0.1.2
 */
@Controller
public class MoviePostController {

    private final MovieManagement movieManagement;

    /**
     * Default Constructor of an {@link MoviePostController}
     *
     * @param movieManagement Business logic for movies
     */
    public MoviePostController(MovieManagement movieManagement) {
        Assert.notNull(movieManagement, "movieManager must not be null!");
        this.movieManagement = movieManagement;
    }

    /**
     * Handles the request for HTTP::POST on /createMovie To create a new
     * {@link Movie}
     *
     * @param movieForm Holds neccessary parameters for creation of an {@link Movie}
     * @param file      file wich holds a picture
     * @param errors    Possible, propagated errors during the filling of the form
     * @return A redirect to the movie overview page
     */
    @PostMapping(value = "/createMovie")
    @PreAuthorize("hasRole('ORGA')")
    String createMovie(@ModelAttribute("movieForm") MovieForm movieForm,
            @RequestParam("picture") MultipartFile file, Errors errors) {

        movieManagement.createMovie(movieForm, file, errors);

        if (errors.hasErrors()) {
            return "movie/createMovie";
        }

        return "redirect:/movies";
    }

    /**
     * Handles the request for HTTP::POST on /movies/delete/$id To delete a
     * {@link Movie}
     *
     * @param id Id of Movie in the database
     * @return A redirect to the movie overview page
     */
    @PostMapping(path = "/movies/delete/{id}")
    @PreAuthorize("hasRole('ORGA')")
    String removeMovie(@PathVariable("id") long id) {

        try {
            this.movieManagement.deleteMovie(id);
        } catch (Exception e) {
            return "redirect:/movies";
        }

        return "redirect:/movies";
    }

    /**
     * Handles the request for HTTP::POST on /movies/edit/$id To edit a
     * {@link Movie}
     *
     * @param id        Id of Movie in the database
     * @param movieForm Holds neccessary parameters for creation of an {@link Movie}
     * @param file      file wich holds a picture
     * @param model     Model of HTML5 page
     * @param errors    Possible, propagated errors during the filling of the form
     * @return A redirect to the movie overview page
     * @throws IllegalStateException Signals that a method has been invoked at an
     *                               illegal or inappropriate time
     * @throws IOException           Signals that an I/O exception of some sort has
     *                               occurred.
     */
    @PostMapping(value = "/movies/edit/{id}")
    @PreAuthorize("hasRole('ORGA')")
    String movieEdit(@PathVariable long id, @Valid @ModelAttribute("movieForm") MovieForm movieForm,
            @RequestParam("picture") MultipartFile file, Errors errors, Model model)
            throws IllegalStateException, IOException {

        movieManagement.editMovie(id, movieForm, file, errors);
        model.addAttribute("movie", this.movieManagement.findMovieById(id));

        if (errors.hasErrors()) {
            return "movie/editMovie";
        }
        return "redirect:/movies";
    }

    /**
     * Handles the request for HTTP::POST on /movies/edit/$id Used to filter movies
     * by a given input
     * 
     * @param input Some sort of string to search after
     * @param model Model of HTML5 page
     * @return Return to the website with the search results
     */
    @PostMapping(value = "/movies")
    String filterMovies(@RequestParam(name = "input") String input, Model model) {

        model.addAttribute("movies", this.movieManagement.getFilteredMovies(input));
        // model.addAttribute("input", input);

        return "movie/movies";
    }
}
