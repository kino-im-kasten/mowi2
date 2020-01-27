package kik.movie.controller;

import com.mysema.commons.lang.Assert;
import kik.movie.data.Movie;
import kik.movie.data.MovieForm;
import kik.movie.management.MovieManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * {@link MovieGetController} handling HTTP::GET requests for movies
 *
 * @author Felix Rülke
 * @version 0.1.2
 */
@Controller
public class MovieGetController {

    private final MovieManagement movieManagement;

    /**
     * Default constructor of an {@link MovieGetController}
     *
     * @param movieManagement   Management layer to distribute functionality
     */
    public MovieGetController(MovieManagement movieManagement) {
        Assert.notNull(movieManagement, "movieManagement must not be null!");
        this.movieManagement = movieManagement;
    }

    /**
     * Handles the request for HTTP::GET on /movies
     *
     * @param model Model of HTML5 page
     * @param sort  The string to sort after
     * @param asc   Boolean value where true means an ascending order
     * @return Name of the html-file in the webserver's file-root folder
     */
    @GetMapping(value = "/movies")
    @PreAuthorize("hasRole('USER')")
    String movieOverview(Model model, @RequestParam(required = false) String sort,
            @RequestParam(required = false) Boolean asc) {

        if (sort == null) {
            // If not specified, will sort by alphabet
            model.addAttribute("movies", this.movieManagement.getAllMovies("germanName", true));
        } else {
            model.addAttribute("asc", asc);
            model.addAttribute("sortedBy", sort);
            model.addAttribute("movies", this.movieManagement.getAllMovies(sort, asc));
        }

        return "movie/movies";
    }

    /**
     * Handles the request for HTTP::GET on /createMovie
     *
     * @param model Model of HTML5 page
     * @param form  Input entry form
     * @return Name of the html-file in the webserver's file-root folder
     */
    @PreAuthorize("hasRole('ORGA')")
    @GetMapping(value = "/createMovie")
    String createMovie(Model model, MovieForm form) {
        model.addAttribute("movieForm", form);
        return "movie/createMovie";
    }

    /**
     * Handles the request for HTTP::GET on /movies/details/$id
     *
     * @param id    Id of Movie in the database
     * @param model Model of HTML5 page
     * @return Name of the html-file in the webserver's file-root folder
     */
    @GetMapping(value = "/movies/details/{id}")
    @PreAuthorize("hasRole('USER')")
    String movieDetails(@PathVariable long id, Model model) {
        model.addAttribute("movie", this.movieManagement.findMovieById(id));
        return "movie/detailMovie";
    }

    /**
     * Handles the request for HTTP::GET on /movies/edit/$id
     *
     * @param id    Id of Movie in the database
     * @param model Model of HTML5 page
     * @return Name of the html-file in the webserver's file-root folder
     */
    @GetMapping(value = "/movies/edit/{id}")
	@PreAuthorize("hasRole('ORGA')")
    String movieEdit(@PathVariable long id, Model model) {
        Movie movie = this.movieManagement.findMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("movieForm", movieManagement.fillForm(movie));
        return "movie/editMovie";

    }
}
